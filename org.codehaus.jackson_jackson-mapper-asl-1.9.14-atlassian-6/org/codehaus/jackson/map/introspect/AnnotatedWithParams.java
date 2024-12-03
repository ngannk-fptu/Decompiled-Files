/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AnnotatedWithParams
extends AnnotatedMember {
    protected final AnnotationMap[] _paramAnnotations;

    protected AnnotatedWithParams(AnnotationMap annotations, AnnotationMap[] paramAnnotations) {
        super(annotations);
        this._paramAnnotations = paramAnnotations;
    }

    public final void addOrOverride(Annotation a) {
        this._annotations.add(a);
    }

    public final void addOrOverrideParam(int paramIndex, Annotation a) {
        AnnotationMap old = this._paramAnnotations[paramIndex];
        if (old == null) {
            this._paramAnnotations[paramIndex] = old = new AnnotationMap();
        }
        old.add(a);
    }

    public final void addIfNotPresent(Annotation a) {
        this._annotations.addIfNotPresent(a);
    }

    protected AnnotatedParameter replaceParameterAnnotations(int index, AnnotationMap ann) {
        this._paramAnnotations[index] = ann;
        return this.getParameter(index);
    }

    protected JavaType getType(TypeBindings bindings, TypeVariable<?>[] typeParams) {
        if (typeParams != null && typeParams.length > 0) {
            bindings = bindings.childInstance();
            for (TypeVariable<?> var : typeParams) {
                String name = var.getName();
                bindings._addPlaceholder(name);
                Type lowerBound = var.getBounds()[0];
                JavaType type = lowerBound == null ? TypeFactory.unknownType() : bindings.resolveType(lowerBound);
                bindings.addBinding(var.getName(), type);
            }
        }
        return bindings.resolveType(this.getGenericType());
    }

    @Override
    public final <A extends Annotation> A getAnnotation(Class<A> acls) {
        return this._annotations.get(acls);
    }

    public final AnnotationMap getParameterAnnotations(int index) {
        if (this._paramAnnotations != null && index >= 0 && index <= this._paramAnnotations.length) {
            return this._paramAnnotations[index];
        }
        return null;
    }

    public final AnnotatedParameter getParameter(int index) {
        return new AnnotatedParameter(this, this.getParameterType(index), this._paramAnnotations[index], index);
    }

    public abstract int getParameterCount();

    public abstract Class<?> getParameterClass(int var1);

    public abstract Type getParameterType(int var1);

    public final JavaType resolveParameterType(int index, TypeBindings bindings) {
        return bindings.resolveType(this.getParameterType(index));
    }

    public final int getAnnotationCount() {
        return this._annotations.size();
    }

    public abstract Object call() throws Exception;

    public abstract Object call(Object[] var1) throws Exception;

    public abstract Object call1(Object var1) throws Exception;
}

