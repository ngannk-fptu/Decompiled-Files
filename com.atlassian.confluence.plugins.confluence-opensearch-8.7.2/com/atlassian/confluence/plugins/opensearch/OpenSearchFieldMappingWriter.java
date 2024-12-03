/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.plugins.index.api.mapping.BinaryFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.BooleanFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.DateFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.DoubleFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.FieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor
 *  com.atlassian.confluence.plugins.index.api.mapping.FloatFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.IntFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.TextFieldMapping
 *  com.atlassian.confluence.search.v2.FieldMappings$FieldMappingWriter
 *  com.atlassian.confluence.search.v2.SearchIndexAccessException
 *  org.opensearch.client.opensearch.OpenSearchClient
 *  org.opensearch.client.opensearch._types.mapping.BooleanProperty$Builder
 *  org.opensearch.client.opensearch._types.mapping.DateProperty$Builder
 *  org.opensearch.client.opensearch._types.mapping.DoubleNumberProperty$Builder
 *  org.opensearch.client.opensearch._types.mapping.FloatNumberProperty$Builder
 *  org.opensearch.client.opensearch._types.mapping.IntegerNumberProperty$Builder
 *  org.opensearch.client.opensearch._types.mapping.KeywordProperty$Builder
 *  org.opensearch.client.opensearch._types.mapping.LongNumberProperty$Builder
 *  org.opensearch.client.opensearch._types.mapping.Property
 *  org.opensearch.client.opensearch._types.mapping.Property$Builder
 *  org.opensearch.client.opensearch._types.mapping.TextProperty$Builder
 *  org.opensearch.client.opensearch.indices.GetFieldMappingRequest
 *  org.opensearch.client.opensearch.indices.GetFieldMappingResponse
 *  org.opensearch.client.opensearch.indices.PutMappingRequest
 *  org.opensearch.client.util.ObjectBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.plugins.index.api.mapping.BinaryFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.BooleanFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.DateFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.DoubleFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.FloatFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.IntFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.TextFieldMapping;
import com.atlassian.confluence.plugins.opensearch.OpenSearchAnalyzerMapper;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import java.io.IOException;
import java.util.Objects;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.BooleanProperty;
import org.opensearch.client.opensearch._types.mapping.DateProperty;
import org.opensearch.client.opensearch._types.mapping.DoubleNumberProperty;
import org.opensearch.client.opensearch._types.mapping.FloatNumberProperty;
import org.opensearch.client.opensearch._types.mapping.IntegerNumberProperty;
import org.opensearch.client.opensearch._types.mapping.KeywordProperty;
import org.opensearch.client.opensearch._types.mapping.LongNumberProperty;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TextProperty;
import org.opensearch.client.opensearch.indices.GetFieldMappingRequest;
import org.opensearch.client.opensearch.indices.GetFieldMappingResponse;
import org.opensearch.client.opensearch.indices.PutMappingRequest;
import org.opensearch.client.util.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class OpenSearchFieldMappingWriter
implements FieldMappings.FieldMappingWriter {
    private static final Logger LOG = LoggerFactory.getLogger(OpenSearchFieldMappingWriter.class);
    private final String indexName;
    private final OpenSearchClient client;
    private final OpenSearchAnalyzerMapper analyzerMapper;

    public OpenSearchFieldMappingWriter(String indexName, OpenSearchClient client, OpenSearchAnalyzerMapper analyzerMapper) {
        this.indexName = Objects.requireNonNull(indexName, "indexName is required");
        this.client = Objects.requireNonNull(client, "client is required");
        this.analyzerMapper = Objects.requireNonNull(analyzerMapper, "analyzerMapper is required");
    }

    public boolean putIfAbsent(FieldMapping mapping) throws SearchIndexAccessException {
        Objects.requireNonNull(mapping, "mapping is required");
        try {
            if (this.fieldExists(mapping.getName())) {
                LOG.debug("Field {} already exists on OpenSearch index {}", (Object)mapping.getName(), (Object)this.indexName);
                return false;
            }
            LOG.info("Creating field '{}' on OpenSearch index {}", (Object)mapping.getName(), (Object)this.indexName);
            Property property = this.mapFieldProperty(mapping);
            LOG.debug("PUT {}/{}/mapping: {}", new Object[]{this.indexName, mapping.getName(), property});
            PutMappingRequest request = PutMappingRequest.of(m -> m.index(this.indexName, new String[0]).properties(mapping.getName(), property));
            this.client.indices().putMapping(request);
            return true;
        }
        catch (IOException e) {
            throw new SearchIndexAccessException("Failed to create field mapping " + mapping.getName(), (Throwable)e);
        }
    }

    private boolean fieldExists(String name) throws IOException {
        GetFieldMappingResponse response = this.client.indices().getFieldMapping(GetFieldMappingRequest.of(f -> f.index(this.indexName, new String[0]).fields(name, new String[0])));
        return response.result().values().stream().anyMatch(v -> !v.mappings().isEmpty());
    }

    private Property mapFieldProperty(FieldMapping mapping) {
        return (Property)((ObjectBuilder)mapping.accept((FieldMappingVisitor)new PropertyFieldMappingVisitor())).build();
    }

    @ParametersAreNonnullByDefault
    private class PropertyFieldMappingVisitor
    implements FieldMappingVisitor<ObjectBuilder<Property>> {
        Property.Builder builder = new Property.Builder();

        private PropertyFieldMappingVisitor() {
        }

        public ObjectBuilder<Property> visit(StringFieldMapping mapping) {
            return this.builder.keyword(k -> ((KeywordProperty.Builder)k.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())));
        }

        public ObjectBuilder<Property> visit(TextFieldMapping mapping) {
            return this.builder.text(t -> ((TextProperty.Builder)t.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())).analyzer(OpenSearchFieldMappingWriter.this.analyzerMapper.getAnalyzerName(mapping.getAnalyzer())).searchAnalyzer(OpenSearchFieldMappingWriter.this.analyzerMapper.getAnalyzerName(mapping.getSearchAnalyzer())));
        }

        public ObjectBuilder<Property> visit(IntFieldMapping mapping) {
            return this.builder.integer(t -> (ObjectBuilder)((IntegerNumberProperty.Builder)t.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())));
        }

        public ObjectBuilder<Property> visit(LongFieldMapping mapping) {
            return this.builder.long_(t -> (ObjectBuilder)((LongNumberProperty.Builder)t.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())));
        }

        public ObjectBuilder<Property> visit(FloatFieldMapping mapping) {
            return this.builder.float_(t -> (ObjectBuilder)((FloatNumberProperty.Builder)t.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())));
        }

        public ObjectBuilder<Property> visit(DoubleFieldMapping mapping) {
            return this.builder.double_(t -> (ObjectBuilder)((DoubleNumberProperty.Builder)t.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())));
        }

        public ObjectBuilder<Property> visit(DateFieldMapping mapping) {
            return this.builder.date(t -> ((DateProperty.Builder)t.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())).format(mapping.getResolution().getFormat().toPattern()));
        }

        public ObjectBuilder<Property> visit(BinaryFieldMapping mapping) {
            return this.builder.binary(t -> (ObjectBuilder)t.store(Boolean.valueOf(mapping.isStored())));
        }

        public ObjectBuilder<Property> visit(BooleanFieldMapping mapping) {
            return this.builder.boolean_(t -> ((BooleanProperty.Builder)t.store(Boolean.valueOf(mapping.isStored()))).index(Boolean.valueOf(mapping.isIndexed())));
        }

        public ObjectBuilder<Property> visit(NestedStringFieldMapping mapping) {
            return this.builder.nested(t -> (ObjectBuilder)t.properties(mapping.getNestedFieldName(), Property.of(p -> p.keyword(k -> k))));
        }
    }
}

