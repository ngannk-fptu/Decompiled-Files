/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.internal.feature;

public enum ApplinksFeatures {
    BITBUCKET_REBRAND("fusion.bitbucket.rebrand", true, true),
    ONE_WAY_APPLINKS("applinks.one.way.applinks", true, false),
    V3_UI("applinks.v3.ui", true, true),
    V4_UI("applinks.v4.ui", true, true),
    TEST_NON_SYSTEM("applinks.test.non-system", false, false);

    final String featureKey;
    private final boolean system;
    private final boolean defaultValue;

    private ApplinksFeatures(String featureKey, boolean system, boolean defaultValue) {
        this.featureKey = featureKey;
        this.system = system;
        this.defaultValue = defaultValue;
    }

    public boolean isSystem() {
        return this.system;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }
}

