/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.RemoteService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CollectionMergeBackupOperation
extends CollectionOperation
implements BackupOperation {
    private Collection<CollectionItem> backupItems;

    public CollectionMergeBackupOperation() {
    }

    public CollectionMergeBackupOperation(String name, Collection<CollectionItem> backupItems) {
        super(name);
        this.backupItems = backupItems;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer container = this.getOrCreateContainer();
        if (this.backupItems.isEmpty()) {
            RemoteService service = (RemoteService)this.getService();
            service.destroyDistributedObject(this.name);
        } else {
            Map<Long, CollectionItem> backupMap = container.getMap();
            backupMap.clear();
            for (CollectionItem backupItem : this.backupItems) {
                backupMap.put(backupItem.getItemId(), backupItem);
            }
        }
    }

    @Override
    public int getId() {
        return 46;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.backupItems.size());
        for (CollectionItem backupItem : this.backupItems) {
            out.writeObject(backupItem);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.backupItems = new ArrayList<CollectionItem>(size);
        for (int i = 0; i < size; ++i) {
            CollectionItem backupItem = (CollectionItem)in.readObject();
            this.backupItems.add(backupItem);
        }
    }
}

