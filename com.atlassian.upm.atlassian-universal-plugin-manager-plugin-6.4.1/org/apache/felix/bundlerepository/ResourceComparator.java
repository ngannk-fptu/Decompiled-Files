/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.util.Comparator;
import org.osgi.service.obr.Resource;

class ResourceComparator
implements Comparator {
    ResourceComparator() {
    }

    public int compare(Object o1, Object o2) {
        Resource r1 = (Resource)o1;
        Resource r2 = (Resource)o2;
        String name1 = r1.getPresentationName();
        String name2 = r2.getPresentationName();
        if (name1 == null) {
            return -1;
        }
        if (name2 == null) {
            return 1;
        }
        return name1.compareToIgnoreCase(name2);
    }
}

