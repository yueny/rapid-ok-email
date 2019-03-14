package com.yueny.rapid.email.sender.internals.tacitly;

import com.yueny.rapid.email.OkEmail;
import com.yueny.rapid.email.cluster.RandomLoadBalance;
import com.yueny.rapid.email.config.EmailConfigureData;
import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.factory.MailConfigureFactory;
import com.yueny.rapid.email.factory.MailJavaxSessionFactory;
import com.yueny.rapid.email.sender.call.IEmailSendCallback;
import com.yueny.rapid.email.sender.entity.MessageData;
import com.yueny.rapid.email.sender.entity.ThreadEmailEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.*;
import java.util.concurrent.Future;

/**
 * 基于 JavaMailSender 的邮件发送服务
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月15日 下午3:54:16
 *
 */
@Slf4j
public class JavaxMailServer extends BaseEmailServer {
    public JavaxMailServer(){
        //.
    }

    @Override
    public Future<ThreadEmailEntry> send(MessageData emailMessage) {
        return null;
    }

    @Override
    public Future<ThreadEmailEntry> send(MessageData emailMessage, IEmailSendCallback callble) {
        return null;
    }

    @Override
    public String sendSyn(MessageData emailMessage) throws Exception {
        if (emailMessage.getText() == null && emailMessage.getHtml() == null) {
            throw new IllegalArgumentException("At least one context has to be provided: Text or Html");
        }

        List<MimeBodyPart> attachments = getAttachments(emailMessage);
        MimeMultipart cover;
        boolean       usingAlternative = false;
        boolean       hasAttachments   = attachments.size() > 0;

        try {
            if (emailMessage.getText() != null && emailMessage.getHtml() == null) {
                // TEXT ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(textPart(emailMessage.getText()));
            } else if (emailMessage.getText() == null && emailMessage.getHtml() != null) {
                // HTML ONLY
                cover = new MimeMultipart("mixed");
                cover.addBodyPart(htmlPart(emailMessage.getHtml()));
            } else {
                // HTML + TEXT
                cover = new MimeMultipart("alternative");
                cover.addBodyPart(textPart(emailMessage.getText()));
                cover.addBodyPart(htmlPart(emailMessage.getHtml()));
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

            Map.Entry<EmailConfigureData, MimeMessage> msgMap = getMessage();
            fillMessageData(msgMap, emailMessage);

            MimeMessage msg = msgMap.getValue();

            msg.setContent(content);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            log.error("邮件发送异常:", e);
            throw new SendMailException(e);
        }

        return "";
    }

    private MimeBodyPart toBodyPart(MimeMultipart cover) throws MessagingException {
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(cover);
        return wrap;
    }

    private MimeBodyPart textPart(String text) throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(text);
        return bodyPart;
    }

    private MimeBodyPart htmlPart(String html) throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(html, "text/html; charset=utf-8");
        return bodyPart;
    }

    private List<MimeBodyPart> getAttachments(MessageData emailMessage) {
        List<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();

        return attachments;
    }

    private void fillMessageData(Map.Entry<EmailConfigureData, MimeMessage> messageMap, MessageData data) throws SendMailException {
        MimeMessage message = messageMap.getValue();
        try {
            message.setSubject(data.getSubject(), "UTF-8");

            String encodeNickName = MimeUtility.encodeText(data.getNickName());
            if(StringUtils.isNotEmpty(data.getFrom())){
                message.setFrom(new InternetAddress(encodeNickName + " <" + data.getFrom() + ">"));
            }else{
                message.setFrom(new InternetAddress(encodeNickName + " <" + messageMap.getKey().getUserName() + ">"));
            }

            if(CollectionUtils.isNotEmpty(data.getReplyTo())){
                String result = Arrays.asList(data.getReplyTo()).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
                message.setReplyTo(InternetAddress.parse(result));
            }

            if(CollectionUtils.isNotEmpty(data.getTo())){
                addRecipients(message, data.getTo(), Message.RecipientType.TO);
            }
            if(CollectionUtils.isNotEmpty(data.getCc())){
                addRecipients(message, data.getCc(), Message.RecipientType.CC);
            }
            if(CollectionUtils.isNotEmpty(data.getBcc())){
                addRecipients(message, data.getBcc(), Message.RecipientType.BCC);
            }


        } catch (Exception e) {
            throw new SendMailException(e);
        }
    }

    private Map.Entry<EmailConfigureData, MimeMessage> getMessage() {
        // 随机选一个有效配置
        EmailConfigureData data = new RandomLoadBalance().select(MailConfigureFactory.getAll());

        Session session = MailJavaxSessionFactory.get(data.getUserName());

        MimeMessage msg  = new MimeMessage(session);

        return new AbstractMap.SimpleEntry<EmailConfigureData, MimeMessage>(data, msg);
    }

    /**
     * 批量发送
     * @param msg
     * @param recipients
     * @param type
     * @throws MessagingException
     */
    private void addRecipients(MimeMessage msg, List<String> recipients, Message.RecipientType type) throws MessagingException {
        String result = recipients.toArray().toString().replace("(^\\[|\\]$)", "").replace(", ", ",");
        // TODO
        msg.setRecipients(type, InternetAddress.parse(result));
    }

    /**
     * 单条发送
     * @param msg
     * @param recipient
     * @param type
     * @throws MessagingException
     */
    private void addRecipient(MimeMessage msg, String recipient, Message.RecipientType type) throws MessagingException {
        msg.setRecipients(type, InternetAddress.parse(recipient.replace(";", ",")));
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


}
