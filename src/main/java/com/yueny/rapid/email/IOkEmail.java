package com.yueny.rapid.email;

import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.sender.entity.ThreadEmailEntry;

import java.util.concurrent.Future;

/**
 *  email工具类
 */
interface IOkEmail {
	
	/**
	 * 邮件发送
	 */
	boolean send() throws SendMailException;

	/**
	 * 邮件发送
	 */
	Future<ThreadEmailEntry> sendFuture();
	
	/**
	 *  destory
	 */
	void destory();
	
}
