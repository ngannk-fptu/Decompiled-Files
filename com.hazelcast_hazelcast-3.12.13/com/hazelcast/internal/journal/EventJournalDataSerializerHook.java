/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.journal;

import com.hazelcast.internal.journal.DeserializingEntry;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class EventJournalDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.event_journal", -45);
    public static final int EVENT_JOURNAL_INITIAL_SUBSCRIBER_STATE = 1;
    public static final int DESERIALIZING_ENTRY = 2;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 1: {
                        return new EventJournalInitialSubscriberState();
                    }
                    case 2: {
                        return new DeserializingEntry();
                    }
                }
                return null;
            }
        };
    }
}

