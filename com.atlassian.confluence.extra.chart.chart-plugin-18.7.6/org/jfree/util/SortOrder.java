/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class SortOrder
implements Serializable {
    private static final long serialVersionUID = -2124469847758108312L;
    public static final SortOrder ASCENDING = new SortOrder("SortOrder.ASCENDING");
    public static final SortOrder DESCENDING = new SortOrder("SortOrder.DESCENDING");
    private String name;

    private SortOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SortOrder)) {
            return false;
        }
        SortOrder that = (SortOrder)obj;
        return this.name.equals(that.toString());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(ASCENDING)) {
            return ASCENDING;
        }
        if (this.equals(DESCENDING)) {
            return DESCENDING;
        }
        return null;
    }
}

