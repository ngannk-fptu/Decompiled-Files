/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.interceptor;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.lang.reflect.InvocationTargetException;

public interface MethodInvocation {
    public Object getResource();

    public HttpContext getHttpContext();

    public AbstractResourceMethod getMethod();

    public Object[] getParameters();

    public void invoke() throws IllegalAccessException, InvocationTargetException;
}

