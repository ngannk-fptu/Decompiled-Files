/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  org.springframework.core.env.EnumerablePropertySource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.context.support;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class ServletConfigPropertySource
extends EnumerablePropertySource<ServletConfig> {
    public ServletConfigPropertySource(String name, ServletConfig servletConfig) {
        super(name, (Object)servletConfig);
    }

    public String[] getPropertyNames() {
        return StringUtils.toStringArray((Enumeration)((ServletConfig)this.source).getInitParameterNames());
    }

    @Nullable
    public String getProperty(String name) {
        return ((ServletConfig)this.source).getInitParameter(name);
    }
}

