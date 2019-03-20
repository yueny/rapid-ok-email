/**
 *
 */
package com.yueny.rapid.email.sender.entity;

/**
 * 邮件附件对象.
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:31:32
 *
 */
public class PathMsgAttachmentEntry extends BaseMsgAttachment<String> {
	/**
	 *
	 */
	private static final long serialVersionUID = -5751374358261759096L;

	public PathMsgAttachmentEntry(){
		setType(Type.PATH);
	}

}
