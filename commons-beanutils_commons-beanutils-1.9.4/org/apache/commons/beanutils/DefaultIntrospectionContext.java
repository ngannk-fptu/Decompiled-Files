/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.IntrospectionContext;

class DefaultIntrospectionContext
implements IntrospectionContext {
    private static final PropertyDescriptor[] EMPTY_DESCRIPTORS = new PropertyDescriptor[0];
    private final Class<?> currentClass;
    private final Map<String, PropertyDescriptor> descriptors;

    public DefaultIntrospectionContext(Class<?> cls) {
        this.currentClass = cls;
        this.descriptors = new HashMap<String, PropertyDescriptor>();
    }

    @Override
    public Class<?> getTargetClass() {
        return this.currentClass;
    }

    @Override
    public void addPropertyDescriptor(PropertyDescriptor desc) {
        if (desc == null) {
            throw new IllegalArgumentException("Property descriptor must not be null!");
        }
        this.descriptors.put(desc.getName(), desc);
    }

    @Override
    public void addPropertyDescriptors(PropertyDescriptor[] descs) {
        if (descs == null) {
            throw new IllegalArgumentException("Array with descriptors must not be null!");
        }
        for (PropertyDescriptor desc : descs) {
            this.addPropertyDescriptor(desc);
        }
    }

    @Override
    public boolean hasProperty(String name) {
        return this.descriptors.containsKey(name);
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String name) {
        return this.descriptors.get(name);
    }

    @Override
    public void removePropertyDescriptor(String name) {
        this.descriptors.remove(name);
    }

    @Override
    public Set<String> propertyNames() {
        return this.descriptors.keySet();
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.descriptors.values().toArray(EMPTY_DESCRIPTORS);
    }
}

