/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.merge;

import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import java.io.Serializable;

public interface ReplicatedMapMergePolicy
extends Serializable {
    public Object merge(String var1, ReplicatedMapEntryView var2, ReplicatedMapEntryView var3);
}

