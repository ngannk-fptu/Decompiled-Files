/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.document.DocumentStoredFieldVisitor
 *  org.apache.lucene.index.StoredFieldVisitor
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.ScoreDoc
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchResult;
import com.atlassian.confluence.search.summary.HitHighlighter;
import com.atlassian.confluence.search.v2.ProjectedSearchResult;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Function;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DocumentStoredFieldVisitor;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public class LuceneSearchResultFactory {
    private final com.atlassian.confluence.impl.search.v2.UserLookupHelper userLookupHelper = new com.atlassian.confluence.impl.search.v2.UserLookupHelper();
    private final Set<String> requestedFields;

    public LuceneSearchResultFactory() {
        this(Collections.emptySet());
    }

    public LuceneSearchResultFactory(Set<String> requestedFields) {
        this.requestedFields = requestedFields != null ? requestedFields : Collections.emptySet();
    }

    SearchResult createSearchResult(ScoreDoc hit, IndexSearcher searcher) throws IOException {
        Document doc;
        if (!this.hasRequestedFields()) {
            doc = searcher.doc(hit.doc);
        } else {
            DocumentStoredFieldVisitor selector = new DocumentStoredFieldVisitor(this.requestedFields);
            searcher.doc(hit.doc, (StoredFieldVisitor)selector);
            doc = selector.getDocument();
        }
        return this.createSearchResult(doc, Optional.empty());
    }

    SearchResult createSearchResult(Document document, Optional<HitHighlighter> maybeHighlighter) {
        return this.createSearchResult(document, maybeHighlighter, Optional.empty());
    }

    public SearchResult createSearchResult(Document document, Optional<HitHighlighter> maybeHighlighter, Optional<String> maybeExplanation) {
        LuceneSearchResult result = new LuceneSearchResult(document, maybeHighlighter, maybeExplanation, (java.util.function.Function<String, ConfluenceUser>)((Object)this.userLookupHelper));
        if (!this.hasRequestedFields()) {
            return result;
        }
        return new ProjectedSearchResult(result, this.requestedFields);
    }

    private boolean hasRequestedFields() {
        return !this.requestedFields.isEmpty();
    }

    @Deprecated
    public static class UserLookupHelper
    implements Function<String, ConfluenceUser> {
        private final Function<String, ConfluenceUser> delegate = new com.atlassian.confluence.impl.search.v2.UserLookupHelper();

        public ConfluenceUser apply(String input) {
            return (ConfluenceUser)this.delegate.apply((Object)input);
        }
    }
}

