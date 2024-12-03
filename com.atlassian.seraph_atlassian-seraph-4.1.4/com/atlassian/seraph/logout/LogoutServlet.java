/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.seraph.logout;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutServlet
extends HttpServlet {
    private SecurityConfig securityConfig;

    public void init() throws ServletException {
        super.init();
        this.securityConfig = SecurityConfigFactory.getInstance();
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        this.securityConfig = (SecurityConfig)servletConfig.getServletContext().getAttribute("seraph_config");
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.isRelativeRedirect()) {
            response.sendRedirect(request.getContextPath() + this.getSecurityConfig().getLogoutURL());
        } else {
            try {
                Authenticator authenticator = this.getAuthenticator();
                authenticator.logout(request, response);
            }
            catch (AuthenticatorException e) {
                throw new ServletException("Seraph authenticator couldn't log out", (Throwable)e);
            }
            response.sendRedirect(this.getSecurityConfig().getLogoutURL());
        }
    }

    private boolean isRelativeRedirect() {
        return this.getSecurityConfig().getLogoutURL().indexOf("://") == -1;
    }

    protected SecurityConfig getSecurityConfig() {
        return this.securityConfig;
    }

    protected Authenticator getAuthenticator() {
        return this.getSecurityConfig().getAuthenticator();
    }
}

