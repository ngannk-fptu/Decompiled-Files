/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.journal;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;

public interface EventJournalReader<E> {
    public ICompletableFuture<EventJournalInitialSubscriberState> subscribeToEventJournal(int var1);

    public <T> ICompletableFuture<ReadResultSet<T>> readFromEventJournal(long var1, int var3, int var4, int var5, Predicate<? super E> var6, Function<? super E, ? extends T> var7);
}

