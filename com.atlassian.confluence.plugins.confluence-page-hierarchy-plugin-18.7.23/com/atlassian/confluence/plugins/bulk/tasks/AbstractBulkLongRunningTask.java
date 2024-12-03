/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.persistence.dao.bulk.DefaultBulkOptions$BaseBuilder
 *  com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.bulk.tasks;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.DefaultBulkOptions;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public abstract class AbstractBulkLongRunningTask<BUILDER extends DefaultBulkOptions.BaseBuilder>
extends ConfluenceAbstractLongRunningTask {
    protected final BUILDER optionsBuilder;
    protected final PageManager pageManager;
    private final TransactionTemplate transactionTemplate;

    public AbstractBulkLongRunningTask(BUILDER optionsBuilder, PageManager pageManager, TransactionTemplate transactionTemplate) {
        this.optionsBuilder = optionsBuilder;
        this.pageManager = pageManager;
        this.transactionTemplate = transactionTemplate;
    }

    protected final void runInternal() {
        this.optionsBuilder.withProgressMeter(this.progress);
        this.transactionTemplate.execute(() -> {
            this.execute();
            return null;
        });
    }

    protected abstract void execute();

    public static abstract class BaseBuilder<T extends BaseBuilder, OB extends DefaultBulkOptions.BaseBuilder> {
        protected OB optionsBuilder;
        protected PageManager pageManager;
        protected TransactionTemplate transactionTemplate;

        protected abstract T builder();

        public final T withOptionsBuilder(OB optionsBuilder) {
            this.optionsBuilder = optionsBuilder;
            return this.builder();
        }

        public final T withPageManager(PageManager pageManager) {
            this.pageManager = pageManager;
            return this.builder();
        }

        public final T withTransactionTemplate(TransactionTemplate transactionTemplate) {
            this.transactionTemplate = transactionTemplate;
            return this.builder();
        }
    }
}

