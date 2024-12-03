/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.Field;
import org.hibernate.property.access.internal.PropertyAccessMixedImpl;
import org.hibernate.property.access.spi.EnhancedSetterImpl;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

public class PropertyAccessEnhancedImpl
extends PropertyAccessMixedImpl {
    public PropertyAccessEnhancedImpl(PropertyAccessStrategy strategy, Class containerJavaType, String propertyName) {
        super(strategy, containerJavaType, propertyName);
    }

    @Override
    protected Setter fieldSetter(Class<?> containerJavaType, String propertyName, Field field) {
        return new EnhancedSetterImpl(containerJavaType, propertyName, field);
    }
}

