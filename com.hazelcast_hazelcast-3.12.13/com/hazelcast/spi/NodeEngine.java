/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.SharedService;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.transaction.TransactionManagerService;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.wan.WanReplicationService;
import java.util.Collection;

public interface NodeEngine {
    public OperationService getOperationService();

    public ExecutionService getExecutionService();

    public ClusterService getClusterService();

    public IPartitionService getPartitionService();

    public EventService getEventService();

    public SerializationService getSerializationService();

    public ProxyService getProxyService();

    public WanReplicationService getWanReplicationService();

    public QuorumService getQuorumService();

    public TransactionManagerService getTransactionManagerService();

    public Address getMasterAddress();

    public Address getThisAddress();

    public Member getLocalMember();

    public Config getConfig();

    public ClassLoader getConfigClassLoader();

    public HazelcastProperties getProperties();

    public ILogger getLogger(String var1);

    public ILogger getLogger(Class var1);

    public Data toData(Object var1);

    public <T> T toObject(Object var1);

    public <T> T toObject(Object var1, Class var2);

    @Deprecated
    public boolean isActive();

    public boolean isRunning();

    public HazelcastInstance getHazelcastInstance();

    public <T> T getService(String var1);

    public <T extends SharedService> T getSharedService(String var1);

    public MemberVersion getVersion();

    public SplitBrainMergePolicyProvider getSplitBrainMergePolicyProvider();

    public <S> Collection<S> getServices(Class<S> var1);
}

