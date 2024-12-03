/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.spi.merge.MergingValue
 *  com.hazelcast.spi.merge.SplitBrainMergePolicy
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.merge.MergingValue;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import java.io.IOException;

@Internal
public class AlwaysNullMapMergePolicy
implements SplitBrainMergePolicy {
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    public void readData(ObjectDataInput in) throws IOException {
    }

    public Object merge(MergingValue mergingValue, MergingValue existingValue) {
        return null;
    }
}

