/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.ws.rs.DefaultValue;

public class DiscoveredParameter {
    private Annotation annotation;
    private Type type;
    private DefaultValue defaultValue;
    private boolean encoded;

    public DiscoveredParameter(Annotation annotation, Type type, DefaultValue defaultValue, boolean encoded) {
        this.annotation = annotation;
        this.type = type;
        this.defaultValue = defaultValue;
        this.encoded = encoded;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public Type getType() {
        return this.type;
    }

    public DefaultValue getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isEncoded() {
        return this.encoded;
    }

    public String getValue() {
        try {
            Method valueMethod = this.annotation.annotationType().getDeclaredMethod("value", new Class[0]);
            String name = (String)valueMethod.invoke((Object)this.annotation, new Object[0]);
            return name;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.annotation == null ? 0 : this.annotation.hashCode());
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        result = 31 * result + (this.defaultValue == null ? 0 : this.defaultValue.hashCode());
        result = 31 * result + (this.encoded ? 7 : 11);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        DiscoveredParameter that = (DiscoveredParameter)obj;
        return (this.annotation == null ? that.annotation == null : this.annotation.equals(that.annotation)) && (this.type == null ? that.type == null : this.type.equals(that.type)) && (this.defaultValue == null ? that.defaultValue == null : this.defaultValue.equals(that.defaultValue)) && this.encoded == that.encoded;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DiscoveredParameter(");
        sb.append(this.annotation);
        sb.append(',');
        sb.append(this.type);
        sb.append(',');
        sb.append(this.defaultValue);
        sb.append(',');
        sb.append(this.encoded);
        sb.append(')');
        return sb.toString();
    }
}

