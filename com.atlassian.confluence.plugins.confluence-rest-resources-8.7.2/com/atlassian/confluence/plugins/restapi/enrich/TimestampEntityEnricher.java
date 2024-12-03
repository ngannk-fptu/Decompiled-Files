/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.plugins.restapi.enrich.EntityEnricher;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

final class TimestampEntityEnricher
implements EntityEnricher {
    private static final String TIMESTAMP_PROPERTY = "timestamp";

    TimestampEntityEnricher() {
    }

    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        return Collections.emptyMap();
    }

    @Override
    public void enrich(@NonNull RestEntity entity, @NonNull SchemaType schemaType) {
        Object delegate = entity.getDelegate();
        if (delegate instanceof SearchResult) {
            SearchResult searchResult = (SearchResult)delegate;
            entity.putProperty(TIMESTAMP_PROPERTY, (Object)searchResult.getLastModifiedAt().toInstant().toEpochMilli());
        }
    }
}

