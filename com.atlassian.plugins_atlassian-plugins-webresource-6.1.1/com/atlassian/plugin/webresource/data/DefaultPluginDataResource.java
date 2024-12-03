/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.atlassian.webresource.api.data.PluginDataResource
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.api.data.PluginDataResource;
import java.util.Optional;
import javax.annotation.Nonnull;

public class DefaultPluginDataResource
implements PluginDataResource {
    private final String key;
    private final Optional<Jsonable> jsonable;
    private final ResourcePhase resourcePhase;

    public DefaultPluginDataResource(@Nonnull String key, @Nonnull Jsonable jsonable) {
        this(key, Optional.of(jsonable), ResourcePhase.defaultPhase());
    }

    public DefaultPluginDataResource(@Nonnull String key, @Nonnull Jsonable jsonable, @Nonnull ResourcePhase resourcePhase) {
        this(key, Optional.of(jsonable), resourcePhase);
    }

    public DefaultPluginDataResource(@Nonnull String key, @Nonnull Optional<Jsonable> jsonable) {
        this(key, jsonable, ResourcePhase.defaultPhase());
    }

    public DefaultPluginDataResource(@Nonnull String key, @Nonnull Optional<Jsonable> jsonable, @Nonnull ResourcePhase resourcePhase) {
        this.key = key;
        this.jsonable = jsonable;
        this.resourcePhase = resourcePhase;
    }

    public String getKey() {
        return this.key;
    }

    public Jsonable getJsonable() {
        return this.jsonable.get();
    }

    public Optional<Jsonable> getData() {
        return this.jsonable;
    }

    @Nonnull
    public ResourcePhase getResourcePhase() {
        return this.resourcePhase;
    }

    public boolean equals(Object thatObject) {
        if (this == thatObject) {
            return true;
        }
        if (!(thatObject instanceof DefaultPluginDataResource)) {
            return false;
        }
        DefaultPluginDataResource thatDefaultPluginDataResource = (DefaultPluginDataResource)thatObject;
        return this.key.equals(thatDefaultPluginDataResource.key);
    }

    public int hashCode() {
        return this.key.hashCode();
    }
}

