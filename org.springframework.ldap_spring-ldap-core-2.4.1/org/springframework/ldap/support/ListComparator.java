/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.support;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class ListComparator
implements Comparator,
Serializable {
    private static final long serialVersionUID = -3068381879731157178L;

    public int compare(Object o1, Object o2) {
        List list1 = (List)o1;
        List list2 = (List)o2;
        for (int i = 0; i < list1.size(); ++i) {
            if (list2.size() > i) {
                Comparable component2;
                Comparable component1 = (Comparable)list1.get(i);
                int componentsCompared = component1.compareTo(component2 = (Comparable)list2.get(i));
                if (componentsCompared == 0) continue;
                return componentsCompared;
            }
            return 1;
        }
        if (list2.size() > list1.size()) {
            return -1;
        }
        return 0;
    }
}

