/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.context.i18n.LocaleContextHolder
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.context.support;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class HttpRequestHandlerServlet
extends HttpServlet {
    @Nullable
    private HttpRequestHandler target;

    public void init() throws ServletException {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
        this.target = (HttpRequestHandler)wac.getBean(this.getServletName(), HttpRequestHandler.class);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.state((this.target != null ? 1 : 0) != 0, (String)"No HttpRequestHandler available");
        LocaleContextHolder.setLocale((Locale)request.getLocale());
        try {
            this.target.handleRequest(request, response);
        }
        catch (HttpRequestMethodNotSupportedException ex) {
            Object[] supportedMethods = ex.getSupportedMethods();
            if (supportedMethods != null) {
                response.setHeader("Allow", StringUtils.arrayToDelimitedString((Object[])supportedMethods, (String)", "));
            }
            response.sendError(405, ex.getMessage());
        }
        finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}

