/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.Data
 *  com.hazelcast.spi.merge.SplitBrainMergePolicy
 *  com.hazelcast.spi.merge.SplitBrainMergeTypes$MapMergeTypes
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.hazelcast.serialization;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiBundleIdMergePolicy
implements SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> {
    private static final Logger log = LoggerFactory.getLogger(OsgiBundleIdMergePolicy.class);

    public Data merge(SplitBrainMergeTypes.MapMergeTypes mergingEntry, SplitBrainMergeTypes.MapMergeTypes existingEntry) {
        SplitBrainMergeTypes.MapMergeTypes older;
        Data mergingValue = (Data)mergingEntry.getValue();
        Data existingValue = (Data)existingEntry.getValue();
        if (existingValue == null) {
            return mergingValue;
        }
        if (mergingValue == null) {
            return existingValue;
        }
        if (existingValue.getType() == -7 && mergingValue.getType() == -7) {
            Integer merging;
            Integer existing = (Integer)existingEntry.getDeserializedValue();
            return existing.compareTo(merging = (Integer)mergingEntry.getDeserializedValue()) < 0 ? existingValue : mergingValue;
        }
        SplitBrainMergeTypes.MapMergeTypes mapMergeTypes = older = mergingEntry.getCreationTime() < existingEntry.getCreationTime() ? mergingEntry : existingEntry;
        if (!mergingValue.equals(existingValue)) {
            log.info("Conflict in ID -> OSGI bundle mapping. '{}' is mapped to both '{}' and '{}'. Picking the older of the two: '{}'", new Object[]{mergingEntry.getKey(), mergingValue, existingValue, older.getValue()});
        }
        return (Data)older.getValue();
    }

    public void readData(ObjectDataInput in) throws IOException {
    }

    public void writeData(ObjectDataOutput out) throws IOException {
    }
}

