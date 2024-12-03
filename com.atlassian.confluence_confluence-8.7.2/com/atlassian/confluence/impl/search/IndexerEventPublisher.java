/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class IndexerEventPublisher {
    private final EventPublisher eventPublisher;

    public IndexerEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void publishCallbackEvent(Consumer<ConfluenceIndexer> indexerTask) {
        this.publishCallbackEvent((ConfluenceIndexer indexer, ChangeIndexer changeIndexer) -> indexerTask.accept((ConfluenceIndexer)indexer));
    }

    public void publishCallbackEvent(BiConsumer<ConfluenceIndexer, ChangeIndexer> indexersTask) {
        this.eventPublisher.publish(indexersTask::accept);
    }
}

