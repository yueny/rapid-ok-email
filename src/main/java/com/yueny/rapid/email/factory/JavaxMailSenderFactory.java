/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yueny.rapid.email.factory;

import com.google.common.collect.MapMaker;
import com.yueny.rapid.email.config.EmailConstant;
import com.yueny.rapid.email.config.EmailInnerConfigureData;
import com.yueny.rapid.email.util.MailSmtpType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JavaxMailSenderFactory
 */
@Slf4j
public class JavaxMailSenderFactory {
    private static JavaxMailSenderFactory _instants = new JavaxMailSenderFactory();

    /**
     * 添加
     * @param config
     * @return
     */
    public static void create(EmailInnerConfigureData config) {
        _instants._create(config);
    }

    public static JavaMailSenderImpl get(String userName) {
       return  _instants._get(userName);
    }

    private ConcurrentMap<String, JavaMailSenderImpl> senderConcurrentMap =
            new ConcurrentHashMap<>();
//            new MapMaker().weakValues().makeMap();

    public JavaxMailSenderFactory() {
        //.
    }

    private static JavaMailSenderImpl getJavaMailSender(EmailInnerConfigureData config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 要连接的SMTP服务器
        mailSender.setHost(config.getSmtpType().getSmtpName());
        // 设置SMTP端口
        mailSender.setPort(Integer.valueOf(config.getSmtpPort()));
        mailSender.setProtocol(config.getTransportProtocol());

        // 设置认证信息
        mailSender.setUsername(config.getUserName());
        mailSender.setPassword(config.getPassword());

        // 设置SMTP端口
        mailSender.getJavaMailProperties().setProperty("mail.smtp.port", config.getSmtpPort());
        // 设置SSL端口
        mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.port", config.getSslPort());
        mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.fallback", "false");
        //使用JSSE的SSL socketfactory来取代默认的socketfactory. 避免出现认证错误
        mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");

        // 开启认证 /设置是否使用SSL
        mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", String.valueOf(config.isSsl()));

        // 如果是网易邮箱， mail.smtp.starttls.enable 设置为 false
        if(Arrays.asList(MailSmtpType._126.getSmtpName(), MailSmtpType._163.getSmtpName()).contains(config.getSmtpType().getSmtpName())){
            mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "false");
        }else{
            mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", String.valueOf(config.isSsl()));
        }

        //设置调试模式可以在控制台查看发送过程
        mailSender.getJavaMailProperties().setProperty("mail.debug", String.valueOf(config.isDebug()));
        // Socket I/O超时值，单位毫秒，缺省值不超时
        mailSender.getJavaMailProperties().setProperty("mail.smtp.timeout", String.valueOf(EmailConstant.DEFAULT_SMTP_TIMEOUT));

        /* 发送信息设置，取自入参 */
        mailSender.setDefaultEncoding("UTF-8");

        return mailSender;
    }

    private void _create(EmailInnerConfigureData config) {
        if(!senderConcurrentMap.containsKey(config.getUserName())) {
            JavaMailSenderImpl mailSender = getJavaMailSender(config);

            senderConcurrentMap.put(config.getUserName(), mailSender);

            if(config.isDebug()){
                log.debug("创建邮箱 {} JavaMailSenderImpl 服务.", config.getUserName());
            }
        }
    }

    private JavaMailSenderImpl _get(String userName) {
        if(senderConcurrentMap.containsKey(userName)) {
            return senderConcurrentMap.get(userName);
        }

        return null;
    }

}
