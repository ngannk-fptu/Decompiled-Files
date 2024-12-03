/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest;

import com.atlassian.applinks.internal.rest.RestUrl;
import javax.annotation.Nonnull;

public enum RestVersion {
    V1("1.0"),
    V2("2.0"),
    V3("3.0"),
    LATEST("latest");

    public static RestVersion DEFAULT;
    private final RestUrl path;

    private RestVersion(String path) {
        this.path = RestUrl.forPath(path);
    }

    @Nonnull
    public RestUrl getPath() {
        return this.path;
    }

    static {
        DEFAULT = LATEST;
    }
}

