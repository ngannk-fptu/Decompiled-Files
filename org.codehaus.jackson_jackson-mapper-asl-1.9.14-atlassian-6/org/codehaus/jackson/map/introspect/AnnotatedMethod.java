/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AnnotatedMethod
extends AnnotatedWithParams {
    protected final Method _method;
    protected Class<?>[] _paramTypes;

    public AnnotatedMethod(Method method, AnnotationMap classAnn, AnnotationMap[] paramAnnotations) {
        super(classAnn, paramAnnotations);
        this._method = method;
    }

    public AnnotatedMethod withMethod(Method m) {
        return new AnnotatedMethod(m, this._annotations, this._paramAnnotations);
    }

    @Override
    public AnnotatedMethod withAnnotations(AnnotationMap ann) {
        return new AnnotatedMethod(this._method, ann, this._paramAnnotations);
    }

    @Override
    public Method getAnnotated() {
        return this._method;
    }

    @Override
    public int getModifiers() {
        return this._method.getModifiers();
    }

    @Override
    public String getName() {
        return this._method.getName();
    }

    @Override
    public Type getGenericType() {
        return this._method.getGenericReturnType();
    }

    @Override
    public Class<?> getRawType() {
        return this._method.getReturnType();
    }

    @Override
    public JavaType getType(TypeBindings bindings) {
        return this.getType(bindings, this._method.getTypeParameters());
    }

    @Override
    public final Object call() throws Exception {
        return this._method.invoke(null, new Object[0]);
    }

    @Override
    public final Object call(Object[] args) throws Exception {
        return this._method.invoke(null, args);
    }

    @Override
    public final Object call1(Object arg) throws Exception {
        return this._method.invoke(null, arg);
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this._method.getDeclaringClass();
    }

    @Override
    public Member getMember() {
        return this._method;
    }

    @Override
    public void setValue(Object pojo, Object value) throws IllegalArgumentException {
        try {
            this._method.invoke(pojo, value);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to setValue() with method " + this.getFullName() + ": " + e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to setValue() with method " + this.getFullName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getParameterCount() {
        return this.getParameterTypes().length;
    }

    public Type[] getParameterTypes() {
        return this._method.getGenericParameterTypes();
    }

    @Override
    public Class<?> getParameterClass(int index) {
        Class<?>[] types = this._method.getParameterTypes();
        return index >= types.length ? null : types[index];
    }

    @Override
    public Type getParameterType(int index) {
        Type[] types = this._method.getGenericParameterTypes();
        return index >= types.length ? null : types[index];
    }

    public Class<?>[] getParameterClasses() {
        if (this._paramTypes == null) {
            this._paramTypes = this._method.getParameterTypes();
        }
        return this._paramTypes;
    }

    public String getFullName() {
        return this.getDeclaringClass().getName() + "#" + this.getName() + "(" + this.getParameterCount() + " params)";
    }

    public String toString() {
        return "[method " + this.getName() + ", annotations: " + this._annotations + "]";
    }
}

