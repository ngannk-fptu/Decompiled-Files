/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.journal;

import com.hazelcast.cache.impl.journal.InternalEventJournalCacheEvent;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.internal.journal.EventJournal;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;

public interface CacheEventJournal
extends EventJournal<InternalEventJournalCacheEvent> {
    public void writeUpdateEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5, Object var6);

    public void writeCreatedEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);

    public void writeRemoveEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);

    public void writeEvictEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);

    public void writeExpiredEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);
}

