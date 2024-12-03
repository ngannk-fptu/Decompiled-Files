/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.core.env.EnumerablePropertySource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.context.support;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class ServletContextPropertySource
extends EnumerablePropertySource<ServletContext> {
    public ServletContextPropertySource(String name, ServletContext servletContext) {
        super(name, (Object)servletContext);
    }

    public String[] getPropertyNames() {
        return StringUtils.toStringArray((Enumeration)((ServletContext)this.source).getInitParameterNames());
    }

    @Nullable
    public String getProperty(String name) {
        return ((ServletContext)this.source).getInitParameter(name);
    }
}

