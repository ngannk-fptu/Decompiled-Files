/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.spaces.Space;
import java.lang.reflect.Type;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ApiRestEntityFactory {
    public RestEntity<Content> buildRestEntityFrom(ContentEntityObject var1, Expansions var2);

    public RestEntity<com.atlassian.confluence.api.model.content.Space> buildRestEntityFrom(Space var1, Expansions var2);

    public Iterable<RestEntity> buildRestEntityFromContent(Iterable<? extends ContentEntityObject> var1, Expansions var2);

    public Iterable<RestEntity> buildRestEntityFromSpaces(Iterable<Space> var1, Expansions var2);

    public boolean isEnrichableList(Class var1);

    public boolean isEnrichableEntity(Class var1);

    public @NonNull Map<String, Type> getEnrichedPropertyTypes(Type var1, boolean var2);

    public Object convertAndEnrich(Object var1, SchemaType var2);

    public static enum SchemaType {
        REST,
        GRAPHQL;

    }
}

