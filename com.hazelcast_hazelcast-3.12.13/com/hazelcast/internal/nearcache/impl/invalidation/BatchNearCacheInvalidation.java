/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BatchNearCacheInvalidation
extends Invalidation {
    private List<Invalidation> invalidations = Collections.emptyList();

    public BatchNearCacheInvalidation() {
    }

    public BatchNearCacheInvalidation(String dataStructureName, List<Invalidation> invalidations) {
        super(dataStructureName);
        this.invalidations = invalidations;
    }

    public List<Invalidation> getInvalidations() {
        return this.invalidations;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.invalidations.size());
        for (Invalidation invalidation : this.invalidations) {
            out.writeObject(invalidation);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        int size = in.readInt();
        if (size != 0) {
            ArrayList<Invalidation> invalidations = new ArrayList<Invalidation>(size);
            for (int i = 0; i < size; ++i) {
                Invalidation invalidation = (Invalidation)in.readObject();
                invalidations.add(invalidation);
            }
            this.invalidations = invalidations;
        }
    }

    @Override
    public String toString() {
        return "BatchNearCacheInvalidation{dataStructureName=" + this.getName() + ", invalidation-count-in-this-batch=" + this.invalidations.size() + '}';
    }

    @Override
    public int getId() {
        return 37;
    }
}

