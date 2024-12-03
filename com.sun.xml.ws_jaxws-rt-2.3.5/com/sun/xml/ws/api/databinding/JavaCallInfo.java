/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.databinding;

import java.lang.reflect.Method;

public class JavaCallInfo
implements com.oracle.webservices.api.databinding.JavaCallInfo {
    private Method method;
    private Object[] parameters;
    private Object returnValue;
    private Throwable exception;

    public JavaCallInfo() {
    }

    public JavaCallInfo(Method m, Object[] args) {
        this.method = m;
        this.parameters = args;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public Object[] getParameters() {
        return this.parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public Object getReturnValue() {
        return this.returnValue;
    }

    @Override
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public Throwable getException() {
        return this.exception;
    }

    @Override
    public void setException(Throwable exception) {
        this.exception = exception;
    }
}

