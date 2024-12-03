/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.journal;

import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;

public interface EventJournal<E> {
    public long newestSequence(ObjectNamespace var1, int var2);

    public long oldestSequence(ObjectNamespace var1, int var2);

    public boolean isPersistenceEnabled(ObjectNamespace var1, int var2);

    public void destroy(ObjectNamespace var1, int var2);

    public void isAvailableOrNextSequence(ObjectNamespace var1, int var2, long var3);

    public boolean isNextAvailableSequence(ObjectNamespace var1, int var2, long var3);

    public WaitNotifyKey getWaitNotifyKey(ObjectNamespace var1, int var2);

    public <T> long readMany(ObjectNamespace var1, int var2, long var3, ReadResultSetImpl<E, T> var5);

    public void cleanup(ObjectNamespace var1, int var2);

    public boolean hasEventJournal(ObjectNamespace var1);

    public EventJournalConfig getEventJournalConfig(ObjectNamespace var1);

    public RingbufferConfig toRingbufferConfig(EventJournalConfig var1, ObjectNamespace var2);
}

