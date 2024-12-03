/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.Method;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.GetterMethodImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.property.access.spi.SetterMethodImpl;
import org.jboss.logging.Logger;

public class PropertyAccessBasicImpl
implements PropertyAccess {
    private static final Logger log = Logger.getLogger(PropertyAccessBasicImpl.class);
    private final PropertyAccessStrategyBasicImpl strategy;
    private final GetterMethodImpl getter;
    private final SetterMethodImpl setter;

    public PropertyAccessBasicImpl(PropertyAccessStrategyBasicImpl strategy, Class containerJavaType, String propertyName) {
        this.strategy = strategy;
        Method getterMethod = ReflectHelper.findGetterMethod(containerJavaType, propertyName);
        this.getter = new GetterMethodImpl(containerJavaType, propertyName, getterMethod);
        Method setterMethod = ReflectHelper.findSetterMethod(containerJavaType, propertyName, getterMethod.getReturnType());
        this.setter = new SetterMethodImpl(containerJavaType, propertyName, setterMethod);
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

