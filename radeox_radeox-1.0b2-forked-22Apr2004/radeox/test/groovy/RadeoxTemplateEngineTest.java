/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.text.Template
 *  junit.framework.TestCase
 */
package radeox.test.groovy;

import groovy.text.Template;
import java.util.HashMap;
import junit.framework.TestCase;
import org.radeox.example.RadeoxTemplateEngine;

public class RadeoxTemplateEngineTest
extends TestCase {
    public RadeoxTemplateEngineTest(String name) {
        super(name);
    }

    public void testRadeoxTemplate() {
        String text = "__Dear__ ${firstname}";
        HashMap<String, String> binding = new HashMap<String, String>();
        binding.put("firstname", "stephan");
        RadeoxTemplateEngine engine = new RadeoxTemplateEngine();
        Template template = null;
        try {
            template = engine.createTemplate(text);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        template.setBinding(binding);
        String result = "<b class=\"bold\">Dear</b> stephan";
        RadeoxTemplateEngineTest.assertEquals((String)result, (String)template.toString());
    }
}

