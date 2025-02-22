/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.MapStoreConfigReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class MapStoreConfig
implements IdentifiedDataSerializable {
    public static final int DEFAULT_WRITE_DELAY_SECONDS = 0;
    public static final int DEFAULT_WRITE_BATCH_SIZE = 1;
    public static final boolean DEFAULT_WRITE_COALESCING = true;
    private boolean enabled = true;
    private boolean writeCoalescing = true;
    private String className;
    private String factoryClassName;
    private int writeDelaySeconds = 0;
    private int writeBatchSize = 1;
    private Object implementation;
    private Object factoryImplementation;
    private Properties properties = new Properties();
    private transient MapStoreConfigReadOnly readOnly;
    private InitialLoadMode initialLoadMode = InitialLoadMode.LAZY;

    public MapStoreConfig() {
    }

    public MapStoreConfig(MapStoreConfig config) {
        this.enabled = config.isEnabled();
        this.className = config.getClassName();
        this.implementation = config.getImplementation();
        this.factoryClassName = config.getFactoryClassName();
        this.factoryImplementation = config.getFactoryImplementation();
        this.writeDelaySeconds = config.getWriteDelaySeconds();
        this.writeBatchSize = config.getWriteBatchSize();
        this.initialLoadMode = config.getInitialLoadMode();
        this.writeCoalescing = config.isWriteCoalescing();
        this.properties.putAll((Map<?, ?>)config.getProperties());
    }

    public MapStoreConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MapStoreConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getClassName() {
        return this.className;
    }

    public MapStoreConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public MapStoreConfig setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
        return this;
    }

    public int getWriteDelaySeconds() {
        return this.writeDelaySeconds;
    }

    public MapStoreConfig setWriteDelaySeconds(int writeDelaySeconds) {
        this.writeDelaySeconds = writeDelaySeconds;
        return this;
    }

    public int getWriteBatchSize() {
        return this.writeBatchSize;
    }

    public MapStoreConfig setWriteBatchSize(int writeBatchSize) {
        if (writeBatchSize < 1) {
            throw new IllegalArgumentException("Write batch size should be at least 1");
        }
        this.writeBatchSize = writeBatchSize;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public MapStoreConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public MapStoreConfig setImplementation(Object implementation) {
        this.implementation = implementation;
        return this;
    }

    public Object getImplementation() {
        return this.implementation;
    }

    public MapStoreConfig setFactoryImplementation(Object factoryImplementation) {
        this.factoryImplementation = factoryImplementation;
        return this;
    }

    public Object getFactoryImplementation() {
        return this.factoryImplementation;
    }

    public MapStoreConfig setProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public MapStoreConfig setProperties(Properties properties) {
        this.properties = Preconditions.isNotNull(properties, "properties");
        return this;
    }

    public InitialLoadMode getInitialLoadMode() {
        return this.initialLoadMode;
    }

    public MapStoreConfig setInitialLoadMode(InitialLoadMode initialLoadMode) {
        this.initialLoadMode = initialLoadMode;
        return this;
    }

    public boolean isWriteCoalescing() {
        return this.writeCoalescing;
    }

    public MapStoreConfig setWriteCoalescing(boolean writeCoalescing) {
        this.writeCoalescing = writeCoalescing;
        return this;
    }

    public String toString() {
        return "MapStoreConfig{enabled=" + this.enabled + ", className='" + this.className + '\'' + ", factoryClassName='" + this.factoryClassName + '\'' + ", writeDelaySeconds=" + this.writeDelaySeconds + ", writeBatchSize=" + this.writeBatchSize + ", implementation=" + this.implementation + ", factoryImplementation=" + this.factoryImplementation + ", properties=" + this.properties + ", initialLoadMode=" + (Object)((Object)this.initialLoadMode) + ", writeCoalescing=" + this.writeCoalescing + '}';
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapStoreConfig)) {
            return false;
        }
        MapStoreConfig that = (MapStoreConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.writeCoalescing != that.writeCoalescing) {
            return false;
        }
        if (this.writeDelaySeconds != that.writeDelaySeconds) {
            return false;
        }
        if (this.writeBatchSize != that.writeBatchSize) {
            return false;
        }
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.factoryClassName != null ? !this.factoryClassName.equals(that.factoryClassName) : that.factoryClassName != null) {
            return false;
        }
        if (this.implementation != null ? !this.implementation.equals(that.implementation) : that.implementation != null) {
            return false;
        }
        if (this.factoryImplementation != null ? !this.factoryImplementation.equals(that.factoryImplementation) : that.factoryImplementation != null) {
            return false;
        }
        if (!this.properties.equals(that.properties)) {
            return false;
        }
        return this.initialLoadMode == that.initialLoadMode;
    }

    public final int hashCode() {
        int prime = 31;
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.writeCoalescing ? 1 : 0);
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.factoryClassName != null ? this.factoryClassName.hashCode() : 0);
        result = 31 * result + this.writeDelaySeconds;
        result = 31 * result + this.writeBatchSize;
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + (this.factoryImplementation != null ? this.factoryImplementation.hashCode() : 0);
        result = 31 * result + this.properties.hashCode();
        result = 31 * result + (this.initialLoadMode != null ? this.initialLoadMode.hashCode() : 0);
        return result;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 14;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.enabled);
        out.writeBoolean(this.writeCoalescing);
        out.writeUTF(this.className);
        out.writeUTF(this.factoryClassName);
        out.writeInt(this.writeDelaySeconds);
        out.writeInt(this.writeBatchSize);
        out.writeObject(this.implementation);
        out.writeObject(this.factoryImplementation);
        out.writeObject(this.properties);
        out.writeUTF(this.initialLoadMode.name());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.enabled = in.readBoolean();
        this.writeCoalescing = in.readBoolean();
        this.className = in.readUTF();
        this.factoryClassName = in.readUTF();
        this.writeDelaySeconds = in.readInt();
        this.writeBatchSize = in.readInt();
        this.implementation = in.readObject();
        this.factoryImplementation = in.readObject();
        this.properties = (Properties)in.readObject();
        this.initialLoadMode = InitialLoadMode.valueOf(in.readUTF());
    }

    public static enum InitialLoadMode {
        LAZY,
        EAGER;

    }
}

