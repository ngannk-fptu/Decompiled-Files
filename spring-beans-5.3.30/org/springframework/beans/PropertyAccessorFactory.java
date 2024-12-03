/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.DirectFieldAccessor;

public final class PropertyAccessorFactory {
    private PropertyAccessorFactory() {
    }

    public static BeanWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

    public static ConfigurablePropertyAccessor forDirectFieldAccess(Object target) {
        return new DirectFieldAccessor(target);
    }
}

