/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.Field;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.GetterFieldImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.property.access.spi.SetterFieldImpl;

public class PropertyAccessFieldImpl
implements PropertyAccess {
    private final PropertyAccessStrategyFieldImpl strategy;
    private final Getter getter;
    private final Setter setter;

    public PropertyAccessFieldImpl(PropertyAccessStrategyFieldImpl strategy, Class containerJavaType, String propertyName) {
        this.strategy = strategy;
        Field field = ReflectHelper.findField(containerJavaType, propertyName);
        this.getter = new GetterFieldImpl(containerJavaType, propertyName, field);
        this.setter = new SetterFieldImpl(containerJavaType, propertyName, field);
    }

    @Override
    public PropertyAccessStrategy getPropertyAccessStrategy() {
        return this.strategy;
    }

    @Override
    public Getter getGetter() {
        return this.getter;
    }

    @Override
    public Setter getSetter() {
        return this.setter;
    }
}

