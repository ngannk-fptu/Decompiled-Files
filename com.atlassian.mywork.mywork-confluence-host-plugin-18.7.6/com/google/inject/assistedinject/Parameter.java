/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.assistedinject;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.util.$Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class Parameter {
    private final Type type;
    private final boolean isAssisted;
    private final Annotation bindingAnnotation;
    private final boolean isProvider;
    private volatile Provider<? extends Object> provider;

    public Parameter(Type type, Annotation[] annotations) {
        this.type = type;
        this.bindingAnnotation = this.getBindingAnnotation(annotations);
        this.isAssisted = this.hasAssistedAnnotation(annotations);
        this.isProvider = this.isProvider(type);
    }

    public boolean isProvidedByFactory() {
        return this.isAssisted;
    }

    public Type getType() {
        return this.type;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if (this.isAssisted) {
            result.append("@Assisted");
            result.append(" ");
        }
        if (this.bindingAnnotation != null) {
            result.append(((Object)this.bindingAnnotation).toString());
            result.append(" ");
        }
        result.append(this.type.toString());
        return result.toString();
    }

    private boolean hasAssistedAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (!annotation.annotationType().equals(Assisted.class)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getValue(Injector injector) {
        if (null == this.provider) {
            Parameter parameter = this;
            synchronized (parameter) {
                if (null == this.provider) {
                    this.provider = this.isProvider ? injector.getProvider(this.getBindingForType(this.getProvidedType(this.type))) : injector.getProvider(this.getPrimaryBindingKey());
                }
            }
        }
        return this.isProvider ? this.provider : this.provider.get();
    }

    public boolean isBound(Injector injector) {
        return this.isBound(injector, this.getPrimaryBindingKey()) || this.isBound(injector, this.fixAnnotations(this.getPrimaryBindingKey()));
    }

    private boolean isBound(Injector injector, Key<?> key) {
        try {
            return injector.getBinding(key) != null;
        }
        catch (ConfigurationException e) {
            return false;
        }
    }

    public Key<?> fixAnnotations(Key<?> key) {
        return key.getAnnotation() == null ? key : Key.get(key.getTypeLiteral(), key.getAnnotation().annotationType());
    }

    Key<?> getPrimaryBindingKey() {
        return this.isProvider ? this.getBindingForType(this.getProvidedType(this.type)) : this.getBindingForType(this.type);
    }

    private Type getProvidedType(Type type) {
        return ((ParameterizedType)type).getActualTypeArguments()[0];
    }

    private boolean isProvider(Type type) {
        return type instanceof ParameterizedType && ((ParameterizedType)type).getRawType() == Provider.class;
    }

    private Key<?> getBindingForType(Type type) {
        return this.bindingAnnotation != null ? Key.get(type, this.bindingAnnotation) : Key.get(type);
    }

    private Annotation getBindingAnnotation(Annotation[] annotations) {
        Annotation bindingAnnotation = null;
        for (Annotation annotation : annotations) {
            if (!Annotations.isBindingAnnotation(annotation.annotationType())) continue;
            $Preconditions.checkArgument(bindingAnnotation == null, "Parameter has multiple binding annotations: %s and %s", bindingAnnotation, annotation);
            bindingAnnotation = annotation;
        }
        return bindingAnnotation;
    }
}

