/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.api.databinding;

import java.lang.reflect.Method;

public interface JavaCallInfo {
    public Method getMethod();

    public Object[] getParameters();

    public Object getReturnValue();

    public void setReturnValue(Object var1);

    public Throwable getException();

    public void setException(Throwable var1);
}

