/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.AnnotatedMethod
 *  javax.enterprise.inject.spi.AnnotatedType
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AnnotatedCallableImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedMethodImpl<T>
extends AnnotatedCallableImpl<T>
implements AnnotatedMethod<T> {
    private Method javaMember;

    public AnnotatedMethodImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations, AnnotatedType<T> declaringType, Method javaMember, boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
        this.javaMember = javaMember;
    }

    public AnnotatedMethodImpl(AnnotatedMethod<? super T> method, AnnotatedType<T> declaringType) {
        this(method.getBaseType(), (Set<Type>)method.getTypeClosure(), (Set<Annotation>)method.getAnnotations(), declaringType, method.getJavaMember(), method.isStatic());
    }

    public AnnotatedMethodImpl(AnnotatedMethod<? super T> method, Set<Annotation> annotations, AnnotatedType<T> declaringType) {
        this(method.getBaseType(), (Set<Type>)method.getTypeClosure(), annotations, declaringType, method.getJavaMember(), method.isStatic());
    }

    @Override
    public Method getJavaMember() {
        return this.javaMember;
    }
}

