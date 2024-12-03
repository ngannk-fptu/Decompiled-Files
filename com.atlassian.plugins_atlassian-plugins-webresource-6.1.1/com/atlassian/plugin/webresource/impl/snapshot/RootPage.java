/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.snapshot;

import com.atlassian.plugin.webresource.impl.snapshot.WebResource;

public final class RootPage {
    private final WebResource webResource;

    public RootPage(WebResource webResource) {
        this.webResource = webResource;
    }

    public WebResource getWebResource() {
        return this.webResource;
    }
}

