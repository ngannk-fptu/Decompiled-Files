/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.preprocessor.anonymizer;

import com.atlassian.analytics.client.logger.EventAnonymizer;
import com.atlassian.analytics.client.pipeline.preprocessor.anonymizer.MetaAnonymizer;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;

public class DefaultMetaAnonymizer
implements MetaAnonymizer {
    private final EventAnonymizer eventAnonymizer;

    public DefaultMetaAnonymizer(EventAnonymizer eventAnonymizer) {
        this.eventAnonymizer = eventAnonymizer;
    }

    @Override
    public ProcessedEvent.Builder addAnonymizedFields(ProcessedEvent.Builder builder, RawEvent event) {
        return builder.server(this.anonymizeServer(event)).user(this.anonymizeUser(event)).sourceIP(this.anonymizeSourceIP(event)).atlPath(this.anonymizeAtlPath(event));
    }

    private String anonymizeServer(RawEvent event) {
        return this.eventAnonymizer.hash(event.getServer());
    }

    private String anonymizeUser(RawEvent event) {
        return this.eventAnonymizer.hash(event.getUser());
    }

    private String anonymizeSourceIP(RawEvent event) {
        return this.eventAnonymizer.hash(event.getSourceIP());
    }

    private String anonymizeAtlPath(RawEvent event) {
        return this.eventAnonymizer.hash(event.getAtlPath());
    }
}

