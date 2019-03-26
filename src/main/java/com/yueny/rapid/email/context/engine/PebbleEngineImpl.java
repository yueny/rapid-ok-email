package com.yueny.rapid.email.context.engine;


import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PebbleEngineImpl implements IEngine {

    @Override
    public String render(String name, Map<String, Object> context) throws Exception {
        try{
            PebbleEngine engine = new PebbleEngine.Builder().build();
            PebbleTemplate compiledTemplate = engine.getTemplate(name);

            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            String output = writer.toString();
            System.out.println(output);

            return output;
        } catch(PebbleException e){
            throw e;
        } catch(Exception e){
            throw e;
        }
    }

    @Override
    public EngineType getType() {
        return EngineType.PEBBLE;
    }

    @Override
    public String getTemplateSuffix() {
        return ".html";
    }

}
