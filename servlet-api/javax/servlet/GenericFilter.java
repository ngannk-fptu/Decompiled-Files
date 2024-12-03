/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.io.Serializable;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public abstract class GenericFilter
implements Filter,
FilterConfig,
Serializable {
    private static final long serialVersionUID = 1L;
    private volatile FilterConfig filterConfig;

    @Override
    public String getInitParameter(String name) {
        return this.getFilterConfig().getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return this.getFilterConfig().getInitParameterNames();
    }

    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    @Override
    public ServletContext getServletContext() {
        return this.getFilterConfig().getServletContext();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.init();
    }

    public void init() throws ServletException {
    }

    @Override
    public String getFilterName() {
        return this.getFilterConfig().getFilterName();
    }
}

