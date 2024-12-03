/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Scorer
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.internal.index.lucene.AbstractDocumentCollector;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;

public class FieldValuesCollector
extends AbstractDocumentCollector<Map<String, String[]>> {
    public FieldValuesCollector(IndexSearcher indexSearcher, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) {
        super(indexSearcher, requestedFields, consumer);
    }

    @Override
    public Map<String, String[]> toConsumable(Document document) {
        return this.getRequestedFields(document);
    }

    public void setScorer(Scorer scorer) {
    }
}

