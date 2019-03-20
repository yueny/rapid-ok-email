package com.yueny.rapid.email;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.yueny.rapid.email.exception.SendMailException;
import com.yueny.rapid.email.util.MailSmtpType;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * 发送邮件测试
 *
 */
public class OkEmailTest {

    // 该邮箱修改为你需要测试的邮箱地址
    private static final String TO_EMAIL = "yuany@aicaigroup.com";

    @Before
    public void before() {
        OkEmail.config(MailSmtpType._126, "deep_blue_yang@126.com", "21C738B2A8FCB58AEB50F272125A4EC0", true);
    }

    @Test
    public void testSendText() throws SendMailException {
        OkEmail.subject("这是一封测试TEXT邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL, "deep_blue_yang@126.com")
                .text("信件内容")
                .send();
        Assert.assertTrue(true);
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
    public void testPebble() throws IOException, PebbleException, SendMailException {
        PebbleEngine   engine           = new PebbleEngine.Builder().build();
        PebbleTemplate compiledTemplate = engine.getTemplate("register.html");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("username", "guest");
        context.put("email", "admin@guest.me");

        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);

        String output = writer.toString();
        System.out.println(output);

        OkEmail.subject("这是一封测试Pebble模板邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL)
                .cc(TO_EMAIL, "deep_blue_yang@126.com")
                .html(output)
                .send();
        Assert.assertTrue(true);
    }

    @Test
    public void testJetx() throws IOException, PebbleException, SendMailException {
//        PebbleEngine   engine           = new PebbleEngine.Builder().build();
//        PebbleTemplate compiledTemplate = engine.getTemplate("register.html");
//
//        Map<String, Object> context = new HashMap<String, Object>();
//        context.put("username", "guest");
//        context.put("email", "admin@guest.me");
//
//        Writer writer = new StringWriter();
//        compiledTemplate.evaluate(writer, context);

        JetEngine   engine   = JetEngine.create();
        JetTemplate template = engine.getTemplate("/register.jetx");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("username", "guest");
        context.put("email", "admin@guest.me");
        context.put("url", "<a href='http://guest.me'>https://guest.me/active/guest</a>");

        StringWriter writer = new StringWriter();
        template.render(context, writer);

        String output = writer.toString();
        System.out.println(output);

        OkEmail.subject("这是一封测试Jetx模板邮件")
                .from("小姐姐的邮箱")
                .to(TO_EMAIL, "deep_blue_yang@126.com")
                .bcc(TO_EMAIL)
                .html(output)
                .send();
        Assert.assertTrue(true);
    }

}