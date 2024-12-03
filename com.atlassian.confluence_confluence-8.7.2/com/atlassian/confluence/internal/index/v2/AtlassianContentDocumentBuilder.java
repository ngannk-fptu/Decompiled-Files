/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.collect.Collections2
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.event.events.index.IndexDocumentBuildEvent;
import com.atlassian.confluence.internal.index.v2.Extractor2DocumentBuilder;
import com.atlassian.confluence.internal.search.extractor2.Extractor2Provider;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchResultType;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.collect.Collections2;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class AtlassianContentDocumentBuilder
implements AtlassianDocumentBuilder<Searchable> {
    private static final Logger log = LoggerFactory.getLogger(AtlassianContentDocumentBuilder.class);
    private final AtlassianDocumentBuilder<Searchable> delegate = new Extractor2DocumentBuilder<Searchable>(() -> extractor2Provider.get(SearchIndex.CONTENT, true));
    private final Supplier<Boolean> isSiteReindexingChecker;
    private final EventPublisher eventPublisher;

    public AtlassianContentDocumentBuilder(Extractor2Provider extractor2Provider, EventPublisher eventPublisher, Supplier<Boolean> isSiteReindexingChecker) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.isSiteReindexingChecker = Objects.requireNonNull(isSiteReindexingChecker);
    }

    @Override
    public AtlassianDocument build(Searchable searchable) {
        try (Ticker ignored = Timers.start((String)"ContentDocumentBuilder.build");){
            log.debug("Creating Lucene Document for {}", (Object)searchable);
            long start = System.currentTimeMillis();
            AtlassianDocument document = this.delegate.build(searchable);
            document.addField(this.createDocumentTypeField());
            document.addField(this.createContentDocumentIdField(searchable));
            long end = System.currentTimeMillis();
            if (!this.isSiteReindexingChecker.get().booleanValue()) {
                this.eventPublisher.publish((Object)new IndexDocumentBuildEvent(start, end, SearchResultType.CONTENT.name(), searchable));
            }
            log.debug("Finished creating Lucene document for {} with fields {}", (Object)searchable, (Object)Collections2.transform(document.getFields(), rec$ -> ((FieldDescriptor)rec$).getName()));
            AtlassianDocument atlassianDocument = document;
            return atlassianDocument;
        }
    }

    private FieldDescriptor createContentDocumentIdField(Searchable searchable) {
        return FieldMappings.CONTENT_DOCUMENT_ID.createField(new HibernateHandle(searchable).toString());
    }

    private FieldDescriptor createDocumentTypeField() {
        return SearchFieldMappings.DOCUMENT_TYPE.createField(SearchResultType.CONTENT.name());
    }

    public static class FieldMappings {
        public static final StringFieldMapping CONTENT_DOCUMENT_ID = StringFieldMapping.builder("content-document-id").build();
    }
}

