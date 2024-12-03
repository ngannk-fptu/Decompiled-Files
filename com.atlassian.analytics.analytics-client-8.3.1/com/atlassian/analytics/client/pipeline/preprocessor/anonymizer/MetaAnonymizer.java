/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.preprocessor.anonymizer;

import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;

public interface MetaAnonymizer {
    public ProcessedEvent.Builder addAnonymizedFields(ProcessedEvent.Builder var1, RawEvent var2);
}

