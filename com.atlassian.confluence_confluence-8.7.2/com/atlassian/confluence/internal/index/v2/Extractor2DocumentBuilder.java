/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.confluence.internal.index.v2.BulkExtractor2Adapter;
import com.atlassian.confluence.internal.index.v2.CompositeExtractor;
import com.atlassian.confluence.internal.search.extractor2.BulkExtractorProvider;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class Extractor2DocumentBuilder<T>
implements AtlassianDocumentBuilder<T> {
    private final Supplier<Iterable<Extractor2>> extractorsSupplier;
    private final CompositeExtractor compositeExtractor = new CompositeExtractor();

    public Extractor2DocumentBuilder(Supplier<Iterable<Extractor2>> extractorsSupplier) {
        this.extractorsSupplier = Objects.requireNonNull(extractorsSupplier);
    }

    public static <T> AtlassianDocumentBuilder<T> forContentIndex(BulkExtractorProvider bulkExtractorProvider) {
        return new Extractor2DocumentBuilder<T>(() -> bulkExtractorProvider.findBulkExtractors(SearchIndex.CONTENT).stream().map(extractor -> extractor).map(BulkExtractor2Adapter::new).collect(Collectors.toList()));
    }

    @Override
    public AtlassianDocument build(T searchable) {
        Iterable<Extractor2> extractors = this.extractorsSupplier.get();
        Collection<FieldDescriptor> fields = this.compositeExtractor.extract(searchable, extractors);
        AtlassianDocument document = new AtlassianDocument();
        document.addFields(fields);
        return document;
    }
}

