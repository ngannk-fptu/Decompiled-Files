/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view.script;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.script.ScriptTemplateView;

public class ScriptTemplateViewResolver
extends UrlBasedViewResolver {
    public ScriptTemplateViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    public ScriptTemplateViewResolver(String prefix, String suffix) {
        this();
        this.setPrefix(prefix);
        this.setSuffix(suffix);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return ScriptTemplateView.class;
    }

    @Override
    protected AbstractUrlBasedView instantiateView() {
        return this.getViewClass() == ScriptTemplateView.class ? new ScriptTemplateView() : super.instantiateView();
    }
}

