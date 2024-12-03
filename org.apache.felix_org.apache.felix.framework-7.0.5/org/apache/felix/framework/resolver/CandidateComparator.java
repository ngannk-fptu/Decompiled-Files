/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.resolver;

import java.util.Comparator;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.resource.Capability;

public class CandidateComparator
implements Comparator<Capability> {
    @Override
    public int compare(Capability cap1, Capability cap2) {
        int c = 0;
        BundleCapability bcap1 = null;
        BundleCapability bcap2 = null;
        if (cap1 instanceof BundleCapability && cap2 instanceof BundleCapability) {
            bcap1 = (BundleCapability)cap1;
            bcap2 = (BundleCapability)cap2;
            if (bcap1.getRevision().getWiring() != null && bcap2.getRevision().getWiring() == null) {
                c = -1;
            } else if (bcap1.getRevision().getWiring() == null && bcap2.getRevision().getWiring() != null) {
                c = 1;
            }
        }
        if (c == 0 && cap1.getNamespace().equals("osgi.wiring.bundle")) {
            c = ((Comparable)cap1.getAttributes().get("osgi.wiring.bundle")).compareTo(cap2.getAttributes().get("osgi.wiring.bundle"));
            if (c == 0) {
                Version v1 = !cap1.getAttributes().containsKey("bundle-version") ? Version.emptyVersion : (Version)cap1.getAttributes().get("bundle-version");
                Version v2 = !cap2.getAttributes().containsKey("bundle-version") ? Version.emptyVersion : (Version)cap2.getAttributes().get("bundle-version");
                c = v2.compareTo(v1);
            }
        } else if (c == 0 && cap1.getNamespace().equals("osgi.wiring.package") && (c = ((Comparable)cap1.getAttributes().get("osgi.wiring.package")).compareTo(cap2.getAttributes().get("osgi.wiring.package"))) == 0) {
            Version v1 = !cap1.getAttributes().containsKey("version") ? Version.emptyVersion : (Version)cap1.getAttributes().get("version");
            Version v2 = !cap2.getAttributes().containsKey("version") ? Version.emptyVersion : (Version)cap2.getAttributes().get("version");
            c = v2.compareTo(v1);
        }
        if (c == 0 && bcap1 != null && bcap2 != null) {
            if (bcap1.getRevision().getBundle().getBundleId() < bcap2.getRevision().getBundle().getBundleId()) {
                c = -1;
            } else if (bcap1.getRevision().getBundle().getBundleId() > bcap2.getRevision().getBundle().getBundleId()) {
                c = 1;
            }
        }
        return c;
    }
}

