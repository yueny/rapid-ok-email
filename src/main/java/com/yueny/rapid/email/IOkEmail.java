package com.yueny.rapid.email;

import com.yueny.rapid.email.exception.SendMailException;

/**
 *  email工具类
 */
interface IOkEmail {
	
	/**
	 * 邮件发送
	 */
	boolean send() throws SendMailException;
	
	/**
	 *  destory
	 */
	void destory();
	
}
