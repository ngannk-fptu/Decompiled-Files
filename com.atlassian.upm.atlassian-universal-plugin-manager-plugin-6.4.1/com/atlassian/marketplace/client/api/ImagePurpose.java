/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.EnumWithKey;

public enum ImagePurpose implements EnumWithKey
{
    LOGO("logo"),
    BANNER("banner"),
    SCREENSHOT("screenshot"),
    THUMBNAIL("screenshot-thumbnail"),
    TITLE_LOGO("title-logo"),
    HERO("hero");

    private final String key;

    private ImagePurpose(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

