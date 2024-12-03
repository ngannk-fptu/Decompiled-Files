/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.fugue.Option;
import java.util.Optional;

@ExperimentalApi
public interface ContentMovedPayload
extends ContentIdPayload {
    public String getOriginalSpaceKey();

    public String getCurrentSpaceKey();

    @Deprecated
    public Option<Long> getOriginalParentPageId();

    default public Optional<Long> optionalOriginalParentPageId() {
        return Optional.ofNullable((Long)this.getOriginalParentPageId().getOrNull());
    }

    @Deprecated
    public Option<Long> getCurrentParentPageId();

    default public Optional<Long> optionalCurrentParentPageId() {
        return Optional.ofNullable((Long)this.getCurrentParentPageId().getOrNull());
    }

    public boolean isMovedBecauseOfParent();

    public boolean hasMovedChildren();
}

