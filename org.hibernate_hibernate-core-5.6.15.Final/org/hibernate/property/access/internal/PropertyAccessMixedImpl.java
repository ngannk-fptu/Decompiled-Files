/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Access
 *  javax.persistence.AccessType
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.persistence.Access;
import javax.persistence.AccessType;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.GetterFieldImpl;
import org.hibernate.property.access.spi.GetterMethodImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessBuildingException;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.property.access.spi.SetterFieldImpl;
import org.hibernate.property.access.spi.SetterMethodImpl;

public class PropertyAccessMixedImpl
implements PropertyAccess {
    private final PropertyAccessStrategy strategy;
    private final Getter getter;
    private final Setter setter;

    public PropertyAccessMixedImpl(PropertyAccessStrategy strategy, Class containerJavaType, String propertyName) {
        this.strategy = strategy;
        AccessType propertyAccessType = PropertyAccessMixedImpl.getAccessType(containerJavaType, propertyName);
        switch (propertyAccessType) {
            case FIELD: {
                Field field = PropertyAccessMixedImpl.fieldOrNull(containerJavaType, propertyName);
                if (field == null) {
                    throw new PropertyAccessBuildingException("Could not locate field for property named [" + containerJavaType.getName() + "#" + propertyName + "]");
                }
                this.getter = this.fieldGetter(containerJavaType, propertyName, field);
                this.setter = this.fieldSetter(containerJavaType, propertyName, field);
                break;
            }
            case PROPERTY: {
                Method getterMethod = ReflectHelper.getterMethodOrNull(containerJavaType, propertyName);
                if (getterMethod == null) {
                    throw new PropertyAccessBuildingException("Could not locate getter for property named [" + containerJavaType.getName() + "#" + propertyName + "]");
                }
                Method setterMethod = ReflectHelper.findSetterMethod(containerJavaType, propertyName, getterMethod.getReturnType());
                this.getter = this.propertyGetter(containerJavaType, propertyName, getterMethod);
                this.setter = this.propertySetter(containerJavaType, propertyName, setterMethod);
                break;
            }
            default: {
                throw new PropertyAccessBuildingException("Invalid access type " + propertyAccessType + " for property named [" + containerJavaType.getName() + "#" + propertyName + "]");
            }
        }
    }

    protected static Field fieldOrNull(Class containerJavaType, String propertyName) {
        try {
            return ReflectHelper.findField(containerJavaType, propertyName);
        }
        catch (PropertyNotFoundException e) {
            return null;
        }
    }

    protected static AccessType getAccessType(Class<?> containerJavaType, String propertyName) {
        Field field = PropertyAccessMixedImpl.fieldOrNull(containerJavaType, propertyName);
        AccessType fieldAccessType = PropertyAccessMixedImpl.getAccessTypeOrNull(field);
        if (fieldAccessType != null) {
            return fieldAccessType;
        }
        AccessType methodAccessType = PropertyAccessMixedImpl.getAccessTypeOrNull(ReflectHelper.getterMethodOrNull(containerJavaType, propertyName));
        if (methodAccessType != null) {
            return methodAccessType;
        }
        AccessType classAccessType = PropertyAccessMixedImpl.getAccessTypeOrNull(containerJavaType);
        if (classAccessType != null) {
            return classAccessType;
        }
        return field != null ? AccessType.FIELD : AccessType.PROPERTY;
    }

    private static AccessType getAccessTypeOrNull(AnnotatedElement element) {
        if (element == null) {
            return null;
        }
        Access elementAccess = element.getAnnotation(Access.class);
        return elementAccess == null ? null : elementAccess.value();
    }

    protected Getter fieldGetter(Class<?> containerJavaType, String propertyName, Field field) {
        return new GetterFieldImpl(containerJavaType, propertyName, field);
    }

    protected Setter fieldSetter(Class<?> containerJavaType, String propertyName, Field field) {
        return new SetterFieldImpl(containerJavaType, propertyName, field);
    }

    protected Getter propertyGetter(Class<?> containerJavaType, String propertyName, Method method) {
        return new GetterMethodImpl(containerJavaType, propertyName, method);
    }

    protected Setter propertySetter(Class<?> containerJavaType, String propertyName, Method method) {
        return method == null ? null : new SetterMethodImpl(containerJavaType, propertyName, method);
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

