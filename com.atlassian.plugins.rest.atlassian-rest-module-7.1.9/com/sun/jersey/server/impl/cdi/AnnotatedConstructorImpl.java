/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.AnnotatedConstructor
 *  javax.enterprise.inject.spi.AnnotatedType
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AnnotatedCallableImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedConstructorImpl<T>
extends AnnotatedCallableImpl<T>
implements AnnotatedConstructor<T> {
    private Constructor<T> javaMember;

    public AnnotatedConstructorImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations, AnnotatedType<T> declaringType, Constructor javaMember, boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
        this.javaMember = javaMember;
    }

    public AnnotatedConstructorImpl(AnnotatedConstructor<T> constructor, AnnotatedType<T> declaringType) {
        this(constructor.getBaseType(), (Set<Type>)constructor.getTypeClosure(), (Set<Annotation>)constructor.getAnnotations(), declaringType, constructor.getJavaMember(), constructor.isStatic());
    }

    @Override
    public Constructor<T> getJavaMember() {
        return this.javaMember;
    }
}

