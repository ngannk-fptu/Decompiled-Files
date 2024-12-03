/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.util.Set;

public interface SessionStatistics {
    public int getEntityCount();

    public int getCollectionCount();

    public Set getEntityKeys();

    public Set getCollectionKeys();
}

