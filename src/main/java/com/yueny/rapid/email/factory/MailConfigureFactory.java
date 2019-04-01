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

import com.yueny.rapid.email.config.EmailInnerConfigureData;
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
    public static boolean register(EmailInnerConfigureData config) {
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

    public static List<EmailInnerConfigureData> getAll() {
        return _instants._getAll();
    }

//    // 使用WeakValueMap，当Value被垃圾回收时会将此value在map中的entry清除，防止内存溢出
//    private ConcurrentMap<String, EmailInnerConfigureData> emailConfigureDataConcurrentMap =
//            new MapMaker().weakValues().makeMap();
    private ConcurrentMap<String, EmailInnerConfigureData> emailConfigureDataConcurrentMap =
        new ConcurrentHashMap<>();

    public MailConfigureFactory() {
        //.
    }

    private boolean _register(EmailInnerConfigureData config) {
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

    private List<EmailInnerConfigureData> _getAll() {
        //此处每次以相同用户发送email时, 只实例化一次
        List<EmailInnerConfigureData> list = new ArrayList<>(emailConfigureDataConcurrentMap.size());

        Collection<EmailInnerConfigureData> data = emailConfigureDataConcurrentMap.values();
        list.addAll(data);

        return Collections.unmodifiableList(list);
    }

}
