/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.manager.util;

import java.util.Comparator;
import org.apache.catalina.Session;

@Deprecated
public abstract class BaseSessionComparator<T>
implements Comparator<Session> {
    public abstract Comparable<T> getComparableObject(Session var1);

    @Override
    public final int compare(Session s1, Session s2) {
        Comparable<Comparable<T>> c1 = this.getComparableObject(s1);
        Comparable<T> c2 = this.getComparableObject(s2);
        return c1 == null ? (c2 == null ? 0 : -1) : (c2 == null ? 1 : c1.compareTo(c2));
    }
}

