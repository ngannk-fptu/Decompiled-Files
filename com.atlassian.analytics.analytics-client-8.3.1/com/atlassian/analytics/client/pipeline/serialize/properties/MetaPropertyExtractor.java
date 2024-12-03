/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.serialize.properties;

import com.atlassian.analytics.client.pipeline.serialize.properties.dto.ForExtractionDTO;
import com.atlassian.analytics.event.RawEvent;

public interface MetaPropertyExtractor {
    public RawEvent.Builder populate(RawEvent.Builder var1, ForExtractionDTO var2);
}

