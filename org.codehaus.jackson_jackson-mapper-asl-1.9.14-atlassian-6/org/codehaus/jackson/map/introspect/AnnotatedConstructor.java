/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AnnotatedConstructor
extends AnnotatedWithParams {
    protected final Constructor<?> _constructor;

    public AnnotatedConstructor(Constructor<?> constructor, AnnotationMap classAnn, AnnotationMap[] paramAnn) {
        super(classAnn, paramAnn);
        if (constructor == null) {
            throw new IllegalArgumentException("Null constructor not allowed");
        }
        this._constructor = constructor;
    }

    @Override
    public AnnotatedConstructor withAnnotations(AnnotationMap ann) {
        return new AnnotatedConstructor(this._constructor, ann, this._paramAnnotations);
    }

    @Override
    public Constructor<?> getAnnotated() {
        return this._constructor;
    }

    @Override
    public int getModifiers() {
        return this._constructor.getModifiers();
    }

    @Override
    public String getName() {
        return this._constructor.getName();
    }

    @Override
    public Type getGenericType() {
        return this.getRawType();
    }

    @Override
    public Class<?> getRawType() {
        return this._constructor.getDeclaringClass();
    }

    @Override
    public JavaType getType(TypeBindings bindings) {
        return this.getType(bindings, this._constructor.getTypeParameters());
    }

    @Override
    public int getParameterCount() {
        return this._constructor.getParameterTypes().length;
    }

    @Override
    public Class<?> getParameterClass(int index) {
        Class<?>[] types = this._constructor.getParameterTypes();
        return index >= types.length ? null : types[index];
    }

    @Override
    public Type getParameterType(int index) {
        Type[] types = this._constructor.getGenericParameterTypes();
        return index >= types.length ? null : types[index];
    }

    @Override
    public final Object call() throws Exception {
        return this._constructor.newInstance(new Object[0]);
    }

    @Override
    public final Object call(Object[] args) throws Exception {
        return this._constructor.newInstance(args);
    }

    @Override
    public final Object call1(Object arg) throws Exception {
        return this._constructor.newInstance(arg);
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this._constructor.getDeclaringClass();
    }

    @Override
    public Member getMember() {
        return this._constructor;
    }

    @Override
    public void setValue(Object pojo, Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call setValue() on constructor of " + this.getDeclaringClass().getName());
    }

    public String toString() {
        return "[constructor for " + this.getName() + ", annotations: " + this._annotations + "]";
    }
}

