/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.servlet.SpringManagedServlet;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletManager {
    public void servletDestroyed(SpringManagedServlet var1);

    public void service(SpringManagedServlet var1, HttpServletRequest var2, HttpServletResponse var3) throws ServletException, IOException;

    public void servletInitialised(SpringManagedServlet var1, ServletConfig var2) throws ServletException;
}

