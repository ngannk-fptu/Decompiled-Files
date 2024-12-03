/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.rest.api.graphql.GraphQL
 *  com.atlassian.confluence.rest.api.services.RestEntityFactory
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.DefaultRestEntityFactory
 *  com.atlassian.confluence.rest.serialization.RestEntityConverter
 *  com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugins.restapi.enrich.BaseLinkEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.CollectionEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.EntityCollectionLinkEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.EntityEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.ExpandableEntityEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.NavigationCollectionEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.RestObjectEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.SelfLinkCollectionEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.SelfLinkEntityEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.TimestampEntityEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.VisitorWrapper;
import com.atlassian.confluence.plugins.restapi.enrich.model.ContentEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.model.RestListLinkEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.model.SpaceEnricher;
import com.atlassian.confluence.rest.api.graphql.GraphQL;
import com.atlassian.confluence.rest.api.services.RestEntityFactory;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.confluence.rest.serialization.DefaultRestEntityFactory;
import com.atlassian.confluence.rest.serialization.RestEntityConverter;
import com.atlassian.confluence.rest.serialization.enrich.RestEntityEnrichmentManager;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="restEntityEnrichmentManager")
@ExportAsService(value={RestEntityEnrichmentManager.class})
@Internal
public class DefaultRestEntityEnrichmentManager
implements RestEntityEnrichmentManager {
    private final List<RestObjectEnricher> enrichers;
    private final GraphQL graphql;
    private final RestEntityConverter converter;
    private List<VisitorWrapper> enricherVistors = new ArrayList<VisitorWrapper>();

    @Autowired
    public DefaultRestEntityEnrichmentManager(@ComponentImport RestNavigationService navBuilderService, @ComponentImport GraphQL graphql, @ClasspathComponent DefaultRestEntityFactory restEntityFactory) {
        this.graphql = graphql;
        this.converter = new RestEntityConverter((RestEntityFactory)restEntityFactory);
        this.enrichers = ImmutableList.of((Object)new TimestampEntityEnricher(), (Object)new RestListLinkEnricher(navBuilderService, graphql), (Object)new SpaceEnricher(navBuilderService, graphql), (Object)new ContentEnricher(navBuilderService, graphql), (Object)new SelfLinkCollectionEnricher(navBuilderService, graphql), (Object)new NavigationCollectionEnricher(navBuilderService, graphql), (Object)new EntityCollectionLinkEnricher(navBuilderService, graphql), (Object)new BaseLinkEnricher(navBuilderService, graphql), (Object)new SelfLinkEntityEnricher(navBuilderService, graphql), (Object)new ExpandableEntityEnricher(navBuilderService));
        for (RestObjectEnricher enricher : this.enrichers) {
            if (enricher.isRecursive()) {
                if (enricher instanceof CollectionEnricher) {
                    this.enricherVistors.add(VisitorWrapper.newTreeFilter((CollectionEnricher)enricher));
                }
                if (!(enricher instanceof EntityEnricher)) continue;
                this.enricherVistors.add(VisitorWrapper.newTreeFilter((EntityEnricher)enricher));
                continue;
            }
            if (enricher instanceof CollectionEnricher) {
                this.enricherVistors.add(VisitorWrapper.newRootEntityFilter((CollectionEnricher)enricher));
            }
            if (!(enricher instanceof EntityEnricher)) continue;
            this.enricherVistors.add(VisitorWrapper.newRootEntityFilter((EntityEnricher)enricher));
        }
    }

    public boolean isEnrichableList(Class listType) {
        return this.converter.isEnrichableList(listType);
    }

    public boolean isEnrichableEntity(Class entityType) {
        return this.converter.isEnrichableEntity(entityType);
    }

    public @NonNull Map<String, Type> getEnrichedPropertyTypes(Type type, boolean isRoot) {
        HashMap<String, Type> result = new HashMap<String, Type>();
        for (RestObjectEnricher enricher : this.enrichers) {
            if (!isRoot && !enricher.isRecursive()) continue;
            Map<String, Type> map = enricher.getEnrichedPropertyTypes(type);
            this.combinePropertyTypesMap(result, map);
        }
        return result;
    }

    private void combinePropertyTypesMap(Map<String, Type> map, Map<String, Type> updateFrom) {
        for (Map.Entry<String, Type> entry : updateFrom.entrySet()) {
            map.put(entry.getKey(), this.combineTypes(map.get(entry.getKey()), entry.getValue()));
        }
    }

    private Type combineTypes(Type x, Type y) {
        if (this.graphql.isDynamicType(x) && this.graphql.isDynamicType(y)) {
            return this.combineDynamicTypes(x, y);
        }
        return x != null ? x : y;
    }

    private Type combineDynamicTypes(Type x, Type y) {
        HashMap fieldTypes = new HashMap();
        fieldTypes.putAll(this.graphql.getDynamicTypeFields(x));
        fieldTypes.putAll(this.graphql.getDynamicTypeFields(y));
        return this.graphql.createDynamicType(x.getTypeName(), fieldTypes);
    }

    public Object convertAndEnrich(Object entity, SchemaType schemaType) {
        Object restEntity = this.converter.convert(entity, schemaType);
        for (VisitorWrapper enricher : this.enricherVistors) {
            enricher.enrich(restEntity, schemaType);
        }
        return restEntity;
    }
}

