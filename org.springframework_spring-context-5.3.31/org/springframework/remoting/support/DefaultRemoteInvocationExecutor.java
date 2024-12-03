/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
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
        Assert.notNull((Object)invocation, (String)"RemoteInvocation must not be null");
        Assert.notNull((Object)targetObject, (String)"Target object must not be null");
        return invocation.invoke(targetObject);
    }
}

