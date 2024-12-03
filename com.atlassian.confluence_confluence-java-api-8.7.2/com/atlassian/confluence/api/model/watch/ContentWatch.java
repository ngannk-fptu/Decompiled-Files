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
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.watch.AbstractWatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
public class ContentWatch
extends AbstractWatch {
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private final Reference<Content> content;

    @JsonCreator
    public ContentWatch(@JsonProperty(value="watcher") @NonNull User watcher, @JsonProperty(value="contentId") @NonNull ContentId contentId) {
        super(watcher);
        this.content = Reference.orEmpty(Content.builder().id(contentId).build(), Content.class);
    }

    public Content getContent() {
        return this.content.get();
    }

    @Override
    public String toString() {
        return "ContentWatch{content=" + this.content + ", watcher=" + this.getWatcher() + '}';
    }
}

