/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.template.Template
 *  freemarker.template.TemplateException
 */
package org.springframework.ui.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class FreeMarkerTemplateUtils {
    public static String processTemplateIntoString(Template template, Object model) throws IOException, TemplateException {
        StringWriter result = new StringWriter(1024);
        template.process(model, (Writer)result);
        return result.toString();
    }
}

