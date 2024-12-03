/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.AnnotatedConstructor
 *  javax.enterprise.inject.spi.AnnotatedField
 *  javax.enterprise.inject.spi.AnnotatedMethod
 *  javax.enterprise.inject.spi.AnnotatedType
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AnnotatedImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedTypeImpl<T>
extends AnnotatedImpl
implements AnnotatedType<T> {
    private Set<AnnotatedConstructor<T>> constructors;
    private Set<AnnotatedField<? super T>> fields;
    private Class<T> javaClass;
    private Set<AnnotatedMethod<? super T>> methods;

    public AnnotatedTypeImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations, Class<T> javaClass) {
        super(baseType, typeClosure, annotations);
        this.javaClass = javaClass;
    }

    public AnnotatedTypeImpl(AnnotatedType type) {
        this(type.getBaseType(), type.getTypeClosure(), type.getAnnotations(), type.getJavaClass());
    }

    public Set<AnnotatedConstructor<T>> getConstructors() {
        return this.constructors;
    }

    public void setConstructors(Set<AnnotatedConstructor<T>> constructors) {
        this.constructors = constructors;
    }

    public Set<AnnotatedField<? super T>> getFields() {
        return this.fields;
    }

    public void setFields(Set<AnnotatedField<? super T>> fields) {
        this.fields = fields;
    }

    public Class<T> getJavaClass() {
        return this.javaClass;
    }

    public Set<AnnotatedMethod<? super T>> getMethods() {
        return this.methods;
    }

    public void setMethods(Set<AnnotatedMethod<? super T>> methods) {
        this.methods = methods;
    }
}

