/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view.groovy;

import java.util.Locale;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.groovy.GroovyMarkupView;

public class GroovyMarkupViewResolver
extends AbstractTemplateViewResolver {
    public GroovyMarkupViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    public GroovyMarkupViewResolver(String prefix, String suffix) {
        this();
        this.setPrefix(prefix);
        this.setSuffix(suffix);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return GroovyMarkupView.class;
    }

    @Override
    protected AbstractUrlBasedView instantiateView() {
        return this.getViewClass() == GroovyMarkupView.class ? new GroovyMarkupView() : super.instantiateView();
    }

    @Override
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + '_' + locale;
    }
}

