/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.common;

import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.core.Member;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

public class DataAwareItemEvent<E>
extends ItemEvent<E> {
    private static final long serialVersionUID = 1L;
    private final transient Data dataItem;
    private final transient SerializationService serializationService;

    public DataAwareItemEvent(String name, ItemEventType itemEventType, Data dataItem, Member member, SerializationService serializationService) {
        super(name, itemEventType, null, member);
        this.dataItem = dataItem;
        this.serializationService = serializationService;
    }

    @Override
    public E getItem() {
        if (this.item == null && this.dataItem != null) {
            this.item = this.serializationService.toObject(this.dataItem);
        }
        return (E)this.item;
    }

    public Data getItemData() {
        return this.dataItem;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new NotSerializableException();
    }
}

