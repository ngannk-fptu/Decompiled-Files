/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.message;

import com.atlassian.sal.api.message.HelpPath;

public class DefaultHelpPath
implements HelpPath {
    private final String key;
    private final String url;
    private final String title;
    private final String alt;
    private final boolean local;

    public DefaultHelpPath(String key, String url, String title, String alt, boolean local) {
        this.key = key;
        this.url = url;
        this.title = title;
        this.alt = alt;
        this.local = local;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getAlt() {
        return this.alt;
    }

    @Override
    public boolean isLocal() {
        return this.local;
    }
}

