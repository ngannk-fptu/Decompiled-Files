/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;
import org.springframework.remoting.support.RemoteInvocation;

public interface RemoteInvocationExecutor {
    public Object invoke(RemoteInvocation var1, Object var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}

