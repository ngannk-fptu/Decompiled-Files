/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.IntrospectionContext;

public class SuppressPropertiesBeanIntrospector
implements BeanIntrospector {
    public static final SuppressPropertiesBeanIntrospector SUPPRESS_CLASS = new SuppressPropertiesBeanIntrospector(Collections.singleton("class"));
    private final Set<String> propertyNames;

    public SuppressPropertiesBeanIntrospector(Collection<String> propertiesToSuppress) {
        if (propertiesToSuppress == null) {
            throw new IllegalArgumentException("Property names must not be null!");
        }
        this.propertyNames = Collections.unmodifiableSet(new HashSet<String>(propertiesToSuppress));
    }

    public Set<String> getSuppressedProperties() {
        return this.propertyNames;
    }

    @Override
    public void introspect(IntrospectionContext icontext) throws IntrospectionException {
        for (String property : this.getSuppressedProperties()) {
            icontext.removePropertyDescriptor(property);
        }
    }
}

