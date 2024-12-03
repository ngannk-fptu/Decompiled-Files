/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.util.concurrent.atomic.AtomicReference
 *  com.google.common.base.Function
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.migration.macro.ContentEntityMigrationBatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.macro.IndexingBatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.macro.RemigrationLongRunningTask;
import com.atlassian.confluence.content.render.xhtml.migration.macro.SearchResultsBatchWorkSource;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.xhtml.XhtmlWikiMarkupMacroMigrator;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.MacroStorageVersionQuery;
import com.atlassian.confluence.search.v2.query.MacroUsageQuery;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.util.concurrent.atomic.AtomicReference;
import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class MacroMigrationService {
    private static final Logger log = LoggerFactory.getLogger(MacroMigrationService.class);
    static final BandanaContext bandanaContext = new ConfluenceBandanaContext(MacroMigrationService.class.getName());
    private static final String MIGRATION_REQUIRED_BANDANA_KEY = "migration.required";
    private static final String IGNORE_ADMIN_TASK_BANDANA_KEY = "migration.admintask.ignore";
    static final int DEFAULT_BATCH_SIZE = 50;
    private static final String BATCH_SIZE_PROP = "remigration.batchsize";
    private final XhtmlWikiMarkupMacroMigrator xhtmlWikiMarkupMacroMigrator;
    private final SearchManager searchManager;
    private final ContentDao contentDao;
    private final LongRunningTaskManager longRunningTaskManager;
    private final PlatformTransactionManager platformTransactionManager;
    private final BandanaManager bandanaManager;
    private final ConfluenceIndexer indexer;
    static final Function<Searchable, ContentEntityObject> searchableToCEOTransformer = from -> {
        if (from instanceof ContentEntityObject && ((ContentEntityObject)from).getBodyContent().getBodyType().equals(BodyType.XHTML)) {
            return (ContentEntityObject)from;
        }
        return null;
    };
    private final AtomicReference<RemigrationLongRunningTask> lastMigrationTaskRef = new AtomicReference();

    public MacroMigrationService(XhtmlWikiMarkupMacroMigrator migrator, SearchManager searchManager, ContentDao contentDao, LongRunningTaskManager longRunningTaskManager, PlatformTransactionManager platformTransactionManager, BandanaManager bandanaManager, ConfluenceIndexer indexer) {
        this.xhtmlWikiMarkupMacroMigrator = migrator;
        this.searchManager = searchManager;
        this.contentDao = contentDao;
        this.longRunningTaskManager = longRunningTaskManager;
        this.platformTransactionManager = platformTransactionManager;
        this.bandanaManager = bandanaManager;
        this.indexer = indexer;
    }

    public @Nullable LongRunningTaskId migrateAll() throws InvalidSearchException {
        RemigrationLongRunningTask currentMigrationTask = (RemigrationLongRunningTask)((Object)this.lastMigrationTaskRef.get());
        if (currentMigrationTask != null && !currentMigrationTask.isComplete()) {
            return currentMigrationTask.getTaskId();
        }
        int unFilteredResultsCount = this.findPagesWithUnmigratedMacros(1).getUnfilteredResultsCount();
        if (unFilteredResultsCount <= 0) {
            return null;
        }
        SearchResults searchResults = this.findPagesWithUnmigratedMacros(unFilteredResultsCount);
        SearchResultsBatchWorkSource<ContentEntityObject> workSource = new SearchResultsBatchWorkSource<ContentEntityObject>(this.searchManager, searchResults.getAll(), Integer.getInteger(BATCH_SIZE_PROP, 50), searchableToCEOTransformer);
        RemigrationLongRunningTask newTask = new RemigrationLongRunningTask(workSource, this.platformTransactionManager, this.getBatchTask(), this);
        RemigrationLongRunningTask lastMigrationTask = (RemigrationLongRunningTask)((Object)this.lastMigrationTaskRef.get());
        if (lastMigrationTask == null || lastMigrationTask.isComplete()) {
            currentMigrationTask = (RemigrationLongRunningTask)((Object)this.lastMigrationTaskRef.getOrSetAndGetIf((Object)lastMigrationTask, (Object)newTask));
        } else {
            log.info("MacroMigrationService: Call to migrate all whilst another migration task is running, other task is {}% complete.", (Object)lastMigrationTask.getPercentageComplete());
            currentMigrationTask = lastMigrationTask;
        }
        if (currentMigrationTask != null && ((Object)((Object)currentMigrationTask)).equals((Object)newTask)) {
            LongRunningTaskId id = this.longRunningTaskManager.startLongRunningTask(null, (LongRunningTask)currentMigrationTask);
            currentMigrationTask.setTaskId(id);
            if (lastMigrationTask != null) {
                this.longRunningTaskManager.stopTrackingLongRunningTask(lastMigrationTask.getTaskId());
            }
        }
        return currentMigrationTask == null ? null : currentMigrationTask.getTaskId();
    }

    public boolean isMigrating() {
        LongRunningTask lastMigrationTask = (LongRunningTask)this.lastMigrationTaskRef.get();
        return lastMigrationTask != null && !lastMigrationTask.isComplete();
    }

    public LongRunningTaskId getLastMigrationId() {
        RemigrationLongRunningTask task = (RemigrationLongRunningTask)((Object)this.lastMigrationTaskRef.get());
        if (task != null) {
            return task.getTaskId();
        }
        return null;
    }

    public SearchResults findPagesWithUnmigratedMacros(int maxResults) throws InvalidSearchException {
        ContentSearch search = new ContentSearch(this.getAllLegacyMacrosQuery(), null, 0, maxResults);
        return this.searchManager.search(search, SearchFieldNames.withDefaultValues());
    }

    private SearchQuery getAllLegacyMacrosQuery() {
        return new BooleanQuery(null, Sets.newHashSet((Object[])new SearchQuery[]{new MacroUsageQuery("unmigrated-wiki-markup"), new MacroUsageQuery("unmigrated-inline-wiki-markup"), new MacroStorageVersionQuery("*", 1)}), null);
    }

    private BatchTask<ContentEntityObject> getBatchTask() {
        return new IndexingBatchTask<ContentEntityObject>(new ContentEntityMigrationBatchTask(this.xhtmlWikiMarkupMacroMigrator, this.contentDao, "Migration of unmigrated content due to installation of a new plugin"), this.indexer);
    }

    public void updateMigrationRequired() {
        try {
            SearchResults results = this.findPagesWithUnmigratedMacros(1);
            boolean required = results.size() > 0;
            this.setMigrationRequired(required);
        }
        catch (InvalidSearchException e) {
            log.warn("Could not store migration required status, error with search", (Throwable)e);
        }
    }

    void setMigrationRequired(boolean req) {
        log.debug("Setting migration required to {}", (Object)req);
        this.bandanaManager.setValue(bandanaContext, MIGRATION_REQUIRED_BANDANA_KEY, (Object)req);
    }

    public boolean isMigrationRequired() {
        Boolean required = (Boolean)this.bandanaManager.getValue(bandanaContext, MIGRATION_REQUIRED_BANDANA_KEY);
        if (required == null) {
            this.setMigrationRequired(false);
            return false;
        }
        return required;
    }

    public void setAdminTaskIgnored(boolean ignoreIt) {
        this.bandanaManager.setValue(bandanaContext, IGNORE_ADMIN_TASK_BANDANA_KEY, (Object)ignoreIt);
    }

    public boolean isAdminTaskIgnored() {
        Object obj = this.bandanaManager.getValue(bandanaContext, IGNORE_ADMIN_TASK_BANDANA_KEY);
        if (obj instanceof Boolean) {
            return (Boolean)obj;
        }
        return false;
    }
}

