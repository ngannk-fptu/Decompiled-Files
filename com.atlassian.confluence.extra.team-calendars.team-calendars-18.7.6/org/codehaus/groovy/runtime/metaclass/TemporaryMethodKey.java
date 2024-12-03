/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.MethodKey;

public class TemporaryMethodKey
extends MethodKey {
    private final Object[] parameterValues;

    public TemporaryMethodKey(Class sender, String name, Object[] parameterValues, boolean isCallToSuper) {
        super(sender, name, isCallToSuper);
        if (parameterValues == null) {
            parameterValues = MetaClassHelper.EMPTY_ARRAY;
        }
        this.parameterValues = parameterValues;
    }

    @Override
    public int getParameterCount() {
        return this.parameterValues.length;
    }

    @Override
    public Class getParameterType(int index) {
        Class<?> value = this.parameterValues[index];
        if (value != null) {
            Class<?> type = value.getClass() == Class.class ? value : value.getClass();
            return type;
        }
        return Object.class;
    }
}

