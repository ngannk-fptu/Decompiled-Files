/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.BatchOperationManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugin.module.SearchBodyProperty
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Strings
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Multimap
 */
package com.atlassian.confluence.plugins.contentproperty.index.extractor;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.module.SearchBodyProperty;
import com.atlassian.confluence.plugins.contentproperty.ContentPropertyFinderFactory;
import com.atlassian.confluence.plugins.contentproperty.ContentPropertyFinderPermissionCheck;
import com.atlassian.confluence.plugins.contentproperty.index.extractor.ContentPropertyExtractionManager;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertyIndexSchemaManager;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class ContentPropertiesExtractor
implements Extractor2 {
    private final ContentPropertyIndexSchemaManager contentPropertyIndexSchemaManager;
    private final ContentPropertyExtractionManager contentPropertyExtractionManager;
    private final ContentPropertyFinderFactory contentPropertyFinderFactory;
    private final PluginAccessor pluginAccessor;
    private final BatchOperationManager batchOperationManager;

    public ContentPropertiesExtractor(ContentPropertyIndexSchemaManager contentPropertyIndexSchemaManager, ContentPropertyExtractionManager contentPropertyExtractionManager, ContentPropertyFinderFactory contentPropertyFinderFactory, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport BatchOperationManager batchOperationManager) {
        this.contentPropertyIndexSchemaManager = Objects.requireNonNull(contentPropertyIndexSchemaManager);
        this.contentPropertyExtractionManager = Objects.requireNonNull(contentPropertyExtractionManager);
        this.contentPropertyFinderFactory = Objects.requireNonNull(contentPropertyFinderFactory);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.batchOperationManager = Objects.requireNonNull(batchOperationManager);
    }

    public StringBuilder extractText(Object searchable) {
        StringBuilder defaultSearchableText = new StringBuilder();
        if (searchable instanceof CustomContentEntityObject) {
            CustomContentEntityObject entity = (CustomContentEntityObject)searchable;
            for (SearchBodyProperty searchBodyProperty : this.pluginAccessor.getEnabledModulesByClass(SearchBodyProperty.class)) {
                if (!entity.getContentTypeObject().equals((Object)searchBodyProperty.getContentType())) continue;
                defaultSearchableText.append(this.getContentProperty(entity, searchBodyProperty.getContentProperty()));
            }
        }
        return defaultSearchableText;
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        Multimap<String, ContentPropertySchemaField> indexSchema;
        ContentEntityObject contentEntityObject;
        ImmutableList.Builder fieldDescriptorBuilder = ImmutableList.builder();
        if (searchable instanceof ContentEntityObject && (contentEntityObject = (ContentEntityObject)searchable).getContentId() != null && !(indexSchema = this.contentPropertyIndexSchemaManager.getIndexSchema()).keySet().isEmpty()) {
            this.extractFieldDescriptors(contentEntityObject, indexSchema).stream().forEach(arg_0 -> ((ImmutableList.Builder)fieldDescriptorBuilder).add(arg_0));
        }
        return fieldDescriptorBuilder.build();
    }

    private Collection<FieldDescriptor> extractFieldDescriptors(ContentEntityObject contentEntityObject, Multimap<String, ContentPropertySchemaField> indexSchema) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        Function<List, List> getJsonContentPropertiesByIdAndKeys = schemaKeys -> {
            PageResponse jsonContentPropertiesPageResponse = this.contentPropertyFinderFactory.createContentPropertyFinder(ContentPropertyFinderPermissionCheck.NO, new Expansion[0]).withContentId(contentEntityObject.getContentId()).withPropertyKeys(schemaKeys).fetchMany((PageRequest)new SimplePageRequest(0, schemaKeys.size()));
            return jsonContentPropertiesPageResponse.getResults();
        };
        int ORACLE_DB_IN_CLAUSE_LIMIT = 1000;
        Iterable jsonContentProperties = this.batchOperationManager.applyInChunks((Iterable)indexSchema.keySet(), 1000, indexSchema.keySet().size(), getJsonContentPropertiesByIdAndKeys);
        StreamSupport.stream(jsonContentProperties.spliterator(), false).forEach(jsonContentProperty -> resultBuilder.addAll(this.contentPropertyExtractionManager.extract(jsonContentProperty.getValue(), Collections2.filter((Collection)indexSchema.get((Object)jsonContentProperty.getKey()), (Predicate)Predicates.notNull()))));
        return resultBuilder.build();
    }

    private String getContentProperty(CustomContentEntityObject searchable, String contentPropertyKey) {
        String value;
        Optional jsonProperty = this.contentPropertyFinderFactory.createContentPropertyFinder(ContentPropertyFinderPermissionCheck.NO, new Expansion[0]).withContentId(searchable.getContentId()).withPropertyKey(contentPropertyKey).fetch();
        JsonContentProperty jsonContentProperty = jsonProperty.orElse(null);
        if (jsonContentProperty != null && !Strings.isNullOrEmpty((String)(value = jsonContentProperty.getValue().getValue()))) {
            return value;
        }
        return "";
    }
}

