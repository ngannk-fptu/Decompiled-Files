/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;

public interface ProxyMethodInvocation
extends MethodInvocation {
    public Object getProxy();

    public MethodInvocation invocableClone();

    public MethodInvocation invocableClone(Object ... var1);

    public void setArguments(Object ... var1);

    public void setUserAttribute(String var1, @Nullable Object var2);

    @Nullable
    public Object getUserAttribute(String var1);
}

