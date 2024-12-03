/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class GenericServlet
implements Servlet,
ServletConfig,
Serializable {
    private static final long serialVersionUID = 1L;
    private transient ServletConfig config;

    @Override
    public void destroy() {
    }

    @Override
    public String getInitParameter(String name) {
        return this.getServletConfig().getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return this.getServletConfig().getInitParameterNames();
    }

    @Override
    public ServletConfig getServletConfig() {
        return this.config;
    }

    @Override
    public ServletContext getServletContext() {
        return this.getServletConfig().getServletContext();
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        this.init();
    }

    public void init() throws ServletException {
    }

    public void log(String message) {
        this.getServletContext().log(this.getServletName() + ": " + message);
    }

    public void log(String message, Throwable t) {
        this.getServletContext().log(this.getServletName() + ": " + message, t);
    }

    @Override
    public abstract void service(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;

    @Override
    public String getServletName() {
        return this.config.getServletName();
    }
}

