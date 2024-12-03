/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.core.RingbufferStoreFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class RingbufferStoreConfig
implements IdentifiedDataSerializable {
    private boolean enabled = true;
    private String className;
    private String factoryClassName;
    private Properties properties = new Properties();
    private RingbufferStore storeImplementation;
    private RingbufferStoreFactory factoryImplementation;
    private transient RingbufferStoreConfigReadOnly readOnly;

    public RingbufferStoreConfig() {
    }

    public RingbufferStoreConfig(RingbufferStoreConfig config) {
        this.enabled = config.isEnabled();
        this.className = config.getClassName();
        this.storeImplementation = config.getStoreImplementation();
        this.factoryClassName = config.getFactoryClassName();
        this.factoryImplementation = config.getFactoryImplementation();
        this.properties.putAll((Map<?, ?>)config.getProperties());
    }

    public RingbufferStore getStoreImplementation() {
        return this.storeImplementation;
    }

    public RingbufferStoreConfig setStoreImplementation(RingbufferStore storeImplementation) {
        this.storeImplementation = storeImplementation;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public RingbufferStoreConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public RingbufferStoreConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public RingbufferStoreConfig setProperties(Properties properties) {
        this.properties = Preconditions.isNotNull(properties, "properties");
        return this;
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public RingbufferStoreConfig setProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public RingbufferStoreConfig setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
        return this;
    }

    public RingbufferStoreFactory getFactoryImplementation() {
        return this.factoryImplementation;
    }

    public RingbufferStoreConfig setFactoryImplementation(RingbufferStoreFactory factoryImplementation) {
        this.factoryImplementation = factoryImplementation;
        return this;
    }

    public String toString() {
        return "RingbufferStoreConfig{enabled=" + this.enabled + ", className='" + this.className + '\'' + ", properties=" + this.properties + '}';
    }

    public RingbufferStoreConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new RingbufferStoreConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 36;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.enabled);
        out.writeUTF(this.className);
        out.writeUTF(this.factoryClassName);
        out.writeObject(this.properties);
        out.writeObject(this.storeImplementation);
        out.writeObject(this.factoryImplementation);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.enabled = in.readBoolean();
        this.className = in.readUTF();
        this.factoryClassName = in.readUTF();
        this.properties = (Properties)in.readObject();
        this.storeImplementation = (RingbufferStore)in.readObject();
        this.factoryImplementation = (RingbufferStoreFactory)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RingbufferStoreConfig)) {
            return false;
        }
        RingbufferStoreConfig that = (RingbufferStoreConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.factoryClassName != null ? !this.factoryClassName.equals(that.factoryClassName) : that.factoryClassName != null) {
            return false;
        }
        if (this.properties != null ? !this.properties.equals(that.properties) : that.properties != null) {
            return false;
        }
        if (this.storeImplementation != null ? !this.storeImplementation.equals(that.storeImplementation) : that.storeImplementation != null) {
            return false;
        }
        return this.factoryImplementation != null ? this.factoryImplementation.equals(that.factoryImplementation) : that.factoryImplementation == null;
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.factoryClassName != null ? this.factoryClassName.hashCode() : 0);
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        result = 31 * result + (this.storeImplementation != null ? this.storeImplementation.hashCode() : 0);
        result = 31 * result + (this.factoryImplementation != null ? this.factoryImplementation.hashCode() : 0);
        return result;
    }

    static class RingbufferStoreConfigReadOnly
    extends RingbufferStoreConfig {
        RingbufferStoreConfigReadOnly(RingbufferStoreConfig config) {
            super(config);
        }

        @Override
        public RingbufferStoreConfig setStoreImplementation(RingbufferStore storeImplementation) {
            throw new UnsupportedOperationException("This config is read-only.");
        }

        @Override
        public RingbufferStoreConfig setEnabled(boolean enabled) {
            throw new UnsupportedOperationException("This config is read-only.");
        }

        @Override
        public RingbufferStoreConfig setClassName(String className) {
            throw new UnsupportedOperationException("This config is read-only.");
        }

        @Override
        public RingbufferStoreConfig setProperties(Properties properties) {
            throw new UnsupportedOperationException("This config is read-only.");
        }

        @Override
        public RingbufferStoreConfig setProperty(String name, String value) {
            throw new UnsupportedOperationException("This config is read-only.");
        }

        @Override
        public RingbufferStoreConfig setFactoryClassName(String factoryClassName) {
            throw new UnsupportedOperationException("This config is read-only.");
        }

        @Override
        public RingbufferStoreConfig setFactoryImplementation(RingbufferStoreFactory factoryImplementation) {
            throw new UnsupportedOperationException("This config is read-only.");
        }
    }
}

