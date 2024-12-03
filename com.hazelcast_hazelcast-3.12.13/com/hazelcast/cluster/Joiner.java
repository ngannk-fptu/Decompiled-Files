/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cluster;

import com.hazelcast.nio.Address;

@Deprecated
public interface Joiner {
    public void join();

    public void searchForOtherClusters();

    public long getStartTime();

    public void setTargetAddress(Address var1);

    public void reset();

    public String getType();

    public void blacklist(Address var1, boolean var2);

    public boolean unblacklist(Address var1);

    public boolean isBlacklisted(Address var1);
}

