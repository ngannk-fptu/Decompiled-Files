/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view;

import java.util.Locale;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class BeanNameViewResolver
extends WebApplicationObjectSupport
implements ViewResolver,
Ordered {
    private int order = Integer.MAX_VALUE;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws BeansException {
        ApplicationContext context = this.obtainApplicationContext();
        if (!context.containsBean(viewName)) {
            return null;
        }
        if (!context.isTypeMatch(viewName, View.class)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Found bean named '" + viewName + "' but it does not implement View"));
            }
            return null;
        }
        return context.getBean(viewName, View.class);
    }
}

