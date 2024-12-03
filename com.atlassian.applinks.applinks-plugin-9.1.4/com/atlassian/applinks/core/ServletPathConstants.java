/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.internal.rest.RestUrl;

public final class ServletPathConstants {
    public static final RestUrl APPLINKS_SERVLETS_URL = RestUrl.forPath("plugins").add("servlet").add("applinks");
    public static final RestUrl APPLINKS_CONFIG_SERVLET_URL = APPLINKS_SERVLETS_URL.add("auth").add("conf");
    public static final String APPLINKS_SERVLETS_PATH = "/" + APPLINKS_SERVLETS_URL.toString();
    public static final String APPLINKS_CONFIG_SERVLET_PATH = "/" + APPLINKS_CONFIG_SERVLET_URL.toString();

    private ServletPathConstants() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }
}

