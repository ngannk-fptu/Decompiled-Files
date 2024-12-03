/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.faces.context.ExternalContext
 *  javax.faces.context.FacesContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context.request;

import java.lang.reflect.Method;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.util.WebUtils;

public class FacesRequestAttributes
implements RequestAttributes {
    private static final Log logger = LogFactory.getLog(FacesRequestAttributes.class);
    private final FacesContext facesContext;

    public FacesRequestAttributes(FacesContext facesContext) {
        Assert.notNull((Object)facesContext, "FacesContext must not be null");
        this.facesContext = facesContext;
    }

    protected final FacesContext getFacesContext() {
        return this.facesContext;
    }

    protected final ExternalContext getExternalContext() {
        return this.getFacesContext().getExternalContext();
    }

    protected Map<String, Object> getAttributeMap(int scope) {
        if (scope == 0) {
            return this.getExternalContext().getRequestMap();
        }
        return this.getExternalContext().getSessionMap();
    }

    @Override
    public Object getAttribute(String name, int scope) {
        return this.getAttributeMap(scope).get(name);
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        this.getAttributeMap(scope).put(name, value);
    }

    @Override
    public void removeAttribute(String name, int scope) {
        this.getAttributeMap(scope).remove(name);
    }

    @Override
    public String[] getAttributeNames(int scope) {
        return StringUtils.toStringArray(this.getAttributeMap(scope).keySet());
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        if (logger.isWarnEnabled()) {
            logger.warn((Object)("Could not register destruction callback [" + callback + "] for attribute '" + name + "' because FacesRequestAttributes does not support such callbacks"));
        }
    }

    @Override
    public Object resolveReference(String key) {
        if ("request".equals(key)) {
            return this.getExternalContext().getRequest();
        }
        if ("session".equals(key)) {
            return this.getExternalContext().getSession(true);
        }
        if ("application".equals(key)) {
            return this.getExternalContext().getContext();
        }
        if ("requestScope".equals(key)) {
            return this.getExternalContext().getRequestMap();
        }
        if ("sessionScope".equals(key)) {
            return this.getExternalContext().getSessionMap();
        }
        if ("applicationScope".equals(key)) {
            return this.getExternalContext().getApplicationMap();
        }
        if ("facesContext".equals(key)) {
            return this.getFacesContext();
        }
        if ("cookie".equals(key)) {
            return this.getExternalContext().getRequestCookieMap();
        }
        if ("header".equals(key)) {
            return this.getExternalContext().getRequestHeaderMap();
        }
        if ("headerValues".equals(key)) {
            return this.getExternalContext().getRequestHeaderValuesMap();
        }
        if ("param".equals(key)) {
            return this.getExternalContext().getRequestParameterMap();
        }
        if ("paramValues".equals(key)) {
            return this.getExternalContext().getRequestParameterValuesMap();
        }
        if ("initParam".equals(key)) {
            return this.getExternalContext().getInitParameterMap();
        }
        if ("view".equals(key)) {
            return this.getFacesContext().getViewRoot();
        }
        if ("viewScope".equals(key)) {
            return this.getFacesContext().getViewRoot().getViewMap();
        }
        if ("flash".equals(key)) {
            return this.getExternalContext().getFlash();
        }
        if ("resource".equals(key)) {
            return this.getFacesContext().getApplication().getResourceHandler();
        }
        return null;
    }

    @Override
    public String getSessionId() {
        Object session = this.getExternalContext().getSession(true);
        try {
            Method getIdMethod = session.getClass().getMethod("getId", new Class[0]);
            return String.valueOf(ReflectionUtils.invokeMethod(getIdMethod, session));
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Session object [" + session + "] does not have a getId() method");
        }
    }

    @Override
    public Object getSessionMutex() {
        ExternalContext externalContext = this.getExternalContext();
        Object session = externalContext.getSession(true);
        Object mutex = externalContext.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = session != null ? session : externalContext;
        }
        return mutex;
    }
}

