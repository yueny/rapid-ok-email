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
	 * 默认smtp port
	 */
	public static final int DEFAULT_SMTP_PORT = 25;
	/**
	 * smtp port
	 */
	public static final int SMTP_PORT_465 = 465;
	/**
	 * 默认是否使用 ssl, 默认使用
	 */
	public static final boolean DEFAULT_USE_SSL = true;
	/**
	 * 默认是否 smtp 鉴权, 默认使用
	 */
	public static final boolean DEFAULT_SMTP_AUTH = true;

	/**
	 * 默认协议, 默认 smtp
	 */
	public static final String DEFAULT_TRANSPORT_PROTOCOL = "smtp";
	/**
	 * 默认开启bug模式, 默认不开启
	 */
	public static final boolean DEFAULT_USE_DEBUG = false;
	/**
	 * 默认超时时间
	 */
	public static final int DEFAULT_SMTP_TIMEOUT = 10000;

}
