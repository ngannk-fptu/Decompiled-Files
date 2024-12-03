/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.spring.interceptor.spi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public interface ExportableMethodInvocation {
    public Method getMethod();

    public Object[] getArguments();

    public Object proceed() throws Throwable;

    public Object getThis();

    public AccessibleObject getStaticPart();
}

