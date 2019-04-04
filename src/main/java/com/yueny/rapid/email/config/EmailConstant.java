/**
 *
 */
package com.yueny.rapid.email.config;

/**
 * email 常量
 *
 * @author yueny09 <yueny09@163.com>
 *
 */
public class EmailConstant {
	/**
	 * 默认是否 smtp 鉴权, 默认使用
	 */
	public static final boolean DEFAULT_SMTP_AUTH = true;

	/**
	 * 默认协议, 默认 smtp. 协议（smtp、pop3、imap、nntp）
	 */
	public static final String DEFAULT_TRANSPORT_PROTOCOL = "smtps";
	/**
	 * 默认超时时间
	 */
	public static final int DEFAULT_SMTP_TIMEOUT = 10000;

}
