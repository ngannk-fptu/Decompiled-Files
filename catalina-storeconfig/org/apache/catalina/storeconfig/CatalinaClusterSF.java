/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.ha.CatalinaCluster
 *  org.apache.catalina.ha.ClusterDeployer
 *  org.apache.catalina.ha.ClusterListener
 *  org.apache.catalina.ha.ClusterManager
 *  org.apache.catalina.ha.tcp.SimpleTcpCluster
 *  org.apache.catalina.tribes.Channel
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.ha.ClusterListener;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.catalina.tribes.Channel;

public class CatalinaClusterSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aCluster, StoreDescription parentDesc) throws Exception {
        if (aCluster instanceof CatalinaCluster) {
            ClusterDeployer deployer;
            Channel channel;
            SimpleTcpCluster tcpCluster;
            ClusterManager manager;
            CatalinaCluster cluster = (CatalinaCluster)aCluster;
            if (cluster instanceof SimpleTcpCluster && (manager = (tcpCluster = (SimpleTcpCluster)cluster).getManagerTemplate()) != null) {
                this.storeElement(aWriter, indent, manager);
            }
            if ((channel = cluster.getChannel()) != null) {
                this.storeElement(aWriter, indent, channel);
            }
            if ((deployer = cluster.getClusterDeployer()) != null) {
                this.storeElement(aWriter, indent, deployer);
            }
            Object[] valves = cluster.getValves();
            this.storeElementArray(aWriter, indent, valves);
            if (aCluster instanceof SimpleTcpCluster) {
                Object[] listeners = ((SimpleTcpCluster)cluster).findLifecycleListeners();
                this.storeElementArray(aWriter, indent, listeners);
                ClusterListener[] mlisteners = ((SimpleTcpCluster)cluster).findClusterListeners();
                ArrayList<ClusterListener> clusterListeners = new ArrayList<ClusterListener>();
                for (ClusterListener clusterListener : mlisteners) {
                    if (clusterListener == deployer) continue;
                    clusterListeners.add(clusterListener);
                }
                this.storeElementArray(aWriter, indent, clusterListeners.toArray());
            }
        }
    }
}

