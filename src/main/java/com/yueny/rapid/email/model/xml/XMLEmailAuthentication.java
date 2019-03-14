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
 *    	<!-- 认证用户 -->
 * 	  	<auth decrypt="true">
 * 			<!-- 认证用户名 -->
 * 		  	<user-name>deep_blue_yang@126.com</user-name>
 * 		  	<!-- 认证用户密码 -->
 * 		  	<password>21C738B2A8FCB58A6C75B9B9BAB753AD</password>
 * 	  	</auth>
 * 	    ...
 * 	  </email>
 * </pre>
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:41:54
 *
 */
@XmlRootElement(name = "auth")
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class XMLEmailAuthentication {
	/**
	 * 设置密码是否加密，默认不加密
	 */
	@XmlAttribute(name = "decrypt", required = false)
	@Getter
	@Setter
	private Boolean decrypt = false;
	/**
	 * 认证用户密码
	 */
	@XmlElement(name = "password")
	@Getter
	@Setter
	private String password;
	/**
	 * 认证用户名
	 */
	@XmlElement(name = "user-name")
	@Getter
	@Setter
	private String userName;

}
