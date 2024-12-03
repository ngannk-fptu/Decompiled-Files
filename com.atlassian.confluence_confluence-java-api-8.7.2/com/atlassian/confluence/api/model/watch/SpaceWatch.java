/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.watch;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.watch.AbstractWatch;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
public class SpaceWatch
extends AbstractWatch {
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Space.class)
    @JsonProperty
    private final Reference<Space> space;
    @JsonProperty
    private final List<ContentType> contentTypes;

    public SpaceWatch(User watcher, String spaceKey, List<ContentType> contentTypes) {
        super(watcher);
        this.space = Reference.orEmpty(Space.builder().key(spaceKey).build(), Space.class);
        this.contentTypes = contentTypes;
    }

    @JsonCreator
    public SpaceWatch(@JsonProperty(value="watcher") @NonNull User watcher, @JsonProperty(value="space") @NonNull Reference<Space> space, @JsonProperty(value="contentTypes") List<ContentType> contentTypes) {
        super(watcher);
        this.space = space;
        this.contentTypes = contentTypes;
    }

    @Override
    public String toString() {
        return "SpaceWatch{space=" + this.space + ", watcher=" + this.getWatcher() + '}';
    }

    public Space getSpace() {
        return this.space.get();
    }

    public List<ContentType> getContentTypes() {
        return this.contentTypes;
    }
}

