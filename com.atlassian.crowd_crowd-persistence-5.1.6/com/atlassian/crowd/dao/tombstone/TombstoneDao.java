/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.dao.tombstone;

import com.atlassian.crowd.model.tombstone.AbstractTombstone;
import com.atlassian.crowd.model.tombstone.ApplicationTombstone;
import java.util.Collection;
import java.util.List;

public interface TombstoneDao {
    public void storeUserTombstones(long var1, Collection<String> var3);

    public void storeGroupTombstones(long var1, Collection<String> var3);

    public void storeUserMembershipTombstone(long var1, String var3, String var4);

    public void storeGroupMembershipTombstone(long var1, String var3, String var4);

    public void storeEventsTombstoneForDirectory(String var1, long var2);

    public void storeEventsTombstoneForApplication(long var1);

    public void storeEventsTombstone(String var1);

    public void storeAliasTombstone(long var1, String var3);

    public <T extends AbstractTombstone> List<T> getTombstonesAfter(long var1, Collection<Long> var3, Class<T> var4);

    public <T extends ApplicationTombstone> List<T> getTombstonesAfter(long var1, Long var3, Class<T> var4);

    public int removeAllUpTo(long var1);
}

