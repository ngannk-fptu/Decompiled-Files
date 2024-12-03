/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Cluster
 *  org.apache.catalina.Realm
 *  org.apache.catalina.Valve
 *  org.apache.catalina.core.StandardHost
 *  org.apache.catalina.ha.ClusterValve
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.catalina.Cluster;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;

public class StandardHostSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aHost, StoreDescription parentDesc) throws Exception {
        if (aHost instanceof StandardHost) {
            Cluster cluster;
            Valve[] valves;
            StandardHost host = (StandardHost)aHost;
            Object[] listeners = host.findLifecycleListeners();
            this.storeElementArray(aWriter, indent, listeners);
            String[] aliases = host.findAliases();
            this.getStoreAppender().printTagArray(aWriter, "Alias", indent + 2, aliases);
            Realm realm = host.getRealm();
            if (realm != null) {
                Realm parentRealm = null;
                if (host.getParent() != null) {
                    parentRealm = host.getParent().getRealm();
                }
                if (realm != parentRealm) {
                    this.storeElement(aWriter, indent, realm);
                }
            }
            if ((valves = host.getPipeline().getValves()) != null && valves.length > 0) {
                ArrayList<Valve> hostValves = new ArrayList<Valve>();
                for (Valve valve : valves) {
                    if (valve instanceof ClusterValve) continue;
                    hostValves.add(valve);
                }
                this.storeElementArray(aWriter, indent, hostValves.toArray());
            }
            if ((cluster = host.getCluster()) != null) {
                Cluster parentCluster = null;
                if (host.getParent() != null) {
                    parentCluster = host.getParent().getCluster();
                }
                if (cluster != parentCluster) {
                    this.storeElement(aWriter, indent, cluster);
                }
            }
            Object[] children = host.findChildren();
            this.storeElementArray(aWriter, indent, children);
        }
    }
}

