/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.ScoreDoc
 *  org.apache.lucene.search.Scorer
 *  org.apache.lucene.search.TopDocsCollector
 *  org.apache.lucene.search.TopScoreDocCollector
 *  org.apache.lucene.util.BytesRef
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class TopScoreDocCategorisedCollector<T>
extends Collector {
    private static final Logger log = LoggerFactory.getLogger(TopScoreDocCategorisedCollector.class);
    private final Map<T, TopDocsCollector> collectorByCategory;
    private final Set<T> supportedCategories;
    private BinaryDocValues docValues;

    TopScoreDocCategorisedCollector(SearchManager.Categorizer<T> categorizer) {
        if (categorizer == null) {
            throw new IllegalArgumentException("categorizer cannot be null");
        }
        this.supportedCategories = categorizer.getCategories();
        this.collectorByCategory = this.supportedCategories.stream().collect(Collectors.toMap(Function.identity(), x -> TopScoreDocCollector.create((int)categorizer.getLimit(x), (boolean)false)));
    }

    public boolean acceptsDocsOutOfOrder() {
        return true;
    }

    public void collect(int doc) throws IOException {
        BytesRef byteRef = new BytesRef();
        this.docValues.get(doc, byteRef);
        String contentType = byteRef.utf8ToString();
        log.debug("doc = {} - contentType = {}", (Object)doc, (Object)contentType);
        Set collectors = Category.getCategories(contentType).stream().filter(this.supportedCategories::contains).map(this.collectorByCategory::get).collect(Collectors.toSet());
        for (TopDocsCollector collector : collectors) {
            collector.collect(doc);
        }
    }

    public void setNextReader(AtomicReaderContext context) throws IOException {
        for (Collector collector : this.collectorByCategory.values()) {
            collector.setNextReader(context);
        }
        this.docValues = FieldCache.DEFAULT.getTerms(context.reader(), SearchFieldNames.TYPE);
    }

    public void setScorer(Scorer scorer) throws IOException {
        for (Collector collector : this.collectorByCategory.values()) {
            collector.setScorer(scorer);
        }
    }

    public void forEach(BiConsumer<T, ScoreDoc[]> consumer) {
        this.collectorByCategory.forEach((? super K key, ? super V value) -> consumer.accept(key, value.topDocs().scoreDocs));
    }

    @VisibleForTesting
    Map<T, TopDocsCollector> getCollectorByCategory() {
        return this.collectorByCategory;
    }
}

