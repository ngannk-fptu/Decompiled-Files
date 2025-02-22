/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.DiscoveryConfigReadOnly;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.discovery.NodeFilter;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscoveryConfig
implements IdentifiedDataSerializable {
    private List<DiscoveryStrategyConfig> discoveryStrategyConfigs = new ArrayList<DiscoveryStrategyConfig>();
    private DiscoveryServiceProvider discoveryServiceProvider;
    private NodeFilter nodeFilter;
    private String nodeFilterClass;
    private DiscoveryConfig readonly;

    public DiscoveryConfig() {
    }

    protected DiscoveryConfig(DiscoveryServiceProvider discoveryServiceProvider, NodeFilter nodeFilter, String nodeFilterClass, Collection<DiscoveryStrategyConfig> discoveryStrategyConfigs) {
        this.discoveryServiceProvider = discoveryServiceProvider;
        this.nodeFilter = nodeFilter;
        this.nodeFilterClass = nodeFilterClass;
        this.discoveryStrategyConfigs.addAll(discoveryStrategyConfigs);
    }

    public DiscoveryConfig(DiscoveryConfig discoveryConfig) {
        this.discoveryStrategyConfigs = new ArrayList<DiscoveryStrategyConfig>(discoveryConfig.discoveryStrategyConfigs);
        this.discoveryServiceProvider = discoveryConfig.discoveryServiceProvider;
        this.nodeFilter = discoveryConfig.nodeFilter;
        this.nodeFilterClass = discoveryConfig.nodeFilterClass;
        this.readonly = discoveryConfig.readonly;
    }

    public DiscoveryConfig getAsReadOnly() {
        if (this.readonly != null) {
            return this.readonly;
        }
        this.readonly = new DiscoveryConfigReadOnly(this);
        return this.readonly;
    }

    public void setDiscoveryServiceProvider(DiscoveryServiceProvider discoveryServiceProvider) {
        this.discoveryServiceProvider = discoveryServiceProvider;
    }

    public DiscoveryServiceProvider getDiscoveryServiceProvider() {
        return this.discoveryServiceProvider;
    }

    public NodeFilter getNodeFilter() {
        return this.nodeFilter;
    }

    public void setNodeFilter(NodeFilter nodeFilter) {
        this.nodeFilter = nodeFilter;
    }

    public String getNodeFilterClass() {
        return this.nodeFilterClass;
    }

    public void setNodeFilterClass(String nodeFilterClass) {
        this.nodeFilterClass = nodeFilterClass;
    }

    public boolean isEnabled() {
        return this.discoveryStrategyConfigs.size() > 0;
    }

    public Collection<DiscoveryStrategyConfig> getDiscoveryStrategyConfigs() {
        return this.discoveryStrategyConfigs;
    }

    public void setDiscoveryStrategyConfigs(List<DiscoveryStrategyConfig> discoveryStrategyConfigs) {
        this.discoveryStrategyConfigs = discoveryStrategyConfigs == null ? new ArrayList(1) : discoveryStrategyConfigs;
    }

    public void addDiscoveryStrategyConfig(DiscoveryStrategyConfig discoveryStrategyConfig) {
        this.discoveryStrategyConfigs.add(discoveryStrategyConfig);
    }

    public String toString() {
        return "DiscoveryConfig{discoveryStrategyConfigs=" + this.discoveryStrategyConfigs + ", discoveryServiceProvider=" + this.discoveryServiceProvider + ", nodeFilter=" + this.nodeFilter + ", nodeFilterClass='" + this.nodeFilterClass + '\'' + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 61;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.discoveryStrategyConfigs);
        out.writeObject(this.discoveryServiceProvider);
        out.writeObject(this.nodeFilter);
        out.writeUTF(this.nodeFilterClass);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.discoveryStrategyConfigs = (List)in.readObject();
        this.discoveryServiceProvider = (DiscoveryServiceProvider)in.readObject();
        this.nodeFilter = (NodeFilter)in.readObject();
        this.nodeFilterClass = in.readUTF();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DiscoveryConfig that = (DiscoveryConfig)o;
        if (!this.discoveryStrategyConfigs.equals(that.discoveryStrategyConfigs)) {
            return false;
        }
        if (this.discoveryServiceProvider != null ? !this.discoveryServiceProvider.equals(that.discoveryServiceProvider) : that.discoveryServiceProvider != null) {
            return false;
        }
        if (this.nodeFilter != null ? !this.nodeFilter.equals(that.nodeFilter) : that.nodeFilter != null) {
            return false;
        }
        return this.nodeFilterClass != null ? this.nodeFilterClass.equals(that.nodeFilterClass) : that.nodeFilterClass == null;
    }

    public int hashCode() {
        int result = this.discoveryStrategyConfigs.hashCode();
        result = 31 * result + (this.discoveryServiceProvider != null ? this.discoveryServiceProvider.hashCode() : 0);
        result = 31 * result + (this.nodeFilter != null ? this.nodeFilter.hashCode() : 0);
        result = 31 * result + (this.nodeFilterClass != null ? this.nodeFilterClass.hashCode() : 0);
        return result;
    }
}

