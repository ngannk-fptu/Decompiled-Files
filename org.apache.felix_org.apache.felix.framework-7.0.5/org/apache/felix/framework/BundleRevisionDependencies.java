/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

class BundleRevisionDependencies {
    private final Map<BundleRevision, Map<BundleCapability, Set<BundleWire>>> m_dependentsMap = new HashMap<BundleRevision, Map<BundleCapability, Set<BundleWire>>>();

    BundleRevisionDependencies() {
    }

    public synchronized void addDependent(BundleWire bw) {
        Set<BundleWire> dependents;
        BundleRevision provider = bw.getProvider();
        Map<BundleCapability, Set<BundleWire>> caps = this.m_dependentsMap.get(provider);
        if (caps == null) {
            caps = new HashMap<BundleCapability, Set<BundleWire>>();
            this.m_dependentsMap.put(provider, caps);
        }
        if ((dependents = caps.get(bw.getCapability())) == null) {
            dependents = new HashSet<BundleWire>();
            caps.put(bw.getCapability(), dependents);
        }
        dependents.add(bw);
    }

    public synchronized void removeDependents(BundleRevision provider) {
        this.m_dependentsMap.remove(provider);
    }

    public synchronized Map<BundleCapability, Set<BundleWire>> getDependents(BundleRevision provider) {
        return this.m_dependentsMap.get(provider);
    }

    public synchronized boolean hasDependents(BundleRevision revision) {
        if (Util.isFragment(revision) && revision.getWiring() != null) {
            for (BundleWire bw : revision.getWiring().getRequiredWires(null)) {
                if (!"osgi.wiring.host".equals(bw.getCapability().getNamespace())) continue;
                return true;
            }
        } else if (this.m_dependentsMap.containsKey(revision)) {
            return true;
        }
        return false;
    }

    public synchronized boolean hasDependents(Bundle bundle) {
        List<BundleRevision> revisions = bundle.adapt(BundleRevisions.class).getRevisions();
        for (BundleRevision revision : revisions) {
            if (!this.hasDependents(revision)) continue;
            return true;
        }
        return false;
    }

    public synchronized List<BundleWire> getProvidedWires(BundleRevision revision, String namespace) {
        BundleWiring wiring;
        ArrayList<BundleWire> providedWires = new ArrayList<BundleWire>();
        Map<BundleCapability, Set<BundleWire>> providedCaps = this.m_dependentsMap.get(revision);
        if (providedCaps != null && (wiring = revision.getWiring()) != null) {
            List<BundleCapability> resolvedCaps = wiring.getCapabilities(namespace);
            for (BundleCapability resolvedCap : resolvedCaps) {
                Set<BundleWire> dependentWires = providedCaps.get(resolvedCap);
                if (dependentWires == null) continue;
                providedWires.addAll(dependentWires);
            }
        }
        return providedWires;
    }

    public synchronized Set<Bundle> getDependentBundles(Bundle bundle) {
        HashSet<Bundle> result = new HashSet<Bundle>();
        List<BundleRevision> revisions = bundle.adapt(BundleRevisions.class).getRevisions();
        for (BundleRevision revision : revisions) {
            if (Util.isFragment(revision)) {
                BundleWiring wiring = revision.getWiring();
                if (wiring == null) continue;
                for (BundleWire bundleWire : wiring.getRequiredWires(null)) {
                    if (!"osgi.wiring.host".equals(bundleWire.getCapability().getNamespace())) continue;
                    result.add(bundleWire.getProvider().getBundle());
                }
                continue;
            }
            Map<BundleCapability, Set<BundleWire>> caps = this.m_dependentsMap.get(revision);
            if (caps == null) continue;
            for (Map.Entry entry : caps.entrySet()) {
                for (BundleWire dependentWire : (Set)entry.getValue()) {
                    result.add(dependentWire.getRequirer().getBundle());
                }
            }
        }
        return result;
    }

    public synchronized Set<Bundle> getImportingBundles(Bundle exporter, BundleCapability exportCap) {
        HashSet<Bundle> result = new HashSet<Bundle>();
        String pkgName = (String)exportCap.getAttributes().get("osgi.wiring.package");
        for (BundleRevision revision : exporter.adapt(BundleRevisions.class).getRevisions()) {
            Map<BundleCapability, Set<BundleWire>> caps = this.m_dependentsMap.get(revision);
            if (caps == null) continue;
            for (Map.Entry<BundleCapability, Set<BundleWire>> entry : caps.entrySet()) {
                BundleCapability cap = entry.getKey();
                if ((!cap.getNamespace().equals("osgi.wiring.package") || !cap.getAttributes().get("osgi.wiring.package").equals(pkgName)) && !cap.getNamespace().equals("osgi.wiring.bundle")) continue;
                for (BundleWire dependentWire : entry.getValue()) {
                    result.add(dependentWire.getRequirer().getBundle());
                }
            }
        }
        return result;
    }

    public synchronized Set<Bundle> getRequiringBundles(Bundle bundle) {
        HashSet<Bundle> result = new HashSet<Bundle>();
        for (BundleRevision revision : bundle.adapt(BundleRevisions.class).getRevisions()) {
            Map<BundleCapability, Set<BundleWire>> caps = this.m_dependentsMap.get(revision);
            if (caps == null) continue;
            for (Map.Entry<BundleCapability, Set<BundleWire>> entry : caps.entrySet()) {
                if (!entry.getKey().getNamespace().equals("osgi.wiring.bundle")) continue;
                for (BundleWire dependentWire : entry.getValue()) {
                    result.add(dependentWire.getRequirer().getBundle());
                }
            }
        }
        return result;
    }

    public synchronized void removeDependencies(Bundle bundle) {
        List<BundleRevision> revs = bundle.adapt(BundleRevisions.class).getRevisions();
        for (BundleRevision rev : revs) {
            BundleWiring wiring = rev.getWiring();
            if (wiring == null) continue;
            for (BundleWire bw : wiring.getRequiredWires(null)) {
                Map<BundleCapability, Set<BundleWire>> caps = this.m_dependentsMap.get(bw.getProvider());
                if (caps == null) continue;
                ArrayList<BundleCapability> gc = new ArrayList<BundleCapability>();
                for (Map.Entry<BundleCapability, Set<BundleWire>> entry : caps.entrySet()) {
                    entry.getValue().remove(bw);
                    if (!entry.getValue().isEmpty()) continue;
                    gc.add(entry.getKey());
                }
                for (BundleCapability cap : gc) {
                    caps.remove(cap);
                }
                if (!caps.isEmpty()) continue;
                this.m_dependentsMap.remove(bw.getProvider());
            }
        }
    }

    public synchronized void dump() {
    }
}

