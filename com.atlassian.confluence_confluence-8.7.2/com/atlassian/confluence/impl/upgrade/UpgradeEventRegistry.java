/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.upgrade;

import com.atlassian.confluence.event.events.admin.UpgradeStartedEvent;
import com.atlassian.event.api.EventListener;

public class UpgradeEventRegistry {
    private static boolean hasUpgradeEventOccurred = false;

    public boolean hasUpgradeEventOccurred() {
        return hasUpgradeEventOccurred;
    }

    @EventListener
    public static void onUpgradeStarted(UpgradeStartedEvent event) {
        hasUpgradeEventOccurred = true;
    }
}

