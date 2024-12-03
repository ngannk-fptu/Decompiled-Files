/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.Assert;

public class DefaultRemoteInvocationExecutor
implements RemoteInvocationExecutor {
    @Override
    public Object invoke(RemoteInvocation invocation, Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Assert.notNull((Object)invocation, "RemoteInvocation must not be null");
        Assert.notNull(targetObject, "Target object must not be null");
        return invocation.invoke(targetObject);
    }
}

