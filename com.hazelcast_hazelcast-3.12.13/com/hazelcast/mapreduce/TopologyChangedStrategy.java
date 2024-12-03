/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

@Deprecated
public enum TopologyChangedStrategy {
    CANCEL_RUNNING_OPERATION,
    DISCARD_AND_RESTART,
    MIGRATE_AND_CONTINUE;

}

