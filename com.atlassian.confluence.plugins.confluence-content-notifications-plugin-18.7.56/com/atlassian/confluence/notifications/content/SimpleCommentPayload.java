/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.notifications.content.SimpleContentIdPayload;
import com.atlassian.fugue.Option;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SimpleCommentPayload
extends SimpleContentIdPayload
implements CommentPayload {
    private final Long parentCommentId;
    private final long containerId;
    private final ContentType containerType;
    private String parentInlineContext;

    public SimpleCommentPayload(Long contentId, Long parentCommentId, String originatingUserKey) {
        this(contentId, 0L, "", parentCommentId == null ? 0L : parentCommentId, originatingUserKey);
    }

    @JsonCreator
    public SimpleCommentPayload(@JsonProperty(value="contentId") long contentId, @JsonProperty(value="containerId") long containerId, @JsonProperty(value="containerType") String containerType, @JsonProperty(value="parentCommentId") Long parentCommentId, @JsonProperty(value="originatingUserKey") String originatingUserKey) {
        super(ContentType.COMMENT, contentId, originatingUserKey);
        this.parentCommentId = parentCommentId;
        this.containerId = containerId;
        this.containerType = ContentType.valueOf((String)containerType);
    }

    @Override
    public Option<Long> getParentCommentId() {
        return Option.option((Object)this.parentCommentId);
    }

    @Override
    public long getContainerId() {
        return this.containerId;
    }

    @Override
    public Option<String> getParentInlineContext() {
        return Option.option((Object)(this.parentInlineContext != null ? this.parentInlineContext : ""));
    }

    public void setParentInlineContext(String parentInlineContext) {
        this.parentInlineContext = parentInlineContext;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleCommentPayload{");
        sb.append("parentInlineContext=").append(this.parentInlineContext).append("; ");
        sb.append("parentCommentId=").append(this.parentCommentId != null ? this.parentInlineContext : "");
        sb.append("; ").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public ContentType getContainerType() {
        return this.containerType;
    }
}

