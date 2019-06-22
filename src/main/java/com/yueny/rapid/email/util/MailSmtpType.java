/**
 *
 */
package com.yueny.rapid.email.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 邮件smtp类型枚举类.
 * "mail.smtp.host"
 *
 * <ul>
 * <li>126: </li>
 * <li>163: </li>
 * </ul>
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2017年12月14日 下午8:30:45
 *
 */
public enum MailSmtpType implements ISmtpType {
	/**
	 * 网易126
	 */
	_126("smtp.126.com"){

		@Override
		public String getImapName() {
			return "imap.126.com";
		}

		@Override
		public String getPop3Name() {
			return "pop3.126.com";
		}
	},
	/**
	 * 网易163
	 */
	_163("smtp.163.com"){
		@Override
		public String getImapName() {
			return "";
		}

		@Override
		public String getPop3Name() {
			return "";
		}
	},
	/**
	 * aliyun
	 */
	_ALIYUN("smtp.qiye.aliyun.com"){
		@Override
		public String getImapName() {
			return "imap.qiye.aliyun.com";
		}

		@Override
		public String getPop3Name() {
			return "pop.qiye.aliyun.com";
		}
	},
	/**
	 * smtp qq
	 */
	_QQ("smtp.qq.com"){
		@Override
		public String getImapName() {
			return "";
		}

		@Override
		public String getPop3Name() {
			return "";
		}
	},
	/**
	 * smtp entnterprise qq
	 */
	_QQ_ENT("smtp.exmail.qq.com"){
		@Override
		public String getImapName() {
			return "";
		}

		@Override
		public String getPop3Name() {
			return "";
		}
	};


	private String smtp;

	MailSmtpType(String hostName){
		this.smtp = hostName;
	}

	public static MailSmtpType getBy(String name) {
		for(MailSmtpType type : values()) {
			if(StringUtils.equals(type.name(), name)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public String getSmtpName() {
		return smtp;
	}

	@Override
	public String getSmtpNormalPort() {
		return "25";
	}

	@Override
	public String getSmtpSSLPort() {
		return "465";
	}

	@Override
	public String getImapNormalPort() {
		return "143";
	}

	@Override
	public String getImapSSLPort() {
		return "993";
	}

	@Override
	public String getPop3NormalPort() {
		return "110";
	}

	@Override
	public String getPop3SSLPort() {
		return "995";
	}
}
