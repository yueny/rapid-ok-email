/**
 *
 */
package com.yueny.rapid.email.sender.entity;

import java.net.URL;

/**
 * 邮件附件对象.
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:31:32
 *
 */
public class URLMsgAttachmentEntry extends BaseMsgAttachment<URL> {
	/**
	 *
	 */
	private static final long serialVersionUID = -5751374358261759096L;

	public URLMsgAttachmentEntry(){
		setType(Type.URL);
	}

}
