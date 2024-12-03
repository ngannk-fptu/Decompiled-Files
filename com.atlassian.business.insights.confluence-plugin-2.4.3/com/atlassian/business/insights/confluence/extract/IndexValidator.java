/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.extract.StreamerValidationResult
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.business.insights.api.extract.StreamerValidationResult;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.sal.api.message.I18nResolver;

public class IndexValidator {
    public static final String REINDEX_IN_PROGESS_KEY = "data-pipeline.confluence.index.error.reindexing";
    private final IndexManager indexManager;
    private final I18nResolver i18nResolver;

    public IndexValidator(IndexManager indexManager, I18nResolver i18nResolver) {
        this.indexManager = indexManager;
        this.i18nResolver = i18nResolver;
    }

    public StreamerValidationResult validate() {
        if (this.indexManager.isReIndexing()) {
            return StreamerValidationResult.fail((String)this.i18nResolver.getText(REINDEX_IN_PROGESS_KEY));
        }
        return StreamerValidationResult.pass();
    }
}

