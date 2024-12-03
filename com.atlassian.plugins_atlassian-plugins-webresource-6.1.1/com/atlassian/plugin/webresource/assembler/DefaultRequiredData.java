/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableBoolean
 *  com.atlassian.json.marshal.wrapped.JsonableNumber
 *  com.atlassian.json.marshal.wrapped.JsonableString
 *  com.atlassian.webresource.api.assembler.RequiredData
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableBoolean;
import com.atlassian.json.marshal.wrapped.JsonableNumber;
import com.atlassian.json.marshal.wrapped.JsonableString;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.webresource.api.assembler.RequiredData;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import javax.annotation.Nonnull;

public class DefaultRequiredData
implements RequiredData {
    private final RequestState requestState;

    public DefaultRequiredData(@Nonnull RequestState requestState) {
        this.requestState = Objects.requireNonNull(requestState, "The request state is mandatory to build the required data.");
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull Jsonable content) {
        return this.requireData(key, content, ResourcePhase.defaultPhase());
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull Jsonable content, @Nonnull ResourcePhase resourcePhase) {
        this.requestState.getIncludedData(resourcePhase).put(key, content);
        return this;
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull Number content) {
        return this.requireData(key, content, ResourcePhase.defaultPhase());
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull Number content, @Nonnull ResourcePhase resourcePhase) {
        this.requestState.getIncludedData(resourcePhase).put(key, (Jsonable)new JsonableNumber(content));
        return this;
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull String content) {
        return this.requireData(key, content, ResourcePhase.defaultPhase());
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull String content, @Nonnull ResourcePhase resourcePhase) {
        this.requestState.getIncludedData(resourcePhase).put(key, (Jsonable)new JsonableString(content));
        return this;
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull Boolean content) {
        return this.requireData(key, content, ResourcePhase.defaultPhase());
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull Boolean content, @Nonnull ResourcePhase resourcePhase) {
        this.requestState.getIncludedData(resourcePhase).put(key, (Jsonable)new JsonableBoolean(content));
        return this;
    }

    @Nonnull
    public RequiredData requireData(@Nonnull String key, @Nonnull CompletionStage<Jsonable> content) {
        this.requestState.getBigPipe().push(key, content);
        return this;
    }
}

