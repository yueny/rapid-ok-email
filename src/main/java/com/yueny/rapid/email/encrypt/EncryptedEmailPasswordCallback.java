package com.yueny.rapid.email.encrypt;

import com.yueny.superclub.util.crypt.core.PBECoder;
import org.apache.commons.lang3.StringUtils;
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
	 *
	 * @param encryptData 解密数据
	 * @param pwPBESalt 解密盐值
	 */
	public static String decrypt(final String encryptData, String pwPBESalt) {
		String password = encryptData;

		// 2
		if(StringUtils.isNotEmpty(pwPBESalt)){ // 二次加密
			password = PBECoder.decryptHex(password, "password", pwPBESalt);
		}

		// 1
		return decrypt(password);
	}

	/**
	 * 密码解密
	 *
	 * @param encryptData 解密数据
	 */
	public static String decrypt(final String encryptData) {
		final String pas = encryptData;
		return TripleDesEncryptUtil.tripleDesDecrypt(pas);
	}

	/**
	 * 密码加密
	 *
	 * @param data 加密数据
	 * @param pwPBESalt 加密盐值
	 */
	public static String encrypt(final String data, String pwPBESalt) {
		// 1
		String password = encrypt(data);

		// 2
		if(StringUtils.isNotEmpty(pwPBESalt)){ // 二次加密
			password = PBECoder.encryptHex(password, "password", pwPBESalt);
		}

		return password;
	}

	/**
	 * 密码加密
	 *
	 * @param data 加密数据
	 */
	public static String encrypt(final String data) {
		try {
			return TripleDesEncryptUtil.tripleDesEncrypt(data);
		} catch (final Exception e) {
			logger.error("set real password error", e);
		}
		return "";
	}

	/**
	 * 获取合法的盐值
	 */
	public static String getSalt() {
		return PBECoder.initSalt();
	}
}
