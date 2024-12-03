/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.lookandfeel;

import java.io.InputStream;

public class SiteLogo {
    private final String url;
    private InputStream content;

    public SiteLogo(String url, InputStream content) {
        this.url = url;
        this.content = content;
    }

    public String getDownloadPath() {
        return this.url;
    }

    public InputStream getContent() {
        return this.content;
    }
}

