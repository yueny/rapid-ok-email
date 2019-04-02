/**
 *
 */
package com.yueny.rapid.email.util;

import lombok.Getter;

/**
 * 邮件smtp类型枚举类.
 * "mail.smtp.host"
 *
 * <ul>
 * <li>126: </li>
 * <li>163: </li>
 * </ul>
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:30:45
 *
 */
public enum MailSmtpType {
	/**
	 * 网易126
	 */
	_126("smtp.126.com"),
	/**
	 * 网易163
	 */
	_163("smtp.163.com"),
	/**
	 * smtp qq
	 */
	QQ("smtp.qq.com"),
	/**
	 * smtp entnterprise qq
	 */
	QQ_ENT("smtp.exmail.qq.com");


	@Getter
	private String hostName;

	MailSmtpType(String hostName){
		this.hostName = hostName;
	}
}
