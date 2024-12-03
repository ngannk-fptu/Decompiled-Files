/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.AnnotatedField
 *  javax.enterprise.inject.spi.AnnotatedType
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AnnotatedMemberImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedFieldImpl<T>
extends AnnotatedMemberImpl<T>
implements AnnotatedField<T> {
    private Field javaMember;

    public AnnotatedFieldImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations, AnnotatedType<T> declaringType, Field javaMember, boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
        this.javaMember = javaMember;
    }

    public AnnotatedFieldImpl(AnnotatedField<? super T> field, AnnotatedType<T> declaringType) {
        this(field.getBaseType(), (Set<Type>)field.getTypeClosure(), (Set<Annotation>)field.getAnnotations(), declaringType, field.getJavaMember(), field.isStatic());
    }

    public AnnotatedFieldImpl(AnnotatedField<? super T> field, Set<Annotation> annotations, AnnotatedType<T> declaringType) {
        this(field.getBaseType(), (Set<Type>)field.getTypeClosure(), annotations, declaringType, field.getJavaMember(), field.isStatic());
    }

    @Override
    public Field getJavaMember() {
        return this.javaMember;
    }
}

