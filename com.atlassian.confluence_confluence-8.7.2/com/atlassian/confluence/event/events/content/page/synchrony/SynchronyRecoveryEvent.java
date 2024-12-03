/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.content.page.synchrony;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.user.ConfluenceUser;
import org.checkerframework.checker.nullness.qual.NonNull;

@Internal
public class SynchronyRecoveryEvent {
    private final ConfluenceUser user;
    private final ContentId currentContentId;
    private final String recoveryState;

    public SynchronyRecoveryEvent(ConfluenceUser user, @NonNull ContentId contentId, @NonNull String recoveryState) {
        this.user = user;
        this.currentContentId = contentId;
        this.recoveryState = recoveryState;
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public ContentId getCurrentContentId() {
        return this.currentContentId;
    }

    public String getRecoveryState() {
        return this.recoveryState;
    }

    @EventName
    public String calculateEventName() {
        return "confluence.synchrony.recovery." + this.recoveryState;
    }
}

