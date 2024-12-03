/*
 * Decompiled with CFR 0.152.
 */
package ognl.security;

import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;

public class UserMethod
implements PrivilegedExceptionAction<Object> {
    private final Object target;
    private final Method method;
    private final Object[] argsArray;

    public UserMethod(Object target, Method method, Object[] argsArray) {
        this.target = target;
        this.method = method;
        this.argsArray = argsArray;
    }

    @Override
    public Object run() throws Exception {
        return this.method.invoke(this.target, this.argsArray);
    }
}

