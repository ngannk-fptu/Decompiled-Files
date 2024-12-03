/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.migration.AbstractExceptionTolerantMigrator
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator$MigrationResult
 *  com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.query.MacroUsageQuery
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.roadmap.upgradetask;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.AbstractExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.MacroUsageQuery;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugins.roadmap.upgradetask.ContentEntityMigrationBatchTask;
import com.atlassian.plugins.roadmap.upgradetask.SearchResultsBatchWorkSource;
import com.atlassian.plugins.roadmap.upgradetask.WorkSourceBatchRunner;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CurrentSpacesParamMigrator {
    private static final Logger log = LoggerFactory.getLogger(CurrentSpacesParamMigrator.class);
    private static final String CURRENT_SPACES_PARAM = "currentspaces";
    private static final int BATCH_SIZE = 50;
    private final SearchManager searchManager;
    private final CustomContentManager contentManager;
    private final WorkSourceBatchRunner<ContentEntityObject> batchRunner;
    private final XhtmlContent xhtmlContent;
    private volatile boolean inProgress = false;
    private AtomicInteger numFailed;
    private AtomicInteger numMigrated;
    private AtomicInteger numMigrationNotRequired;
    private final Function<Searchable, ContentEntityObject> searchableToCEOTransformer = from -> {
        BodyContent bodyContent;
        if (from instanceof ContentEntityObject && (bodyContent = ((ContentEntityObject)from).getBodyContent()).getBodyType().equals((Object)BodyType.XHTML)) {
            return (ContentEntityObject)from;
        }
        return null;
    };

    public CurrentSpacesParamMigrator(XhtmlContent xhtmlContent, SearchManager searchManager, CustomContentManager contentManager, WorkSourceBatchRunner<ContentEntityObject> batchRunner) {
        this.contentManager = contentManager;
        this.searchManager = searchManager;
        this.batchRunner = batchRunner;
        this.xhtmlContent = xhtmlContent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void migrate() throws MigrationException {
        CurrentSpacesParamMigrator currentSpacesParamMigrator = this;
        synchronized (currentSpacesParamMigrator) {
            if (this.inProgress) {
                throw new IllegalStateException("Another 'currentspaces' parameter migration is currently in progress");
            }
            this.resetMigrationFlags();
        }
        try {
            this.doMigration();
        }
        catch (InvalidSearchException e) {
            throw new MigrationException("Error searching for macro usages", (Throwable)e);
        }
        catch (Exception e) {
            throw new MigrationException((Throwable)e);
        }
        finally {
            this.inProgress = false;
        }
    }

    private void resetMigrationFlags() {
        this.inProgress = true;
        this.numFailed = new AtomicInteger(0);
        this.numMigrated = new AtomicInteger(0);
        this.numMigrationNotRequired = new AtomicInteger(0);
    }

    private void doMigration() throws Exception {
        log.info("Started migration of Roadmap macros");
        this.migrateCurrentContents();
        log.info("Finished migration of Roadmap macros:\n" + this.numMigrated + " Roadmap macro(s) were updated\n" + this.numMigrationNotRequired + " Roadmap macro(s) did not require any changes\n" + this.numFailed + " Roadmap macro(s) could not be updated\n");
    }

    private SearchResults findPagesWithRoadmapMacro() throws InvalidSearchException {
        MacroUsageQuery query = new MacroUsageQuery("roadmap");
        ContentSearch search = new ContentSearch((SearchQuery)query, null, 0, Integer.MAX_VALUE);
        return this.searchManager.search((ISearch)search);
    }

    private void migrateCurrentContents() throws Exception {
        SearchResults searchResults = this.findPagesWithRoadmapMacro();
        SearchResultsBatchWorkSource workSource = new SearchResultsBatchWorkSource(this.searchManager, searchResults.getAll(), 50, this.searchableToCEOTransformer::apply);
        ContentEntityMigrationBatchTask batchTask = new ContentEntityMigrationBatchTask((ExceptionTolerantMigrator)new RoadmapMacroParamsContentEntityMigrator(), (ContentEntityManager)this.contentManager);
        this.batchRunner.run(workSource, batchTask);
    }

    class RoadmapMacroParamsContentEntityMigrator
    extends AbstractExceptionTolerantMigrator {
        RoadmapMacroParamsContentEntityMigrator() {
        }

        public ExceptionTolerantMigrator.MigrationResult migrate(String content, ConversionContext conversionContext) {
            try {
                final ArrayList migrations = Lists.newArrayList();
                String migratedContent = CurrentSpacesParamMigrator.this.xhtmlContent.updateMacroDefinitions(content, conversionContext, new MacroDefinitionUpdater(){

                    public MacroDefinition update(MacroDefinition macroDefinition) {
                        if (StringUtils.equals((CharSequence)macroDefinition.getName(), (CharSequence)"roadmap")) {
                            Map params = macroDefinition.getParameters();
                            if (params.containsKey(CurrentSpacesParamMigrator.CURRENT_SPACES_PARAM)) {
                                migrations.add(true);
                                RoadmapMacroParamsContentEntityMigrator.this.processParams(macroDefinition);
                                CurrentSpacesParamMigrator.this.numMigrated.incrementAndGet();
                            } else {
                                CurrentSpacesParamMigrator.this.numMigrationNotRequired.incrementAndGet();
                            }
                        }
                        return macroDefinition;
                    }
                });
                return new ExceptionTolerantMigrator.MigrationResult(migratedContent, migrations.size() > 0);
            }
            catch (XhtmlException e) {
                log.info("Encountered an exception during Roadmap macro migration", (Throwable)e);
                CurrentSpacesParamMigrator.this.numFailed.incrementAndGet();
                return new ExceptionTolerantMigrator.MigrationResult(content, false);
            }
        }

        private void processParams(MacroDefinition macroDefinition) {
            macroDefinition.setParameter(CurrentSpacesParamMigrator.CURRENT_SPACES_PARAM, null);
            macroDefinition.setTypedParameter(CurrentSpacesParamMigrator.CURRENT_SPACES_PARAM, null);
        }
    }
}

