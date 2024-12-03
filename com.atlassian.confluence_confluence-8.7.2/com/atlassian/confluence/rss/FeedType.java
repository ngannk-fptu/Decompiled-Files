/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rss;

public enum FeedType {
    RSS2("rss2"),
    RSS("rss"),
    ATOM("atom");

    private final String code;

    private FeedType(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}

