/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.nio.serialization.Serializer;

public class SerializerConfig {
    private String className;
    private Serializer implementation;
    private Class typeClass;
    private String typeClassName;

    public SerializerConfig() {
    }

    public SerializerConfig(SerializerConfig serializerConfig) {
        this.className = serializerConfig.className;
        this.implementation = serializerConfig.implementation;
        this.typeClass = serializerConfig.typeClass;
        this.typeClassName = serializerConfig.typeClassName;
    }

    public String getClassName() {
        return this.className;
    }

    public SerializerConfig setClass(Class<? extends Serializer> clazz) {
        String className = clazz == null ? null : clazz.getName();
        return this.setClassName(className);
    }

    public SerializerConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Serializer getImplementation() {
        return this.implementation;
    }

    public SerializerConfig setImplementation(Serializer implementation) {
        this.implementation = implementation;
        return this;
    }

    public Class getTypeClass() {
        return this.typeClass;
    }

    public SerializerConfig setTypeClass(Class typeClass) {
        this.typeClass = typeClass;
        return this;
    }

    public String getTypeClassName() {
        return this.typeClassName;
    }

    public SerializerConfig setTypeClassName(String typeClassName) {
        this.typeClassName = typeClassName;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof SerializerConfig)) {
            return false;
        }
        SerializerConfig that = (SerializerConfig)o;
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.implementation != null ? !this.implementation.equals(that.implementation) : that.implementation != null) {
            return false;
        }
        if (this.typeClass != null ? !this.typeClass.equals(that.typeClass) : that.typeClass != null) {
            return false;
        }
        return this.typeClassName != null ? this.typeClassName.equals(that.typeClassName) : that.typeClassName == null;
    }

    public final int hashCode() {
        int result = this.className != null ? this.className.hashCode() : 0;
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + (this.typeClass != null ? this.typeClass.hashCode() : 0);
        result = 31 * result + (this.typeClassName != null ? this.typeClassName.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "SerializerConfig{className='" + this.className + '\'' + ", implementation=" + this.implementation + ", typeClass=" + this.typeClass + ", typeClassName='" + this.typeClassName + '\'' + '}';
    }
}

