/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.beans.PropertyDescriptor;
import java.util.Set;

public interface IntrospectionContext {
    public Class<?> getTargetClass();

    public void addPropertyDescriptor(PropertyDescriptor var1);

    public void addPropertyDescriptors(PropertyDescriptor[] var1);

    public boolean hasProperty(String var1);

    public PropertyDescriptor getPropertyDescriptor(String var1);

    public void removePropertyDescriptor(String var1);

    public Set<String> propertyNames();
}

