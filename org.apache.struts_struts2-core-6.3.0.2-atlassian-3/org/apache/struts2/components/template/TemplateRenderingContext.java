/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components.template;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.Map;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.components.template.Template;

public class TemplateRenderingContext {
    Template template;
    ValueStack stack;
    Map parameters;
    UIBean tag;
    Writer writer;

    public TemplateRenderingContext(Template template, Writer writer, ValueStack stack, Map params, UIBean tag) {
        this.template = template;
        this.writer = writer;
        this.stack = stack;
        this.parameters = params;
        this.tag = tag;
    }

    public Template getTemplate() {
        return this.template;
    }

    public ValueStack getStack() {
        return this.stack;
    }

    public Map getParameters() {
        return this.parameters;
    }

    public UIBean getTag() {
        return this.tag;
    }

    public Writer getWriter() {
        return this.writer;
    }
}

