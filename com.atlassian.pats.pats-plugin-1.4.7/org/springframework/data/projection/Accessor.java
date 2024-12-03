/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.util.Assert
 */
package org.springframework.data.projection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

public final class Accessor {
    private final PropertyDescriptor descriptor;
    private final Method method;

    public Accessor(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        PropertyDescriptor descriptor = BeanUtils.findPropertyForMethod((Method)method);
        if (descriptor == null) {
            throw new IllegalArgumentException(String.format("Invoked method %s is no accessor method!", method));
        }
        this.descriptor = descriptor;
        this.method = method;
    }

    public boolean isGetter() {
        return this.method.equals(this.descriptor.getReadMethod());
    }

    public boolean isSetter() {
        return this.method.equals(this.descriptor.getWriteMethod());
    }

    public String getPropertyName() {
        return this.descriptor.getName();
    }
}

