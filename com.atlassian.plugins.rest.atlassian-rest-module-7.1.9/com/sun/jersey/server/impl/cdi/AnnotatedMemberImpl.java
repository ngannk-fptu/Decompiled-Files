/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.AnnotatedMember
 *  javax.enterprise.inject.spi.AnnotatedType
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AnnotatedImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedMemberImpl<T>
extends AnnotatedImpl
implements AnnotatedMember<T> {
    private AnnotatedType<T> declaringType;
    private Member javaMember;
    private boolean isStatic;

    public AnnotatedMemberImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations, AnnotatedType<T> declaringType, Member javaMember, boolean isStatic) {
        super(baseType, typeClosure, annotations);
        this.declaringType = declaringType;
        this.javaMember = javaMember;
        this.isStatic = isStatic;
    }

    public AnnotatedType<T> getDeclaringType() {
        return this.declaringType;
    }

    public Member getJavaMember() {
        return this.javaMember;
    }

    public boolean isStatic() {
        return this.isStatic;
    }
}

