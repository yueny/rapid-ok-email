/**
 *
 */
package com.yueny.rapid.email.sender.internals.tacitly;

import com.yueny.rapid.email.sender.entity.MessageData;
import com.yueny.rapid.email.sender.listener.IEmailSendListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象邮件发送服务
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2018年4月11日 下午11:13:42
 *
 */
abstract class BaseEmailServer implements IEmailServer {
    /**
     * 对邮件发送的全局列表
     */
    private final List<IEmailSendListener> emailSendListeners = new ArrayList<>();

    protected void addListener(final IEmailSendListener emailSendListener) {
        this.emailSendListeners.add(emailSendListener);
    }

    protected void doAfter(final MessageData messageData) {
        for (final IEmailSendListener emailSendListener : this.emailSendListeners) {
            emailSendListener.after(messageData);
        }
    }

    protected void doAfterThrowable(MessageData messageData, Throwable throwable) {
        for (final IEmailSendListener emailSendListener : this.emailSendListeners) {
            emailSendListener.afterThrowable(messageData, throwable);
        }
    }

    protected void doBefore(final MessageData messageData) {
        for (final IEmailSendListener emailSendListener : this.emailSendListeners) {
            emailSendListener.before(messageData);
        }
    }

}
