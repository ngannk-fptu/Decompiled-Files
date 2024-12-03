/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.crowd.manager.tombstone;

import com.atlassian.annotations.Internal;
import java.time.Instant;

@Internal
public interface TombstoneManager {
    public void removeOldTombstones();

    public void removeTombstonesOlderThan(Instant var1);
}

