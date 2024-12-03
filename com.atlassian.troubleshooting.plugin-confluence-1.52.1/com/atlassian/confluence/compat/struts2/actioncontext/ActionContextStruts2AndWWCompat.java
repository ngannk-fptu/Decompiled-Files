/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 */
package com.atlassian.confluence.compat.struts2.actioncontext;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.compat.struts2.actioncontext.ActionContextCompat;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

class ActionContextStruts2AndWWCompat
implements ActionContextCompat {
    private final Method getContext;
    private final Method setApplication;
    private final Method getApplication;
    private final Method setContextMap;
    private final Method getContextMap;
    private final Method setConversionErrors;
    private final Method getConversionErrors;
    private final Method setLocale;
    private final Method getLocale;
    private final Method setName;
    private final Method getName;
    private final Method setParameters;
    private final Method getParameters;
    private final Method setSession;
    private final Method getSession;
    private final Method get;
    private final Method put;
    private final Object context;
    private static String actionContextClass;

    ActionContextStruts2AndWWCompat(String actionContextClass, ClassLoader classLoader) throws ReflectiveOperationException {
        ActionContextStruts2AndWWCompat.actionContextClass = actionContextClass;
        this.getContext = this.getACStruts2Method("getContext", classLoader, new Class[0]);
        this.setApplication = this.getACStruts2Method("setApplication", classLoader, Map.class);
        this.getApplication = this.getACStruts2Method("getApplication", classLoader, new Class[0]);
        this.setContextMap = this.getACStruts2Method("setContextMap", classLoader, Map.class);
        this.getContextMap = this.getACStruts2Method("getContextMap", classLoader, new Class[0]);
        this.setConversionErrors = this.getACStruts2Method("setConversionErrors", classLoader, Map.class);
        this.getConversionErrors = this.getACStruts2Method("getConversionErrors", classLoader, new Class[0]);
        this.setLocale = this.getACStruts2Method("setLocale", classLoader, Locale.class);
        this.getLocale = this.getACStruts2Method("getLocale", classLoader, new Class[0]);
        this.setName = this.getACStruts2Method("setName", classLoader, String.class);
        this.getName = this.getACStruts2Method("getName", classLoader, new Class[0]);
        this.setParameters = this.getACStruts2Method("setParameters", classLoader, Map.class);
        this.getParameters = this.getACStruts2Method("getParameters", classLoader, new Class[0]);
        this.setSession = this.getACStruts2Method("setSession", classLoader, Map.class);
        this.getSession = this.getACStruts2Method("getSession", classLoader, new Class[0]);
        this.get = this.getACStruts2Method("get", classLoader, Object.class);
        this.put = this.getACStruts2Method("put", classLoader, Object.class, Object.class);
        this.context = this.getContext.invoke(null, new Object[0]);
    }

    @Override
    public void setApplication(Map application) {
        try {
            this.setApplication.invoke(Class.forName(actionContextClass).cast(this.context), application);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext application", (Throwable)e);
        }
    }

    @Override
    public Map getApplication() {
        try {
            return (Map)this.getApplication.invoke(Class.forName(actionContextClass).cast(this.context), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext application", (Throwable)e);
        }
    }

    @Override
    public void setContextMap(Map contextMap) {
        try {
            this.setContextMap.invoke(Class.forName(actionContextClass).cast(this.context), contextMap);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext contextmap", (Throwable)e);
        }
    }

    @Override
    public Map getContextMap() {
        try {
            return (Map)this.getContextMap.invoke(Class.forName(actionContextClass).cast(this.context), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext contextmap", (Throwable)e);
        }
    }

    @Override
    public void setConversionErrors(Map conversionErrors) {
        try {
            this.setConversionErrors.invoke(Class.forName(actionContextClass).cast(this.context), conversionErrors);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext conversionerrors", (Throwable)e);
        }
    }

    @Override
    public Map getConversionErrors() {
        try {
            return (Map)this.getConversionErrors.invoke(Class.forName(actionContextClass).cast(this.context), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext conversionerrors", (Throwable)e);
        }
    }

    @Override
    public void setLocale(Locale locale) {
        try {
            this.setLocale.invoke(Class.forName(actionContextClass).cast(this.context), locale);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext locale", (Throwable)e);
        }
    }

    @Override
    public Locale getLocale() {
        try {
            return (Locale)this.getLocale.invoke(Class.forName(actionContextClass).cast(this.context), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext locale", (Throwable)e);
        }
    }

    @Override
    public void setName(String name) {
        try {
            this.setName.invoke(Class.forName(actionContextClass).cast(this.context), name);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext name", (Throwable)e);
        }
    }

    @Override
    public String getName() {
        try {
            return (String)this.getName.invoke(Class.forName(actionContextClass).cast(this.context), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext name", (Throwable)e);
        }
    }

    @Override
    public void setParameters(Map parameters) {
        try {
            this.setParameters.invoke(Class.forName(actionContextClass).cast(this.context), parameters);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext parameter", (Throwable)e);
        }
    }

    @Override
    public Map getParameters() {
        try {
            return (Map)this.getParameters.invoke(Class.forName(actionContextClass).cast(this.context), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext parameter", (Throwable)e);
        }
    }

    @Override
    public void setSession(Map session) {
        try {
            this.setSession.invoke(Class.forName(actionContextClass).cast(this.context), session);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext session", (Throwable)e);
        }
    }

    @Override
    public Map getSession() {
        try {
            return (Map)this.getSession.invoke(Class.forName(actionContextClass).cast(this.context), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext", (Throwable)e);
        }
    }

    @Override
    public Object get(Object key) {
        try {
            return this.get.invoke(Class.forName(actionContextClass).cast(this.context), key);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext value for given key", (Throwable)e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        try {
            this.put.invoke(Class.forName(actionContextClass).cast(this.context), key, value);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext key and value", (Throwable)e);
        }
    }

    private Method getACStruts2Method(String methodName, ClassLoader classLoader, Class<?> ... parameterTypes) throws ReflectiveOperationException {
        return Class.forName(actionContextClass, false, classLoader).getMethod(methodName, parameterTypes);
    }
}

