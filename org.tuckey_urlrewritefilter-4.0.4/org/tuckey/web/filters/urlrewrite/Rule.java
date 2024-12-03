/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.RuleChain;

public interface Rule {
    public RewrittenUrl matches(String var1, HttpServletRequest var2, HttpServletResponse var3, RuleChain var4) throws IOException, ServletException, InvocationTargetException;

    public RewrittenUrl matches(String var1, HttpServletRequest var2, HttpServletResponse var3) throws IOException, ServletException, InvocationTargetException;

    public boolean initialise(ServletContext var1);

    public void destroy();

    public String getName();

    public String getDisplayName();

    public boolean isLast();

    public void setId(int var1);

    public int getId();

    public boolean isValid();

    public boolean isFilter();

    public List getErrors();
}

