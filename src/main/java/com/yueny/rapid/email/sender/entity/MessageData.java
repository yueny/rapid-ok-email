/**
 *
 */
package com.yueny.rapid.email.sender.entity;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 邮件对象，可以包含多个邮件附件对象.
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:33:52
 *
 */
@Getter
@ToString
@RequiredArgsConstructor
public class MessageData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2265956892482817063L;

//	/**
//	 * 附件列表<br>
//	 * 非必传
//	 */
//	private final List<EmailMessageAttachmentEntry> attachements = new ArrayList<>();

	/**
	 * 密送地址
	 */
	private final List<String> bcc = new ArrayList<String>();

	/**
	 * 抄送地址
	 */
	private final List<String> cc = new ArrayList<String>();

	/**
	 * 字符集<br>
	 * 非必传
	 */
	@Setter
	private String charset = "UTF-8";

	/**
	 * 邮件正文/HTML邮件正文<br>
	 * 或者TEXT邮件正文，用于HTML正文无法显示的替代文本<br>
	 * 必传
	 */
	@NonNull
	@Setter
	private String msg;
	/**
	 * 发送时间<br>
	 * 非必传
	 */
	@Setter
	private Date sentDate = new Date();

	/**
	 * 主题<br>
	 * 非必传
	 */
	@Setter
	@NonNull
	private String subject;

	/**
	 * 目的地址, equals replyTo<br>
	 * 选择性必传:, to, cc, bcc 三选一
	 */
	private final List<String> to = new ArrayList<String>();
	// /**
	// * 回复地址
	// */
	// @Singular("replyTo")
	// private List<String> replyTo = new ArrayList<String>();

//	/**
//	 * 邮件类型，默认为PLAIN
//	 */
//	@Setter
//	private EmailType type = EmailType.PLAIN;

//	public void attachement(final EmailMessageAttachmentEntry attachment) {
//		this.attachements.add(attachment);
//	}

	public void bcc(final String bcc) {
		this.bcc.add(bcc);
	}

	public void cc(final String cc) {
		this.cc.add(cc);
	}

	// public void addReplyTo(final String replyTo) {
	// this.replyTo.add(replyTo);
	// }

	public void to(final String to) {
		this.to.add(to);
	}

}