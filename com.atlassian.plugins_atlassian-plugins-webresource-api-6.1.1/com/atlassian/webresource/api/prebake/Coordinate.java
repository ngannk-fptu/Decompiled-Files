/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.api.prebake;

import com.atlassian.plugin.webresource.url.UrlBuilder;

public interface Coordinate {
    public void copyTo(UrlBuilder var1, String var2);

    public String get(String var1);

    public Iterable<String> getKeys();
}

