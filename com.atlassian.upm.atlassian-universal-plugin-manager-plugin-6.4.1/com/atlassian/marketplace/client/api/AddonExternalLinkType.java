/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

public enum AddonExternalLinkType {
    BUILDS("builds", false),
    FORUMS("forums", true),
    ISSUE_TRACKER("issueTracker", true),
    PRIVACY("privacy", true),
    SOURCE("source", false),
    WIKI("wiki", false);

    private final String key;
    private final boolean canSetForNewAddons;

    private AddonExternalLinkType(String key, boolean canSetForNewAddons) {
        this.key = key;
        this.canSetForNewAddons = canSetForNewAddons;
    }

    public boolean canSetForNewAddons() {
        return this.canSetForNewAddons;
    }

    public String getKey() {
        return this.key;
    }
}

