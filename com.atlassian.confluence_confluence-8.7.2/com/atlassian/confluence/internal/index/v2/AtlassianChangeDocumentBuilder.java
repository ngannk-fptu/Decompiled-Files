/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.internal.index.v2.Extractor2DocumentBuilder;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
import com.atlassian.confluence.internal.search.extractor2.Extractor2Provider;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;

@Internal
public class AtlassianChangeDocumentBuilder
implements AtlassianDocumentBuilder<Searchable> {
    private final AtlassianDocumentBuilder<Searchable> latestVersionedDelegate = new Extractor2DocumentBuilder<Searchable>(() -> extractor2Provider.get(SearchIndex.CHANGE, true));
    private final AtlassianDocumentBuilder<Searchable> nonLatestVersionedDelegate = new Extractor2DocumentBuilder<Searchable>(() -> extractor2Provider.get(SearchIndex.CHANGE, false));

    public AtlassianChangeDocumentBuilder(Extractor2Provider extractor2Provider) {
    }

    @Override
    public AtlassianDocument build(Searchable searchable) {
        try (Ticker ignored = Timers.start((String)"ChangeDocumentBuilder.build");){
            ChangeDocumentIndexPolicy.PolicyCheckResult policyCheckResult = ChangeDocumentIndexPolicy.buildFor(searchable);
            if (policyCheckResult.passed()) {
                AtlassianDocument completeDocument = this.nonLatestVersionedDelegate.build(searchable);
                Versioned latestVersion = ((Versioned)searchable).getLatestVersion();
                Searchable latestSearchable = (Searchable)latestVersion;
                completeDocument.addFields(this.latestVersionedDelegate.build(latestSearchable).getFields());
                AtlassianDocument atlassianDocument = completeDocument;
                return atlassianDocument;
            }
            throw new UnsupportedOperationException(policyCheckResult.getErrorMessage());
        }
    }
}

