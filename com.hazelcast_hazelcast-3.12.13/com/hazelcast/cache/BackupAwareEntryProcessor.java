/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.processor.EntryProcessor
 */
package com.hazelcast.cache;

import javax.cache.processor.EntryProcessor;

public interface BackupAwareEntryProcessor<K, V, T>
extends EntryProcessor<K, V, T> {
    public EntryProcessor<K, V, T> createBackupEntryProcessor();
}

