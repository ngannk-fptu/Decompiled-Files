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
import com.atlassian.confluence.notifications.content.ContentMovedPayload;
import com.atlassian.confluence.notifications.content.SimpleContentIdPayload;
import com.atlassian.fugue.Option;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SimpleContentMovedPayload
extends SimpleContentIdPayload
implements ContentMovedPayload {
    private final String originalSpaceKey;
    private final String currentSpaceKey;
    private final Long originalParentPageId;
    private final Long currentParentPageId;
    private final boolean movedBecauseOfParent;
    private final boolean hasMovedChildren;

    @JsonCreator
    public SimpleContentMovedPayload(@JsonProperty(value="contentType") ContentType contentType, @JsonProperty(value="contentId") long contentId, @JsonProperty(value="originalSpaceKey") String originalSpaceKey, @JsonProperty(value="currentSpaceKey") String currentSpaceKey, @JsonProperty(value="originalParentPageId") Long originalParentPageId, @JsonProperty(value="currentParentPageId") Long currentParentPageId, @JsonProperty(value="movedBecauseOfParent") boolean movedBecauseOfParent, @JsonProperty(value="hasMovedChildren") boolean hasMovedChildren, @JsonProperty(value="originatingUserKey") String originatingUserKey) {
        super(contentType, contentId, originatingUserKey);
        this.originalSpaceKey = originalSpaceKey;
        this.currentSpaceKey = currentSpaceKey;
        this.originalParentPageId = originalParentPageId;
        this.currentParentPageId = currentParentPageId;
        this.movedBecauseOfParent = movedBecauseOfParent;
        this.hasMovedChildren = hasMovedChildren;
    }

    @Override
    public String getOriginalSpaceKey() {
        return this.originalSpaceKey;
    }

    @Override
    public String getCurrentSpaceKey() {
        return this.currentSpaceKey;
    }

    @Override
    public Option<Long> getOriginalParentPageId() {
        return Option.option((Object)this.originalParentPageId);
    }

    @Override
    public Option<Long> getCurrentParentPageId() {
        return Option.option((Object)this.currentParentPageId);
    }

    @Override
    public boolean isMovedBecauseOfParent() {
        return this.movedBecauseOfParent;
    }

    @Override
    public boolean hasMovedChildren() {
        return this.hasMovedChildren;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleContentMovedPayload{");
        sb.append("originalSpaceKey='").append(this.originalSpaceKey).append('\'');
        sb.append(", currentSpaceKey='").append(this.currentSpaceKey).append('\'');
        sb.append(", originalParentPageId=").append(this.originalParentPageId);
        sb.append(", currentParentPageId=").append(this.currentParentPageId);
        sb.append(", movedBecauseOfParent=").append(this.movedBecauseOfParent);
        sb.append(", hasMovedChildren=").append(this.hasMovedChildren);
        sb.append("; ").append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}

