/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.compat.struts2.servletactioncontext;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.compat.struts2.servletactioncontext.ServletActionContextCompat;
import com.atlassian.core.filters.ServletContextThreadLocal;
import java.lang.reflect.Method;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServletActionContextStruts2AndWWCompat
implements ServletActionContextCompat {
    private static final Logger log = LoggerFactory.getLogger(ServletActionContextStruts2AndWWCompat.class);
    public static final String STRUTS_2_SERVLET_ACTION_CONTEXT = "org.apache.struts2.ServletActionContext";
    private final Method setRequest;
    private final Method getRequest;
    private final Method setResponse;
    private final Method getResponse;
    private Method getServletContext;
    private Method setServletContext;
    private Method getServletConfig;
    private Method setServletConfig;
    private static String sacClass;

    ServletActionContextStruts2AndWWCompat(String sacClass, ClassLoader classLoader) throws ReflectiveOperationException {
        ServletActionContextStruts2AndWWCompat.sacClass = sacClass;
        this.setRequest = this.getSACStruts2Method("setRequest", classLoader, HttpServletRequest.class);
        this.getRequest = this.getSACStruts2Method("getRequest", classLoader, new Class[0]);
        this.setResponse = this.getSACStruts2Method("setResponse", classLoader, HttpServletResponse.class);
        this.getResponse = this.getSACStruts2Method("getResponse", classLoader, new Class[0]);
        if (sacClass.equals(STRUTS_2_SERVLET_ACTION_CONTEXT)) {
            this.getServletContext = this.getSACStruts2Method("getServletContext", classLoader, new Class[0]);
            this.setServletContext = this.getSACStruts2Method("setServletContext", classLoader, ServletContext.class);
        } else {
            this.getServletConfig = this.getSACStruts2Method("getServletConfig", classLoader, new Class[0]);
            this.setServletConfig = this.getSACStruts2Method("setServletConfig", classLoader, ServletConfig.class);
        }
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        try {
            this.setRequest.invoke(null, request);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ServletActionContext request", (Throwable)e);
        }
    }

    @Override
    public HttpServletRequest getRequest() {
        try {
            return (HttpServletRequest)this.getRequest.invoke(null, new Object[0]) == null ? ServletContextThreadLocal.getRequest() : (HttpServletRequest)this.getRequest.invoke(null, new Object[0]);
        }
        catch (NullPointerException e) {
            return ServletContextThreadLocal.getRequest();
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ServletActionContext request", (Throwable)e);
        }
    }

    @Override
    public void setResponse(HttpServletResponse response) {
        try {
            this.setResponse.invoke(null, response);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ServletActionContext response", (Throwable)e);
        }
    }

    @Override
    public HttpServletResponse getResponse() {
        try {
            return (HttpServletResponse)this.getResponse.invoke(null, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ServletActionContext response", (Throwable)e);
        }
    }

    @Override
    public void setServletConfig(ServletConfig config) {
        try {
            if (sacClass.equals(STRUTS_2_SERVLET_ACTION_CONTEXT)) {
                this.setServletContext.invoke((Object)config.getServletContext(), new Object[0]);
            } else {
                this.setServletConfig.invoke(null, config);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ServletActionContext response", (Throwable)e);
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        if (sacClass.equals(STRUTS_2_SERVLET_ACTION_CONTEXT)) {
            return new ServletConfig(){

                public String getServletName() {
                    return null;
                }

                public ServletContext getServletContext() {
                    ServletContext context = null;
                    try {
                        context = (ServletContext)ServletActionContextStruts2AndWWCompat.this.getServletContext.invoke(null, new Object[0]);
                    }
                    catch (ReflectiveOperationException e) {
                        throw new ServiceException("Couldn't get ServletActionContext response", (Throwable)e);
                    }
                    return context;
                }

                public String getInitParameter(String s) {
                    return null;
                }

                public Enumeration<String> getInitParameterNames() {
                    return null;
                }
            };
        }
        try {
            return (ServletConfig)this.getServletConfig.invoke(null, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ServletActionContext response", (Throwable)e);
        }
    }

    private Method getSACStruts2Method(String methodName, ClassLoader classLoader, Class<?> ... parameterTypes) throws ReflectiveOperationException {
        return Class.forName(sacClass, false, classLoader).getMethod(methodName, parameterTypes);
    }
}

