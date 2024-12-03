/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.runtime.MethodKey;

public class DefaultMethodKey
extends MethodKey {
    private final Class[] parameterTypes;

    public DefaultMethodKey(Class sender, String name, Class[] parameterTypes, boolean isCallToSuper) {
        super(sender, name, isCallToSuper);
        this.parameterTypes = parameterTypes;
    }

    @Override
    public int getParameterCount() {
        return this.parameterTypes.length;
    }

    @Override
    public Class getParameterType(int index) {
        Class c = this.parameterTypes[index];
        if (c == null) {
            return Object.class;
        }
        return c;
    }
}

