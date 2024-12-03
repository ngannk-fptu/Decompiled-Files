/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonSubTypes
 *  org.codehaus.jackson.annotate.JsonSubTypes$Type
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 */
package com.atlassian.confluence.api.model.watch;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.watch.ContentWatch;
import com.atlassian.confluence.api.model.watch.SpaceWatch;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@ExperimentalApi
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes(value={@JsonSubTypes.Type(value=ContentWatch.class, name="content-watch"), @JsonSubTypes.Type(value=SpaceWatch.class, name="space-watch")})
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class AbstractWatch {
    @JsonProperty
    private final User watcher;

    @JsonCreator
    AbstractWatch(@JsonProperty(value="watcher") @NonNull User watcher) {
        Objects.requireNonNull(watcher);
        this.watcher = watcher;
    }

    public final User getWatcher() {
        return this.watcher;
    }

    public String toString() {
        return "AbstractWatch{watcher=" + this.watcher + '}';
    }
}

