/**
 *
 */
package com.yueny.rapid.email.sender.entity;

import lombok.*;

import java.util.*;

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
@NoArgsConstructor
public class MessageData {
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
	@Setter
	private String text;
	/**
	 * 邮件正文/HTML邮件正文<br>
	 * 或者TEXT邮件正文，用于HTML正文无法显示的替代文本<br>
	 * 必传
	 */
	@Setter
	private String html;

	/**
	 * 昵称
	 */
	@Setter
	private String nickName;
	/**
	 * from, 如果为空, 则默认为 userName
	 */
	@Setter
	private String from;

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

	/**
	 * 密送地址
	 */
	private final List<String> bcc = new ArrayList<String>();

	/**
	 * 抄送地址
	 */
	private final List<String> cc = new ArrayList<String>();

	 /**
	 * 回复地址
	 */
	 @Singular("replyTo")
	 private List<String> replyTo = new ArrayList<String>();

	/**
	 * 附件列表<br>
	 * 非必传
	 */
	@Singular("attachements")
	private List<BaseMsgAttachment> attachements = new ArrayList<>();

	public void attachement(final BaseMsgAttachment attachment) {
		this.attachements.add(attachment);
	}

	public void bcc(final String bcc) {
		this.bcc.add(bcc);
	}
	public void bcc(final String... bcc) {
		this.bcc.addAll(Arrays.asList(bcc));
	}

	public void cc(final String cc) {
		this.cc.add(cc);
	}
	public void cc(final String... cc) {
		this.cc.addAll(Arrays.asList(cc));
	}

	public void replyTo(final String replyTo) {
	 	this.replyTo.add(replyTo);
	}
	public void replyTo(final String... replyTo) {
		for(String rt : replyTo){
			replyTo(rt);
		}
	}

	public void to(final String to) {
		this.to.add(to);
	}
	public void to(final String... to) {
		this.to.addAll(Arrays.asList(to));
	}

}
