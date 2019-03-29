/**
 *
 */
package com.yueny.rapid.email.sender.listener;

import com.yueny.rapid.email.sender.entity.MessageData;

/**
 * 邮件发送器监听程序，一个observer模式的实现，当有邮件要发送时触发，可以为邮件服务器配置一个或多个监听程序
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月15日 下午8:26:52
 *
 */
public interface IEmailSendListener {
	/**
	 * 邮件发送结束后做的操作
	 *
	 * @param messageData
	 */
	void after(MessageData messageData);

	/**
	 * 邮件发送出现异常时做的处理
	 *
	 * @param messageData
	 */
	void afterThrowable(MessageData messageData, Throwable throwable);

	/**
	 * 邮件发送前做的操作
	 *
	 * @param messageData
	 */
	void before(MessageData messageData);

}
