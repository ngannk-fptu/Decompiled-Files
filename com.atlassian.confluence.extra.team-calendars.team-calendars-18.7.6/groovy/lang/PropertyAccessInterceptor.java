/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Interceptor;

public interface PropertyAccessInterceptor
extends Interceptor {
    public Object beforeGet(Object var1, String var2);

    public void beforeSet(Object var1, String var2, Object var3);
}

