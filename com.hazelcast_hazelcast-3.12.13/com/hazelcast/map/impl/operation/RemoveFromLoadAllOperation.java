/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.recordstore.Storage;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RemoveFromLoadAllOperation
extends MapOperation
implements PartitionAwareOperation,
MutatingOperation {
    private List<Data> keys;

    public RemoveFromLoadAllOperation() {
        this.keys = Collections.emptyList();
    }

    public RemoveFromLoadAllOperation(String name, List<Data> keys) {
        super(name);
        this.keys = keys;
    }

    @Override
    public void run() throws Exception {
        this.removeExistingKeys(this.keys);
    }

    private void removeExistingKeys(Collection<Data> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        Storage storage = this.recordStore.getStorage();
        Iterator<Data> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Data key = iterator.next();
            if (!storage.containsKey(key)) continue;
            iterator.remove();
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        int size = this.keys.size();
        out.writeInt(size);
        for (Data key : this.keys) {
            out.writeData(key);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        if (size > 0) {
            this.keys = new ArrayList<Data>(size);
        }
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.keys.add(data);
        }
    }

    @Override
    public int getId() {
        return 134;
    }
}

