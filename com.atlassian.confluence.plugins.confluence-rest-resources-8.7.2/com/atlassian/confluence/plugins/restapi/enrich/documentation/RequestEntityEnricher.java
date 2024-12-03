/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich.documentation;

import com.atlassian.confluence.plugins.restapi.enrich.EntityEnricher;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RequestEntityEnricher
implements EntityEnricher {
    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        return Collections.emptyMap();
    }

    @Override
    public void enrich(@NonNull RestEntity entity, SchemaType schemaType) {
        entity.removeProperty("_expandable");
        entity.removeProperty("_links");
    }
}

