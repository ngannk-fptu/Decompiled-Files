/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.spi;

public enum ServletPath {
    COMMENTS("/plugins/servlet/streamscomments");

    private final String path;

    private ServletPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String toString() {
        return this.path;
    }
}

