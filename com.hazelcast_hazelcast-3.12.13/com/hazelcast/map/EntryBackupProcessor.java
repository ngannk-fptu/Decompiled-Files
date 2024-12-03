/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import java.io.Serializable;
import java.util.Map;

public interface EntryBackupProcessor<K, V>
extends Serializable {
    public void processBackup(Map.Entry<K, V> var1);
}

