/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.google.common.base.Preconditions;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.MessageFormat;

public class ClusterConfig {
    private final ClusterJoinConfig joinConfig;
    private final String clusterName;
    private final File clusterHome;
    private final NetworkInterface networkInterface;

    public ClusterConfig(ClusterJoinConfig clusterJoinConfig, String clusterName, File clusterHome, NetworkInterface iface) throws ClusterException {
        if (clusterName == null) {
            throw new ClusterException("Cluster name must be configured");
        }
        this.joinConfig = (ClusterJoinConfig)Preconditions.checkNotNull((Object)clusterJoinConfig);
        this.clusterName = clusterName;
        if (clusterHome == null) {
            throw new ClusterException("Shared home directory must be configured");
        }
        if (!clusterHome.isDirectory() || !clusterHome.canRead()) {
            throw new ClusterException(String.format("Shared home directory is not configured correctly: location=%s, exists=%s, isDirectory=%s, canRead=%s", clusterHome.getPath(), clusterHome.exists(), clusterHome.isDirectory(), clusterHome.canRead()));
        }
        this.clusterHome = clusterHome;
        this.networkInterface = iface;
    }

    public String getClusterName() {
        return this.clusterName;
    }

    public ClusterJoinConfig getJoinConfig() {
        return this.joinConfig;
    }

    public File getClusterHome() {
        return this.clusterHome;
    }

    public InetAddress getUnicastAddress() {
        if (this.getNetworkInterface() == null) {
            return null;
        }
        return this.getNetworkInterface().getInetAddresses().nextElement();
    }

    public NetworkInterface getNetworkInterface() {
        return this.networkInterface;
    }

    public String toString() {
        return MessageFormat.format("ClusterConfig: {0}/{1}, Interface:{2}", this.clusterName, this.joinConfig, this.networkInterface);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterConfig that = (ClusterConfig)o;
        if (!this.joinConfig.equals(that.getJoinConfig())) {
            return false;
        }
        if (!this.clusterName.equals(that.clusterName)) {
            return false;
        }
        if (!this.clusterHome.equals(that.clusterHome)) {
            return false;
        }
        return !(this.networkInterface != null ? !this.networkInterface.equals(that.networkInterface) : that.networkInterface != null);
    }

    public int hashCode() {
        int result = this.clusterName.hashCode();
        result = 31 * result + this.joinConfig.hashCode();
        result = 31 * result + this.clusterHome.hashCode();
        result = 31 * result + (this.networkInterface != null ? this.networkInterface.hashCode() : 0);
        return result;
    }
}

