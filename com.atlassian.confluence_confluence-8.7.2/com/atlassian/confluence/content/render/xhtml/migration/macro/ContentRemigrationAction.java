/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.confluence.content.render.xhtml.migration.macro.MacroMigrationService;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.Collections;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class ContentRemigrationAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ContentRemigrationAction.class);
    private static final long serialVersionUID = 1L;
    private static final int MAX_SHOWN_RESULTS = 25;
    private SearchResults macroSearchResult;
    private MacroMigrationService macroMigrationService;

    public void setMacroMigrationService(MacroMigrationService migrationService) {
        this.macroMigrationService = migrationService;
    }

    @Override
    public String doDefault() throws Exception {
        if (!this.macroMigrationService.isMigrating()) {
            this.macroSearchResult = this.macroMigrationService.findPagesWithUnmigratedMacros(25);
            return "input";
        }
        return "success";
    }

    public String execute() throws Exception {
        log.info("Executing MacroMigration Action ");
        this.macroMigrationService.migrateAll();
        return "success";
    }

    public boolean hasSearchResults() {
        if (this.macroSearchResult == null) {
            return false;
        }
        return this.macroSearchResult.getUnfilteredResultsCount() > 0;
    }

    public int getNumSearchResults() {
        if (this.macroSearchResult == null) {
            return 0;
        }
        return this.macroSearchResult.getUnfilteredResultsCount();
    }

    public LongRunningTaskId getTaskId() {
        return this.macroMigrationService.getLastMigrationId();
    }

    public Iterator<SearchResult> getSearchResults() {
        if (this.macroSearchResult == null) {
            return Collections.emptyIterator();
        }
        return this.macroSearchResult.iterator();
    }

    public int getMaxShownResults() {
        return 25;
    }

    public boolean isMigrationRequired() {
        return this.macroMigrationService.isMigrationRequired();
    }
}

