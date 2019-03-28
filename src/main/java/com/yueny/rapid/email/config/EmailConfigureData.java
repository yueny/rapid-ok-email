/**
 *
 */
package com.yueny.rapid.email.config;

import com.yueny.rapid.lang.mask.annotation.Mask;
import com.yueny.rapid.lang.mask.pojo.instance.AbstractMaskBo;
import lombok.Builder;
import lombok.Getter;

/**
 * email配置类
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月15日 下午8:31:22
 *
 */
@Getter
@Builder
public class EmailConfigureData extends AbstractMaskBo {
	/**
	 * 设置缺省的FROM地址别名
	 */
	private String alias;

	/**
	 * 设置密码是否加密，默认不加密
	 */
	private boolean decrypt = false;

	/**
	 * 发件地址
	 */
	private String from;
	/**
	 * 设置SMTP服务器名称
	 */
	private String hostName;
	/**
	 * 是否同步发送
	 */
	private boolean isAsynSend = true;
	/**
	 * 认证用户密码, 此处已解密, 为明文
	 */
	@Mask(left=2, right = 1)
	private String password;

	/**
	 * 设置SMTP端口
	 */
	private String smtpPort;
	/**
	 * 设置是否使用SSL
	 */
	private boolean ssl = false;
	/**
	 * 设置SSL端口
	 */
	private String sslPort;
	/**
	 * 认证用户名
	 */
	private String userName;

	/**
	 * 控制台打印邮件发送耗时。 日志级别info。默认不打印
	 */
	private boolean printDurationTimer = false;
	/**
	 * 是否开启调试模式
	 */
	private boolean debug = false;
}
