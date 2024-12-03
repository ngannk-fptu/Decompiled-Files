/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.user.UserUpdatedEvent;
import com.atlassian.crowd.model.user.User;
import java.util.Objects;

public class ResetPasswordEvent
extends UserUpdatedEvent {
    private final String newPassword;

    public ResetPasswordEvent(Object source, Directory directory, User user, String newPassword) {
        super(source, directory, user);
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ResetPasswordEvent that = (ResetPasswordEvent)o;
        return Objects.equals(this.newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.newPassword);
    }
}

