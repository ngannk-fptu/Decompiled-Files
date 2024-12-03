/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
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
    private final Class<?> servletActionContextClass;
    private final Method setRequest;
    private final Method getRequest;
    private final Method setResponse;
    private final Method getResponse;
    private final Method getServletContext;
    private final Method setServletContext;
    private final Method getServletConfig;
    private final Method setServletConfig;

    ServletActionContextStruts2AndWWCompat(String sacClassName, ClassLoader classLoader) throws ReflectiveOperationException {
        this.servletActionContextClass = Class.forName(sacClassName, false, classLoader);
        this.setRequest = this.getSACStruts2Method("setRequest", HttpServletRequest.class);
        this.getRequest = this.getSACStruts2Method("getRequest", new Class[0]);
        this.setResponse = this.getSACStruts2Method("setResponse", HttpServletResponse.class);
        this.getResponse = this.getSACStruts2Method("getResponse", new Class[0]);
        if (sacClassName.equals("org.apache.struts2.ServletActionContext")) {
            this.getServletContext = this.getSACStruts2Method("getServletContext", new Class[0]);
            this.setServletContext = this.getSACStruts2Method("setServletContext", ServletContext.class);
            this.getServletConfig = null;
            this.setServletConfig = null;
        } else {
            this.getServletContext = null;
            this.setServletContext = null;
            this.getServletConfig = this.getSACStruts2Method("getServletConfig", new Class[0]);
            this.setServletConfig = this.getSACStruts2Method("setServletConfig", ServletConfig.class);
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
            return (HttpServletRequest)this.getRequest.invoke(null, new Object[0]);
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
            if (this.setServletContext != null) {
                this.setServletContext.invoke(null, config.getServletContext());
            } else {
                this.setServletConfig.invoke(null, config);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ServletConfig", (Throwable)e);
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        if (this.getServletContext != null) {
            return new ServletConfig(){

                public String getServletName() {
                    return null;
                }

                public ServletContext getServletContext() {
                    ServletContext context;
                    try {
                        context = (ServletContext)ServletActionContextStruts2AndWWCompat.this.getServletContext.invoke(null, new Object[0]);
                    }
                    catch (ReflectiveOperationException e) {
                        throw new ServiceException("Couldn't get ServletConfig", (Throwable)e);
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
            throw new ServiceException("Couldn't get ServletConfig", (Throwable)e);
        }
    }

    @Override
    public ServletContext getServletContext() {
        try {
            if (this.getServletContext != null) {
                return (ServletContext)this.getServletContext.invoke(null, new Object[0]);
            }
            ServletConfig config = (ServletConfig)this.getServletConfig.invoke(null, new Object[0]);
            return config != null ? config.getServletContext() : null;
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ServletContext", (Throwable)e);
        }
    }

    private Method getSACStruts2Method(String methodName, Class<?> ... parameterTypes) throws ReflectiveOperationException {
        return this.servletActionContextClass.getMethod(methodName, parameterTypes);
    }
}

