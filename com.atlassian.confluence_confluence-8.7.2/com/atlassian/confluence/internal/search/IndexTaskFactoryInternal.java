/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.internal.search.tasks.ContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllBlogsChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllBlogsContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllSpacesChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllSpacesContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllUsersChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllUsersContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexUsersInGroupChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexUsersInGroupContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnIndexSpaceChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnIndexSpaceContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnindexContentTypeChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnindexContentTypeContentIndexTask;
import com.atlassian.confluence.search.IndexTaskFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

@Internal
public interface IndexTaskFactoryInternal
extends IndexTaskFactory {
    public ContentIndexTask createContentIndexTask(List<ContentType> var1, List<ContentStatus> var2, JournalEntryType var3);

    public ContentIndexTask createIndexDraftsTask();

    public UnIndexSpaceContentIndexTask createUnIndexSpaceContentIndexTask(String var1);

    public UnIndexSpaceContentIndexTask createUnIndexSpaceContentIndexTask(Space var1);

    public UnIndexSpaceChangeIndexTask createUnIndexSpaceChangeIndexTask(String var1);

    public UnIndexSpaceChangeIndexTask createUnIndexSpaceChangeIndexTask(Space var1);

    public UnindexContentTypeContentIndexTask createUnindexContentTypeContentTask(String var1);

    public UnindexContentTypeChangeIndexTask createUnindexContentTypeChangeTask(String var1);

    public ReindexAllUsersContentIndexTask createReindexAllUsersContentTask();

    public ReindexAllUsersChangeIndexTask createReindexAllUsersChangeTask();

    public ReindexAllBlogsContentIndexTask createReindexAllBlogsContentTask();

    public ReindexAllBlogsChangeIndexTask createReindexAllBlogsChangeTask();

    public ReindexUsersInGroupContentIndexTask createReindexUsersInGroupContentTask(String var1);

    public ReindexUsersInGroupChangeIndexTask createReindexUsersInGroupChangeTask(String var1);

    public ReindexAllSpacesContentIndexTask createReindexAllSpacesContentTask();

    public ReindexAllSpacesChangeIndexTask createReindexAllSpacesChangeTask();
}

