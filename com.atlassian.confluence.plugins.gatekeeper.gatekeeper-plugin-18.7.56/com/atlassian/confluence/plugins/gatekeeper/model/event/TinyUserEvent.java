/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;

public class TinyUserEvent
extends TinyEvent {
    private static final long serialVersionUID = 7949253178635820312L;
    private String oldUsername;
    private String username;
    private String displayName;
    private boolean isActive;

    private TinyUserEvent(EventType eventType, String username) {
        super(eventType);
        this.username = username.toLowerCase();
    }

    public static TinyUserEvent added(String username, String displayName, boolean isActive) {
        TinyUserEvent e = new TinyUserEvent(EventType.USER_ADDED, username);
        e.displayName = displayName;
        e.isActive = isActive;
        return e;
    }

    public static TinyUserEvent updated(String username, String displayName) {
        TinyUserEvent e = new TinyUserEvent(EventType.USER_UPDATED, username);
        e.displayName = displayName;
        return e;
    }

    public static TinyEvent deleted(String username) {
        return new TinyUserEvent(EventType.USER_DELETED, username);
    }

    public static TinyUserEvent renamed(String oldUsername, String newUsername) {
        TinyUserEvent e = new TinyUserEvent(EventType.USER_RENAMED, newUsername);
        e.oldUsername = oldUsername.toLowerCase();
        return e;
    }

    public static TinyEvent activated(String username) {
        return new TinyUserEvent(EventType.USER_ACTIVATED, username);
    }

    public static TinyEvent deactivated(String username) {
        return new TinyUserEvent(EventType.USER_DEACTIVATED, username);
    }

    public boolean isActive() {
        return this.isActive;
    }

    public String getOldUsername() {
        return this.oldUsername;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String toString() {
        return this.eventType + "{" + (String)(this.oldUsername != null ? ", oldUsername='" + this.oldUsername + "'" : "") + ", username='" + this.username + "', displayName='" + this.displayName + "'}";
    }
}

