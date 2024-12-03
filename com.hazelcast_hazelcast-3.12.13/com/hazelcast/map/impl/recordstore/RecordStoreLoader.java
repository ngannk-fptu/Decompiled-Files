/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.nio.serialization.Data;
import java.util.List;
import java.util.concurrent.Future;

interface RecordStoreLoader {
    public static final RecordStoreLoader EMPTY_LOADER = new RecordStoreLoader(){

        public Future loadValues(List<Data> keys, boolean replaceExistingValues) {
            return null;
        }
    };

    public Future<?> loadValues(List<Data> var1, boolean var2);
}

