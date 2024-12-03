/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.Serializable;
import java.util.Comparator;

public class ClassComparator
implements Comparator,
Serializable {
    private static final long serialVersionUID = -5225335361837391120L;

    public int compare(Object o1, Object o2) {
        Class c1 = (Class)o1;
        Class c2 = (Class)o2;
        if (c1.equals(o2)) {
            return 0;
        }
        if (c1.isAssignableFrom(c2)) {
            return -1;
        }
        if (!c2.isAssignableFrom(c2)) {
            throw new IllegalArgumentException("The classes share no relation");
        }
        return 1;
    }

    public boolean isComparable(Class c1, Class c2) {
        return c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1);
    }
}

