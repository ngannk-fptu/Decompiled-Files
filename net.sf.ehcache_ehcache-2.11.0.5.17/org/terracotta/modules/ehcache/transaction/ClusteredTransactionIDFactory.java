/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.terracotta.modules.ehcache.transaction;

import java.util.concurrent.ConcurrentMap;
import javax.transaction.xa.Xid;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.transaction.AbstractTransactionIDFactory;
import net.sf.ehcache.transaction.Decision;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.TransactionIDSerializedForm;
import net.sf.ehcache.transaction.XidTransactionIDSerializedForm;
import net.sf.ehcache.transaction.xa.XidTransactionID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.collections.SerializedToolkitCache;
import org.terracotta.modules.ehcache.transaction.ClusteredID;
import org.terracotta.modules.ehcache.transaction.ClusteredTransactionID;
import org.terracotta.modules.ehcache.transaction.xa.ClusteredXidTransactionID;

public class ClusteredTransactionIDFactory
extends AbstractTransactionIDFactory {
    private static final Logger LOG = LoggerFactory.getLogger((String)ClusteredTransactionIDFactory.class.getName());
    private final String clusterUUID;
    private final String cacheManagerName;
    private final SerializedToolkitCache<TransactionID, Decision> transactionStates;
    private final CacheCluster clusterTopology;

    public ClusteredTransactionIDFactory(String clusterUUID, String cacheManagerName, ToolkitInstanceFactory toolkitInstanceFactory, CacheCluster topology) {
        this.clusterUUID = clusterUUID;
        this.cacheManagerName = cacheManagerName;
        this.transactionStates = toolkitInstanceFactory.getOrCreateTransactionCommitStateMap(cacheManagerName);
        this.clusterTopology = topology;
        LOG.debug("ClusteredTransactionIDFactory UUID: {}", (Object)clusterUUID);
    }

    @Override
    public TransactionID createTransactionID() {
        ClusteredTransactionID id = new ClusteredTransactionID(this.clusterTopology.getCurrentNode().getId(), this.clusterUUID, this.cacheManagerName);
        this.getTransactionStates().putIfAbsent(id, Decision.IN_DOUBT);
        return id;
    }

    @Override
    public boolean isExpired(TransactionID id) {
        if (id instanceof ClusteredID) {
            String ownerClientId = ((ClusteredID)((Object)id)).getOwnerID();
            for (ClusterNode node : this.clusterTopology.getNodes()) {
                if (!node.getId().equals(ownerClientId)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public TransactionID restoreTransactionID(TransactionIDSerializedForm serializedForm) {
        return new ClusteredTransactionID(serializedForm);
    }

    @Override
    public XidTransactionID createXidTransactionID(Xid xid, Ehcache cache) {
        ClusteredXidTransactionID id = new ClusteredXidTransactionID(xid, this.cacheManagerName, cache.getName(), this.clusterTopology.getCurrentNode().getId());
        this.getTransactionStates().putIfAbsent(id, Decision.IN_DOUBT);
        return id;
    }

    @Override
    public XidTransactionID restoreXidTransactionID(XidTransactionIDSerializedForm serializedForm) {
        return new ClusteredXidTransactionID(serializedForm);
    }

    @Override
    protected ConcurrentMap<TransactionID, Decision> getTransactionStates() {
        return this.transactionStates;
    }

    @Override
    public Boolean isPersistent() {
        return Boolean.TRUE;
    }
}

