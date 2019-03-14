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

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 * MailJavaxSessionFactory
 */
public class MailJavaxSessionFactory {
    private static MailJavaxSessionFactory _instants = new MailJavaxSessionFactory();

    private static Properties defaultConfig(Boolean debug) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", null != debug ? debug.toString() : "false");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.port", "465");
        return props;
    }

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

    private void _create(EmailConfigureData config) {
        if(config == null || !sessionConcurrentMap.containsKey(config.getUserName())) {
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
