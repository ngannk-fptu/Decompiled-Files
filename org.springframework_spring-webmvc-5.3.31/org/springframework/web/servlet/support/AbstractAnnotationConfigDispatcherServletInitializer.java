/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 */
package org.springframework.web.servlet.support;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

public abstract class AbstractAnnotationConfigDispatcherServletInitializer
extends AbstractDispatcherServletInitializer {
    @Nullable
    protected WebApplicationContext createRootApplicationContext() {
        Object[] configClasses = this.getRootConfigClasses();
        if (!ObjectUtils.isEmpty((Object[])configClasses)) {
            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.register((Class[])configClasses);
            return context;
        }
        return null;
    }

    @Override
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        Object[] configClasses = this.getServletConfigClasses();
        if (!ObjectUtils.isEmpty((Object[])configClasses)) {
            context.register((Class[])configClasses);
        }
        return context;
    }

    @Nullable
    protected abstract Class<?>[] getRootConfigClasses();

    @Nullable
    protected abstract Class<?>[] getServletConfigClasses();
}

