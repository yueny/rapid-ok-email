/**
 *
 */
package com.yueny.rapid.email.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * email配置类
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月15日 下午8:31:22
 *
 */
@Getter
@ToString
public class EmailConfigureData {
	/**
	 * 设置缺省的FROM地址别名
	 */
	@Setter
	private String alias;

	/**
	 * 设置密码是否加密，默认不加密
	 */
	@Setter
	private boolean decrypt = false;

	/**
	 * 发件地址
	 */
	@Setter
	private String from;
	/**
	 * 设置SMTP服务器名称
	 */
	@Setter
	private String hostName;
	/**
	 * 是否同步发送
	 */
	@Setter
	private boolean isAsynSend = true;
	/**
	 * 认证用户密码, 此处已解密, 为明文
	 */
	@Setter
	private String password;
	/**
	 * 控制台打印邮件发送耗时。 日志级别info。默认不打印
	 */
	@Setter
	private boolean printDurationTimer = false;
	/**
	 * 设置SMTP端口
	 */
	@Setter
	private String smtpPort;
	/**
	 * 设置是否使用SSL
	 */
	@Setter
	private boolean ssl = false;
	/**
	 * 设置SSL端口
	 */
	@Setter
	private String sslPort;
	/**
	 * 认证用户名
	 */
	@Setter
	private String userName;
}
