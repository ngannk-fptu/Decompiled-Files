/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.terracotta;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.sf.ehcache.cluster.ClusterNode;

public class DisconnectedClusterNode
implements ClusterNode {
    private final String id;

    public DisconnectedClusterNode(ClusterNode node) {
        this.id = node.getId();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getHostname() {
        Object hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            hostName = "[Can't determine hostname and " + this.id + " has DISCONNECTED]";
        }
        return hostName;
    }

    @Override
    public String getIp() {
        Object ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            ip = "[Can't determine IP and " + this.id + " has DISCONNECTED]";
        }
        return ip;
    }
}

