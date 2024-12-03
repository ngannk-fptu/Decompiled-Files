/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.PublicApi;
import com.microsoft.aad.msal4j.StringHelper;

class CurrentRequest {
    private final PublicApi publicApi;
    private int cacheInfo = -1;
    private String regionUsed = StringHelper.EMPTY_STRING;
    private int regionSource = 0;
    private int regionOutcome = 0;

    CurrentRequest(PublicApi publicApi) {
        this.publicApi = publicApi;
    }

    public PublicApi publicApi() {
        return this.publicApi;
    }

    public int cacheInfo() {
        return this.cacheInfo;
    }

    public String regionUsed() {
        return this.regionUsed;
    }

    public int regionSource() {
        return this.regionSource;
    }

    public int regionOutcome() {
        return this.regionOutcome;
    }

    public CurrentRequest cacheInfo(int cacheInfo) {
        this.cacheInfo = cacheInfo;
        return this;
    }

    public CurrentRequest regionUsed(String regionUsed) {
        this.regionUsed = regionUsed;
        return this;
    }

    public CurrentRequest regionSource(int regionSource) {
        this.regionSource = regionSource;
        return this;
    }

    public CurrentRequest regionOutcome(int regionOutcome) {
        this.regionOutcome = regionOutcome;
        return this;
    }
}

