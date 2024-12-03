/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.AbstractBootstrapHotSwappingFilter;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class ConfigurableBootstrapHotSwappingFilter
extends AbstractBootstrapHotSwappingFilter {
    private Class swapClass = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        String className = filterConfig.getInitParameter("swapClass");
        try {
            this.swapClass = Class.forName(className);
            if (!Filter.class.isAssignableFrom(this.swapClass)) {
                throw new ServletException("Swap class does not implement " + Filter.class.getName());
            }
        }
        catch (ClassNotFoundException e) {
            throw new ServletException("Swap class " + className + " not found");
        }
    }

    @Override
    public Filter getSwapTarget() throws ServletException {
        try {
            return (Filter)this.swapClass.newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new ServletException("Could not instantiate swap class", (Throwable)e);
        }
    }
}

