package com.yueny.rapid.email.util;

/**
 * @author yueny09 <deep_blue_yang@163.com>
 * @DATE 2019/6/22 下午4:13
 */
public interface ISmtpType {
    /**
     * 服务器地址
     */
    String getSmtpName();
    /**
     * 服务器端口号（常规）
     */
    String getSmtpNormalPort();
    /**
     * 服务器端口号（加密）
     */
    String getSmtpSSLPort();

    String getImapName();
    String getImapNormalPort();
    String getImapSSLPort();
    String getPop3Name();
    String getPop3NormalPort();
    String getPop3SSLPort();

}
