/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.util.profiling.object;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.object.ObjectProfiler;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Internal
public class TimerInvocationHandler
implements InvocationHandler,
Serializable {
    protected Object target;

    public TimerInvocationHandler(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Target Object passed to timer cannot be null");
        }
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return ObjectProfiler.profiledInvoke(method, this.target, args);
    }
}

