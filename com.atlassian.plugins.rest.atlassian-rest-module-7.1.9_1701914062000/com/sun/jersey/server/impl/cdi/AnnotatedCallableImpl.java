/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.AnnotatedCallable
 *  javax.enterprise.inject.spi.AnnotatedParameter
 *  javax.enterprise.inject.spi.AnnotatedType
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AnnotatedMemberImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedCallableImpl<T>
extends AnnotatedMemberImpl<T>
implements AnnotatedCallable<T> {
    private List<AnnotatedParameter<T>> parameters;

    public AnnotatedCallableImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations, AnnotatedType<T> declaringType, Member javaMember, boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
    }

    public List<AnnotatedParameter<T>> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<AnnotatedParameter<T>> parameters) {
        this.parameters = parameters;
    }
}

