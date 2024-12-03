/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.runtime.MethodKey;

public class DefaultCachedMethodKey
extends MethodKey {
    private final CachedClass[] parameterTypes;

    public DefaultCachedMethodKey(Class sender, String name, CachedClass[] parameterTypes, boolean isCallToSuper) {
        super(sender, name, isCallToSuper);
        this.parameterTypes = parameterTypes;
    }

    @Override
    public int getParameterCount() {
        return this.parameterTypes.length;
    }

    @Override
    public Class getParameterType(int index) {
        CachedClass c = this.parameterTypes[index];
        if (c == null) {
            return Object.class;
        }
        return c.getTheClass();
    }
}

