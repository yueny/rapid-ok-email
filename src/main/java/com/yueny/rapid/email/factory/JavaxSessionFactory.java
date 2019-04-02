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
import com.yueny.rapid.email.config.EmailInnerConfigureData;
import com.yueny.rapid.email.config.EmailConstant;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.security.Security;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JavaxSessionFactory
 */
@Slf4j
public class JavaxSessionFactory {
    private static JavaxSessionFactory _instants = new JavaxSessionFactory();

        /**
     * 添加
     * @param config
     * @return
     */
    public static void create(EmailInnerConfigureData config) {
        _instants._create(config);
    }

    public static Session get(String userName) {
       return  _instants._get(userName);
    }

//    private Table<String, Session, Session> tables = HashBasedTable.create();
    private ConcurrentMap<String, Session> sessionConcurrentMap =
        new ConcurrentHashMap<>();
//            new MapMaker().weakValues().makeMap();

    public JavaxSessionFactory() {
        //.
    }

    private static Properties getConfig(EmailInnerConfigureData config) {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        Properties props = new Properties();
        // 要连接的SMTP服务器
        props.setProperty("mail.smtp.host", config.getHostName());
        //使用JSSE的SSL socketfactory来取代默认的socketfactory. 避免出现认证错误
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        // 要连接的SMTP服务器的端口号
        props.setProperty("mail.smtp.port", config.getSmtpPort());
        props.setProperty("mail.smtp.socketFactory.port", config.getSslPort());

        // 需要身份验证. 缺省是false，如果为true，尝试使用AUTH命令认证用户。
//        props.put("mail.smtp.localhost", "127.0.0.1");
        props.put("mail.smtp.auth", String.valueOf(EmailConstant.DEFAULT_SMTP_AUTH));
        props.put("mail.smtp.ssl.enable", String.valueOf(config.isSsl()));
        // 要装入session的协议（smtp、pop3、imap、nntp）
        props.put("mail.transport.protocol", EmailConstant.DEFAULT_TRANSPORT_PROTOCOL);
        //设置调试模式可以在控制台查看发送过程
        props.put("mail.debug", config.isDebug());

        // Socket连接超时值，单位毫秒，缺省值不超时。
        props.put("mail.smtp.connectiontimeout", String.valueOf(EmailConstant.DEFAULT_SMTP_TIMEOUT));
        // Socket I/O超时值，单位毫秒，缺省值不超时
        props.put("mail.smtp.timeout", String.valueOf(EmailConstant.DEFAULT_SMTP_TIMEOUT));

        return props;
    }

    //		/* 认证信息设置，取自配置 */
//		// 设置SMTP服务器名称
//		mailSender.setHost(getEmailConfigure().getHostName());
//		// 设置SMTP端口
//		mailSender.setPort(Integer.valueOf(getEmailConfigure().getSmtpPort()));
//
//		// Default is "smtp".
//		// mailSender.setProtocol(protocol);
//
//		// 设置认证信息
//		mailSender.setUsername(getEmailConfigure().getUserName());
//        mailSender.setPassword(getEmailConfigure().getPassword());
//
//		// Properties properties = new Properties();
//		// //启用调试
//		// properties.setProperty("mail.debug", "true");
//		//// 设置链接超时
//		// properties.setProperty("mail.smtp.timeout", "1000");
//
//		// 设置SMTP端口
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.port", getEmailConfigure().getSmtpPort());
//		// 开启认证 /设置是否使用SSL
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", String.valueOf(getEmailConfigure().isSsl()));
//		// 设置SSL端口
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.port", getEmailConfigure().getSslPort());
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.fallback", "false");
//		// 避免出现认证错误
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.class",
//				"javax.net.ssl.SSLSocketFactory");
//
//		// 如果是网易邮箱， mail.smtp.starttls.enable 设置为 false
//		mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", "true");
//
//		/* 发送信息设置，取自入参 */
//		mailSenderr.setDefaultEncoding("UTF-8");

    private void _create(EmailInnerConfigureData config) {
        if(!sessionConcurrentMap.containsKey(config.getUserName())) {
            Properties props = getConfig(config);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUserName(), config.getPassword());
                }
            });
            // 置true可以在控制台（console)上看到发送邮件的过程
            session.setDebug(config.isDebug());

            sessionConcurrentMap.put(config.getUserName(), session);

            if(config.isDebug()){
                log.debug("创建邮箱 {} Session服务.", config.getUserName());
            }
        }
    }

    private Session _get(String userName) {
        if(sessionConcurrentMap.containsKey(userName)) {
            return sessionConcurrentMap.get(userName);
        }

        return null;
    }

}
