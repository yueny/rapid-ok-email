package com.yueny.rapid.email.context.engine;

import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;

import java.io.StringWriter;
import java.util.Map;

public class JetEngineImpl implements IEngine {

    @Override
    public String render(String name, Map<String, Object> context) throws Exception {
        JetEngine engine   = JetEngine.create();
        JetTemplate template = engine.getTemplate(name);

        StringWriter writer = new StringWriter();
        template.render(context, writer);

        String output = writer.toString();
        System.out.println(output);

        return output;
    }

    @Override
    public EngineType getType() {
        return EngineType.JET;
    }

    @Override
    public String getTemplateSuffix() {
        return ".jetx";
    }

}
