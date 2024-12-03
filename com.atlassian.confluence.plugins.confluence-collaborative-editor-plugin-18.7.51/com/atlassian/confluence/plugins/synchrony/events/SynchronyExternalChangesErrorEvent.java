/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.plugins.synchrony.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;

@EventName(value="confluence.synchrony.external-changes.error")
@Internal
public class SynchronyExternalChangesErrorEvent {
    private final int statusCode;
    private final String message;
    private final long contentId;
    private final String ancestor;

    public SynchronyExternalChangesErrorEvent(int statusCode, String message, long contentId, String ancestor) {
        this.statusCode = statusCode;
        this.message = message;
        this.contentId = contentId;
        this.ancestor = ancestor == null ? "null" : ancestor;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public long getContentId() {
        return this.contentId;
    }

    public String getAncestor() {
        return this.ancestor;
    }
}

