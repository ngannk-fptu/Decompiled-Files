/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import java.util.Set;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;

public interface FetchStats {
    public boolean hasSubselectFetches();

    public Set<CollectionAttributeFetch> getJoinedBagAttributeFetches();
}

