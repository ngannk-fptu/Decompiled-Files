/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.fugue.Option;
import java.util.Optional;

@ExperimentalApi
public interface CommentPayload
extends ContentIdPayload {
    @Deprecated
    public Option<Long> getParentCommentId();

    default public Optional<Long> optionalParentCommentId() {
        return Optional.ofNullable((Long)this.getParentCommentId().getOrNull());
    }

    public long getContainerId();

    public ContentType getContainerType();

    @Deprecated
    public Option<String> getParentInlineContext();

    default public Optional<String> optionalParentInlineContext() {
        return Optional.ofNullable((String)this.getParentInlineContext().getOrNull());
    }
}

