/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.velocity.runtime.log.Log;

public abstract class SetExecutor {
    protected Log log = null;
    private Method method = null;

    public abstract Object execute(Object var1, Object var2) throws IllegalAccessException, InvocationTargetException;

    public boolean isAlive() {
        return this.method != null;
    }

    public Method getMethod() {
        return this.method;
    }

    protected void setMethod(Method method) {
        this.method = method;
    }
}

