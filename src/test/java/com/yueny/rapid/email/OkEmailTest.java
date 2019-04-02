package com.yueny.rapid.email;

import com.yueny.rapid.email.context.engine.EngineType;
import com.yueny.rapid.email.context.engine.FreemarkEngineImpl;
import com.yueny.rapid.email.context.engine.JetEngineImpl;
import com.yueny.rapid.email.context.engine.PebbleEngineImpl;
import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.sender.entity.ThreadEmailEntry;
import com.yueny.rapid.email.util.MailSmtpType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;


/**
 * 发送邮件测试
 *
 */
@Slf4j
public class OkEmailTest {

    // 该邮箱修改为你需要测试的邮箱地址
    private static final String TO_EMAIL = "yuany@.com";

    @Before
    public void before() {
        OkEmail.config(MailSmtpType._126, "deep_blue_yang@126.com", "21C738B2A8FCB58AEB50F272125A4EC0", true);
    }

    @Test
    public void testSendText() throws Exception {
        Future<ThreadEmailEntry> future = OkEmail.subject("这是一封测试TEXT邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL, "deep_blue_yang@126.com")
                .text("信件内容")
                .sendFuture();

        log.debug("邮件已发送, future:{}!", future.get());
        Assert.assertTrue(future!=null && future.get().isSucess());
    }

    @Test
    public void testSendHtml() throws SendMailException {
        OkEmail.subject("这是一封测试HTML邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL)
                .html("<h1 font=red>信件内容</h1>")
                .send();
        Assert.assertTrue(true);
    }

    @Test
    public void testSendAttach() throws SendMailException {
        OkEmail.subject("这是一封测试附件邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL)
                .html("<h1 font=red>信件内容</h1>")
                .attach(new File("/Users/guest/Downloads/guest.jpeg"), "测试图片.jpeg")
                .send();
        Assert.assertTrue(true);
    }

    @Test
    public void testSendAttachURL() throws SendMailException, MalformedURLException {
        OkEmail.subject("这是一封测试网络资源作为附件的邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL)
                .cc(TO_EMAIL, "deep_blue_yang@126.com")
                .html("<h1 font=red>信件内容</h1>")
                .attachURL(new URL("https://guest.guest.com/u/guest?s=40&v=4"), "测试图片.jpeg")
                .send();
        Assert.assertTrue(true);
    }

    @Test
    public void testFreemark() throws Exception {
        final Map<String, Object> context = new HashMap<>();
        context.put("userName", "鸳鸯");

        FreemarkEngineImpl engine = new FreemarkEngineImpl();
        OkEmail.subject("这是一封测试 Freemark 模板邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL)
                .cc(TO_EMAIL, "deep_blue_yang@126.com")
//                .html(engine.render("register.html", context))
                .html(EngineType.FREEMARKER, "demo.ftl", context)
                .send();
        Assert.assertTrue(true);
    }

    @Test
    public void testPebble() throws Exception {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("username", "guest");
        context.put("email", "admin@guest.me");

        PebbleEngineImpl engine = new PebbleEngineImpl();
        OkEmail.subject("这是一封测试Pebble模板邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL)
                .cc(TO_EMAIL, "deep_blue_yang@126.com")
//                .html(engine.render("register.html", context))
                .html(EngineType.PEBBLE, "register.html", context)
                .send();
        Assert.assertTrue(true);
    }

    @Test
    public void testJetx() throws Exception {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("username", "guest");
        context.put("email", "admin@guest.me");
        context.put("url", "<a href='http://guest.me'>https://guest.me/active/guest</a>");

        JetEngineImpl engine   = new JetEngineImpl();

        OkEmail.subject("这是一封测试Jetx模板邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL, "deep_blue_yang@126.com")
                .bcc(TO_EMAIL)
                .html(EngineType.JET, "/register.jetx", context)
                .send();
        Assert.assertTrue(true);
    }

}