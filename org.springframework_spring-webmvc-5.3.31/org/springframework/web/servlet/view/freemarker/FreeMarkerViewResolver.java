/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view.freemarker;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

public class FreeMarkerViewResolver
extends AbstractTemplateViewResolver {
    public FreeMarkerViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    public FreeMarkerViewResolver(String prefix, String suffix) {
        this();
        this.setPrefix(prefix);
        this.setSuffix(suffix);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return FreeMarkerView.class;
    }

    @Override
    protected AbstractUrlBasedView instantiateView() {
        return this.getViewClass() == FreeMarkerView.class ? new FreeMarkerView() : super.instantiateView();
    }
}

