/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.api.model.reference.EnrichableMap
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.serialization.RestEnrichable$Helper
 *  com.atlassian.confluence.api.serialization.RestEnrichableProperty
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.services.RestEntityFactory
 *  com.atlassian.fugue.Option
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.serialization.RestEnrichableProperty;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.services.RestEntityFactory;
import com.atlassian.fugue.Option;
import com.atlassian.graphql.annotations.GraphQLName;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public class DefaultRestEntityFactory
implements RestEntityFactory {
    public <T> RestEntity<T> create(T entity, boolean graphql) {
        if (entity instanceof Map) {
            return this.createFromMap(entity, graphql);
        }
        return this.createFromIntrospection(entity, graphql);
    }

    private <T> RestEntity<T> createFromIntrospection(T entity, boolean graphql) {
        RestEntity restEntity = new RestEntity(entity);
        try {
            for (Class<?> clazz = entity.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                this.resolveClassProperties(restEntity, entity, clazz, graphql);
            }
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        return restEntity;
    }

    private <T> void resolveClassProperties(RestEntity<T> restEntity, T entity, Class<?> clazz, boolean graphql) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            if (!this.isRestEntityProperty(field, graphql)) continue;
            field.setAccessible(true);
            String fieldName = field.getName();
            if (restEntity.hasProperty(fieldName)) {
                throw new IllegalStateException(String.format("JsonProperty-annotated fields in API Model classes *must not* shadow fields in superclasses, but %s shadows the field %s", clazz, fieldName));
            }
            Object fieldValue = field.get(entity);
            this.resolveProperty(restEntity, fieldName, fieldValue, graphql);
        }
    }

    private boolean isRestEntityProperty(Field field, boolean graphql) {
        return field.isAnnotationPresent(JsonProperty.class) || graphql && field.isAnnotationPresent(GraphQLName.class) || field.isAnnotationPresent(RestEnrichableProperty.class);
    }

    private <T> RestEntity<T> createFromMap(T entity, boolean graphql) {
        Map mapEntity = (Map)entity;
        RestEntity restEntity = new RestEntity(entity);
        for (Map.Entry o : mapEntity.entrySet()) {
            String fieldName = String.valueOf(o.getKey());
            Object fieldValue = o.getValue();
            this.resolveProperty(restEntity, fieldName, fieldValue, graphql);
        }
        if (mapEntity instanceof EnrichableMap) {
            EnrichableMap enrichableMap = (EnrichableMap)mapEntity;
            for (Object o : enrichableMap.getCollapsedEntries()) {
                this.resolveCollapsedProperty(restEntity, String.valueOf(o), navigationService -> null);
            }
        }
        return restEntity;
    }

    private <T> void resolveProperty(RestEntity<T> restEntity, String fieldName, Object fieldValue, boolean graphql) {
        try {
            if (!(graphql && fieldValue instanceof Reference || !(fieldValue instanceof Collapsed))) {
                this.resolveCollapsedProperty(restEntity, fieldName, (Collapsed)fieldValue);
            } else if (fieldValue instanceof Reference) {
                this.resolveReferenceProperty(restEntity, fieldName, (Reference)fieldValue, graphql);
            } else if (fieldValue != null) {
                this.putIfValueNotNull(restEntity, fieldName, this.createPropertyValue(fieldValue, graphql));
            }
        }
        catch (IllegalAccessException ex) {
            throw Throwables.propagate((Throwable)ex);
        }
    }

    private <T> void resolveCollapsedProperty(RestEntity<T> restEntity, String propertyName, Collapsed value) {
        Map expandables = (Map)restEntity.getProperty("_expandable");
        if (expandables == null) {
            expandables = Maps.newHashMap();
            this.putIfValueNotNull(restEntity, "_expandable", expandables);
        }
        expandables.put(propertyName, value);
    }

    private void putIfValueNotNull(RestEntity entity, String key, Object value) {
        if (value != null) {
            entity.putProperty(key, value);
        }
    }

    private <T> void resolveReferenceProperty(RestEntity<T> restEntity, String propertyName, Reference<?> reference, boolean graphql) throws IllegalAccessException {
        if (reference.exists() && (graphql || reference.isExpanded())) {
            Object toAdd = this.createPropertyValue(reference.isExpanded() ? reference.get() : reference, graphql);
            this.putIfValueNotNull(restEntity, propertyName, toAdd);
        }
    }

    private Iterable createIterableProperty(Iterable iterableValue, boolean graphql) throws IllegalAccessException {
        if (iterableValue instanceof Option) {
            Option option = (Option)iterableValue;
            if (option.isDefined()) {
                Object propertyValue = this.createPropertyValue(option.get(), graphql);
                if (propertyValue != null) {
                    return Option.some((Object)propertyValue);
                }
            } else {
                return null;
            }
        }
        boolean hasEnrichedEntities = false;
        ImmutableList.Builder listBuilder = ImmutableList.builder();
        Object unenrichedItem = null;
        for (Object obj : iterableValue) {
            unenrichedItem = obj;
            Object enriched = this.createPropertyValue(obj, graphql);
            hasEnrichedEntities |= enriched != obj;
            if (enriched == null) continue;
            listBuilder.add(enriched);
        }
        if (!hasEnrichedEntities) {
            return iterableValue;
        }
        ImmutableList enrichedList = listBuilder.build();
        if (iterableValue instanceof RestList || iterableValue instanceof PageResponse && (this.isRestEnrichable(iterableValue) || this.isRestEnrichable(unenrichedItem))) {
            PageResponse orig = (PageResponse)iterableValue;
            RestList restListEnriched = RestList.newRestList((PageRequest)orig.getPageRequest()).results((List)enrichedList, orig.getNextCursor(), orig.getPrevCursor(), orig.hasMore()).build();
            if (iterableValue instanceof ContentRestrictionsPageResponse) {
                ContentRestrictionsPageResponse restrictionsResponse = (ContentRestrictionsPageResponse)iterableValue;
                restListEnriched.putProperty("links", (Object)restrictionsResponse.getLinks());
                restListEnriched.putProperty("restrictionsHash", (Object)restrictionsResponse.getRestrictionsHash());
            }
            return restListEnriched;
        }
        return enrichedList;
    }

    private boolean isRestEnrichable(Object unenrichedItem) {
        return RestEnrichable.Helper.isAnnotationOnClass((Object)unenrichedItem);
    }

    private Map createMapProperty(Map<?, ?> value, boolean graphql) throws IllegalAccessException {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        boolean hasRestEntity = false;
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object entryValue = entry.getValue();
            if (entryValue == null) continue;
            Object enriched = this.createPropertyValue(entryValue, graphql);
            hasRestEntity |= enriched != entryValue;
            if (enriched == null) continue;
            mapBuilder.put(entry.getKey(), enriched);
        }
        if (hasRestEntity) {
            return mapBuilder.build();
        }
        return value;
    }

    private Object createPropertyValue(Object value, boolean graphql) throws IllegalAccessException {
        if (value instanceof Collapsed) {
            return value;
        }
        if (value instanceof Iterable) {
            return this.createIterableProperty((Iterable)value, graphql);
        }
        if (RestEnrichable.Helper.isAnnotationOnClass((Object)value)) {
            return this.create(value, graphql);
        }
        if (value instanceof Map) {
            return this.createMapProperty((Map)value, graphql);
        }
        return value;
    }
}

