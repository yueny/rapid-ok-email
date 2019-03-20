package com.yueny.rapid.email;

import com.yueny.rapid.email.config.EmailConfigureData;
import com.yueny.rapid.email.config.EmailConstant;
import com.yueny.rapid.email.encrypt.EncryptedEmailPasswordCallback;
import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.factory.MailConfigureFactory;
import com.yueny.rapid.email.model.xml.XMLEmailConfiguration;
import com.yueny.rapid.email.sender.entity.FileMsgAttachmentEntry;
import com.yueny.rapid.email.sender.entity.MessageData;
import com.yueny.rapid.email.sender.entity.URLMsgAttachmentEntry;
import com.yueny.rapid.email.sender.internals.tacitly.IEmailServer;
import com.yueny.rapid.email.util.MailSmtpType;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 *
 */
@Slf4j
public class OkEmail implements IOkEmail {
    private static String xmlPath = "/email/email-config.xml";

    static {
        EmailConfigureData ec = init();
        if(ec != null) {
            MailConfigureFactory.register(ec);
        }
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
            if (ec.isDecrypt()) {
                final String dePasswd = emailDefaultConfiguration.getAuth().getPassword();
                // 密码解密
                final String pas = EncryptedEmailPasswordCallback.decrypt(dePasswd);
                ec.setPassword(pas);
            }else{
                ec.setPassword(emailDefaultConfiguration.getAuth().getPassword());
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
     * @param password email auth password 明文密码. 默认密码不加密
     */
    public static void config(MailSmtpType mailType, final String username, final String password) {
        config(mailType, username, password, false);
    }
    /**
     * 即使存在, 也进行邮箱初始化配置.
     * only config username and password.
     *
     * config(MailSmtpType._126, "deep_blue_yang@126.com", "sdfasfdsfgsfas", true);
     *
     * @param username email auth username
     * @param password email auth password 密码
     * @param isEncrypt 是否已加密
     */
    public static void config(MailSmtpType mailType, final String username, final String password, boolean isEncrypt) {
        config(mailType, username, password, isEncrypt, false);
    }
    /**
     * 即使存在, 也进行邮箱初始化配置.
     * only config username and password.
     *
     * config(MailSmtpType._126, "deep_blue_yang@126.com", "sdfasfdsfgsfas", true, false);
     *
     * @param username email auth username
     * @param password email auth password 密码
     * @param isEncrypt 是否已加密
     * @param debug 是否debug模式
     */
    public static void config(MailSmtpType mailType, final String username, final String password, boolean isEncrypt, boolean debug) {
        String pw = password;
        if(isEncrypt){
            pw = EncryptedEmailPasswordCallback.decrypt(password);
        }

        if(MailConfigureFactory.exist(username)){
            MailConfigureFactory.refresh(username, pw, mailType.getHostName());
        }else{
            EmailConfigureData ec = defaultConfig(debug);
            ec.setHostName(mailType.getHostName());
            ec.setUserName(username);
            ec.setPassword(pw);

            MailConfigureFactory.register(ec);
        }
    }


    //		/* 认证信息设置，取自配置 */
//		// 设置SMTP服务器名称
//		mailSender.setHost(getEmailConfigure().getHostName());
//		// 设置SMTP端口
//		mailSender.setPort(Integer.valueOf(getEmailConfigure().getSmtpPort()));
//
//		// Default is "smtp".
//		// mailSender.setProtocol(protocol);
//
//		// 设置认证信息
//		mailSender.setUsername(getEmailConfigure().getUserName());
//        mailSender.setPassword(getEmailConfigure().getPassword());
//
//		// Properties properties = new Properties();
//		// //启用调试
//		// properties.setProperty("mail.debug", "true");
//		//// 设置链接超时
//		// properties.setProperty("mail.smtp.timeout", "1000");
//
//		// 设置SMTP端口
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.port", getEmailConfigure().getSmtpPort());
//		// 开启认证 /设置是否使用SSL
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", String.valueOf(getEmailConfigure().isSsl()));
//		// 设置SSL端口
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.port", getEmailConfigure().getSslPort());
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.fallback", "false");
//		// 避免出现认证错误
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.class",
//				"javax.net.ssl.SSLSocketFactory");
//
//		// 如果是网易邮箱， mail.smtp.starttls.enable 设置为 false
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "true");
//
//		/* 发送信息设置，取自入参 */
//		mailSenderr.setDefaultEncoding("UTF-8");

    private static EmailConfigureData defaultConfig(Boolean debug) {
        final EmailConfigureData ec = new EmailConfigureData();

        ec.setSmtpPort(String.valueOf(EmailConstant.SMTP_PORT_465));
        ec.setSsl(EmailConstant.DEFAULT_USE_SSL);
        ec.setPrintDurationTimer(false);

        return ec;
    }

    private MessageData emailMessage;
//    private List<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();

    /**
     * set email subject
     *
     * @param subject subject title
     */
    public static OkEmail subject(String subject) {
        // 每一次都是new的, 所以每一次请求的实体对象和数据均不一样. 故不存在并发问题
        OkEmail okEmail = new OkEmail();
        okEmail.emailMessage = new MessageData();
        okEmail.emailMessage.setSubject(subject);

        return okEmail;
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
        ServiceLoader<IEmailServer> loadedDrivers = ServiceLoader.load(IEmailServer.class);
        Iterator<IEmailServer> driversIterator = loadedDrivers.iterator();
        try{
            //查找具体的实现类的全限定名称
            while(driversIterator.hasNext()) {
                //加载并初始化实现
                IEmailServer emailServer = driversIterator.next();

                String msgId = emailServer.sendSyn(emailMessage);
                log.debug("邮件发送成功:{}!", msgId);
            }
            return true;
        } catch(Throwable t) {
            log.error("邮件发送失败!", t);
            // Do nothing
            t.printStackTrace();
        }

        return false;
    }

    /**
     * set email from
     *
     * @param nickName from nickname
     */
    public OkEmail from(String nickName) {
        return from(nickName, null);
    }

    /**
     * set email nickname and from user
     *
     * @param nickName from nickname
     * @param from     from email
     */
    public OkEmail from(String nickName, String from) {
        emailMessage.setNickName(nickName);
        if(StringUtils.isNotEmpty(from)){
            emailMessage.setFrom(from);
        }

        return this;
    }

    public OkEmail replyTo(String... replyTo) {
        emailMessage.replyTo(replyTo);
        return this;
    }

    public OkEmail replyTo(String replyTo) {
        emailMessage.replyTo(replyTo);
        return this;
    }

    public OkEmail to(String... to) {
        emailMessage.to(to);
        return this;
    }

    public OkEmail to(String to) {
        emailMessage.to(to);
        return this;
    }

    public OkEmail cc(String... cc) {
        emailMessage.cc(cc);
        return this;
    }

    public OkEmail cc(String cc) {
        emailMessage.cc(cc);
        return this;
    }

    public OkEmail bcc(String... bcc) {
        emailMessage.bcc(bcc);
        return this;
    }

    public OkEmail bcc(String bcc) {
        emailMessage.bcc(bcc);
        return this;
    }

    public OkEmail text(String text) {
        emailMessage.setText(text);
        return this;
    }

    public OkEmail html(String html) {
        emailMessage.setHtml(html);
        return this;
    }

    public OkEmail attach(File file) throws SendMailException {
        attach(file, null);
        return this;
    }

    public OkEmail attach(File file, String fileName) throws SendMailException {
        FileMsgAttachmentEntry attachement = new FileMsgAttachmentEntry();
        attachement.setUri(file);
        attachement.setName(fileName);
        attachement.setDescription(fileName);

        emailMessage.attachement(attachement);

        return this;
    }

    public OkEmail attachURL(URL url, String fileName) throws SendMailException {
        URLMsgAttachmentEntry attachement = new URLMsgAttachmentEntry();
        attachement.setUri(url);
        attachement.setName(fileName);
        attachement.setDescription(fileName);

        emailMessage.attachement(attachement);

        return this;
    }

}