/**
 *
 */
package com.yueny.rapid.email.sender.internals.tacitly;

import com.yueny.rapid.email.sender.entity.MessageData;
import com.yueny.rapid.email.sender.listener.ConsoleEmailSendListener;
import com.yueny.rapid.email.sender.listener.IEmailSendListener;
import com.yueny.rapid.lang.thread.executor.AsyncLoadExecutors;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象邮件发送服务
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2018年4月11日 下午11:13:42
 *
 */
abstract class BaseEmailServer implements IEmailServer {
    @Getter
    private static final AsyncLoadExecutors executor = new AsyncLoadExecutors(2);
    static{
        // final Properties properties = SysConfig.getConfiguration();
        executor.initital();
    }

    public BaseEmailServer(){
        this.initital();
    }

    protected void initital() {
        // 增加控制台输出
        addListener(new ConsoleEmailSendListener());
    }

    /**
     * 对邮件发送的全局列表
     * @key 实例化的类名
     * @value  监听器实例对象
     */
    private final Map<String, IEmailSendListener> emailSendListeners = new HashMap<>();

    protected void addListener(final IEmailSendListener emailSendListener) {
        String listenerName = emailSendListener.getClass().getCanonicalName();
        if(emailSendListeners.containsKey(listenerName)){
            return;
        }

        this.emailSendListeners.putIfAbsent(listenerName, emailSendListener);
    }

    protected void doAfter(final MessageData messageData) {
        for (final IEmailSendListener emailSendListener : this.emailSendListeners.values()) {
            emailSendListener.after(messageData);
        }
    }

    protected void doAfterThrowable(MessageData messageData, Throwable throwable) {
        for (final IEmailSendListener emailSendListener : this.emailSendListeners.values()) {
            emailSendListener.afterThrowable(messageData, throwable);
        }
    }

    protected void doBefore(final MessageData messageData) {
        for (final IEmailSendListener emailSendListener : this.emailSendListeners.values()) {
            emailSendListener.before(messageData);
        }
    }

}
