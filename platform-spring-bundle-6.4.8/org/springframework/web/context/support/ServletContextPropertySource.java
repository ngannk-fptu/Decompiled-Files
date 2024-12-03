/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.springframework.web.context.support;

import javax.servlet.ServletContext;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class ServletContextPropertySource
extends EnumerablePropertySource<ServletContext> {
    public ServletContextPropertySource(String name, ServletContext servletContext) {
        super(name, servletContext);
    }

    @Override
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((ServletContext)this.source).getInitParameterNames());
    }

    @Override
    @Nullable
    public String getProperty(String name) {
        return ((ServletContext)this.source).getInitParameter(name);
    }
}

