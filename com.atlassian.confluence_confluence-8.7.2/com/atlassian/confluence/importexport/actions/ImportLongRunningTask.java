/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.event.events.cluster.ClusterReindexRequiredEvent;
import com.atlassian.confluence.importexport.ImmutableImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ImportLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private static final Logger log = LoggerFactory.getLogger(ImportLongRunningTask.class);
    private final EventPublisher eventPublisher;
    private final IndexManager indexManager;
    private final ImportExportManager importExportManager;
    private final ImportContext context;
    private final Supplier<Boolean> shouldPublishReindexEvent;
    private final AtomicReference<ImmutableImportProcessorSummary> resultRef = new AtomicReference();

    public ImportLongRunningTask(EventPublisher eventPublisher, IndexManager indexManager, ImportExportManager importExportManager, ImportContext context) {
        this(eventPublisher, indexManager, importExportManager, context, () -> true);
    }

    public ImportLongRunningTask(EventPublisher eventPublisher, IndexManager indexManager, ImportExportManager importExportManager, ImportContext context, Supplier<Boolean> shouldPublishReindexEvent) {
        this.eventPublisher = eventPublisher;
        this.indexManager = indexManager;
        this.importExportManager = importExportManager;
        this.context = context;
        this.shouldPublishReindexEvent = Objects.requireNonNull(shouldPublishReindexEvent);
    }

    @Override
    public void runInternal() {
        ConfluenceUser user = this.context.getUser();
        if (log.isInfoEnabled()) {
            log.info("Beginning import by user {}", (Object)(user != null ? user.getName() : "null"));
        }
        if (user != null) {
            AuthenticatedUserThreadLocal.set(user);
        }
        try {
            this.context.setProgressMeter(this.progress);
            this.resultRef.set(this.importExportManager.performImport(this.context));
            if (this.context.isRebuildIndex()) {
                if (this.shouldPublishReindexEvent.get().booleanValue()) {
                    this.eventPublisher.publish((Object)new ClusterReindexRequiredEvent("global import"));
                }
                this.indexManager.reIndex();
            }
            log.info("Finished import.");
        }
        catch (Exception e) {
            log.error("Failure during import", (Throwable)e);
            this.progress.setStatus(this.getI18nBean().getText("error.restore.failed", new String[]{e.getMessage()}));
            this.progress.setCompletedSuccessfully(false);
        }
        finally {
            AuthenticatedUserThreadLocal.reset();
        }
    }

    protected I18NBean getI18nBean() {
        return GeneralUtil.getI18n();
    }

    public String getName() {
        return "Importing data";
    }

    public String getNameKey() {
        return "import.data.task.name";
    }

    public ImmutableImportProcessorSummary getImportProcessorSummary() {
        return this.resultRef.get();
    }
}

