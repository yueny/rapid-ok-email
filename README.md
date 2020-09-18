# rapid-my-email  邮件工具服务类

## 使用文档
### **maven坐标**

```
<dependency>
   <groupId>com.yueny.rapid.message</groupId>
   	<artifactId>rapid-ok-email</artifactId>
   	<version>xxxx</version>
</dependency>
```
最新版本 ：1.1.0-SNAPSHOT

### 配置
#### 方式一、javaconfig
```
// mailType, username, password, isEncrypt, pwPBESalt, debug
OkEmail.config(MailSmtpType._126, "aaaa@126.com",
        "a668cbc1e9f66ff061f6fde864dfa39c6c368368957381ca0fe",
        true, "e3194b6bae5a0");
```

#### 方式二、在 资源路径resources下增加配置文件： email/email-config.xml。格式如下
```
<?xml version="1.0" encoding="gb2312"?>
<email>
	<!-- 设置缺省的FROM地址别名-->
	<alias>aa@</alias>

	<!-- 设置SMTP服务器名称 -->
	<smtp-type>_ALIYUN</smtp-type>

	<!-- 认证用户. pw-ps 为 pwPBESalt -->
	<auth decrypt="true" pw-ps="3e0deb39e46b3c75">
		<!-- 认证用户名 -->
		<user-name>aa@163.com</user-name>
		<!-- 认证用户密码  -->
		<password>ccd16297df5b39c5edw83a479b0b24fa0404ebdb0baf6466e658859d25fed1c1787b58693c3a64917cb85a43e2f2fce0525a6738956de6</password>
	</auth>

	<!-- 配置项 -->
	<config print-duration-timer="true">
		<debug>true</debug>
	</config>

</email>
```
* transport-protocol: 默认 smtp。 选配。
* alias: 设置缺省的FROM地址别名。 必配项
* smtp-port: 设置SMTP端口， 默认465。 选配。
* ssl: 设置是否使用SSL，默认 true。 选配。
* ssl-port: 设置SSL端口， 默认465。 选配。
* smtp-type: MailSmtpType。 必配项. 取值范围来自 MailSmtpType(_126,_163,_ALIYUN,_QQ,_QQ_ENT)
* auth: 认证用户信息。 必配项
    + decrypt: 设置密码是否加密，默认不加密
    + user-name: 认证用户名
    + password: 认证用户密码。 如果密码为密文，则需要配置 decrypt 和 pw-ps
    + pw-ps: password 字段额外加密，默认空。
             该配置仅当 decrypt=true 有效。此处会调用rapid-lang-crypt的PBECoder动作. 值为 PBE加密的盐

* config: 邮件发送的配置信息。 选配。
    + print-duration-timer: 控制台打印邮件发送耗时。 日志级别info。默认不打印
    + debug： 是否开启调试模式。 默认否

#### 密码加密说明
* 不加密时，密码为明文。
* 加密时，  isEncrypt为true， 无pw-ps value时， 加解密模式为  TripleDesEncryptUtil.tripleDesDecrypt
* 加密时，  isEncrypt为true， 有pw-ps value时， 盐值为 pw-ps value，加解密模式为  PBECoder.decryptHex 


#### 使用API
```
Future<ThreadEmailEntry> future = OkEmail
            .subject(subject.toString()).from(mappedType.getMsg())
            .to(target).html(EngineType.FREEMARKER, mappedType.getTemplateId(), context)
            .sendFuture();
```
详细的见  OkEmailTest


## 历史版本
#### 1.0.0-SNAPSHOT
```
<dependency>
		<groupId>com.yueny.rapid.message</groupId>
		<artifactId>rapid-message-email</artifactId>
		<version>1.0.0-SNAPSHOT</version>
</dependency>
```
已作废，已迁移至https://github.com/yueny/rapid-ok-email。

#### 1.0.1-SNAPSHOT
* 增加密码密文配置功能。
* 增加盐值配置

##### 1.1.0-SNAPSHOT/1.1.0
* 补充邮件发送的非必填参数的配置化