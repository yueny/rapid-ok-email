package com.yueny.rapid.email.context.engine;

import java.util.Map;

public interface IEngine {
    /**
     * @param name 模板名称, 如 "/register.jetx"(Jet), "register.html"(Pebble)
     * @param context 上下文替换信息
     * @return
     */
    String render(String name, Map<String, Object> context) throws Exception;

    EngineType getType();

    /**
     * @return 模板文件后缀，如 freemark为 .ftl
     */
    String getTemplateSuffix();

}
