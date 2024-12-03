/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.merge;

import com.hazelcast.core.EntryView;
import com.hazelcast.nio.serialization.DataSerializable;

public interface MapMergePolicy
extends DataSerializable {
    public Object merge(String var1, EntryView var2, EntryView var3);
}

