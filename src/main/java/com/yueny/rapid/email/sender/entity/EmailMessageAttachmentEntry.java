/**
 *
 */
package com.yueny.rapid.email.sender.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 邮件附件对象.
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:31:32
 *
 */
@ToString
public class EmailMessageAttachmentEntry implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -5751374358261759096L;

	/**
	 * 附件描述.
	 */
	@Getter
	@Setter
	private String description;
	/**
	 * 邮件Id
	 */
	@Getter
	@Setter
	private String emailId;
	/**
	 * 附件名称.
	 */
	@Getter
	@Setter
	private String name;

	/**
	 * 附件路径.
	 */
	@Getter
	@Setter
	private String path;

}
