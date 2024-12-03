/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.topic.TopicOverloadPolicy;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

public class ReliableTopicConfig
implements IdentifiedDataSerializable,
NamedConfig {
    public static final int DEFAULT_READ_BATCH_SIZE = 10;
    public static final TopicOverloadPolicy DEFAULT_TOPIC_OVERLOAD_POLICY = TopicOverloadPolicy.BLOCK;
    public static final boolean DEFAULT_STATISTICS_ENABLED = true;
    private Executor executor;
    private int readBatchSize = 10;
    private String name;
    private boolean statisticsEnabled = true;
    private List<ListenerConfig> listenerConfigs = new LinkedList<ListenerConfig>();
    private TopicOverloadPolicy topicOverloadPolicy = DEFAULT_TOPIC_OVERLOAD_POLICY;

    public ReliableTopicConfig() {
    }

    public ReliableTopicConfig(String name) {
        this.name = Preconditions.checkNotNull(name, "name");
    }

    ReliableTopicConfig(ReliableTopicConfig config) {
        this.name = config.name;
        this.statisticsEnabled = config.statisticsEnabled;
        this.readBatchSize = config.readBatchSize;
        this.executor = config.executor;
        this.topicOverloadPolicy = config.topicOverloadPolicy;
        this.listenerConfigs = config.listenerConfigs;
    }

    ReliableTopicConfig(ReliableTopicConfig config, String name) {
        this(config);
        this.name = name;
    }

    @Override
    public ReliableTopicConfig setName(String name) {
        this.name = Preconditions.checkHasText(name, "name must contain text");
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public TopicOverloadPolicy getTopicOverloadPolicy() {
        return this.topicOverloadPolicy;
    }

    public ReliableTopicConfig setTopicOverloadPolicy(TopicOverloadPolicy topicOverloadPolicy) {
        this.topicOverloadPolicy = Preconditions.checkNotNull(topicOverloadPolicy, "topicOverloadPolicy can't be null");
        return this;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public ReliableTopicConfig setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public int getReadBatchSize() {
        return this.readBatchSize;
    }

    public ReliableTopicConfig setReadBatchSize(int readBatchSize) {
        this.readBatchSize = Preconditions.checkPositive(readBatchSize, "readBatchSize should be positive");
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public ReliableTopicConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public ReliableTopicConfig setMessageListenerConfigs(List<ListenerConfig> listenerConfigs) {
        this.listenerConfigs = listenerConfigs != null ? listenerConfigs : new LinkedList();
        return this;
    }

    public List<ListenerConfig> getMessageListenerConfigs() {
        return this.listenerConfigs;
    }

    public ReliableTopicConfig addMessageListenerConfig(ListenerConfig listenerConfig) {
        Preconditions.checkNotNull(listenerConfig, "listenerConfig can't be null");
        this.listenerConfigs.add(listenerConfig);
        return this;
    }

    public String toString() {
        return "ReliableTopicConfig{name='" + this.name + '\'' + ", topicOverloadPolicy=" + (Object)((Object)this.topicOverloadPolicy) + ", executor=" + this.executor + ", readBatchSize=" + this.readBatchSize + ", statisticsEnabled=" + this.statisticsEnabled + ", listenerConfigs=" + this.listenerConfigs + '}';
    }

    public ReliableTopicConfig getAsReadOnly() {
        return new ReliableTopicConfigReadOnly(this);
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 23;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.executor);
        out.writeInt(this.readBatchSize);
        out.writeUTF(this.name);
        out.writeBoolean(this.statisticsEnabled);
        SerializationUtil.writeNullableList(this.listenerConfigs, out);
        out.writeUTF(this.topicOverloadPolicy.name());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.executor = (Executor)in.readObject();
        this.readBatchSize = in.readInt();
        this.name = in.readUTF();
        this.statisticsEnabled = in.readBoolean();
        this.listenerConfigs = SerializationUtil.readNullableList(in);
        this.topicOverloadPolicy = TopicOverloadPolicy.valueOf(in.readUTF());
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReliableTopicConfig)) {
            return false;
        }
        ReliableTopicConfig that = (ReliableTopicConfig)o;
        if (this.readBatchSize != that.readBatchSize) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (this.executor != null ? !this.executor.equals(that.executor) : that.executor != null) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.listenerConfigs != null ? !this.listenerConfigs.equals(that.listenerConfigs) : that.listenerConfigs != null) {
            return false;
        }
        return this.topicOverloadPolicy == that.topicOverloadPolicy;
    }

    public final int hashCode() {
        int result = this.executor != null ? this.executor.hashCode() : 0;
        result = 31 * result + this.readBatchSize;
        result = 31 * result + this.name.hashCode();
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.listenerConfigs != null ? this.listenerConfigs.hashCode() : 0);
        result = 31 * result + (this.topicOverloadPolicy != null ? this.topicOverloadPolicy.hashCode() : 0);
        return result;
    }

    static class ReliableTopicConfigReadOnly
    extends ReliableTopicConfig {
        ReliableTopicConfigReadOnly(ReliableTopicConfig config) {
            super(config);
        }

        @Override
        public ReliableTopicConfig setExecutor(Executor executor) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public ReliableTopicConfig setReadBatchSize(int readBatchSize) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public ReliableTopicConfig setStatisticsEnabled(boolean statisticsEnabled) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public ReliableTopicConfig addMessageListenerConfig(ListenerConfig listenerConfig) {
            throw new UnsupportedOperationException("This config is read-only");
        }

        @Override
        public ReliableTopicConfig setTopicOverloadPolicy(TopicOverloadPolicy topicOverloadPolicy) {
            throw new UnsupportedOperationException("This config is read-only");
        }
    }
}

