/**
 *
 */
package com.yueny.rapid.email.sender;

import com.yueny.rapid.email.sender.entity.MessageData;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象邮件发送服务
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2018年4月11日 下午11:13:42
 *
 */
@Slf4j
public abstract class BaseEmailServer implements IEmailServer {
	/**
	 * 初始化
	 */
	protected void initital() {
		log.debug("查询邮箱配置:{}.", getEmailConfigure());
	}

	/**
	 * 检查输入是否合法.
	 *
	 * @param emailMessage
	 */
	protected void checkArgumentValid(final MessageData emailMessage) {
		if (getEmailConfigure() == null) {
			throw new IllegalArgumentException("邮件配置文件未初始化");
		}

		if (emailMessage == null) {
			throw new IllegalArgumentException("输入参数为空");
		}

		// 到少要有一个收件人
		if ((emailMessage.getTo() == null || emailMessage.getTo().size() == 0)
				&& (emailMessage.getCc() == null || emailMessage.getCc().size() == 0)
				&& (emailMessage.getBcc() == null || emailMessage.getBcc().size() == 0)) {
			throw new IllegalArgumentException("收件人不能为空");
		}
	}


}
