/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

public enum ConfluenceFlavour {
    VANILLA,
    STANDALONE,
    ALACARTE;


    public static ConfluenceFlavour selected() {
        return VANILLA;
    }
}

