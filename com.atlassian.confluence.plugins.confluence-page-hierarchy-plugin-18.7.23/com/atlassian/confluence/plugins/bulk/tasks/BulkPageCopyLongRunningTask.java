/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions
 *  com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions$Builder
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.bulk.tasks;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.plugins.bulk.tasks.AbstractBulkLongRunningTask;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class BulkPageCopyLongRunningTask
extends AbstractBulkLongRunningTask<PageCopyOptions.Builder> {
    public static final String TASK_NAME = "Copy page hierarchy long running task";
    private final ContentId originalPageId;
    private final ContentId destinationPageId;

    private BulkPageCopyLongRunningTask(PageCopyOptions.Builder optionsBuilder, ContentId originalPageId, ContentId destinationPageId, PageManager pageManager, TransactionTemplate transactionTemplate) {
        super(optionsBuilder, pageManager, transactionTemplate);
        this.originalPageId = originalPageId;
        this.destinationPageId = destinationPageId;
    }

    @Override
    protected final void execute() {
        Page originalPage = this.pageManager.getPage(this.originalPageId.asLong());
        Page destinationPage = this.pageManager.getPage(this.destinationPageId.asLong());
        PageCopyOptions pageCopyOptions = ((PageCopyOptions.Builder)this.optionsBuilder).build();
        this.pageManager.deepCopyPage(pageCopyOptions, originalPage, destinationPage);
    }

    public String getName() {
        return TASK_NAME;
    }

    public static class Builder
    extends AbstractBulkLongRunningTask.BaseBuilder<Builder, PageCopyOptions.Builder> {
        private ContentId originalPageId;
        private ContentId destinationPageId;

        @Override
        protected Builder builder() {
            return this;
        }

        public Builder withOriginalPage(ContentId originalPageId) {
            this.originalPageId = originalPageId;
            return this;
        }

        public Builder withDestinationPage(ContentId destinationPageId) {
            this.destinationPageId = destinationPageId;
            return this;
        }

        public BulkPageCopyLongRunningTask build() {
            return new BulkPageCopyLongRunningTask((PageCopyOptions.Builder)this.optionsBuilder, this.originalPageId, this.destinationPageId, this.pageManager, this.transactionTemplate);
        }
    }
}

