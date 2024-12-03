/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.gadgets.renderer.internal.http;

import javax.annotation.concurrent.Immutable;

@Immutable
public class HttpClientSpec {
    private final boolean followRedirects;

    public HttpClientSpec(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isFollowRedirects() {
        return this.followRedirects;
    }
}

