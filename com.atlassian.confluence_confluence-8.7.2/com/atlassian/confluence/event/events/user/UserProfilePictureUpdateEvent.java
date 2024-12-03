/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.user.User;

public class UserProfilePictureUpdateEvent
extends UserEvent
implements Updated {
    private static final long serialVersionUID = 4735772966978696105L;
    private ProfilePictureInfo profilePictureInfo;

    public UserProfilePictureUpdateEvent(Object src, User user, Attachment image) {
        super(src, user);
        this.profilePictureInfo = new ProfilePictureInfo(image);
    }

    public UserProfilePictureUpdateEvent(Object src, User user, String imagePath) {
        super(src, user);
        if (imagePath != null) {
            this.profilePictureInfo = new ProfilePictureInfo(imagePath);
        }
    }

    public ProfilePictureInfo getProfilePictureInfo() {
        return this.profilePictureInfo;
    }
}

