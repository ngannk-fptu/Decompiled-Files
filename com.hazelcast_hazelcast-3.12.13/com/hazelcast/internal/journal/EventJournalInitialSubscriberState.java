/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.journal;

import com.hazelcast.internal.journal.EventJournalDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class EventJournalInitialSubscriberState
implements IdentifiedDataSerializable {
    private long oldestSequence;
    private long newestSequence;

    public EventJournalInitialSubscriberState() {
    }

    public EventJournalInitialSubscriberState(long oldestSequence, long newestSequence) {
        this.oldestSequence = oldestSequence;
        this.newestSequence = newestSequence;
    }

    public long getOldestSequence() {
        return this.oldestSequence;
    }

    public long getNewestSequence() {
        return this.newestSequence;
    }

    @Override
    public int getFactoryId() {
        return EventJournalDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.oldestSequence);
        out.writeLong(this.newestSequence);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.oldestSequence = in.readLong();
        this.newestSequence = in.readLong();
    }
}

