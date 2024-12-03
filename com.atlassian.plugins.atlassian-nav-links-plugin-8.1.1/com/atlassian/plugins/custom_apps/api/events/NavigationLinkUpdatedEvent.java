/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.custom_apps.api.events;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkEvent;

public class NavigationLinkUpdatedEvent
extends NavigationLinkEvent {
    private final CustomApp oldValue;

    public NavigationLinkUpdatedEvent(CustomApp oldValue, CustomApp newValue) {
        super(newValue);
        this.oldValue = oldValue;
    }

    public CustomApp getOldValue() {
        return this.oldValue;
    }
}

