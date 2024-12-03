/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Cluster
 *  org.apache.catalina.Realm
 *  org.apache.catalina.Valve
 *  org.apache.catalina.core.StandardEngine
 *  org.apache.catalina.ha.ClusterValve
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.catalina.Cluster;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;

public class StandardEngineSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aEngine, StoreDescription parentDesc) throws Exception {
        if (aEngine instanceof StandardEngine) {
            Cluster cluster;
            Valve[] valves;
            StandardEngine engine = (StandardEngine)aEngine;
            Object[] listeners = engine.findLifecycleListeners();
            this.storeElementArray(aWriter, indent, listeners);
            Realm realm = engine.getRealm();
            Realm parentRealm = null;
            if (engine.getParent() != null) {
                parentRealm = engine.getParent().getRealm();
            }
            if (realm != parentRealm) {
                this.storeElement(aWriter, indent, realm);
            }
            if ((valves = engine.getPipeline().getValves()) != null && valves.length > 0) {
                ArrayList<Valve> engineValves = new ArrayList<Valve>();
                for (Valve valve : valves) {
                    if (valve instanceof ClusterValve) continue;
                    engineValves.add(valve);
                }
                this.storeElementArray(aWriter, indent, engineValves.toArray());
            }
            if ((cluster = engine.getCluster()) != null) {
                this.storeElement(aWriter, indent, cluster);
            }
            Object[] children = engine.findChildren();
            this.storeElementArray(aWriter, indent, children);
        }
    }
}

