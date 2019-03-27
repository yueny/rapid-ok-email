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

import com.yueny.rapid.email.config.EmailConfigureData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * MailConfigureFactory
 */
@Slf4j
public class MailConfigureFactory {
    private static MailConfigureFactory _instants = new MailConfigureFactory();

    /**
     * 配置注册
     * @param config
     * @return
     */
    public static boolean register(EmailConfigureData config) {
        return _instants._register(config);
    }

    /**
     * 判断配置是否存在
     *
     * @param userName
     * @return true为存在
     */
    public static boolean exist(String userName) {
        return _instants._exist(userName);
    }

    /**
     * 配置刷新
     *
     * @param userName 用户名
     * @param password 密码
     * @return 刷新结果, true为刷新成功
     */
    public static boolean refresh(String userName, String password, String hostName) {
        return _instants._refresh(userName, password, hostName);
    }

    public static List<EmailConfigureData> getAll() {
        return _instants._getAll();
    }

//    // 使用WeakValueMap，当Value被垃圾回收时会将此value在map中的entry清除，防止内存溢出
//    private ConcurrentMap<String, EmailConfigureData> emailConfigureDataConcurrentMap =
//            new MapMaker().weakValues().makeMap();
    private ConcurrentMap<String, EmailConfigureData> emailConfigureDataConcurrentMap =
        new ConcurrentHashMap<>();

    public MailConfigureFactory() {
        //.
    }

    private boolean _register(EmailConfigureData config) {
        if(!emailConfigureDataConcurrentMap.containsKey(config.getUserName())){
            emailConfigureDataConcurrentMap.putIfAbsent(config.getUserName(), config);

            //创建session
            MailJavaxSessionFactory.create(config);

            if(config.isDebug()){
                log.debug("注册新的邮箱服务:{}", config);
            }

            return true;
        }

        return false;
    }

    private boolean _exist(String userName) {
        return emailConfigureDataConcurrentMap.containsKey(userName);
    }

    private boolean _refresh(String userName, String password, String hostName) {
        if(_exist(userName)){
            EmailConfigureData oldConfig = emailConfigureDataConcurrentMap.get(userName);

            EmailConfigureData.EmailConfigureDataBuilder builder = EmailConfigureData.builder()
                    .alias(oldConfig.getAlias())
                    .from(oldConfig.getFrom())
                    .userName(oldConfig.getUserName())
                    .decrypt(oldConfig.isDecrypt())
                    .smtpPort(oldConfig.getSmtpPort())
                    .ssl(oldConfig.isSsl())
                    .sslPort(oldConfig.getSslPort())
                    .smtpPort(oldConfig.getSslPort())
                    .printDurationTimer(oldConfig.isPrintDurationTimer())
                    .debug(oldConfig.isDebug())
                    .hostName(hostName)
                    .password(password);

            emailConfigureDataConcurrentMap.put(userName, builder.build());
            oldConfig = null;

            MailJavaxSessionFactory.refresh(userName, password, hostName);
            return true;
        }

        return false;
    }

    private List<EmailConfigureData> _getAll() {
        //此处每次以相同用户发送email时, 只实例化一次
        List<EmailConfigureData> list = new ArrayList<>(emailConfigureDataConcurrentMap.size());

        Collection<EmailConfigureData> data = emailConfigureDataConcurrentMap.values();
        list.addAll(data);

        return Collections.unmodifiableList(list);
    }

}