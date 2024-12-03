/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collections;

@WebSudoRequired
@AdminOnly
public class SearchIndexesAction
extends ConfluenceActionSupport
implements Beanable {
    private IndexManager indexManager;
    private ILuceneConnection searchLuceneConnection;
    private DarkFeatureManager salDarkFeatureManager;

    public boolean isSearchIndexBuildInProgress() {
        return this.indexManager.isReIndexing();
    }

    public boolean searchIndexExists() {
        return this.searchLuceneConnection.getNumDocs() > 0;
    }

    public String getLastSearchIndexRebuildElapsedTime() {
        ReIndexTask lastReindexingTask = this.indexManager.getLastReindexingTask();
        if (lastReindexingTask != null) {
            return lastReindexingTask.getCompactElapsedTime();
        }
        return null;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) || this.permissionManager.isSystemAdministrator(this.getAuthenticatedUser());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return this.salDarkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false) != false ? "redirect" : "success";
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setLuceneConnection(ILuceneConnection luceneConnection) {
        this.searchLuceneConnection = luceneConnection;
    }

    public void setSalDarkFeatureManager(DarkFeatureManager salDarkFeatureManager) {
        this.salDarkFeatureManager = salDarkFeatureManager;
    }

    public String reIndex() {
        this.indexManager.reIndex();
        return "success";
    }

    @Override
    public Object getBean() {
        return Collections.emptyMap();
    }
}

