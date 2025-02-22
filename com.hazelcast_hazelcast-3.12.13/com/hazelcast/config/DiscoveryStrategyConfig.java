/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DiscoveryStrategyConfig
implements IdentifiedDataSerializable {
    private String className;
    private transient DiscoveryStrategyFactory discoveryStrategyFactory;
    private Map<String, Comparable> properties;

    public DiscoveryStrategyConfig() {
        this.properties = MapUtil.createHashMap(1);
    }

    public DiscoveryStrategyConfig(String className) {
        this(className, Collections.emptyMap());
    }

    public DiscoveryStrategyConfig(String className, Map<String, Comparable> properties) {
        this.className = className;
        this.properties = properties == null ? MapUtil.createHashMap(1) : new HashMap<String, Comparable>(properties);
        this.discoveryStrategyFactory = null;
    }

    public DiscoveryStrategyConfig(DiscoveryStrategyFactory discoveryStrategyFactory) {
        this(discoveryStrategyFactory, Collections.emptyMap());
    }

    public DiscoveryStrategyConfig(DiscoveryStrategyFactory discoveryStrategyFactory, Map<String, Comparable> properties) {
        this.className = null;
        this.properties = properties == null ? MapUtil.createHashMap(1) : new HashMap<String, Comparable>(properties);
        this.discoveryStrategyFactory = discoveryStrategyFactory;
    }

    public DiscoveryStrategyConfig(DiscoveryStrategyConfig config) {
        this.className = config.className;
        this.discoveryStrategyFactory = config.discoveryStrategyFactory;
        this.properties = new HashMap<String, Comparable>(config.properties);
    }

    public String getClassName() {
        return this.className;
    }

    public DiscoveryStrategyConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public DiscoveryStrategyConfig setDiscoveryStrategyFactory(DiscoveryStrategyFactory discoveryStrategyFactory) {
        this.discoveryStrategyFactory = discoveryStrategyFactory;
        return this;
    }

    public DiscoveryStrategyFactory getDiscoveryStrategyFactory() {
        return this.discoveryStrategyFactory;
    }

    public void addProperty(String key, Comparable value) {
        this.properties.put(key, value);
    }

    public void removeProperty(String key) {
        this.properties.remove(key);
    }

    public DiscoveryStrategyConfig setProperties(Map<String, Comparable> properties) {
        this.properties = properties == null ? MapUtil.createHashMap(1) : new HashMap<String, Comparable>(properties);
        return this;
    }

    public Map<String, Comparable> getProperties() {
        return this.properties;
    }

    public String toString() {
        return "DiscoveryStrategyConfig{properties=" + this.properties + ", className='" + this.className + '\'' + ", discoveryStrategyFactory=" + this.discoveryStrategyFactory + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 62;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.className);
        out.writeInt(this.properties.size());
        for (Map.Entry<String, Comparable> entry : this.properties.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.className = in.readUTF();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.properties.put(in.readUTF(), (Comparable)in.readObject());
        }
    }
}

