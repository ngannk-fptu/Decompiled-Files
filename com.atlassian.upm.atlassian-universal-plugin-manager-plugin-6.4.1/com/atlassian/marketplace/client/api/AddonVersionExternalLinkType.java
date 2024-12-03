/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

public enum AddonVersionExternalLinkType {
    BINARY("binary", true),
    DOCUMENTATION("documentation", true),
    DONATE("donate", false),
    EULA("eula", true),
    EVALUATION_LICENSE("evaluationLicense", false),
    JAVADOC("javadocs", false),
    LEARN_MORE("learnMore", true),
    LICENSE("license", true),
    PURCHASE("purchase", true),
    RELEASE_NOTES("releaseNotes", true),
    SOURCE("source", false);

    private final String key;
    private final boolean canSetForNewAddonVersions;

    private AddonVersionExternalLinkType(String key, boolean canSetForNewAddonVersions) {
        this.key = key;
        this.canSetForNewAddonVersions = canSetForNewAddonVersions;
    }

    public boolean canSetForNewAddonVersions() {
        return this.canSetForNewAddonVersions;
    }

    public String getKey() {
        return this.key;
    }
}

