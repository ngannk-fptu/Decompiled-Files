/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import org.springframework.lang.Nullable;

public interface BeanInfoFactory {
    @Nullable
    public BeanInfo getBeanInfo(Class<?> var1) throws IntrospectionException;
}

