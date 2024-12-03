/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.version.Version;

public abstract class AbstractDistributedObject<S extends RemoteService>
implements DistributedObject {
    protected static final PartitioningStrategy PARTITIONING_STRATEGY = StringPartitioningStrategy.INSTANCE;
    private volatile NodeEngine nodeEngine;
    private volatile S service;

    protected AbstractDistributedObject(NodeEngine nodeEngine, S service) {
        this.nodeEngine = nodeEngine;
        this.service = service;
    }

    protected String getDistributedObjectName() {
        return this.getName();
    }

    protected Data getNameAsPartitionAwareData() {
        String name = this.getDistributedObjectName();
        return this.getNodeEngine().getSerializationService().toData(name, PARTITIONING_STRATEGY);
    }

    @Override
    public String getPartitionKey() {
        return StringPartitioningStrategy.getPartitionKey(this.getDistributedObjectName());
    }

    @Override
    public final void destroy() {
        if (this.preDestroy()) {
            NodeEngine engine = this.getNodeEngine();
            ProxyService proxyService = engine.getProxyService();
            proxyService.destroyDistributedObject(this.getServiceName(), this.getDistributedObjectName());
            this.postDestroy();
        }
    }

    protected final Data toData(Object object) {
        return this.getNodeEngine().toData(object);
    }

    protected final <E> InternalCompletableFuture<E> invokeOnPartition(Operation operation) {
        return this.getNodeEngine().getOperationService().invokeOnPartition(operation);
    }

    protected final int getPartitionId(Data key) {
        return this.getNodeEngine().getPartitionService().getPartitionId(key);
    }

    protected boolean preDestroy() {
        return true;
    }

    protected void postDestroy() {
    }

    public final NodeEngine getNodeEngine() {
        NodeEngine engine = this.nodeEngine;
        this.lifecycleCheck(engine);
        return engine;
    }

    private void lifecycleCheck(NodeEngine engine) {
        if (engine == null || !engine.isRunning()) {
            this.throwNotActiveException();
        }
    }

    protected void throwNotActiveException() {
        throw new HazelcastInstanceNotActiveException();
    }

    public final S getService() {
        S s = this.service;
        if (s == null) {
            throw new HazelcastInstanceNotActiveException();
        }
        return s;
    }

    public final OperationService getOperationService() {
        return this.getNodeEngine().getOperationService();
    }

    @Override
    public abstract String getServiceName();

    public final void invalidate() {
        this.nodeEngine = null;
        this.service = null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractDistributedObject that = (AbstractDistributedObject)o;
        String name = this.getDistributedObjectName();
        if (name != null ? !name.equals(that.getDistributedObjectName()) : that.getDistributedObjectName() != null) {
            return false;
        }
        String serviceName = this.getServiceName();
        return !(serviceName != null ? !serviceName.equals(that.getServiceName()) : that.getServiceName() != null);
    }

    public int hashCode() {
        int result = this.getServiceName() != null ? this.getServiceName().hashCode() : 0;
        result = 31 * result + (this.getDistributedObjectName() != null ? this.getDistributedObjectName().hashCode() : 0);
        return result;
    }

    public String toString() {
        return this.getClass().getName() + '{' + "service=" + this.getServiceName() + ", name=" + this.getName() + '}';
    }

    protected boolean isClusterVersionLessThan(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isLessThan(version);
    }

    protected boolean isClusterVersionUnknownOrLessThan(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isUnknownOrLessThan(version);
    }

    protected boolean isClusterVersionLessOrEqual(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isLessOrEqual(version);
    }

    protected boolean isClusterVersionUnknownOrLessOrEqual(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isUnknownOrLessOrEqual(version);
    }

    protected boolean isClusterVersionGreaterThan(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isGreaterThan(version);
    }

    protected boolean isClusterVersionUnknownOrGreaterThan(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isUnknownOrGreaterThan(version);
    }

    protected boolean isClusterVersionGreaterOrEqual(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isGreaterOrEqual(version);
    }

    protected boolean isClusterVersionUnknownOrGreaterOrEqual(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isUnknownOrGreaterOrEqual(version);
    }

    protected boolean isClusterVersionEqualTo(Version version) {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isEqualTo(version);
    }

    protected boolean isClusterVersionUnknown() {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isUnknown();
    }
}

