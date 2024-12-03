/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyValue
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.eclipse.gemini.blueprint.blueprint.reflect.ValueFactory;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Metadata;
import org.springframework.beans.PropertyValue;

class SimpleBeanProperty
implements BeanProperty {
    private final String name;
    private final Metadata value;

    public SimpleBeanProperty(String name, Metadata value) {
        this.name = name;
        this.value = value;
    }

    public SimpleBeanProperty(PropertyValue propertyValue) {
        this.name = propertyValue.getName();
        Object value = propertyValue.getValue();
        this.value = ValueFactory.buildValue(value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Metadata getValue() {
        return this.value;
    }
}

