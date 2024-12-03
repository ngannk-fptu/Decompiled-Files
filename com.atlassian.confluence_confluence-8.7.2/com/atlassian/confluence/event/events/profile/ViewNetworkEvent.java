/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.profile;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.user.actions.ViewFollowAction;

public class ViewNetworkEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = 9196005282570495829L;
    private final boolean isMyProfile;

    public ViewNetworkEvent(ViewFollowAction src) {
        super(src);
        this.isMyProfile = src.isMyProfile();
    }

    @EventName
    public String calculateEventName() {
        if (this.isMyProfile) {
            return "confluence.user-profile.my.network.view";
        }
        return "confluence.user-profile.other.network.view";
    }
}

