/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.manifest;

public class ManifestNotFoundException
extends Exception {
    private String url;

    public ManifestNotFoundException(String url) {
        this.url = url;
    }

    public ManifestNotFoundException(String url, String message) {
        super(message);
        this.url = url;
    }

    public ManifestNotFoundException(String url, Throwable cause) {
        super(cause);
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}

