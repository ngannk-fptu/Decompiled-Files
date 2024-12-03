/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventPublisher
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.event.events.admin.AsyncImportStartedEvent;
import com.atlassian.confluence.event.events.admin.ImportFinishedEvent;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportMutex;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.PostImportTask;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventPublisher;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class Importer {
    private static final Logger log = LoggerFactory.getLogger(Importer.class);
    protected ImportContext context;
    protected SessionFactory sessionFactory;
    private EventPublisher eventPublisher;
    private List<PostImportTask> postImportTasks = Collections.emptyList();

    public final void setContext(ImportContext context) {
        this.context = context;
    }

    public final void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public final void setPostImportTasks(List<PostImportTask> postImportTasks) {
        this.postImportTasks = postImportTasks;
    }

    public final void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final ImportProcessorSummary doImport() throws ImportExportException {
        this.publishEvent(new AsyncImportStartedEvent(this, this.context));
        log.info("Running pre-import tasks");
        this.preImport();
        boolean success = false;
        try {
            boolean deleted;
            log.info("Running main import");
            ImportMutex.INSTANCE.lockMutex(this);
            ImportProcessorSummary summary = this.doImportInternal();
            log.info("Running post-import success tasks");
            ArrayList<PostImportTask> allPostImportTasks = new ArrayList<PostImportTask>();
            allPostImportTasks.addAll(this.postImportTasks);
            allPostImportTasks.addAll(this.context.getPostImportTasks());
            for (PostImportTask postImportTask : allPostImportTasks) {
                postImportTask.execute(this.context);
            }
            if (this.context.isDeleteWorkingFile() && this.context.getWorkingFile() != null && !(deleted = new File(this.context.getWorkingFile()).delete())) {
                log.warn("Import working file [ {} ] could not be deleted", (Object)this.context.getWorkingFile());
            }
            success = true;
            ImportProcessorSummary importProcessorSummary = summary;
            return importProcessorSummary;
        }
        finally {
            try {
                log.info("Running post-import attempt tasks");
                this.postImportAndCleanUp();
                log.info("Finished running the post-import tasks");
            }
            catch (Throwable e) {
                if (success) {
                    log.info("Import was successful, but an exception was encountered while running the post-import tasks");
                    if (e instanceof ImportExportException) {
                        throw (ImportExportException)e;
                    }
                    if (e instanceof Error) {
                        throw (Error)e;
                    }
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException)e;
                    }
                    throw new ImportExportException(e);
                }
                log.info("Post-import attempt tasks also failed: ", e);
            }
            finally {
                if (success) {
                    log.info("Publishing import finished event");
                    this.publishEvent(new ImportFinishedEvent(this, this.context));
                    this.publishEvent(new AsyncImportFinishedEvent(this, this.context));
                    this.completeProgessMeter();
                }
                ImportMutex.INSTANCE.unlockMutex(this);
            }
        }
    }

    protected void completeProgessMeter() {
        ProgressMeter meter = this.context.getProgressMeter();
        meter.setPercentage(100);
        meter.setStatus("Complete.");
    }

    protected void preImport() throws ImportExportException {
    }

    protected void postImportAndCleanUp() throws ImportExportException {
    }

    protected abstract ImportProcessorSummary doImportInternal() throws ImportExportException;

    protected final void publishEvent(Object event) {
        this.eventPublisher.publish(event);
    }

    protected EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }
}

