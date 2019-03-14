//package com.yueny.rapid.message.email.logback.appender;
//
//import java.util.Properties;
//
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import javax.naming.Context;
//import javax.naming.InitialContext;
//
//import ch.qos.logback.classic.boolex.OnErrorEvaluator;
//import ch.qos.logback.classic.net.SMTPAppender;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import ch.qos.logback.core.spi.CyclicBufferTracker;
//import ch.qos.logback.core.util.OptionHelper;
//
///**
// * logback源代码中将mail.smtp.auth属性写死为true，不能满足mail.smtp.auth=false的情况发送邮件，
// * 因此在此appender中将相应方法覆盖， 并用此appender作为批次发送邮件的方法.
// *
// * 在logback.xml中的配置示例eg.
// *
// * <pre>
// * &lt;appender name="EMAIL" class="com.yueny.rapid.message.email.logback.appender.LogbackBatchMailAppender"&gt;
// * 		&lt;filter class="ch.qos.logback.classic.filter.ThresholdFilter"&gt;
// * 			&lt;level&gt;ERROR&lt;/level&gt;
// * 		&lt;/filter&gt;
// * 		&lt;smtpHost&gt;smtp.163.com&lt;/smtpHost&gt;
// *
// * 		&lt;to&gt;aa@163.com&lt;/to&gt;
// * 		&lt;to&gt;bb@126.com&lt;/to&gt;
// *
// * 		&lt;from&gt;demo_notify@yueny.com&lt;/from&gt;
// *
// * 		&lt;username&gt;demo_notify@yueny.com&lt;/username&gt;
// * 		&lt;password&gt;&lt;/password&gt;
// *
// * 		&lt;subject&gt;[${user.name}]Auto batch error message from Log, don't reply&lt;/subject&gt;
// * 		&lt;layout class="ch.qos.logback.classic.PatternLayout"&gt;
// * 			&lt;pattern&gt;[%-5level] %date %logger{80} - %msg%n&lt;/pattern&gt;
// * 		&lt;/layout&gt;
// * 	&lt;/appender&gt;
// *
// * </pre>
// *
// * @author 袁洋 <a href="mailto:yueny09@126.com">
// * @date 2015年3月18日 下午5:20:10
// * @category tag
// */
//public class LogbackBatchMailAppender extends SMTPAppender {
//	private static final boolean MAIL_SMTP_AUTH = true;
//	private static final boolean sessionViaJNDI = false;
//
//	private Session buildSessionFromProperties() {
//		final Properties props = new Properties(OptionHelper.getSystemProperties());
//		if (getSMTPHost() != null) {
//			props.put("mail.smtp.host", getSMTPHost());
//		}
//		props.put("mail.smtp.port", Integer.toString(getSmtpPort()));
//
//		if (getLocalhost() != null) {
//			props.put("mail.smtp.localhost", getLocalhost());
//		}
//
//		LogbackLoginAuthenticator loginAuthenticator = null;
//		if (getUsername() != null) {
//			loginAuthenticator = new LogbackLoginAuthenticator(getUsername(), getPassword());
//			props.put("mail.smtp.auth", MAIL_SMTP_AUTH);
//		}
//
//		if (isSTARTTLS() && isSSL()) {
//			addError("Both SSL and StartTLS cannot be enabled simultaneously");
//		} else {
//			if (isSTARTTLS()) {
//				// see also http://jira.qos.ch/browse/LBCORE-225
//				props.put("mail.smtp.starttls.enable", "true");
//			}
//			if (isSSL()) {
//				props.put("mail.smtp.socketFactory.port", Integer.toString(getSmtpPort()));
//				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//				props.put("mail.smtp.socketFactory.fallback", "true");
//			}
//		}
//		return Session.getInstance(props, loginAuthenticator);
//	}
//
//	/**
//	 * @param addressStr
//	 *            地址
//	 * @return 因特网地址
//	 */
//	private InternetAddress getInternetAddress(final String addressStr) {
//		try {
//			return new InternetAddress(addressStr);
//		} catch (final AddressException e) {
//			addError("Could not parse address [" + addressStr + "].", e);
//			return null;
//		}
//	}
//
//	private Session lookupSessionInJNDI() {
//		addInfo("Looking up javax.mail.Session at JNDI location [" + getJndiLocation() + "]");
//		try {
//			final Context initialContext = new InitialContext();
//			return (Session) initialContext.lookup(getJndiLocation());
//		} catch (final Exception e) {
//			addError("Failed to obtain javax.mail.Session from JNDI location [" + getJndiLocation() + "]");
//			return null;
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see ch.qos.logback.classic.net.SMTPAppender#start()
//	 */
//	@Override
//	public void start() {
//		if (eventEvaluator == null) {
//			final OnErrorEvaluator onError = new OnErrorEvaluator();
//			onError.setContext(getContext());
//			onError.setName("onError");
//			onError.start();
//			this.eventEvaluator = onError;
//		}
//		if (cbTracker == null) {
//			cbTracker = new CyclicBufferTracker<ILoggingEvent>();
//		}
//
//		Session session = null;
//		if (sessionViaJNDI) {
//			session = lookupSessionInJNDI();
//		} else {
//			session = buildSessionFromProperties();
//		}
//
//		if (session == null) {
//			addError("Failed to obtain javax.mail.Session. Cannot start.");
//			return;
//		}
//		final MimeMessage mimeMsg = new MimeMessage(session);
//
//		try {
//			if (getFrom() != null) {
//				mimeMsg.setFrom(getInternetAddress(getFrom()));
//			} else {
//				mimeMsg.setFrom();
//			}
//
//			subjectLayout = makeSubjectLayout(getSubject());
//			started = true;
//		} catch (final MessagingException e) {
//			addError("Could not activate SMTPAppender options.", e);
//		}
//	}
//
//}
