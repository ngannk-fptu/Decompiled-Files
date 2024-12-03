/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.helpers;

import com.atlassian.plugin.webresource.url.DefaultUrlBuilder;

public class StateEncodedUrlResult {
    private final boolean taint;
    private final DefaultUrlBuilder urlBuilder;

    public StateEncodedUrlResult(boolean taint, DefaultUrlBuilder urlBuilder) {
        this.taint = taint;
        this.urlBuilder = urlBuilder;
    }

    public boolean isTaint() {
        return this.taint;
    }

    public DefaultUrlBuilder getUrlBuilder() {
        return this.urlBuilder;
    }
}

