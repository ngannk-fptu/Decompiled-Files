/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.container;

import com.atlassian.plugin.osgi.container.OsgiContainerManager;

public class OsgiContainerStoppedEvent {
    private final OsgiContainerManager osgiContainerManager;

    public OsgiContainerStoppedEvent(OsgiContainerManager osgiContainerManager) {
        this.osgiContainerManager = osgiContainerManager;
    }

    public OsgiContainerManager getOsgiContainerManager() {
        return this.osgiContainerManager;
    }
}

