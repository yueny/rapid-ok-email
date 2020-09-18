/**
 *
 */
package com.yueny.rapid.email.sender.internals.tacitly;

import com.yueny.rapid.email.cluster.RandomLoadBalance;
import com.yueny.rapid.email.config.EmailInnerConfigureData;
import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.factory.JavaxMailSenderFactory;
import com.yueny.rapid.email.factory.MailConfigureFactory;
import com.yueny.rapid.email.sender.call.IEmailSendCallback;
import com.yueny.rapid.email.sender.entity.*;
import com.yueny.rapid.email.util.MailMethodType;
import com.yueny.rapid.lang.thread.AsyncLoadCallable;
import com.yueny.rapid.lang.thread.AsyncLoadConfig;
import com.yueny.rapid.lang.util.UuidUtil;
import com.yueny.rapid.lang.util.time.DurationTimer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.Future;

/**
 * 基于 JavaMailSender 的邮件发送服务
 *
 * 多种实现方式，二选一
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月15日 下午3:54:16
 *
 */
@Slf4j
public class JavaMailSenderServer extends BaseEmailServer {
	@Override
	public MailMethodType getType() {
		return MailMethodType.JAVA_MAIL;
	}

	/**
	 * 检查输入是否合法.
	 *
	 */
	private void checkArgumentValid(final MessageData messageData) {
		if (messageData == null) {
			throw new IllegalArgumentException("输入参数为空");
		}

		// 到少要有一个收件人
		if ((messageData.getTo() == null || messageData.getTo().size() == 0)
				&& (messageData.getCc() == null || messageData.getCc().size() == 0)
				&& (messageData.getBcc() == null || messageData.getBcc().size() == 0)) {
			throw new IllegalArgumentException("收件人不能为空");
		}
	}

	/**
	 * 加载邮件附件信息.
	 *
	 * @param messageData
	 *            邮件信息对象
	 */
	private void setAttachment(final MimeMessageHelper helper, final MessageData messageData)
			throws MessagingException {
		for (final Iterator<BaseMsgAttachment> iter = messageData.getAttachements().iterator(); iter.hasNext();) {
			final BaseMsgAttachment baseMsgAttachment = iter.next();
			try {
				String path = "";
				if(baseMsgAttachment.getType() == BaseMsgAttachment.Type.FILE){
					FileMsgAttachmentEntry fileAttachment = (FileMsgAttachmentEntry)baseMsgAttachment;

					path = fileAttachment.getUri().getPath();
				}else if(baseMsgAttachment.getType() == BaseMsgAttachment.Type.URL){
					URLMsgAttachmentEntry urlAttachment = (URLMsgAttachmentEntry)baseMsgAttachment;

					path = urlAttachment.getUri().getPath();
				}else{
					log.warn("path 路径的附件, 暂不支持!");
				}

				// 如果附件没有名称,取文件名.
				String name = baseMsgAttachment.getName();
				if (name == null) {
					name = StringUtils.substringAfterLast(path, File.separator);
				}

				final String attachmentName = MimeUtility.encodeText(name);

				// 加载文件资源，作为附件
				// new File(messageAttachement.getPath())
				final ClassPathResource file = new ClassPathResource(path);
				// 加入附件
				helper.addAttachment(attachmentName, file);
				// if (messageAttachement.getDescription() != null) {
				// attachment.setDescription(MimeUtility.encodeText(messageAttachement.getDescription()));
				// } else {
				// attachment.setDescription("");
				// }
			} catch (final Exception e) {
				log.error("构造Email附件出现异常: ", e);
				throw new MessagingException("构造Email附件出现异常");
			}
		}
	}

	/**
	 * 设置邮件基本信息.
	 *
	 * @param messageData
	 *            邮件信息对象
	 */
	private void setEmail(final MimeMessageHelper helper, final MessageData messageData, EmailInnerConfigureData data)
			throws MessagingException {
		try {
			// 发送的邮件地址:如果用户设置了自己的from，则用自己设置的from地址
			log.debug("System default encoding: " + System.getProperty("file.encoding"));

			String encodeNickName = messageData.getNickName();
			if(StringUtils.isNotEmpty(messageData.getFrom())){
				helper.setFrom(encodeNickName + " <" + messageData.getFrom() + ">");
			}else{
				helper.setFrom(encodeNickName + " <" + data.getUserName() + ">");
			}

			// 发送到的地址
			for (final Iterator<String> iter = messageData.getTo().iterator(); iter.hasNext();) {
				helper.addTo(iter.next());
			}
			// 抄送地址
			for (final Iterator<String> iter = messageData.getCc().iterator(); iter.hasNext();) {
				helper.addCc(iter.next());
			}
			// 密送地址
			for (final Iterator<String> iter = messageData.getBcc().iterator(); iter.hasNext();) {
				helper.addBcc(iter.next());
			}

			// 发送时间
			if (messageData.getSentDate() != null) {
				helper.setSentDate(messageData.getSentDate());
			}
			// 主题
			helper.setSubject(messageData.getSubject());

			if (messageData.getText() != null && messageData.getHtml() == null) {
				helper.setText(messageData.getText());
			} else if (messageData.getText() == null && messageData.getHtml() != null) {
				helper.setText(messageData.getHtml(), true);
			} else {
				helper.setText(messageData.getText());
				helper.setText(messageData.getHtml(), true);
			}
		} catch (final MessagingException e) {
			log.error("构造Email出现异常: ", e);
			throw new MessagingException("构造Email出现异常");
		}
	}

		@Override
	public Future<ThreadEmailEntry> send(MessageData messageData) {
			final Future<ThreadEmailEntry> future = getExecutor().submit(new AsyncLoadCallable<ThreadEmailEntry>() {
				@Override
				public AsyncLoadConfig getConfig() {
					return new AsyncLoadConfig();
				}

				@Override
				public ThreadEmailEntry call() {
					final ThreadEmailEntry entry = new ThreadEmailEntry();

					try {
						final String msgId = sendSyn(messageData);
						entry.setMsgId(msgId);
						entry.release();
					} catch (final Exception e) {
						entry.setThrowable(e);
						entry.setErrorMessage(e.getMessage());
					}

					return entry;
				}
			});

			return future;
	}

	@Override
	public Future<ThreadEmailEntry> send(MessageData emailMessage, IEmailSendCallback callble) {
		return null;
	}

	@Override
	public String sendSyn(MessageData messageData) throws SendMailException {
		// 检查输入参数
		checkArgumentValid(messageData);

		// 随机选一个有效配置
		EmailInnerConfigureData data = new RandomLoadBalance().select(MailConfigureFactory.getAll());
		// 如果未配置, 则直接抛出异常
		if(data == null){
			throw new SendMailException("邮箱基本配置异常, 请确认 MailConfigureFactory 配置信息:"+ data);
		}

		if(data.isDebug()){
			log.debug("使用{}发送邮件中~", data.getAlias());
		}

		// 打印邮件发送耗时日志
		if (data.isPrintDurationTimer()) {
			final DurationTimer emailTimer = new DurationTimer();

			String msgId =  sendN(data, messageData);

			log.info("acturly 邮件发送耗时：{}/s", emailTimer.durationSecond());

			return msgId;
		}

		return sendN(data, messageData);
	}

	private String sendN(EmailInnerConfigureData data, MessageData messageData)
			throws SendMailException {
		JavaMailSenderImpl mailSender = JavaxMailSenderFactory.get(data.getUserName());

		try{
			final MimeMessage message = mailSender.createMimeMessage();
			// use the true flag to indicate you need a multipart message
			final MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

			if (messageData.getAttachements() != null && messageData.getAttachements().size() > 0) {
				// 有附件
				setAttachment(helper, messageData);
				setEmail(helper, messageData, data);
			} else {
				// 没有附件
				setEmail(helper, messageData, data);
			}

			mailSender.send(message);

			return UuidUtil.getUUIDForNumber30();
		} catch(MessagingException ex){
			throw new SendMailException(ex);
		}
	}
}
