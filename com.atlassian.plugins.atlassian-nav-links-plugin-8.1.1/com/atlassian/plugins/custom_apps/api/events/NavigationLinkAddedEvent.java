/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.custom_apps.api.events;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkEvent;

public class NavigationLinkAddedEvent
extends NavigationLinkEvent {
    public NavigationLinkAddedEvent(CustomApp app) {
        super(app);
    }
}

