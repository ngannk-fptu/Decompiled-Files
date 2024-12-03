/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.cluster.ClusterEvent
 *  org.terracotta.toolkit.cluster.ClusterEvent$Type
 *  org.terracotta.toolkit.cluster.ClusterInfo
 *  org.terracotta.toolkit.internal.cluster.OutOfBandClusterListener
 */
package org.terracotta.modules.ehcache.event;

import net.sf.ehcache.cluster.ClusterTopologyListener;
import org.terracotta.modules.ehcache.event.TerracottaNodeImpl;
import org.terracotta.toolkit.cluster.ClusterEvent;
import org.terracotta.toolkit.cluster.ClusterInfo;
import org.terracotta.toolkit.internal.cluster.OutOfBandClusterListener;

public class ClusterListenerAdapter
implements OutOfBandClusterListener {
    private static final String EHCACHE_TERRACOTTA_PACKAGE_NAME = "net.sf.ehcache.terracotta";
    private final ClusterTopologyListener topologyListener;
    private volatile TerracottaNodeImpl currentNode;
    private final ClusterInfo cluster;

    public ClusterListenerAdapter(ClusterTopologyListener topologyListener, ClusterInfo cluster) {
        this.topologyListener = topologyListener;
        this.cluster = cluster;
    }

    public void onClusterEvent(ClusterEvent event) {
        switch (event.getType()) {
            case NODE_JOINED: {
                if (this.currentNode == null) {
                    this.currentNode = new TerracottaNodeImpl(this.cluster.getCurrentNode());
                }
                this.topologyListener.nodeJoined(new TerracottaNodeImpl(event.getNode()));
                break;
            }
            case NODE_LEFT: {
                this.topologyListener.nodeLeft(new TerracottaNodeImpl(event.getNode()));
                break;
            }
            case OPERATIONS_DISABLED: {
                this.topologyListener.clusterOffline(new TerracottaNodeImpl(event.getNode()));
                break;
            }
            case OPERATIONS_ENABLED: {
                this.topologyListener.clusterOnline(new TerracottaNodeImpl(event.getNode()));
                break;
            }
            case NODE_REJOINED: {
                TerracottaNodeImpl oldNode = this.currentNode;
                this.currentNode = new TerracottaNodeImpl(event.getNode());
                this.topologyListener.clusterRejoined(oldNode, this.currentNode);
                break;
            }
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.topologyListener == null ? 0 : this.topologyListener.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ClusterListenerAdapter other = (ClusterListenerAdapter)obj;
        return !(this.topologyListener == null ? other.topologyListener != null : !this.topologyListener.equals(other.topologyListener));
    }

    public boolean useOutOfBandNotification(ClusterEvent event) {
        return this.topologyListener.getClass().getName().startsWith(EHCACHE_TERRACOTTA_PACKAGE_NAME) && (event.getType() == ClusterEvent.Type.NODE_LEFT || event.getType() == ClusterEvent.Type.OPERATIONS_DISABLED);
    }
}

