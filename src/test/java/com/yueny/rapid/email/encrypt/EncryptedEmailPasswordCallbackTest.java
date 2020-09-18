/**
 *
 */
package com.yueny.rapid.email.encrypt;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author yueny09 <yueny09@163.com>
 *
 * @DATE 2018年2月28日 下午3:24:23
 *
 */
public class EncryptedEmailPasswordCallbackTest {
	@Test
	public void test() {
		final String passwd = "aaaaa";
		System.out.println("加密前的数据:" + passwd);

		final String s = EncryptedEmailPasswordCallback.encrypt(passwd);
		System.out.println("加密后的密文:" + s);

		final String data = EncryptedEmailPasswordCallback.decrypt(s);
		System.out.println("解密后的字符串:" + data);

		Assert.assertEquals(passwd, data);
	}

	@Test
	public void test1() {
		final String passwd = "aa";
		String pwPBESalt = EncryptedEmailPasswordCallback.getSalt();
//		String pwPBESalt = "aaa";
		System.out.println("加密前的数据:" + passwd + ", 盐：" + pwPBESalt);

		final String s = EncryptedEmailPasswordCallback.encrypt(passwd, pwPBESalt);
		System.out.println("加密后的密文:" + s);

		final String data = EncryptedEmailPasswordCallback.decrypt(s, pwPBESalt);
		System.out.println("解密后的字符串:" + data);

		Assert.assertEquals(passwd, data);
	}

}
