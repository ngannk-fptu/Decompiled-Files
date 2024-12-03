/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.custom_apps.api.events;

import com.atlassian.plugins.custom_apps.api.CustomApp;

public abstract class NavigationLinkEvent {
    private final CustomApp affectedApp;

    NavigationLinkEvent(CustomApp app) {
        this.affectedApp = app;
    }

    public CustomApp getAffectedApp() {
        return this.affectedApp;
    }
}

