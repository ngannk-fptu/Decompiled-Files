/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.util;

import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Links;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.function.Function;

public abstract class EntityFunctions {
    private EntityFunctions() {
    }

    public static <T extends Entity> Function<T, Links> links() {
        return Entity::getLinks;
    }

    public static <T extends Entity> Option<URI> selfUri(T entity) {
        return entity.getLinks().getUri("self");
    }

    public static <T extends Entity> Function<T, Option<URI>> selfUri() {
        return EntityFunctions::selfUri;
    }

    public static <T extends Entity> Iterable<URI> entityLinks(Iterable<T> entities) {
        ImmutableList.Builder ret = ImmutableList.builder();
        for (Entity e : entities) {
            for (URI u : EntityFunctions.selfUri(e)) {
                ret.add((Object)u);
            }
        }
        return ret.build();
    }
}

