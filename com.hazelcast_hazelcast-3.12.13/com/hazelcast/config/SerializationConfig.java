/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.util.Preconditions;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SerializationConfig {
    private int portableVersion;
    private final Map<Integer, String> dataSerializableFactoryClasses;
    private final Map<Integer, DataSerializableFactory> dataSerializableFactories;
    private final Map<Integer, String> portableFactoryClasses;
    private final Map<Integer, PortableFactory> portableFactories;
    private GlobalSerializerConfig globalSerializerConfig;
    private final Collection<SerializerConfig> serializerConfigs;
    private boolean checkClassDefErrors = true;
    private boolean useNativeByteOrder;
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private boolean enableCompression;
    private boolean enableSharedObject = true;
    private boolean allowUnsafe;
    private final Set<ClassDefinition> classDefinitions;
    private JavaSerializationFilterConfig javaSerializationFilterConfig;

    public SerializationConfig() {
        this.dataSerializableFactoryClasses = new HashMap<Integer, String>();
        this.dataSerializableFactories = new HashMap<Integer, DataSerializableFactory>();
        this.portableFactoryClasses = new HashMap<Integer, String>();
        this.portableFactories = new HashMap<Integer, PortableFactory>();
        this.serializerConfigs = new LinkedList<SerializerConfig>();
        this.classDefinitions = new HashSet<ClassDefinition>();
    }

    public SerializationConfig(SerializationConfig serializationConfig) {
        this.portableVersion = serializationConfig.portableVersion;
        this.dataSerializableFactoryClasses = new HashMap<Integer, String>(serializationConfig.dataSerializableFactoryClasses);
        this.dataSerializableFactories = new HashMap<Integer, DataSerializableFactory>(serializationConfig.dataSerializableFactories);
        this.portableFactoryClasses = new HashMap<Integer, String>(serializationConfig.portableFactoryClasses);
        this.portableFactories = new HashMap<Integer, PortableFactory>(serializationConfig.portableFactories);
        this.globalSerializerConfig = serializationConfig.globalSerializerConfig == null ? null : new GlobalSerializerConfig(serializationConfig.globalSerializerConfig);
        this.serializerConfigs = new LinkedList<SerializerConfig>();
        for (SerializerConfig serializerConfig : serializationConfig.serializerConfigs) {
            this.serializerConfigs.add(new SerializerConfig(serializerConfig));
        }
        this.checkClassDefErrors = serializationConfig.checkClassDefErrors;
        this.useNativeByteOrder = serializationConfig.useNativeByteOrder;
        this.byteOrder = serializationConfig.byteOrder;
        this.enableCompression = serializationConfig.enableCompression;
        this.enableSharedObject = serializationConfig.enableSharedObject;
        this.allowUnsafe = serializationConfig.allowUnsafe;
        this.classDefinitions = new HashSet<ClassDefinition>(serializationConfig.classDefinitions);
        this.javaSerializationFilterConfig = serializationConfig.javaSerializationFilterConfig == null ? null : new JavaSerializationFilterConfig(serializationConfig.javaSerializationFilterConfig);
    }

    public GlobalSerializerConfig getGlobalSerializerConfig() {
        return this.globalSerializerConfig;
    }

    public SerializationConfig setGlobalSerializerConfig(GlobalSerializerConfig globalSerializerConfig) {
        this.globalSerializerConfig = globalSerializerConfig;
        return this;
    }

    public Collection<SerializerConfig> getSerializerConfigs() {
        return this.serializerConfigs;
    }

    public SerializationConfig addSerializerConfig(SerializerConfig serializerConfig) {
        this.getSerializerConfigs().add(serializerConfig);
        return this;
    }

    public SerializationConfig setSerializerConfigs(Collection<SerializerConfig> serializerConfigs) {
        Preconditions.isNotNull(serializerConfigs, "serializerConfigs");
        this.serializerConfigs.clear();
        this.serializerConfigs.addAll(serializerConfigs);
        return this;
    }

    public int getPortableVersion() {
        return this.portableVersion;
    }

    public SerializationConfig setPortableVersion(int portableVersion) {
        if (portableVersion < 0) {
            throw new IllegalArgumentException("Portable version cannot be negative!");
        }
        this.portableVersion = portableVersion;
        return this;
    }

    public Map<Integer, String> getDataSerializableFactoryClasses() {
        return this.dataSerializableFactoryClasses;
    }

    public SerializationConfig setDataSerializableFactoryClasses(Map<Integer, String> dataSerializableFactoryClasses) {
        Preconditions.isNotNull(dataSerializableFactoryClasses, "dataSerializableFactoryClasses");
        this.dataSerializableFactoryClasses.clear();
        this.dataSerializableFactoryClasses.putAll(dataSerializableFactoryClasses);
        return this;
    }

    public SerializationConfig addDataSerializableFactoryClass(int factoryId, String dataSerializableFactoryClass) {
        this.getDataSerializableFactoryClasses().put(factoryId, dataSerializableFactoryClass);
        return this;
    }

    public SerializationConfig addDataSerializableFactoryClass(int factoryId, Class<? extends DataSerializableFactory> dataSerializableFactoryClass) {
        String factoryClassName = Preconditions.isNotNull(dataSerializableFactoryClass, "dataSerializableFactoryClass").getName();
        return this.addDataSerializableFactoryClass(factoryId, factoryClassName);
    }

    public Map<Integer, DataSerializableFactory> getDataSerializableFactories() {
        return this.dataSerializableFactories;
    }

    public SerializationConfig setDataSerializableFactories(Map<Integer, DataSerializableFactory> dataSerializableFactories) {
        Preconditions.isNotNull(dataSerializableFactories, "dataSerializableFactories");
        this.dataSerializableFactories.clear();
        this.dataSerializableFactories.putAll(dataSerializableFactories);
        return this;
    }

    public SerializationConfig addDataSerializableFactory(int factoryId, DataSerializableFactory dataSerializableFactory) {
        this.getDataSerializableFactories().put(factoryId, dataSerializableFactory);
        return this;
    }

    public Map<Integer, String> getPortableFactoryClasses() {
        return this.portableFactoryClasses;
    }

    public SerializationConfig setPortableFactoryClasses(Map<Integer, String> portableFactoryClasses) {
        Preconditions.isNotNull(portableFactoryClasses, "portableFactoryClasses");
        this.portableFactoryClasses.clear();
        this.portableFactoryClasses.putAll(portableFactoryClasses);
        return this;
    }

    public SerializationConfig addPortableFactoryClass(int factoryId, Class<? extends PortableFactory> portableFactoryClass) {
        String portableFactoryClassName = Preconditions.isNotNull(portableFactoryClass, "portableFactoryClass").getName();
        return this.addPortableFactoryClass(factoryId, portableFactoryClassName);
    }

    public SerializationConfig addPortableFactoryClass(int factoryId, String portableFactoryClass) {
        this.getPortableFactoryClasses().put(factoryId, portableFactoryClass);
        return this;
    }

    public Map<Integer, PortableFactory> getPortableFactories() {
        return this.portableFactories;
    }

    public SerializationConfig setPortableFactories(Map<Integer, PortableFactory> portableFactories) {
        Preconditions.isNotNull(portableFactories, "portableFactories");
        this.portableFactories.clear();
        this.portableFactories.putAll(portableFactories);
        return this;
    }

    public SerializationConfig addPortableFactory(int factoryId, PortableFactory portableFactory) {
        this.getPortableFactories().put(factoryId, portableFactory);
        return this;
    }

    public Set<ClassDefinition> getClassDefinitions() {
        return this.classDefinitions;
    }

    public SerializationConfig addClassDefinition(ClassDefinition classDefinition) {
        if (!this.getClassDefinitions().add(classDefinition)) {
            throw new IllegalArgumentException("ClassDefinition for class-id[" + classDefinition.getClassId() + "] already exists!");
        }
        return this;
    }

    public SerializationConfig setClassDefinitions(Set<ClassDefinition> classDefinitions) {
        Preconditions.isNotNull(classDefinitions, "classDefinitions");
        this.classDefinitions.clear();
        this.classDefinitions.addAll(classDefinitions);
        return this;
    }

    public boolean isCheckClassDefErrors() {
        return this.checkClassDefErrors;
    }

    public SerializationConfig setCheckClassDefErrors(boolean checkClassDefErrors) {
        this.checkClassDefErrors = checkClassDefErrors;
        return this;
    }

    public boolean isUseNativeByteOrder() {
        return this.useNativeByteOrder;
    }

    public SerializationConfig setUseNativeByteOrder(boolean useNativeByteOrder) {
        this.useNativeByteOrder = useNativeByteOrder;
        return this;
    }

    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }

    public SerializationConfig setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        return this;
    }

    public boolean isEnableCompression() {
        return this.enableCompression;
    }

    public SerializationConfig setEnableCompression(boolean enableCompression) {
        this.enableCompression = enableCompression;
        return this;
    }

    public boolean isEnableSharedObject() {
        return this.enableSharedObject;
    }

    public SerializationConfig setEnableSharedObject(boolean enableSharedObject) {
        this.enableSharedObject = enableSharedObject;
        return this;
    }

    public boolean isAllowUnsafe() {
        return this.allowUnsafe;
    }

    public SerializationConfig setAllowUnsafe(boolean allowUnsafe) {
        this.allowUnsafe = allowUnsafe;
        return this;
    }

    public JavaSerializationFilterConfig getJavaSerializationFilterConfig() {
        return this.javaSerializationFilterConfig;
    }

    public SerializationConfig setJavaSerializationFilterConfig(JavaSerializationFilterConfig javaSerializationFilterConfig) {
        this.javaSerializationFilterConfig = javaSerializationFilterConfig;
        return this;
    }

    public String toString() {
        return "SerializationConfig{portableVersion=" + this.portableVersion + ", dataSerializableFactoryClasses=" + this.dataSerializableFactoryClasses + ", dataSerializableFactories=" + this.dataSerializableFactories + ", portableFactoryClasses=" + this.portableFactoryClasses + ", portableFactories=" + this.portableFactories + ", globalSerializerConfig=" + this.globalSerializerConfig + ", serializerConfigs=" + this.serializerConfigs + ", checkClassDefErrors=" + this.checkClassDefErrors + ", classDefinitions=" + this.classDefinitions + ", byteOrder=" + this.byteOrder + ", useNativeByteOrder=" + this.useNativeByteOrder + ", javaSerializationFilterConfig=" + this.javaSerializationFilterConfig + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SerializationConfig that = (SerializationConfig)o;
        if (this.portableVersion != that.portableVersion) {
            return false;
        }
        if (this.checkClassDefErrors != that.checkClassDefErrors) {
            return false;
        }
        if (this.useNativeByteOrder != that.useNativeByteOrder) {
            return false;
        }
        if (this.enableCompression != that.enableCompression) {
            return false;
        }
        if (this.enableSharedObject != that.enableSharedObject) {
            return false;
        }
        if (this.allowUnsafe != that.allowUnsafe) {
            return false;
        }
        if (!this.dataSerializableFactoryClasses.equals(that.dataSerializableFactoryClasses)) {
            return false;
        }
        if (!this.dataSerializableFactories.equals(that.dataSerializableFactories)) {
            return false;
        }
        if (!this.portableFactoryClasses.equals(that.portableFactoryClasses)) {
            return false;
        }
        if (!this.portableFactories.equals(that.portableFactories)) {
            return false;
        }
        if (this.globalSerializerConfig != null ? !this.globalSerializerConfig.equals(that.globalSerializerConfig) : that.globalSerializerConfig != null) {
            return false;
        }
        if (!this.serializerConfigs.equals(that.serializerConfigs)) {
            return false;
        }
        if (this.byteOrder != null ? !this.byteOrder.equals(that.byteOrder) : that.byteOrder != null) {
            return false;
        }
        if (!this.classDefinitions.equals(that.classDefinitions)) {
            return false;
        }
        return this.javaSerializationFilterConfig != null ? this.javaSerializationFilterConfig.equals(that.javaSerializationFilterConfig) : that.javaSerializationFilterConfig == null;
    }

    public int hashCode() {
        int result = this.portableVersion;
        result = 31 * result + this.dataSerializableFactoryClasses.hashCode();
        result = 31 * result + this.dataSerializableFactories.hashCode();
        result = 31 * result + this.portableFactoryClasses.hashCode();
        result = 31 * result + this.portableFactories.hashCode();
        result = 31 * result + (this.globalSerializerConfig != null ? this.globalSerializerConfig.hashCode() : 0);
        result = 31 * result + this.serializerConfigs.hashCode();
        result = 31 * result + (this.checkClassDefErrors ? 1 : 0);
        result = 31 * result + (this.useNativeByteOrder ? 1 : 0);
        result = 31 * result + (this.byteOrder != null ? this.byteOrder.hashCode() : 0);
        result = 31 * result + (this.enableCompression ? 1 : 0);
        result = 31 * result + (this.enableSharedObject ? 1 : 0);
        result = 31 * result + (this.allowUnsafe ? 1 : 0);
        result = 31 * result + this.classDefinitions.hashCode();
        result = 31 * result + (this.javaSerializationFilterConfig != null ? this.javaSerializationFilterConfig.hashCode() : 0);
        return result;
    }
}

