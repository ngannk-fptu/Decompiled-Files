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
    public static final String STRUTS_HTTP_PARAMETERS = "org.apache.struts2.dispatcher.HttpParameters";
    public static final String STRUTS_HTTP_PARAMETERS_BUILDER = "org.apache.struts2.dispatcher.HttpParameters$Builder";
    private final Class<?> actionContextClass;
    private final Object actionContextInstance;
    private final Class<?> strutsHttpParametersClass;
    private final Class<?> strutsHttpParametersBuilderClass;
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

    ActionContextStruts2AndWWCompat(String actionContextClassName, ClassLoader classLoader) throws ReflectiveOperationException {
        this.actionContextClass = Class.forName(actionContextClassName, false, classLoader);
        this.actionContextInstance = this.getACStruts2Method("getContext", new Class[0]).invoke(null, new Object[0]);
        this.setApplication = this.getACStruts2Method("setApplication", Map.class);
        this.getApplication = this.getACStruts2Method("getApplication", new Class[0]);
        this.setContextMap = this.getACStruts2Method("setContextMap", Map.class);
        this.getContextMap = this.getACStruts2Method("getContextMap", new Class[0]);
        this.setConversionErrors = this.getACStruts2Method("setConversionErrors", Map.class);
        this.getConversionErrors = this.getACStruts2Method("getConversionErrors", new Class[0]);
        this.setLocale = this.getACStruts2Method("setLocale", Locale.class);
        this.getLocale = this.getACStruts2Method("getLocale", new Class[0]);
        this.setName = this.getACStruts2Method("setName", String.class);
        this.getName = this.getACStruts2Method("getName", new Class[0]);
        if ("com.opensymphony.xwork2.ActionContext".equals(actionContextClassName)) {
            this.strutsHttpParametersClass = Class.forName(STRUTS_HTTP_PARAMETERS, false, classLoader);
            this.strutsHttpParametersBuilderClass = Class.forName(STRUTS_HTTP_PARAMETERS_BUILDER, false, classLoader);
            this.setParameters = this.getACStruts2Method("setParameters", this.strutsHttpParametersClass);
        } else {
            this.strutsHttpParametersClass = null;
            this.strutsHttpParametersBuilderClass = null;
            this.setParameters = this.getACStruts2Method("setParameters", Map.class);
        }
        this.getParameters = this.getACStruts2Method("getParameters", new Class[0]);
        this.setSession = this.getACStruts2Method("setSession", Map.class);
        this.getSession = this.getACStruts2Method("getSession", new Class[0]);
        this.get = this.getACStruts2Method("get", Object.class);
        this.put = this.getACStruts2Method("put", Object.class, Object.class);
    }

    @Override
    public void setApplication(Map application) {
        try {
            this.setApplication.invoke(this.actionContextInstance, application);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext application", (Throwable)e);
        }
    }

    @Override
    public Map getApplication() {
        try {
            return (Map)this.getApplication.invoke(this.actionContextInstance, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext application", (Throwable)e);
        }
    }

    @Override
    public void setContextMap(Map contextMap) {
        try {
            this.setContextMap.invoke(this.actionContextInstance, contextMap);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext contextmap", (Throwable)e);
        }
    }

    @Override
    public Map getContextMap() {
        try {
            return (Map)this.getContextMap.invoke(this.actionContextInstance, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext contextmap", (Throwable)e);
        }
    }

    @Override
    public void setConversionErrors(Map conversionErrors) {
        try {
            this.setConversionErrors.invoke(this.actionContextInstance, conversionErrors);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext conversionerrors", (Throwable)e);
        }
    }

    @Override
    public Map getConversionErrors() {
        try {
            return (Map)this.getConversionErrors.invoke(this.actionContextInstance, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext conversionerrors", (Throwable)e);
        }
    }

    @Override
    public void setLocale(Locale locale) {
        try {
            this.setLocale.invoke(this.actionContextInstance, locale);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext locale", (Throwable)e);
        }
    }

    @Override
    public Locale getLocale() {
        try {
            return (Locale)this.getLocale.invoke(this.actionContextInstance, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ActionContext locale", (Throwable)e);
        }
    }

    @Override
    public void setName(String name) {
        try {
            this.setName.invoke(this.actionContextInstance, name);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext name", (Throwable)e);
        }
    }

    @Override
    public String getName() {
        try {
            return (String)this.getName.invoke(this.actionContextInstance, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ActionContext name", (Throwable)e);
        }
    }

    @Override
    public void setParameters(Map parameters) {
        try {
            if (this.strutsHttpParametersClass != null) {
                Method buildMethod = this.strutsHttpParametersBuilderClass.getMethod("build", new Class[0]);
                Object strutsHttpParametersBuilder = this.strutsHttpParametersClass.getMethod("create", Map.class).invoke(null, parameters);
                Object strutsHttpParametersMap = buildMethod.invoke(strutsHttpParametersBuilder, new Object[0]);
                this.setParameters.invoke(this.actionContextInstance, strutsHttpParametersMap);
            } else {
                this.setParameters.invoke(this.actionContextInstance, parameters);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext parameter", (Throwable)e);
        }
    }

    @Override
    public Map getParameters() {
        try {
            if (this.strutsHttpParametersClass != null) {
                Object obj = this.getParameters.invoke(this.actionContextInstance, new Object[0]);
                Method toMapMethod = this.strutsHttpParametersClass.getMethod("toMap", new Class[0]);
                return (Map)toMapMethod.invoke(obj, new Object[0]);
            }
            return (Map)this.getParameters.invoke(this.actionContextInstance, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't get ActionContext parameter", (Throwable)e);
        }
    }

    @Override
    public void setSession(Map session) {
        try {
            this.setSession.invoke(this.actionContextInstance, session);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext session", (Throwable)e);
        }
    }

    @Override
    public Map getSession() {
        try {
            return (Map)this.getSession.invoke(this.actionContextInstance, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext", (Throwable)e);
        }
    }

    @Override
    public Object get(Object key) {
        try {
            return this.get.invoke(this.actionContextInstance, key);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext value for given key", (Throwable)e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        try {
            this.put.invoke(this.actionContextInstance, key, value);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException("Couldn't set ActionContext key and value", (Throwable)e);
        }
    }

    private Method getACStruts2Method(String methodName, Class<?> ... parameterTypes) throws ReflectiveOperationException {
        return this.actionContextClass.getMethod(methodName, parameterTypes);
    }
}

