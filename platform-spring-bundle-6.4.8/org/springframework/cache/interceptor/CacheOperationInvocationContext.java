/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.BasicOperation;

public interface CacheOperationInvocationContext<O extends BasicOperation> {
    public O getOperation();

    public Object getTarget();

    public Method getMethod();

    public Object[] getArgs();
}

