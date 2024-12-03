/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ConfigurationEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.setup.settings.Settings;

public class GlobalSettingsChangedEvent
extends ConfigurationEvent
implements ClusterEvent {
    private static final long serialVersionUID = 2896861798750432820L;
    private final Settings oldSettings;
    private final Settings newSettings;
    private final String oldDomainName;
    private final String newDomainName;
    private final Type type;

    public GlobalSettingsChangedEvent(Object src, Settings oldSettings, Settings newSettings, String oldDomainName, String newDomainName, Type type) {
        super(src);
        this.oldSettings = oldSettings;
        this.newSettings = newSettings;
        this.oldDomainName = oldDomainName;
        this.newDomainName = newDomainName;
        this.type = type;
    }

    public GlobalSettingsChangedEvent(Object src, Settings oldSettings, Settings newSettings, String oldDomainName, String newDomainName) {
        this(src, oldSettings, newSettings, oldDomainName, newDomainName, Type.GENERAL);
    }

    public GlobalSettingsChangedEvent(Object src, Settings oldSettings, Settings newSettings) {
        this(src, oldSettings, newSettings, oldSettings.getBaseUrl(), newSettings.getBaseUrl(), Type.GENERAL);
    }

    public Settings getOldSettings() {
        return this.oldSettings;
    }

    public Settings getNewSettings() {
        return this.newSettings;
    }

    public String getOldDomainName() {
        return this.oldDomainName;
    }

    public String getNewDomainName() {
        return this.newDomainName;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        SECURITY,
        GENERAL;

    }
}

