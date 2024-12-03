/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.Type;
import java.util.Collection;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXType;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

final class JavaXSimpleType
extends JavaXType {
    public JavaXSimpleType(Type type, TypeEnvironment context, JavaReflectionManager factory) {
        super(type, context, factory);
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public XClass getElementClass() {
        return this.toXClass(this.approximate());
    }

    @Override
    public XClass getClassOrElementClass() {
        return this.getElementClass();
    }

    @Override
    public Class<? extends Collection> getCollectionClass() {
        return null;
    }

    @Override
    public XClass getType() {
        return this.toXClass(this.approximate());
    }

    @Override
    public XClass getMapKey() {
        return null;
    }
}

