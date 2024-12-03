/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.AnnotatedCallable
 *  javax.enterprise.inject.spi.AnnotatedParameter
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AnnotatedImpl;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

public class AnnotatedParameterImpl<T>
extends AnnotatedImpl
implements AnnotatedParameter<T> {
    private AnnotatedCallable<T> declaringCallable;
    private int position;

    public AnnotatedParameterImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations, AnnotatedCallable<T> declaringCallable, int position) {
        super(baseType, typeClosure, annotations);
        this.declaringCallable = declaringCallable;
        this.position = position;
    }

    public AnnotatedParameterImpl(AnnotatedParameter<? super T> param, AnnotatedCallable<T> declaringCallable) {
        this(param.getBaseType(), param.getTypeClosure(), param.getAnnotations(), declaringCallable, param.getPosition());
    }

    public AnnotatedParameterImpl(AnnotatedParameter<? super T> param, Set<Annotation> annotations, AnnotatedCallable<T> declaringCallable) {
        this(param.getBaseType(), param.getTypeClosure(), annotations, declaringCallable, param.getPosition());
    }

    public AnnotatedCallable<T> getDeclaringCallable() {
        return this.declaringCallable;
    }

    public int getPosition() {
        return this.position;
    }
}

