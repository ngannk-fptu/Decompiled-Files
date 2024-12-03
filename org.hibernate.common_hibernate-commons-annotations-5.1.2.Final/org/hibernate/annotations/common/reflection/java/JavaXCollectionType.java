/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXType;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeSwitch;
import org.hibernate.annotations.common.reflection.java.generics.TypeUtils;

final class JavaXCollectionType
extends JavaXType {
    public JavaXCollectionType(Type type, TypeEnvironment context, JavaReflectionManager factory) {
        super(type, context, factory);
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public XClass getElementClass() {
        return (XClass)new TypeSwitch<XClass>(){

            @Override
            public XClass caseParameterizedType(ParameterizedType parameterizedType) {
                Type[] args = parameterizedType.getActualTypeArguments();
                Class<? extends Collection> collectionClass = JavaXCollectionType.this.getCollectionClass();
                Type componentType = Map.class.isAssignableFrom(collectionClass) || SortedMap.class.isAssignableFrom(collectionClass) ? args[1] : args[0];
                return JavaXCollectionType.this.toXClass(componentType);
            }
        }.doSwitch(this.approximate());
    }

    @Override
    public XClass getMapKey() {
        return (XClass)new TypeSwitch<XClass>(){

            @Override
            public XClass caseParameterizedType(ParameterizedType parameterizedType) {
                if (Map.class.isAssignableFrom(JavaXCollectionType.this.getCollectionClass())) {
                    return JavaXCollectionType.this.toXClass(parameterizedType.getActualTypeArguments()[0]);
                }
                return null;
            }
        }.doSwitch(this.approximate());
    }

    @Override
    public XClass getClassOrElementClass() {
        return this.toXClass(this.approximate());
    }

    @Override
    public Class<? extends Collection> getCollectionClass() {
        return TypeUtils.getCollectionClass(this.approximate());
    }

    @Override
    public XClass getType() {
        return this.toXClass(this.approximate());
    }
}

