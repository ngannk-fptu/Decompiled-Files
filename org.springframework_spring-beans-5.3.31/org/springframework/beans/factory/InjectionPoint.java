/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class InjectionPoint {
    @Nullable
    protected MethodParameter methodParameter;
    @Nullable
    protected Field field;
    @Nullable
    private volatile Annotation[] fieldAnnotations;

    public InjectionPoint(MethodParameter methodParameter) {
        Assert.notNull((Object)methodParameter, (String)"MethodParameter must not be null");
        this.methodParameter = methodParameter;
    }

    public InjectionPoint(Field field) {
        Assert.notNull((Object)field, (String)"Field must not be null");
        this.field = field;
    }

    protected InjectionPoint(InjectionPoint original) {
        this.methodParameter = original.methodParameter != null ? new MethodParameter(original.methodParameter) : null;
        this.field = original.field;
        this.fieldAnnotations = original.fieldAnnotations;
    }

    protected InjectionPoint() {
    }

    @Nullable
    public MethodParameter getMethodParameter() {
        return this.methodParameter;
    }

    @Nullable
    public Field getField() {
        return this.field;
    }

    protected final MethodParameter obtainMethodParameter() {
        Assert.state((this.methodParameter != null ? 1 : 0) != 0, (String)"Neither Field nor MethodParameter");
        return this.methodParameter;
    }

    public Annotation[] getAnnotations() {
        if (this.field != null) {
            Annotation[] fieldAnnotations = this.fieldAnnotations;
            if (fieldAnnotations == null) {
                fieldAnnotations = this.field.getAnnotations();
                this.fieldAnnotations = fieldAnnotations;
            }
            return fieldAnnotations;
        }
        return this.obtainMethodParameter().getParameterAnnotations();
    }

    @Nullable
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return (A)(this.field != null ? this.field.getAnnotation(annotationType) : this.obtainMethodParameter().getParameterAnnotation(annotationType));
    }

    public Class<?> getDeclaredType() {
        return this.field != null ? this.field.getType() : this.obtainMethodParameter().getParameterType();
    }

    public Member getMember() {
        return this.field != null ? this.field : this.obtainMethodParameter().getMember();
    }

    public AnnotatedElement getAnnotatedElement() {
        return this.field != null ? this.field : this.obtainMethodParameter().getAnnotatedElement();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        InjectionPoint otherPoint = (InjectionPoint)other;
        return ObjectUtils.nullSafeEquals((Object)this.field, (Object)otherPoint.field) && ObjectUtils.nullSafeEquals((Object)this.methodParameter, (Object)otherPoint.methodParameter);
    }

    public int hashCode() {
        return this.field != null ? this.field.hashCode() : ObjectUtils.nullSafeHashCode((Object)this.methodParameter);
    }

    public String toString() {
        return this.field != null ? "field '" + this.field.getName() + "'" : String.valueOf(this.methodParameter);
    }
}

