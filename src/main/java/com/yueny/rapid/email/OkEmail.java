package com.yueny.rapid.email;

import com.yueny.rapid.email.config.EmailInnerConfigureData;
import com.yueny.rapid.email.context.engine.*;
import com.yueny.rapid.email.encrypt.EncryptedEmailPasswordCallback;
import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.factory.MailConfigureFactory;
import com.yueny.rapid.email.model.xml.XMLEmailConfiguration;
import com.yueny.rapid.email.sender.entity.FileMsgAttachmentEntry;
import com.yueny.rapid.email.sender.entity.MessageData;
import com.yueny.rapid.email.sender.entity.ThreadEmailEntry;
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
import java.util.concurrent.Future;

/**
 *
 */
@Slf4j
public class OkEmail implements IOkEmail {
    private static String xmlPath = "/email/email-config.xml";

    private static IEngine jetEngine = null;
    private static IEngine pebbleEngine = null;
    private static IEngine freemarkEngine = null;
    static {
        EmailInnerConfigureData ec = init();
        if(ec != null) {
            MailConfigureFactory.register(ec);
        }

        jetEngine = new JetEngineImpl();
        pebbleEngine = new PebbleEngineImpl();
        freemarkEngine = new FreemarkEngineImpl();
    }

    /**
     * init 邮件配置信息
     */
    private static EmailInnerConfigureData init() {
        try {
            @Cleanup
            final InputStreamReader reader = new InputStreamReader(OkEmail.class.getResourceAsStream(xmlPath),
                    "UTF-8");
            final XMLEmailConfiguration emailDefaultConfiguration = JAXB.unmarshal(reader, XMLEmailConfiguration.class);
            log.debug("读取到的email配置: {}.", emailDefaultConfiguration);

            EmailInnerConfigureData.EmailInnerConfigureDataBuilder builder = EmailInnerConfigureData.builder()
                    .transportProtocol(emailDefaultConfiguration.getTransportProtocol())
                    .alias(emailDefaultConfiguration.getAlias())
                    .from(emailDefaultConfiguration.getFrom())
                    .hostName(emailDefaultConfiguration.getHostName())
                    .userName(emailDefaultConfiguration.getAuth().getUserName())
                    .decrypt(emailDefaultConfiguration.getAuth().getDecrypt())
                    .pwPBESalt(emailDefaultConfiguration.getAuth().getPwPBESalt())
                    .smtpPort(emailDefaultConfiguration.getSmtpPort())
                    .ssl(emailDefaultConfiguration.getSsl())
                    .sslPort(emailDefaultConfiguration.getSslPort());

            builder.password(getPassword(emailDefaultConfiguration.getAuth().getPassword(),
                    emailDefaultConfiguration.getAuth().getDecrypt(),
                    emailDefaultConfiguration.getAuth().getPwPBESalt()));

            // config
            if (emailDefaultConfiguration.getConfig() != null) {
                builder.printDurationTimer(emailDefaultConfiguration.getConfig().getPrintDurationTimer());
                builder.debug(emailDefaultConfiguration.getConfig().getDebug());
            }

            EmailInnerConfigureData config = builder.build();
            if(config.isDebug()){
                log.debug("读取到的email配置组装完成: {}.", config);
            }

            return config;
        } catch (final Exception e) {
            log.error("加载配置异常，默认配置置为空, 不影响功能性.！", e);
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
        String pw = getPassword(password, isEncrypt, "");

        if(!MailConfigureFactory.exist(username)){
            EmailInnerConfigureData ec = defaultConfig(mailType.getHostName(), username, pw, debug);

            MailConfigureFactory.register(ec);
        }
    }

    private static EmailInnerConfigureData defaultConfig(String hostName, String userName, String password, Boolean debug) {
        return EmailInnerConfigureData.builder()
                .hostName(hostName)
                .userName(userName)
                .password(password)
                .smtpPort("465")
                .sslPort("465")
                .ssl(true)
                .printDurationTimer(false)
                .debug(false)
                .build();
    }

    private static String getPassword(String password, boolean decrypt, String pwPBESalt) {
        if (decrypt) {
            // 密码解密
            if(StringUtils.isNotEmpty(pwPBESalt)){ // 二次加密
                return EncryptedEmailPasswordCallback.decrypt(password, pwPBESalt);
            }
            return EncryptedEmailPasswordCallback.decrypt(password);
        }else{
            return password;
        }
    }

    private MessageData emailMessage;

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
    public boolean send() {
        ServiceLoader<IEmailServer> loadedDrivers = ServiceLoader.load(IEmailServer.class);
        Iterator<IEmailServer> driversIterator = loadedDrivers.iterator();
        //加载并初始化实现
        IEmailServer emailServer = driversIterator.next();

        try{
            String msgId = emailServer.sendSyn(emailMessage);

            log.debug("邮件发送成功, msgId:{}!", msgId);
            return true;
        } catch(Throwable t) {
            log.error("邮件发送失败!", t);
            // Do nothing
        }

        return false;
    }

    @Override
    public Future<ThreadEmailEntry> sendFuture() {
        ServiceLoader<IEmailServer> loadedDrivers = ServiceLoader.load(IEmailServer.class);
        Iterator<IEmailServer> driversIterator = loadedDrivers.iterator();

        //加载并初始化实现
        IEmailServer emailServer = driversIterator.next();

        Future<ThreadEmailEntry> future = emailServer.send(emailMessage);
        log.debug("邮件已发送, future:{}!", future);

        return future;
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
    public OkEmail html(EngineType type, String name, Map<String, Object> context) {
        String html = "";

        try{
            if(type == EngineType.JET){
                html = jetEngine.render(name, context);
            }else if(type == EngineType.PEBBLE){
                html = pebbleEngine.render(name, context);
            } else if(type == EngineType.FREEMARKER){
                html = freemarkEngine.render(name, context);
            }
        } catch(Exception e){
            //.
        }

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