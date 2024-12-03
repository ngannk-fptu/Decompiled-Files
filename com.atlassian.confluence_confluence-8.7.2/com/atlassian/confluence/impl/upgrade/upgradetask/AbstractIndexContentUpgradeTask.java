/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask
 */
package com.atlassian.confluence.impl.upgrade.upgradetask;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.queue.JournalIndexTaskQueue;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask;
import java.util.List;
import java.util.Objects;

public abstract class AbstractIndexContentUpgradeTask
extends AbstractDeferredRunUpgradeTask {
    private final JournalIndexTaskQueue journalIndexTaskQueue;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final List<ContentType> contentTypes;
    private final List<ContentStatus> contentStatuses;
    private final JournalEntryType journalEntryType;

    AbstractIndexContentUpgradeTask(JournalIndexTaskQueue journalIndexTaskQueue, IndexTaskFactoryInternal indexTaskFactory, List<ContentType> contentTypes, List<ContentStatus> contentStatus, JournalEntryType journalEntryType) {
        this.journalIndexTaskQueue = Objects.requireNonNull(journalIndexTaskQueue);
        this.indexTaskFactory = Objects.requireNonNull(indexTaskFactory);
        this.contentTypes = Objects.requireNonNull(contentTypes);
        this.contentStatuses = Objects.requireNonNull(contentStatus);
        this.journalEntryType = journalEntryType;
    }

    public final void doDeferredUpgrade() throws Exception {
        String className = ((Object)((Object)this)).getClass().getSimpleName();
        if (!this.shouldRun()) {
            log.warn("{} was skipped", (Object)className);
            return;
        }
        log.info(String.format("%s for types : %s and statuses : %s", className, this.contentTypes, this.contentStatuses));
        this.journalIndexTaskQueue.enqueue(this.indexTaskFactory.createContentIndexTask(this.contentTypes, this.contentStatuses, this.journalEntryType));
        log.info("{} enqueued", (Object)className);
    }

    protected boolean shouldRun() {
        return true;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }
}

