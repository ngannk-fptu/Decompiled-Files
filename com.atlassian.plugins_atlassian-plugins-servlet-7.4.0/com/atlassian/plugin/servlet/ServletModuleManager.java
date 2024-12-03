/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  javax.servlet.DispatcherType
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.atlassian.plugin.servlet.filter.FilterDispatcherCondition;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public interface ServletModuleManager {
    public void addServletModule(ServletModuleDescriptor var1);

    public HttpServlet getServlet(String var1, ServletConfig var2) throws ServletException;

    public void removeServletModule(ServletModuleDescriptor var1);

    public void addFilterModule(ServletFilterModuleDescriptor var1);

    @Deprecated
    public Iterable<Filter> getFilters(FilterLocation var1, String var2, FilterConfig var3, FilterDispatcherCondition var4) throws ServletException;

    public Iterable<Filter> getFilters(FilterLocation var1, String var2, FilterConfig var3, DispatcherType var4);

    public void removeFilterModule(ServletFilterModuleDescriptor var1);

    public void addServlet(Plugin var1, String var2, String var3);

    public void addServlet(Plugin var1, String var2, HttpServlet var3, ServletContext var4);
}

