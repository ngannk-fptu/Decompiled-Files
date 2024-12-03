/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletContext
 */
package org.apache.velocity.tools.view;

import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.apache.velocity.tools.view.JeeConfig;

public class JeeFilterConfig
implements JeeConfig {
    protected FilterConfig filter;

    public JeeFilterConfig(FilterConfig filter) {
        if (filter == null) {
            throw new NullPointerException("FilterConfig should not be null; there must be a way to get a ServletContext");
        }
        this.filter = filter;
    }

    @Override
    public String getInitParameter(String name) {
        return this.filter.getInitParameter(name);
    }

    @Override
    public String findInitParameter(String key) {
        String param = this.getInitParameter(key);
        if (param == null) {
            param = this.getServletContext().getInitParameter(key);
        }
        return param;
    }

    @Override
    public Enumeration getInitParameterNames() {
        return this.filter.getInitParameterNames();
    }

    @Override
    public String getName() {
        return this.filter.getFilterName();
    }

    @Override
    public ServletContext getServletContext() {
        return this.filter.getServletContext();
    }
}

