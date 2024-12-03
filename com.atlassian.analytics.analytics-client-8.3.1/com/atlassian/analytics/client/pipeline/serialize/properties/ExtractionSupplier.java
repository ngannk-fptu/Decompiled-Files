/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.serialize.properties;

import com.atlassian.analytics.client.pipeline.serialize.properties.MetaPropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.dto.ForExtractionDTO;
import com.atlassian.analytics.event.RawEvent;
import java.util.function.Supplier;

public interface ExtractionSupplier {
    public Supplier<RawEvent> toExtractionSupplier(ForExtractionDTO var1);

    public static Supplier<RawEvent> createTimedSupplier(MetaPropertyExtractor instantExtractor, MetaPropertyExtractor delayedExtractor, ForExtractionDTO forExtractionDTO) {
        RawEvent.Builder builder = new RawEvent.Builder();
        RawEvent.Builder instantlyPopulatedBuilder = instantExtractor.populate(builder, forExtractionDTO);
        return () -> {
            RawEvent.Builder delayinglyPopulatedBuilder = delayedExtractor.populate(instantlyPopulatedBuilder, forExtractionDTO);
            return delayinglyPopulatedBuilder.build();
        };
    }
}

