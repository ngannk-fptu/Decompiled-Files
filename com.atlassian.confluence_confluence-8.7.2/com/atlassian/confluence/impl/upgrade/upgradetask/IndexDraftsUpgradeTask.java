/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 */
package com.atlassian.confluence.impl.upgrade.upgradetask;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.impl.upgrade.upgradetask.AbstractIndexContentUpgradeTask;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.queue.JournalIndexTaskQueue;
import com.atlassian.confluence.search.queue.JournalEntryType;
import java.util.Arrays;
import java.util.Collections;

public class IndexDraftsUpgradeTask
extends AbstractIndexContentUpgradeTask {
    public IndexDraftsUpgradeTask(JournalIndexTaskQueue journalIndexTaskQueue, IndexTaskFactoryInternal indexTaskFactory) {
        super(journalIndexTaskQueue, indexTaskFactory, Arrays.asList(ContentType.PAGE, ContentType.BLOG_POST), Collections.singletonList(ContentStatus.DRAFT), JournalEntryType.INDEX_DRAFTS);
    }

    public final String getBuildNumber() {
        return "7400";
    }

    @Override
    protected boolean shouldRun() {
        boolean shouldSkip = Boolean.parseBoolean(System.getProperty("confluence.upgrade.skip.draft.indexing", "false"));
        return !shouldSkip;
    }

    @Override
    public final boolean runOnSpaceImport() {
        return false;
    }

    @Override
    public final boolean breaksBackwardCompatibility() {
        return false;
    }
}

