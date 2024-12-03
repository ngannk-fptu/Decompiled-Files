/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.query.impl.DefaultValueCollector;
import com.hazelcast.query.impl.getters.Getter;

final class ExtractorGetter
extends Getter {
    private final ValueExtractor extractor;
    private final Object arguments;
    private final InternalSerializationService serializationService;

    ExtractorGetter(InternalSerializationService serializationService, ValueExtractor extractor, Object arguments) {
        super(null);
        this.extractor = extractor;
        this.arguments = arguments;
        this.serializationService = serializationService;
    }

    @Override
    Object getValue(Object target) throws Exception {
        Object extractionTarget = target;
        DefaultValueCollector collector = new DefaultValueCollector();
        if (target instanceof Data) {
            extractionTarget = this.serializationService.createPortableReader((Data)target);
        }
        this.extractor.extract(extractionTarget, this.arguments, collector);
        return collector.getResult();
    }

    @Override
    Class getReturnType() {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean isCacheable() {
        return true;
    }
}

