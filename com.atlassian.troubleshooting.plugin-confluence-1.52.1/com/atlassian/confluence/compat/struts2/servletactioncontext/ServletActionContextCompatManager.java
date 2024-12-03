/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.component.Component
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  javax.servlet.ServletConfig
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.compat.struts2.servletactioncontext;

import aQute.bnd.annotation.component.Component;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.compat.struts2.servletactioncontext.ServletActionContextCompat;
import com.atlassian.confluence.compat.struts2.servletactioncontext.ServletActionContextStruts2AndWWCompat;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ServletActionContextCompatManager
implements ServletActionContextCompat {
    private static final Logger log = LoggerFactory.getLogger(ServletActionContextCompatManager.class);
    private final Supplier<ServletActionContextCompat> delegate = Suppliers.memoize(() -> this.initialiseServletActionContextCompat(classLoader));

    public ServletActionContextCompatManager() {
        this(ServletActionContextCompatManager.class.getClassLoader());
    }

    public ServletActionContextCompatManager(ClassLoader classLoader) {
    }

    private ServletActionContextCompat initialiseServletActionContextCompat(ClassLoader classLoader) {
        ServletActionContextStruts2AndWWCompat internalDelegate;
        try {
            Class.forName("org.apache.struts2.ServletActionContext", false, classLoader);
            internalDelegate = new ServletActionContextStruts2AndWWCompat("org.apache.struts2.ServletActionContext", classLoader);
        }
        catch (ClassNotFoundException e) {
            log.debug("Could not find struts2 ServletActionContext, falling back to webwork ServletActionContext", (Throwable)e);
            try {
                internalDelegate = new ServletActionContextStruts2AndWWCompat("com.opensymphony.webwork.ServletActionContext", classLoader);
            }
            catch (ReflectiveOperationException ex) {
                throw new ServiceException("ServletActionContext couldn't be initialized.", (Throwable)ex);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("ServletActionContext couldn't be initialized.", (Throwable)e);
        }
        return internalDelegate;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        try {
            ((ServletActionContextCompat)this.delegate.get()).setRequest(request);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the request");
        }
    }

    @Override
    public HttpServletRequest getRequest() {
        try {
            return ((ServletActionContextCompat)this.delegate.get()).getRequest();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the request");
            return null;
        }
    }

    @Override
    public void setResponse(HttpServletResponse response) {
        try {
            ((ServletActionContextCompat)this.delegate.get()).setResponse(response);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the response");
        }
    }

    @Override
    public HttpServletResponse getResponse() {
        try {
            return ((ServletActionContextCompat)this.delegate.get()).getResponse();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the response");
            return null;
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        try {
            return ((ServletActionContextCompat)this.delegate.get()).getServletConfig();
        }
        catch (NullPointerException ex) {
            log.error("Couldn't get the ServletConfig");
            return null;
        }
    }

    @Override
    public void setServletConfig(ServletConfig config) {
        try {
            ((ServletActionContextCompat)this.delegate.get()).setServletConfig(config);
        }
        catch (NullPointerException ex) {
            log.error("Couldn't set the ServletConfig");
        }
    }
}

