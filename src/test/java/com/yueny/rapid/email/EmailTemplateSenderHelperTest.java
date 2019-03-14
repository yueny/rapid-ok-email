///**
// *
// */
//package com.yueny.rapid.email;
//
//import org.apache.commons.lang3.StringUtils;
//import org.junit.Test;
//
//import com.yueny.rapid.message.email.sender.EmailTemplateSenderHelper;
//import com.yueny.rapid.message.email.sender.EmailType;
//import com.yueny.rapid.message.email.sender.callback.AbstractEmailSendCallback;
//import com.yueny.rapid.message.email.sender.core.EmailContext;
//import com.yueny.rapid.message.email.sender.core.EmailMessage;
//import com.yueny.rapid.message.email.sender.core.EmailMessageForTemplate;
//import com.yueny.rapid.message.email.sender.entity.ThreadEmailEntry;
//
///**
// * @author yueny09 <yueny09@163.com>
// *
// * @DATE 2017年12月14日 下午9:14:23
// *
// */
//public class EmailTemplateSenderHelperTest {
//	/**
//	 * 测试附件信息
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testAttach() throws Exception {
//		final EmailMessage emailMessage = new EmailMessage("测试附件信息", "测试附件信息");
//		emailMessage.to("yuany@aicaigroup.com");
//
//		// final EmailMessageAttachment attachment = new
//		// EmailMessageAttachment();
//		// // 附件描述
//		// attachment.setDescription("日志文件");
//		// // 附件名称
//		// attachment.setName("log4j.properties");
//		// // 附件路径
//		// attachment.setPath(System.getProperty("user.dir") +
//		// "/src/main/java/log4j.properties");
//		// emailMessage.addAttachment(attachment);
//
//		EmailTemplateSenderHelper.send(emailMessage);
//	}
//
//	/**
//	 * 测试密送信息
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testBcc() throws Exception {
//		final EmailMessage emailMessage = new EmailMessage("测试密送信息", "测试密送信息");
//		emailMessage.to("yuany@aicaigroup.com");
//		// 密送
//		emailMessage.bcc("yuany@aicaigroup.com");
//
//		EmailTemplateSenderHelper.send(emailMessage);
//	}
//
//	/**
//	 * 测试抄送信息
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testCC() throws Exception {
//		final EmailMessage emailMessage = new EmailMessage("测试抄送信息", "测试抄送信息");
//		emailMessage.to("yuany@aicaigroup.com");
//		// 抄送
//		emailMessage.cc("yuany@aicaigroup.com");
//
//		EmailTemplateSenderHelper.send(emailMessage);
//	}
//
//	/**
//	 * 测试html信息
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testHtmlEmail() throws Exception {
//		// // 新建标准的格式
//		// final MessageFormat formatter = new MessageFormat("");
//		// // 从资源文件中获取相应的模板信息
//		// formatter.applyPattern("<b>我是html信息:{0}人</b>");
//		//
//		// // 获得填充的数据
//		// final Object[] args = { "8" };
//		// // 填充模板
//		// final String content = formatter.format(args);
//
//		final String email_ = "<b>我是html信息:${content}人</b>";
//		String content = "88";
//		// 填充模板
//		content = StringUtils.replace(email_, "${content}", content);// 替换模板中的正文
//
//		final EmailMessage emailMessage = new EmailMessage(content, "测试html信息");
//		emailMessage.to("yuany@aicaigroup.com");
//		emailMessage.setType(EmailType.HTML);
//
//		EmailTemplateSenderHelper.send(emailMessage);
//	}
//
//	/**
//	 * 文本信息
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testTextEmail() throws Exception {
//		final EmailMessage emailMessage = new EmailMessage("我的文本信息", "测试文本信息");
//		emailMessage.to("yuany@aicaigroup.com");
//
//		EmailTemplateSenderHelper.send(emailMessage);
//		Thread.sleep(10000);
//	}
//
//	/**
//	 * 模板邮件发送
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testTextEmailForCallbleTemplate() throws Exception {
//		// final EmailMessageForTemplate emailMessage1 = new
//		// EmailMessageForTemplate("文本信息", "测试信息");
//		// emailMessage1.to("deep_blue_yang@163.com");
//		// emailMessage1.setType(EmailType.HTML);
//		//
//		// final ThreadEmailEntry entry1 =
//		// EmailTemplateSenderHelper.sendResult(emailMessage1);
//		// System.out.println("发送结果：" + entry1);
//
//		final EmailMessageForTemplate emailMessage = new EmailMessageForTemplate("demo", "我的文本信息", "测试文本信息");
//		emailMessage.getParameters().put("userName", "yuanyang");
//
//		emailMessage.to("deep_blue_yang@163.com");
//		emailMessage.setType(EmailType.HTML);
//
//		final ThreadEmailEntry entry = EmailTemplateSenderHelper.sendResult(emailMessage,
//				new AbstractEmailSendCallback() {
//					@Override
//					public void after(final EmailContext emailContext, final String msgId) {
//						// .
//					}
//				});
//		System.out.println("发送结果：" + entry);
//
//		System.out.println("end~");
//	}
//
//	/**
//	 * 模板邮件发送
//	 *
//	 * @throws Exception
//	 */
//	@Test
//	public void testTextEmailForTemplate() throws Exception {
//		final EmailMessageForTemplate emailMessage = new EmailMessageForTemplate("我的文本信息", "测试文本信息");
//		emailMessage.to("yuany@aicaigroup.com");
//		emailMessage.to("deep_blue_yang@163.com");
//		emailMessage.setType(EmailType.HTML);
//
//		EmailTemplateSenderHelper.send(emailMessage);
//		Thread.sleep(20000);
//	}
//
//}
