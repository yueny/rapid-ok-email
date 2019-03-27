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
import com.yueny.rapid.email.config.EmailConfigureData;
import com.yueny.rapid.email.config.EmailConstant;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 * MailJavaxSessionFactory
 */
@Slf4j
public class MailJavaxSessionFactory {
    private static MailJavaxSessionFactory _instants = new MailJavaxSessionFactory();

        /**
     * 添加
     * @param config
     * @return
     */
    public static void create(EmailConfigureData config) {
        _instants._create(config);
    }

    public static void refresh(String userName, String password, String hostName) {
        _instants._refresh(userName, password, hostName);
    }

    public static Session get(String userName) {
       return  _instants._get(userName);
    }

//    private Table<String, Session, Session> tables = HashBasedTable.create();
    private ConcurrentMap<String, Session> sessionConcurrentMap =
            new MapMaker().weakValues().makeMap();

    public MailJavaxSessionFactory() {
        //.
    }

    private static Properties defaultConfig(Boolean debug) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", EmailConstant.DEFAULT_SMTP_AUTH);
        props.put("mail.smtp.ssl.enable", EmailConstant.DEFAULT_USE_SSL);
        props.put("mail.transport.protocol", EmailConstant.DEFAULT_TRANSPORT_PROTOCOL);
        props.put("mail.debug", null != debug ? debug.toString() : EmailConstant.DEFAULT_USE_DEBUG);
        props.put("mail.smtp.timeout", EmailConstant.DEFAULT_SMTP_TIMEOUT);
        props.put("mail.smtp.port", EmailConstant.SMTP_PORT_465);
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

    private void _create(EmailConfigureData config) {
        if(!sessionConcurrentMap.containsKey(config.getUserName())) {
            Properties props = defaultConfig(false);
            props.put("mail.smtp.host", config.getHostName());

            props.setProperty("username", config.getUserName());
            props.setProperty("password", config.getPassword());

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUserName(), config.getPassword());
                }
            });

            sessionConcurrentMap.put(config.getUserName(), session);

            if(config.isDebug()){
                log.debug("创建邮箱 {} Session服务.", config.getUserName());
            }
        }
    }

    private void _refresh(String userName, String password, String hostName) {
        Properties props = defaultConfig(false);
        props.put("mail.smtp.host", hostName);

        props.setProperty("username", userName);
        props.setProperty("password", password);

        if(!sessionConcurrentMap.containsKey(userName)) {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            });

            sessionConcurrentMap.put(userName, session);
        }else{
            Session session = sessionConcurrentMap.get(userName);
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            });
        }
    }

    private Session _get(String userName) {
        if(sessionConcurrentMap.containsKey(userName)) {
            return sessionConcurrentMap.get(userName);
        }

        return null;
    }

}
