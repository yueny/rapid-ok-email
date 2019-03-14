/**
 *
 */
package com.yueny.rapid.email.sender.call;

import com.yueny.rapid.email.sender.entity.MessageData;

/**
 * 邮件操作之后的回调
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月15日 下午9:55:23
 *
 */
public interface IEmailSendCallback {
	/**
	 * 邮件发送结束后做的操作
	 *
	 * @param message
	 */
	void after(MessageData message, String msgId);

	/**
	 * 邮件发送出现异常时做的处理
	 *
	 * @param throwable
	 */
	void afterThrowable(Throwable throwable);

	/**
	 * 邮件发送前做的操作
	 *
	 * @param message
	 */
	void before(MessageData message);
}
