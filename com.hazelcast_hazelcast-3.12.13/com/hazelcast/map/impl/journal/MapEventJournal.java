/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.internal.journal.EventJournal;
import com.hazelcast.map.impl.journal.InternalEventJournalMapEvent;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;

public interface MapEventJournal
extends EventJournal<InternalEventJournalMapEvent> {
    public void writeUpdateEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5, Object var6);

    public void writeAddEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);

    public void writeRemoveEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);

    public void writeEvictEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);

    public void writeLoadEvent(EventJournalConfig var1, ObjectNamespace var2, int var3, Data var4, Object var5);

    @Override
    public boolean hasEventJournal(ObjectNamespace var1);
}

