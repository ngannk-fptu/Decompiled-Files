/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.tools.PointcutParameter;

public class PointcutParameterImpl
implements PointcutParameter {
    String name;
    Class type;
    Object binding;

    public PointcutParameterImpl(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class getType() {
        return this.type;
    }

    @Override
    public Object getBinding() {
        return this.binding;
    }

    void setBinding(Object boundValue) {
        this.binding = boundValue;
    }
}

