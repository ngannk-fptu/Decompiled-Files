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
import com.atlassian.confluence.user.actions.ViewMyFavouritesAction;

public class ViewMyFavouritesEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = 3978915635782801115L;
    private final boolean isMyProfile;

    public ViewMyFavouritesEvent(ViewMyFavouritesAction src) {
        super(src);
        this.isMyProfile = src.isMyProfile();
    }

    @EventName
    public String calculateEventName() {
        if (this.isMyProfile) {
            return "confluence.user-profile.my.favourites.view";
        }
        return "confluence.user-profile.other.favourites.view";
    }
}

