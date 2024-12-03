/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import java.time.Instant;

public interface TrackedUse {
    @Deprecated
    public long getLastUsed();

    default public Instant getLastUsedInstant() {
        return Instant.ofEpochMilli(this.getLastUsed());
    }
}

