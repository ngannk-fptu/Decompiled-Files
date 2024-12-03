/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.servlet.filter;

import com.atlassian.confluence.extra.webdav.servlet.filter.AbstractHttpFilter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractPrefixAwareFilter
extends AbstractHttpFilter {
    private static final String INIT_PARAM_PREFIX = "resource-prefix";
    private String prefix;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.prefix = StringUtils.defaultString((String)filterConfig.getInitParameter(INIT_PARAM_PREFIX), (String)"/plugins/servlet/confluence/default");
    }

    protected String getPrefix() {
        return this.prefix;
    }

    @Override
    public void destroy() {
        this.prefix = null;
    }
}

