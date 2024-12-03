/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Scorer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.internal.index.lucene.AbstractDocumentCollector;
import com.atlassian.confluence.search.v2.ScannedDocument;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoredDocumentCollector
extends AbstractDocumentCollector<ScannedDocument> {
    private static final Logger log = LoggerFactory.getLogger(ScoredDocumentCollector.class);
    private final float defaultScore;
    private Scorer scorer;

    public ScoredDocumentCollector(IndexSearcher indexSearcher, Set<String> requestedFields, Consumer<ScannedDocument> consumer, float defaultScore) {
        super(indexSearcher, requestedFields, consumer);
        this.defaultScore = defaultScore;
    }

    @Override
    public ScannedDocument toConsumable(Document document) {
        float score = this.defaultScore;
        try {
            score = this.scorer.score();
        }
        catch (IOException e) {
            log.error("Cannot get the score for the document", (Throwable)e);
        }
        return new ScannedDocument(score, this.getRequestedFields(document));
    }

    public void setScorer(Scorer scorer) throws IOException {
        this.scorer = scorer;
    }
}

