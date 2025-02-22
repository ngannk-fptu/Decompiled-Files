/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster;

import com.hazelcast.version.Version;

public final class Versions {
    public static final Version V3_8 = Version.of(3, 8);
    public static final Version V3_9 = Version.of(3, 9);
    public static final Version V3_10 = Version.of(3, 10);
    public static final Version V3_11 = Version.of(3, 11);
    public static final Version V3_12 = Version.of(3, 12);
    public static final Version PREVIOUS_CLUSTER_VERSION = V3_11;
    public static final Version CURRENT_CLUSTER_VERSION = V3_12;

    private Versions() {
    }
}

