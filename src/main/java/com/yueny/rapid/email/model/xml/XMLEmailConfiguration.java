/**
 *
 */
package com.yueny.rapid.email.model.xml;

import com.yueny.rapid.email.config.EmailConstant;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * <pre>
 *    <email>
 *      <!-- 设置缺省的FROM地址-->
 * 		<from>deep_blue_yang@126.com</from>
 *      <!-- 设置缺省的FROM地址别名-->
 * 		<alias>deep_blue_yang@126</alias>
 *      <!-- 设置SMTP服务器名称 -->
 * 		<host-name>smtp.126.com</host-name>
 * 		<!-- 设置非SSL协议SMTP端口 -->
 * 		<smtp-port>25</smtp-port>
 * 		<!-- 设置是否使用SSL -->
 * 		<ssl>true</ssl>
 * 		<!-- 设置SSL协议SMTP端口 -->
 * 		<ssl-port>465</ssl-port>
 *
 *      <auth ...>...</auth>
 *    	<!-- 配置项 -->
 * 		<config ...>...</config>
 * 	  </email>
 * </pre>
 *
 * 默认配置项:
 * "mail.smtp.auth", "true"
 * "mail.transport.protocol", "smtp"
 * "mail.debug", null != debug ? debug.toString() : "false"
 * "mail.smtp.timeout", "10000"
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:41:54
 *
 */
@XmlRootElement(name = "email")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class XMLEmailConfiguration {
	/**
	 * transport.protocol。 选配。
	 */
	@XmlElement(name = "transport-protocol", required = false)
	private String transportProtocol = EmailConstant.DEFAULT_TRANSPORT_PROTOCOL;

	/**
	 * 设置缺省的FROM地址别名。 必配项
	 */
	@XmlElement(name = "alias")
	private String alias;

	/**
	 * 设置SMTP端口。 选配。
	 *
	 * "mail.smtp.port"
	 */
	@XmlElement(name = "smtp-port")
	private String smtpPort = "465";

	/**
	 * 设置是否使用SSL，默认 true.
	 * "mail.smtp.ssl.enable"
	 */
	@XmlElement(name = "ssl")
	private Boolean ssl = EmailConstant.DEFAULT_SMTP_AUTH;

	/**
	 * 设置SSL端口
	 */
	@XmlElement(name = "ssl-port")
	private String sslPort = "465";

	/**
	 * MailSmtpType， 取值范围来自 MailSmtpType(_126,_163,_ALIYUN,_QQ,_QQ_ENT)
	 *
	 * SMTP服务器名称 hostName 来自  MailSmtpType
	 */
	@XmlElement(name = "smtp-type", required = true)
	private String smtpType;

	/**
	 * 认证用户信息
	 */
	@XmlElement(name = "auth", required = true)
	private XMLEmailAuthentication auth;

	/**
	 * 邮件发送的配置信息
	 */
	@XmlElement(name = "config", required = false)
	private XMLEmailConfig config;

}
