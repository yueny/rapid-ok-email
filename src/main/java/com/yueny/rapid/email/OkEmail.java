package com.yueny.rapid.email;

import com.yueny.rapid.email.cluster.RandomLoadBalance;
import com.yueny.rapid.email.config.EmailConfigureData;
import com.yueny.rapid.email.encrypt.EncryptedEmailPasswordCallback;
import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.factory.MailMonitorFactory;
import com.yueny.rapid.email.model.xml.XMLEmailConfiguration;
import com.yueny.rapid.email.util.MailSmtpType;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.xml.bind.JAXB;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Slf4j
public class OkEmail implements IOkEmail {
    private static String xmlPath = "/email/email-config.xml";

    /**
     * 邮箱初始化的配置列
     */
    private static Map<String, EmailConfigureData> esConfigMap = new ConcurrentHashMap<>();

    private static Session session;
    private static String  user;

    private MimeMessage msg;
    private String             text;
    private String             html;
    private List<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();

    static{
        EmailConfigureData ec = init();
        MailMonitorFactory.register(ec);
    }

    /**
     * init 邮件配置信息
     */
    private static EmailConfigureData init() {
        try {
            @Cleanup
            final InputStreamReader reader = new InputStreamReader(OkEmail.class.getResourceAsStream(xmlPath),
                    "UTF-8");
            final XMLEmailConfiguration emailDefaultConfiguration = JAXB.unmarshal(reader, XMLEmailConfiguration.class);
            log.debug("读取到的email配置: {}.", emailDefaultConfiguration);

            final EmailConfigureData ec = new EmailConfigureData();
            ec.setAlias(emailDefaultConfiguration.getAlias());
            ec.setFrom(emailDefaultConfiguration.getFrom());
            ec.setHostName(emailDefaultConfiguration.getHostName());

            ec.setUserName(emailDefaultConfiguration.getAuth().getUserName());
            ec.setDecrypt(emailDefaultConfiguration.getAuth().getDecrypt());

            ec.setPassword(emailDefaultConfiguration.getAuth().getPassword());
            if (ec.isDecrypt()) {
                final String dePasswd = emailDefaultConfiguration.getAuth().getPassword();
                // 密码解密
                final String pas = EncryptedEmailPasswordCallback.decrypt(dePasswd);
                ec.setPassword(pas);
            }

            ec.setSmtpPort(emailDefaultConfiguration.getSmtpPort());
            ec.setSsl(emailDefaultConfiguration.getSsl());
            ec.setSslPort(emailDefaultConfiguration.getSslPort());

            // config
            if (emailDefaultConfiguration.getConfig() != null) {
                ec.setPrintDurationTimer(emailDefaultConfiguration.getConfig().getPrintDurationTimer());
            }

            return ec;
        } catch (final Exception e) {
            log.error("加载配置异常，默认配置置为空！", e);
        } finally {
            // IOUtils.closeQuietly(reader);
        }
        return null;
    }

    /**
     * 即使存在, 也进行邮箱初始化配置.
     * only config username and password.
     *
     *
     * // 初始化
     * config(MailSmtpType._126, "deep_blue_yang@126.com", "li....");
     *
     *
     * @param username email auth username
     * @param password email auth password
     */
    public static void config(MailSmtpType mailType, final String username, final String password) {
        config(mailType, username, password, false);
    }
    /**
     * 即使存在, 也进行邮箱初始化配置.
     * only config username and password.
     *
     *
     * // 初始化
     * refreshEven(OkEmail.SMTP_126(false), "deep_blue_yang@126.com", "li....");
     *
     *
     * @param username email auth username
     * @param password email auth password
     */
    public static void config(MailSmtpType mailType, final String username, final String password, boolean debug) {
        if(MailMonitorFactory.exist(username)){
            MailMonitorFactory.refresh(username, password, mailType.getHostName());
        }else{
            EmailConfigureData ec = defaultConfig(debug);
            ec.setHostName(mailType.getHostName());
            ec.setUserName(username);
            ec.setPassword(password);

            MailMonitorFactory.register(ec);
        }
    }

    private static EmailConfigureData defaultConfig(Boolean debug) {
        final EmailConfigureData ec = new EmailConfigureData();

        ec.setSmtpPort("465");
        ec.setSsl(true);
        ec.setPrintDurationTimer(false);

        return ec;
    }

    @Deprecated
    public static Properties defaultConfigDel(Boolean debug) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", null != debug ? debug.toString() : "false");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.port", "465");
        return props;
    }
    /**
     * set email subject
     *
     * @param subject subject title
     */
    public static OkEmail subject(String subject) throws SendMailException {
        EmailConfigureData data = new RandomLoadBalance().select(MailMonitorFactory.getAll());

        Properties props = defaultConfigDel(false);
        props.put("mail.smtp.host", data.getHostName());

        props.setProperty("username", data.getUserName());
        props.setProperty("password", data.getPassword());

        user = data.getUserName();
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(data.getUserName(), data.getPassword());
            }
        });


        OkEmail ohMyEmail = new OkEmail();
        ohMyEmail.msg = new MimeMessage(session);
        try {
            ohMyEmail.msg.setSubject(subject, "UTF-8");
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return ohMyEmail;
    }

    private OkEmail() {
        //.
    }

    @Override
    public void destory() {
        //.
    }

    @Override
    public boolean send() throws SendMailException {
        if (text == null && html == null) {
            throw new IllegalArgumentException("At least one context has to be provided: Text or Html");
        }

        MimeMultipart cover;
        boolean       usingAlternative = false;
        boolean       hasAttachments   = attachments.size() > 0;

        try {
            if (text != null && html == null) {
                // TEXT ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(textPart());
            } else if (text == null && html != null) {
                // HTML ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(htmlPart());
            } else {
                // HTML + TEXT
                cover = new MimeMultipart("alternative");
                cover.addBodyPart(textPart());
                cover.addBodyPart(htmlPart());
                usingAlternative = true;
            }

            MimeMultipart content = cover;
            if (usingAlternative && hasAttachments) {
                content = new MimeMultipart("mixed");
                content.addBodyPart(toBodyPart(cover));
            }

            for (MimeBodyPart attachment : attachments) {
                content.addBodyPart(attachment);
            }

            msg.setContent(content);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            throw new SendMailException(e);
        }

        return true;
    }

    /**
     * set email from
     *
     * @param nickName from nickname
     */
    public OkEmail from(String nickName) throws SendMailException {
        return from(nickName, user);
    }

    /**
     * set email nickname and from user
     *
     * @param nickName from nickname
     * @param from     from email
     */
    public OkEmail from(String nickName, String from) throws SendMailException {
        try {
            String encodeNickName = MimeUtility.encodeText(nickName);
            msg.setFrom(new InternetAddress(encodeNickName + " <" + from + ">"));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return this;
    }

    public OkEmail replyTo(String... replyTo) throws SendMailException {
        String result = Arrays.asList(replyTo).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
        try {
            msg.setReplyTo(InternetAddress.parse(result));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return this;
    }

    public OkEmail replyTo(String replyTo) throws SendMailException {
        try {
            msg.setReplyTo(InternetAddress.parse(replyTo.replace(";", ",")));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return this;
    }

    public OkEmail to(String... to) throws SendMailException {
        try {
            return addRecipients(to, Message.RecipientType.TO);
        } catch (MessagingException e) {
            throw new SendMailException(e);
        }
    }

    public OkEmail to(String to) throws SendMailException {
        try {
            return addRecipient(to, Message.RecipientType.TO);
        } catch (MessagingException e) {
            throw new SendMailException(e);
        }
    }

    public OkEmail cc(String... cc) throws SendMailException {
        try {
            return addRecipients(cc, Message.RecipientType.CC);
        } catch (MessagingException e) {
            throw new SendMailException(e);
        }
    }

    public OkEmail cc(String cc) throws SendMailException {
        try {
            return addRecipient(cc, Message.RecipientType.CC);
        } catch (MessagingException e) {
            throw new SendMailException(e);
        }
    }

    public OkEmail bcc(String... bcc) throws SendMailException {
        try {
            return addRecipients(bcc, Message.RecipientType.BCC);
        } catch (MessagingException e) {
            throw new SendMailException(e);
        }
    }

    public OkEmail bcc(String bcc) throws MessagingException {
        return addRecipient(bcc, Message.RecipientType.BCC);
    }

    private OkEmail addRecipients(String[] recipients, Message.RecipientType type) throws MessagingException {
        String result = Arrays.asList(recipients).toString().replace("(^\\[|\\]$)", "").replace(", ", ",");
        msg.setRecipients(type, InternetAddress.parse(result));
        return this;
    }

    private OkEmail addRecipient(String recipient, Message.RecipientType type) throws MessagingException {
        msg.setRecipients(type, InternetAddress.parse(recipient.replace(";", ",")));
        return this;
    }

    public OkEmail text(String text) {
        this.text = text;
        return this;
    }

    public OkEmail html(String html) {
        this.html = html;
        return this;
    }

    public OkEmail attach(File file) throws SendMailException {
        attachments.add(createAttachment(file, null));
        return this;
    }

    public OkEmail attach(File file, String fileName) throws SendMailException {
        attachments.add(createAttachment(file, fileName));
        return this;
    }

    public OkEmail attachURL(URL url, String fileName) throws SendMailException {
        attachments.add(createURLAttachment(url, fileName));
        return this;
    }

    private MimeBodyPart createAttachment(File file, String fileName) throws SendMailException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        FileDataSource fds            = new FileDataSource(file);
        try {
            attachmentPart.setDataHandler(new DataHandler(fds));
            attachmentPart.setFileName(null == fileName ? MimeUtility.encodeText(fds.getName()) : MimeUtility.encodeText(fileName));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return attachmentPart;
    }

    private MimeBodyPart createURLAttachment(URL url, String fileName) throws SendMailException {
        MimeBodyPart attachmentPart = new MimeBodyPart();

        DataHandler dataHandler = new DataHandler(url);
        try {
            attachmentPart.setDataHandler(dataHandler);
            attachmentPart.setFileName(null == fileName ? MimeUtility.encodeText(fileName) : MimeUtility.encodeText(fileName));
        } catch (Exception e) {
            throw new SendMailException(e);
        }
        return attachmentPart;
    }

    private MimeBodyPart toBodyPart(MimeMultipart cover) throws MessagingException {
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(cover);
        return wrap;
    }

    private MimeBodyPart textPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(text);
        return bodyPart;
    }

    private MimeBodyPart htmlPart() throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(html, "text/html; charset=utf-8");
        return bodyPart;
    }

}