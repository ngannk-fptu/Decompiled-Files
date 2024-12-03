/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import org.springframework.lang.Nullable;

public interface AopProxy {
    public Object getProxy();

    public Object getProxy(@Nullable ClassLoader var1);
}

