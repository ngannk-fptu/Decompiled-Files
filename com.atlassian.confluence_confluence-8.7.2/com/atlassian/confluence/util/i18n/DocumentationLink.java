/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.i18n;

public final class DocumentationLink {
    private final String key;

    public static DocumentationLink getInstance(String key) {
        return new DocumentationLink(key);
    }

    private DocumentationLink(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String toString() {
        return "DocumentationUrl [key: " + this.key + "]";
    }
}

