/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.text.SimpleTemplateEngine
 *  groovy.text.Template
 *  groovy.text.TemplateEngine
 *  org.codehaus.groovy.syntax.SyntaxException
 */
package org.radeox.example;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.codehaus.groovy.syntax.SyntaxException;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.BaseRenderEngine;
import org.radeox.engine.context.BaseRenderContext;

public class RadeoxTemplateEngine
extends TemplateEngine {
    public Template createTemplate(Reader reader) throws SyntaxException, ClassNotFoundException, IOException {
        BaseRenderContext context = new BaseRenderContext();
        BaseRenderEngine engine = new BaseRenderEngine();
        String renderedText = engine.render(reader, (RenderContext)context);
        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
        return templateEngine.createTemplate((Reader)new StringReader(renderedText));
    }
}

