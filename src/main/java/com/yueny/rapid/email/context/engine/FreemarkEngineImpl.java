package com.yueny.rapid.email.context.engine;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FreemarkEngineImpl implements IEngine {
    /**
     * 默认的邮件模板存放位置, '/template/ftl'
     */
    private static final String DEFAULT_TEMPLATE_PATH = "/template/ftl";

    /**
     * 启动模板缓存<br>
     * 对模板采用了缓存技术，第一次用到模板的时候会去读取文件，以后都共享内存中的实例了<br>
     * key为 templateFile，即templateId + getTemplateSuffix();
     */
    private static final Map<String, Template> TEMPLATE_CACHE = new HashMap<>();
    /**
     * 模板引擎配置
     */
    private final Configuration configuration;

    public FreemarkEngineImpl(){
        this(DEFAULT_TEMPLATE_PATH);
    }

    public FreemarkEngineImpl(final String templatePath){
        configuration = new Configuration(Configuration.VERSION_2_3_20);
        // 设置模板目录
        configuration.setTemplateLoader(new ClassTemplateLoader(FreemarkEngineImpl.class, templatePath));

        // 设置默认编码格式
        configuration.setEncoding(Locale.getDefault(), "UTF-8");

        configuration.setDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public String render(String name, Map<String, Object> context) throws Exception {
        try {
            // 从设置的目录中获得模板
            Template template = TEMPLATE_CACHE.get(name);
            if (template == null) {
                template = configuration.getTemplate(name, "utf-8");
                TEMPLATE_CACHE.put(name, template);
            }

            // 合并模板和数据模型
            final StringWriter writer = new StringWriter();
            // // 将数据与模板渲染的结果写入文件 file 中
            // Writer writer=new OutputStreamWriter(new FileOutputStream(file),
            // "UTF-8");
            template.process(context, writer);

            final String val = writer.toString();

            // 关闭
            writer.flush();
            writer.close();
            context.clear();

            return val;
        } catch (final Exception e) {
            throw e;
        }
    }

    @Override
    public EngineType getType() {
        return EngineType.FREEMARKER;
    }

    @Override
    public String getTemplateSuffix() {
        return ".ftl";
    }

}
