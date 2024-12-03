/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 */
package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class ServletConfigPropertySource
extends EnumerablePropertySource<ServletConfig> {
    public ServletConfigPropertySource(String name, ServletConfig servletConfig) {
        super(name, servletConfig);
    }

    @Override
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((ServletConfig)this.source).getInitParameterNames());
    }

    @Override
    @Nullable
    public String getProperty(String name) {
        return ((ServletConfig)this.source).getInitParameter(name);
    }
}

