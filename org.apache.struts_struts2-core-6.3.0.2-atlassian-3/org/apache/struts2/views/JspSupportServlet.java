/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 */
package org.apache.struts2.views;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class JspSupportServlet
extends HttpServlet {
    private static final long serialVersionUID = 8302309812391541933L;
    public static JspSupportServlet jspSupportServlet;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        jspSupportServlet = this;
    }
}

