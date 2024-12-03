/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.TypeHelper;
import org.hibernate.type.BasicType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.CompositeUserType;

public class TypeLocatorImpl
implements TypeHelper,
Serializable {
    private final TypeResolver typeResolver;

    public TypeLocatorImpl(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    @Override
    public BasicType basic(String name) {
        return this.typeResolver.basic(name);
    }

    @Override
    public BasicType basic(Class javaType) {
        Class variant;
        BasicType type = this.typeResolver.basic(javaType.getName());
        if (type == null && (variant = this.resolvePrimitiveOrPrimitiveWrapperVariantJavaType(javaType)) != null) {
            type = this.typeResolver.basic(variant.getName());
        }
        return type;
    }

    private Class resolvePrimitiveOrPrimitiveWrapperVariantJavaType(Class javaType) {
        if (Boolean.TYPE.equals(javaType)) {
            return Boolean.class;
        }
        if (Boolean.class.equals((Object)javaType)) {
            return Boolean.TYPE;
        }
        if (Character.TYPE.equals(javaType)) {
            return Character.class;
        }
        if (Character.class.equals((Object)javaType)) {
            return Character.TYPE;
        }
        if (Byte.TYPE.equals(javaType)) {
            return Byte.class;
        }
        if (Byte.class.equals((Object)javaType)) {
            return Byte.TYPE;
        }
        if (Short.TYPE.equals(javaType)) {
            return Short.class;
        }
        if (Short.class.equals((Object)javaType)) {
            return Short.TYPE;
        }
        if (Integer.TYPE.equals(javaType)) {
            return Integer.class;
        }
        if (Integer.class.equals((Object)javaType)) {
            return Integer.TYPE;
        }
        if (Long.TYPE.equals(javaType)) {
            return Long.class;
        }
        if (Long.class.equals((Object)javaType)) {
            return Long.TYPE;
        }
        if (Float.TYPE.equals(javaType)) {
            return Float.class;
        }
        if (Float.class.equals((Object)javaType)) {
            return Float.TYPE;
        }
        if (Double.TYPE.equals(javaType)) {
            return Double.class;
        }
        if (Double.class.equals((Object)javaType)) {
            return Double.TYPE;
        }
        return null;
    }

    @Override
    public Type heuristicType(String name) {
        return this.typeResolver.heuristicType(name);
    }

    @Override
    public Type entity(Class entityClass) {
        return this.entity(entityClass.getName());
    }

    @Override
    public Type entity(String entityName) {
        return this.typeResolver.getTypeFactory().manyToOne(entityName);
    }

    @Override
    public Type custom(Class userTypeClass) {
        return this.custom(userTypeClass, null);
    }

    @Override
    public Type custom(Class userTypeClass, Properties parameters) {
        if (CompositeUserType.class.isAssignableFrom(userTypeClass)) {
            return this.typeResolver.getTypeFactory().customComponent(userTypeClass, parameters);
        }
        return this.typeResolver.getTypeFactory().custom(userTypeClass, parameters);
    }

    @Override
    public Type any(Type metaType, Type identifierType) {
        return this.typeResolver.getTypeFactory().any(metaType, identifierType);
    }
}

