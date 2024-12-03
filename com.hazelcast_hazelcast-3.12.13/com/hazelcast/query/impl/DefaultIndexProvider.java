/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexDefinition;
import com.hazelcast.query.impl.IndexImpl;
import com.hazelcast.query.impl.IndexProvider;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.getters.Extractors;

public class DefaultIndexProvider
implements IndexProvider {
    @Override
    public InternalIndex createIndex(IndexDefinition definition, Extractors extractors, InternalSerializationService ss, IndexCopyBehavior copyBehavior, PerIndexStats stats) {
        return new IndexImpl(definition, ss, extractors, copyBehavior, stats);
    }
}

