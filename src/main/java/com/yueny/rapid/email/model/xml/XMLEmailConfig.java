/**
 *
 */
package com.yueny.rapid.email.model.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

/**
 *
 * <pre>
 *    <email>
 *      ...
 *    	<!-- 配置项 -->
 * 		<config print-duration-timer="true">
 * 		</config>
 * 	    ...
 * 	  </email>
 * </pre>
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:41:54
 *
 */
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class XMLEmailConfig {
	// /**
	// * 认证用户密码
	// */
	// @XmlElement(name = "password")
	// @Getter
	// @Setter
	// private String password;

	/**
	 * 控制台打印邮件发送耗时。 日志级别info。<br>
	 * EmailTemplateSenderHelper辅助服务的耗时打印不受控制。<br>
	 * 默认不打印
	 */
	@XmlAttribute(name = "print-duration-timer", required = false)
	@Getter
	@Setter
	private Boolean printDurationTimer = false;

	/**
	 * 是否开启调试模式<br>
	 */
	@XmlElement(name = "debug", required = false)
	@Getter
	@Setter
	private Boolean debug = false;

}
