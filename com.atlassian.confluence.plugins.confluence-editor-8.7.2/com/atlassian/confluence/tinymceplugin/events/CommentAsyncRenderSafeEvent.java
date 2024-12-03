/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.analytics.api.annotations.PrivacyPolicySafe
 */
package com.atlassian.confluence.tinymceplugin.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.analytics.api.annotations.PrivacyPolicySafe;

@PrivacyPolicySafe
@EventName(value="confluence.comment.asyncRenderSafe")
public class CommentAsyncRenderSafeEvent {
    @PrivacyPolicySafe
    private final boolean asyncRenderSafe;

    public CommentAsyncRenderSafeEvent(boolean asyncRenderSafe) {
        this.asyncRenderSafe = asyncRenderSafe;
    }

    public boolean isAsyncRenderSafe() {
        return this.asyncRenderSafe;
    }
}

