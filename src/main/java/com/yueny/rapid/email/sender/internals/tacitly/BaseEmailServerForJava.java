///**
// *
// */
//package com.yueny.rapid.email.sender;
//
//import com.yueny.rapid.lang.util.time.DurationTimer;
//import com.yueny.rapid.message.email.sender.EmailType;
//import com.yueny.rapid.message.email.sender.core.EmailMessage;
//import com.yueny.rapid.message.email.sender.core.EmailMessageAttachment;
//import com.yueny.rapid.message.email.util.EncryptedEmailPasswordCallback;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.mail.javamail.MimeMessageHelper;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeUtility;
//import java.io.File;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 基于 JavaMailSender 的邮件发送服务
// *
// * @author yueny09 <yueny09@163.com>
// *
// * @DATE 2017年12月15日 下午3:54:16
// *
// */
//@Slf4j
//public abstract class BaseEmailServerForJava extends BaseEmailServer {
//	private JavaMailSenderImpl mailSender = null;
//	/**
//	 * 用户邮箱的明文与密文密码映射表，目的是希望邮件用户密文密码直接解密一次就可以了<br>
//	 * key为密文，value为明文
//	 */
//	private final Map<String, String> passwdForUserNameMap = new ConcurrentHashMap<>();
//
//	/**
//	 * 检查输入是否合法.
//	 *
//	 * @param emailMessage
//	 */
//	private void checkArgumentValid(final EmailMessage emailMessage) {
//		if (getEmailConfigure() == null) {
//			throw new IllegalArgumentException("邮件配置文件未初始化");
//		}
//
//		if (emailMessage == null) {
//			throw new IllegalArgumentException("输入参数为空");
//		}
//
//		// 到少要有一个收件人
//		if ((emailMessage.getTo() == null || emailMessage.getTo().size() == 0)
//				&& (emailMessage.getCc() == null || emailMessage.getCc().size() == 0)
//				&& (emailMessage.getBcc() == null || emailMessage.getBcc().size() == 0)) {
//			throw new IllegalArgumentException("收件人不能为空");
//		}
//	}
//
//	@Override
//	protected void initital() {
//		super.initital();
//
//		mailSender = new JavaMailSenderImpl();
//
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
//		if (getEmailConfigure().isDecrypt()) {
//			final String dePasswd = getEmailConfigure().getPassword();
//			if (passwdForUserNameMap.containsKey(dePasswd)) {
//				mailSender.setPassword(passwdForUserNameMap.get(dePasswd));
//			} else {
//				// 密码解密
//				final String pas = EncryptedEmailPasswordCallback.decrypt(getEmailConfigure().getPassword());
//				mailSender.setPassword(pas);
//
//				passwdForUserNameMap.put(dePasswd, pas);
//			}
//		} else {
//			mailSender.setPassword(getEmailConfigure().getPassword());
//		}
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
//		mailSender.setDefaultEncoding("UTF-8");
//	}
//
//	private String send(final MimeMessage message) throws MailException {
//		// 打印邮件发送耗时日志
//		if (getEmailConfigure().isPrintDurationTimer()) {
//			final DurationTimer emailTimer = new DurationTimer();
//			mailSender.send(message);
//			log.info("acturly 邮件发送耗时：{}/s", emailTimer.durationSecond());
//
//			return "";
//		}
//
//		mailSender.send(message);
//		return "";
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * com.yueny.rapid.message.email.sender.ext.IEmailServer#sendSyn(com.yueny.
//	 * rapid.message.email.sender.core.EmailMessage)
//	 */
//	@Override
//	public final String sendSyn(final EmailMessage emailMessage) throws Exception {
//		// 检查输入参数
//		checkArgumentValid(emailMessage);
//
//		log.debug("使用{}发送邮件中~", getEmailConfigure().getAlias());
//
//		if (emailMessage.getType().equals(EmailType.PLAIN)) {
//			String messageID = "";
//			// 简单邮件
//			if (emailMessage.getAttachements() != null && emailMessage.getAttachements().size() > 0) {
//				// 有附件
//				final MimeMessage message = mailSender.createMimeMessage();
//
//				// use the true flag to indicate you need a multipart message
//				final MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
//
//				setAttachment(helper, emailMessage);
//				setEmail(helper, emailMessage);
//
//				messageID = send(message);
//			} else {
//				// 没有附件
//				final MimeMessage message = mailSender.createMimeMessage();
//
//				// 创建MimeMessageHelper对象，处理MimeMessage的辅助类
//				final MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
//
//				setEmail(helper, emailMessage);
//
//				messageID = send(message);
//			}
//
//			return messageID;
//		}
//
//		// HTML邮件: EmailType.HTML
//		final MimeMessage message = mailSender.createMimeMessage();
//		// 创建MimeMessageHelper对象，处理MimeMessage的辅助类
//		final MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
//
//		setAttachment(helper, emailMessage);
//		setHtmlEmail(helper, emailMessage);
//
//		return send(message);
//	}
//
//	/**
//	 * 加载邮件附件信息.
//	 *
//	 * @param email
//	 *            邮件对象
//	 * @param emailMessage
//	 *            邮件信息对象
//	 * @throws EmailSendException
//	 *             邮件发送异常
//	 */
//	private void setAttachment(final MimeMessageHelper helper, final EmailMessage emailMessage)
//			throws MessagingException {
//		for (final Iterator<EmailMessageAttachment> iter = emailMessage.getAttachements().iterator(); iter.hasNext();) {
//			final EmailMessageAttachment messageAttachement = iter.next();
//			try {
//				// 如果附件没有名称,取文件名.
//				String name = messageAttachement.getName();
//				if (name == null) {
//					name = StringUtils.substringAfterLast(messageAttachement.getPath(), File.separator);
//				}
//
//				final String attachmentName = MimeUtility.encodeText(name);
//
//				// 加载文件资源，作为附件
//				// new File(messageAttachement.getPath())
//				final ClassPathResource file = new ClassPathResource(messageAttachement.getPath());
//				// 加入附件
//				helper.addAttachment(attachmentName, file);
//				// if (messageAttachement.getDescription() != null) {
//				// attachment.setDescription(MimeUtility.encodeText(messageAttachement.getDescription()));
//				// } else {
//				// attachment.setDescription("");
//				// }
//			} catch (final Exception e) {
//				log.error("构造Email附件出现异常: ", e);
//				throw new MessagingException("构造Email附件出现异常");
//			}
//		}
//	}
//
//	/**
//	 * 设置邮件基本信息.
//	 *
//	 * @param email
//	 *            邮件对象
//	 * @param emailMessage
//	 *            邮件信息对象
//	 * @throws MessagingException
//	 * @throws EmailSendException
//	 *             邮件发送异常
//	 */
//	private void setEmail(final MimeMessageHelper helper, final EmailMessage emailMessage) throws MessagingException {
//		try {
//			// 发送的邮件地址:如果用户设置了自己的from，则用自己设置的from地址
//			log.debug("System default encoding: " + System.getProperty("file.encoding"));
//			helper.setFrom(getEmailConfigure().getFrom());
//
//			// 发送到的地址
//			for (final Iterator<String> iter = emailMessage.getTo().iterator(); iter.hasNext();) {
//				helper.addTo(iter.next());
//			}
//			// 抄送地址
//			for (final Iterator<String> iter = emailMessage.getCc().iterator(); iter.hasNext();) {
//				helper.addCc(iter.next());
//			}
//			// 密送地址
//			for (final Iterator<String> iter = emailMessage.getBcc().iterator(); iter.hasNext();) {
//				helper.addBcc(iter.next());
//			}
//
//			// 发送时间
//			if (emailMessage.getSentDate() != null) {
//				helper.setSentDate(emailMessage.getSentDate());
//			}
//			// 主题
//			helper.setSubject(emailMessage.getSubject());
//
//			// 设置简单类型邮件的邮件正文,HTML正文不在此处设置
//			if (emailMessage.getType().equals(EmailType.PLAIN)) {
//				helper.setText(emailMessage.getMsg());
//			}
//		} catch (final MessagingException e) {
//			log.error("构造Email出现异常: ", e);
//			throw new MessagingException("构造Email出现异常");
//		}
//	}
//
//	/**
//	 * 设置HTML邮件基本信息.
//	 *
//	 * @param htmlEmail
//	 *            邮件对象
//	 * @param emailMessage
//	 *            邮件信息对象
//	 * @throws MessagingException
//	 *             邮件发送异常
//	 */
//	private void setHtmlEmail(final MimeMessageHelper helper, final EmailMessage emailMessage)
//			throws MessagingException {
//		setEmail(helper, emailMessage);
//		helper.setText(emailMessage.getMsg(), true);
//	}
//
//}
