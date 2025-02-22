/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.nio.serialization.Serializer;

public class GlobalSerializerConfig {
    private String className;
    private Serializer implementation;
    private boolean overrideJavaSerialization;

    public GlobalSerializerConfig() {
    }

    public GlobalSerializerConfig(GlobalSerializerConfig globalSerializerConfig) {
        this.className = globalSerializerConfig.className;
        this.implementation = globalSerializerConfig.implementation;
        this.overrideJavaSerialization = globalSerializerConfig.overrideJavaSerialization;
    }

    public String getClassName() {
        return this.className;
    }

    public GlobalSerializerConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Serializer getImplementation() {
        return this.implementation;
    }

    public GlobalSerializerConfig setImplementation(Serializer implementation) {
        this.implementation = implementation;
        return this;
    }

    public boolean isOverrideJavaSerialization() {
        return this.overrideJavaSerialization;
    }

    public GlobalSerializerConfig setOverrideJavaSerialization(boolean overrideJavaSerialization) {
        this.overrideJavaSerialization = overrideJavaSerialization;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof GlobalSerializerConfig)) {
            return false;
        }
        GlobalSerializerConfig that = (GlobalSerializerConfig)o;
        if (this.overrideJavaSerialization != that.overrideJavaSerialization) {
            return false;
        }
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        return this.implementation != null ? this.implementation.equals(that.implementation) : that.implementation == null;
    }

    public final int hashCode() {
        int result = this.className != null ? this.className.hashCode() : 0;
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + (this.overrideJavaSerialization ? 1 : 0);
        return result;
    }

    public String toString() {
        return "GlobalSerializerConfig{className='" + this.className + '\'' + ", implementation=" + this.implementation + ", overrideJavaSerialization=" + this.overrideJavaSerialization + '}';
    }
}

