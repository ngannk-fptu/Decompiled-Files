/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.analytics.api.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@EventName(value="UserActivity")
@AsynchronousPreferred
public final class MauEvent {
    public static final String EVENT_NAME = "UserActivity";
    private final String email;
    private final String application;

    private MauEvent(String email, String application) {
        this.email = email;
        this.application = application;
    }

    public String getEmail() {
        return this.email;
    }

    public String getApplication() {
        return this.application;
    }

    public static MauEvent withEmail(String email) {
        return new Builder().build(email);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MauEvent mauEvent = (MauEvent)o;
        if (this.email != null ? !this.email.equals(mauEvent.email) : mauEvent.email != null) {
            return false;
        }
        return !(this.application == null ? mauEvent.application != null : !this.application.equals(mauEvent.application));
    }

    public int hashCode() {
        int result = this.email != null ? this.email.hashCode() : 0;
        result = 31 * result + (this.application != null ? this.application.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private String application;

        public Builder application(String application) {
            this.application = application;
            return this;
        }

        public MauEvent build(String email) {
            if (email == null) {
                throw new IllegalArgumentException("Email cannot be null.");
            }
            return new MauEvent(email, this.application);
        }
    }
}

