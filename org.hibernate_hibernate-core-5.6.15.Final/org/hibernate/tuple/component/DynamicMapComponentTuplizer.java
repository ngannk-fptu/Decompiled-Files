/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import java.util.Map;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.tuple.DynamicMapInstantiator;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.component.AbstractComponentTuplizer;

public class DynamicMapComponentTuplizer
extends AbstractComponentTuplizer {
    @Override
    public Class getMappedClass() {
        return Map.class;
    }

    @Override
    protected Instantiator buildInstantiator(Component component) {
        return new DynamicMapInstantiator();
    }

    public DynamicMapComponentTuplizer(Component component) {
        super(component);
    }

    @Override
    protected Getter buildGetter(Component component, Property prop) {
        return PropertyAccessStrategyMapImpl.INSTANCE.buildPropertyAccess(null, prop.getName()).getGetter();
    }

    @Override
    protected Setter buildSetter(Component component, Property prop) {
        return PropertyAccessStrategyMapImpl.INSTANCE.buildPropertyAccess(null, prop.getName()).getSetter();
    }
}

