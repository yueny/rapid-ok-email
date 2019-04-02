/**
 *
 */
package com.yueny.rapid.email.sender.listener;

import com.yueny.rapid.email.sender.entity.MessageData;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月15日 下午9:40:27
 *
 */
@Slf4j
public class ConsoleEmailSendListener implements IEmailSendListener {

	@Override
	public void after(MessageData messageData) {
		log.info("控制台输出log. 邮件主题 「{}」发送完成。", messageData.getSubject());
	}

	@Override
	public void afterThrowable(final MessageData messageData,  Throwable throwable) {
		log.error("控制台输出log. . 邮件主题 「" + messageData.getSubject() + "」发送操作异常, afterThrowable: ", throwable);
	}

	@Override
	public void before(final MessageData messageData) {
		// nothing
		// log.debug("[默认监听] before");
	}

}
