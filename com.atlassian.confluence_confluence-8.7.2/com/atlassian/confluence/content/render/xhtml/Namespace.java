/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

public final class Namespace {
    private final String prefix;
    private final String uri;
    private final boolean defaultNamespace;

    Namespace(String prefix, String uri, boolean defaultNamespace) {
        this.prefix = prefix;
        this.uri = uri;
        this.defaultNamespace = defaultNamespace;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getUri() {
        return this.uri;
    }

    public boolean isDefaultNamespace() {
        return this.defaultNamespace;
    }
}

