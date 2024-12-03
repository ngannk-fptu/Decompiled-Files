/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.QueueStoreConfigReadOnly;
import com.hazelcast.core.QueueStore;
import com.hazelcast.core.QueueStoreFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class QueueStoreConfig
implements IdentifiedDataSerializable {
    private boolean enabled = true;
    private String className;
    private String factoryClassName;
    private Properties properties = new Properties();
    private QueueStore storeImplementation;
    private QueueStoreFactory factoryImplementation;
    private transient QueueStoreConfigReadOnly readOnly;

    public QueueStoreConfig() {
    }

    public QueueStoreConfig(QueueStoreConfig config) {
        this.enabled = config.isEnabled();
        this.className = config.getClassName();
        this.storeImplementation = config.getStoreImplementation();
        this.factoryClassName = config.getFactoryClassName();
        this.factoryImplementation = config.getFactoryImplementation();
        this.properties.putAll((Map<?, ?>)config.getProperties());
    }

    public QueueStore getStoreImplementation() {
        return this.storeImplementation;
    }

    public QueueStoreConfig setStoreImplementation(QueueStore storeImplementation) {
        this.storeImplementation = storeImplementation;
        return this;
    }

    public QueueStoreConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new QueueStoreConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public QueueStoreConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public QueueStoreConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public QueueStoreConfig setProperties(Properties properties) {
        this.properties = Preconditions.isNotNull(properties, "properties");
        return this;
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public QueueStoreConfig setProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public QueueStoreConfig setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
        return this;
    }

    public QueueStoreFactory getFactoryImplementation() {
        return this.factoryImplementation;
    }

    public QueueStoreConfig setFactoryImplementation(QueueStoreFactory factoryImplementation) {
        this.factoryImplementation = factoryImplementation;
        return this;
    }

    public String toString() {
        return "QueueStoreConfig{enabled=" + this.enabled + ", className='" + this.className + '\'' + ", properties=" + this.properties + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 25;
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
        this.storeImplementation = (QueueStore)in.readObject();
        this.factoryImplementation = (QueueStoreFactory)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueueStoreConfig)) {
            return false;
        }
        QueueStoreConfig that = (QueueStoreConfig)o;
        if (this.isEnabled() != that.isEnabled()) {
            return false;
        }
        if (this.getClassName() != null ? !this.getClassName().equals(that.getClassName()) : that.getClassName() != null) {
            return false;
        }
        if (this.getFactoryClassName() != null ? !this.getFactoryClassName().equals(that.getFactoryClassName()) : that.getFactoryClassName() != null) {
            return false;
        }
        if (this.getProperties() != null ? !this.getProperties().equals(that.getProperties()) : that.getProperties() != null) {
            return false;
        }
        if (this.getStoreImplementation() != null ? !this.getStoreImplementation().equals(that.getStoreImplementation()) : that.getStoreImplementation() != null) {
            return false;
        }
        return this.getFactoryImplementation() != null ? this.getFactoryImplementation().equals(that.getFactoryImplementation()) : that.getFactoryImplementation() == null;
    }

    public final int hashCode() {
        int result = this.isEnabled() ? 1 : 0;
        result = 31 * result + (this.getClassName() != null ? this.getClassName().hashCode() : 0);
        result = 31 * result + (this.getFactoryClassName() != null ? this.getFactoryClassName().hashCode() : 0);
        result = 31 * result + (this.getProperties() != null ? this.getProperties().hashCode() : 0);
        result = 31 * result + (this.getStoreImplementation() != null ? this.getStoreImplementation().hashCode() : 0);
        result = 31 * result + (this.getFactoryImplementation() != null ? this.getFactoryImplementation().hashCode() : 0);
        return result;
    }
}

