/**
 *
 */
package com.yueny.rapid.email.sender.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 邮件附件对象.
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:31:32
 *
 */
public abstract class BaseMsgAttachment<T> implements Serializable {
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
	 * 附件名称.
	 */
	@Getter
	@Setter
	private String name;

	/**
	 * 附件类型
	 */
	@Getter
	private Type type;

	/**
	 * 附件路径.
	 */
	@Getter
	@Setter
	private T uri;

	protected void setType(Type type){
		this.type = type;
	}

	public enum Type{
		URL,
		PATH,
		FILE;
	}

}
