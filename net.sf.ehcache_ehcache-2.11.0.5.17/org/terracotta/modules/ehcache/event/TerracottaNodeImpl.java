/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.cluster.ClusterNode
 */
package org.terracotta.modules.ehcache.event;

import org.terracotta.toolkit.cluster.ClusterNode;

public class TerracottaNodeImpl
implements net.sf.ehcache.cluster.ClusterNode {
    private final ClusterNode node;

    public TerracottaNodeImpl(ClusterNode node) {
        this.node = node;
    }

    @Override
    public String getHostname() {
        return this.node.getAddress().getHostName();
    }

    @Override
    public String getId() {
        return this.node.getId();
    }

    @Override
    public String getIp() {
        return this.node.getAddress().getHostAddress();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.node == null ? 0 : this.node.hashCode());
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
        TerracottaNodeImpl other = (TerracottaNodeImpl)obj;
        return !(this.node == null ? other.node != null : !this.node.equals(other.node));
    }

    public String toString() {
        return "TerracottaNodeImpl{node=" + this.node + "}";
    }
}

