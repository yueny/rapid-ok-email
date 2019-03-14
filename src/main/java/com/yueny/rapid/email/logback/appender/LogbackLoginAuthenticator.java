//package com.yueny.rapid.message.email.logback.appender;
//
//import javax.mail.Authenticator;
//import javax.mail.PasswordAuthentication;
//
///**
// * logback中的LoginAuthenticator无法继承使用，这里需直接继承mail中的Authenticator，
// * 支持批次发送邮件的appender
// *
// * @author 袁洋 <a href="mailto:yueny09@126.com">
// * @date 2015年3月18日 下午5:38:29
// * @category tag
// */
//class LogbackLoginAuthenticator extends Authenticator {
//	private String password;
//	private String username;
//
//	/**
//	 * @param username
//	 *            用户名
//	 * @param password
//	 *            密码
//	 */
//	LogbackLoginAuthenticator(final String username, final String password) {
//		this.username = username;
//		this.password = password;
//	}
//
//	/**
//	 * @return 密码
//	 */
//	public String getPassword() {
//		return password;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see javax.mail.Authenticator#getPasswordAuthentication()
//	 */
//	@Override
//	public PasswordAuthentication getPasswordAuthentication() {
//		return new PasswordAuthentication(username, password);
//	}
//
//	/**
//	 * @return 用户名
//	 */
//	public String getUsername() {
//		return username;
//	}
//
//	/**
//	 * @param password
//	 *            密码
//	 */
//	public void setPassword(final String password) {
//		this.password = password;
//	}
//
//	/**
//	 * @param username
//	 *            用户名
//	 */
//	public void setUsername(final String username) {
//		this.username = username;
//	}
//
//}
