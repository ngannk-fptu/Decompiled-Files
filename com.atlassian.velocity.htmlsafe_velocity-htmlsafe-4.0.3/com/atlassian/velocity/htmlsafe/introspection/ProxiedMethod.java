/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.velocity.util.introspection.VelMethod
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.google.common.base.Preconditions;
import org.apache.velocity.util.introspection.VelMethod;

final class ProxiedMethod
implements VelMethod {
    private final VelMethod delegateMethod;
    private final Object proxyObject;

    public ProxiedMethod(VelMethod delegateMethod, Object proxyObject) {
        this.delegateMethod = (VelMethod)Preconditions.checkNotNull((Object)delegateMethod, (Object)"delegateMethod must not be null");
        this.proxyObject = Preconditions.checkNotNull((Object)proxyObject, (Object)"proxyObject must not be null");
    }

    public Object invoke(Object o, Object[] params) throws Exception {
        return this.delegateMethod.invoke(this.proxyObject, params);
    }

    public boolean isCacheable() {
        return this.delegateMethod.isCacheable();
    }

    public String getMethodName() {
        return this.delegateMethod.getMethodName();
    }

    public Class getReturnType() {
        return this.delegateMethod.getReturnType();
    }
}

