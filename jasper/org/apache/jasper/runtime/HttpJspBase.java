/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.HttpJspPage
 */
package org.apache.jasper.runtime;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;
import org.apache.jasper.compiler.Localizer;

public abstract class HttpJspBase
extends HttpServlet
implements HttpJspPage {
    private static final long serialVersionUID = 1L;

    protected HttpJspBase() {
    }

    public final void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.jspInit();
        this._jspInit();
    }

    public String getServletInfo() {
        return Localizer.getMessage("jsp.engine.info", "2.3");
    }

    public final void destroy() {
        this.jspDestroy();
        this._jspDestroy();
    }

    public final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._jspService(request, response);
    }

    public void jspInit() {
    }

    public void _jspInit() {
    }

    public void jspDestroy() {
    }

    protected void _jspDestroy() {
    }

    public abstract void _jspService(HttpServletRequest var1, HttpServletResponse var2) throws ServletException, IOException;
}

