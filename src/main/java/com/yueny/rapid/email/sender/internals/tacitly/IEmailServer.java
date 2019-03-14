/**
 *
 */
package com.yueny.rapid.email.sender.internals.tacitly;

import com.yueny.rapid.email.sender.call.IEmailSendCallback;
import com.yueny.rapid.email.sender.entity.MessageData;
import com.yueny.rapid.email.sender.entity.ThreadEmailEntry;

import java.util.concurrent.Future;

/**
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2018年4月10日 下午6:56:19
 *
 */
public interface IEmailServer {
	/**
	 * 异步发送邮件
	 *
	 * @param emailMessage
	 *            邮件对象， 也可以为Template对象
	 * @return 发送结果
	 */
	Future<ThreadEmailEntry> send(final MessageData emailMessage);

	/**
	 * 异步 send 发送邮件
	 *
	 * @param emailMessage
	 *            邮件对象， 也可以为Template对象
	 * @param callble
	 *            邮件操作之后的回调操作
	 * @return 发送结果
	 */
	Future<ThreadEmailEntry> send(final MessageData emailMessage, final IEmailSendCallback callble);

	/**
	 * 同步发送邮件
	 *
	 * @param emailMessage
	 *            Email对象
	 *
	 * @return messageID
	 *
	 * @throws Exception
	 *             Email异常
	 */
	String sendSyn(final MessageData emailMessage) throws Exception;

}
