/**
 *
 */
package com.yueny.rapid.email.sender.call;

import com.yueny.rapid.email.sender.entity.MessageData;

/**
 * 默认的 邮件操作之后的回调
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月16日 下午4:49:05
 *
 */
public abstract class AbstractEmailSendCallback implements IEmailSendCallback {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yueny.rapid.message.email.sender.callback.IEmailSendCallback#after(com
	 * .yueny.rapid.message.email.sender.core.EmailContext, java.lang.String)
	 */
	@Override
	public void after(MessageData message, String msgId) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yueny.rapid.message.email.sender.callback.IEmailSendCallback#
	 * afterThrowable(com.yueny.rapid.message.email.sender.core.EmailContext)
	 */
	@Override
	public void afterThrowable(Throwable throwable) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yueny.rapid.message.email.sender.callback.IEmailSendCallback#before(
	 * com.yueny.rapid.message.email.sender.core.EmailContext)
	 */
	@Override
	public void before(final MessageData message) {
		// nothing
	}

}
