package com.yueny.rapid.email.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yueny.superclub.util.crypt.util.TripleDesEncryptUtil;

/**
 * 密码加解密工具<br>
 *
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2018年2月28日 下午1:58:56
 *
 */
public class EncryptedEmailPasswordCallback {
	private static final Logger logger = LoggerFactory.getLogger(EncryptedEmailPasswordCallback.class);

	/**
	 * 密码解密
	 */
	public static String decrypt(final String encryptData) {
		final String pas = encryptData;
		return TripleDesEncryptUtil.tripleDesDecrypt(pas);
	}

	/**
	 * 密码加密
	 */
	public static String encrypt(final String data) {
		try {
			return TripleDesEncryptUtil.tripleDesEncrypt(data);
		} catch (final Exception e) {
			logger.error("set real password error", e);
		}
		return "";
	}

}
