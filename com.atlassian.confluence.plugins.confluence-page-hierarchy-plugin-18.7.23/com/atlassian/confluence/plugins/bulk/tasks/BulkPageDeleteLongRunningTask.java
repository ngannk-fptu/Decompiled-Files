/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions$Builder
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.bulk.tasks;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions;
import com.atlassian.confluence.plugins.bulk.tasks.AbstractBulkLongRunningTask;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class BulkPageDeleteLongRunningTask
extends AbstractBulkLongRunningTask<PageDeleteOptions.Builder> {
    public static final String TASK_NAME = "Delete page hierarchy long running task";
    private final ContentId targetPageId;

    private BulkPageDeleteLongRunningTask(PageDeleteOptions.Builder optionsBuilder, ContentId targetPageId, PageManager pageManager, TransactionTemplate transactionTemplate) {
        super(optionsBuilder, pageManager, transactionTemplate);
        this.targetPageId = targetPageId;
    }

    @Override
    protected final void execute() {
        Page targetPage = this.pageManager.getPage(this.targetPageId.asLong());
        this.pageManager.deepDeletePage(((PageDeleteOptions.Builder)this.optionsBuilder).build(), targetPage);
    }

    public String getName() {
        return TASK_NAME;
    }

    public static class Builder
    extends AbstractBulkLongRunningTask.BaseBuilder<Builder, PageDeleteOptions.Builder> {
        private ContentId targetPageId;

        @Override
        protected Builder builder() {
            return this;
        }

        public Builder withTargetPageId(ContentId targetPageId) {
            this.targetPageId = targetPageId;
            return this;
        }

        public BulkPageDeleteLongRunningTask build() {
            return new BulkPageDeleteLongRunningTask((PageDeleteOptions.Builder)this.optionsBuilder, this.targetPageId, this.pageManager, this.transactionTemplate);
        }
    }
}

