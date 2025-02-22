/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import net.jcip.annotations.Immutable;

@Immutable
public class Resource {
    private final String content;
    private final String contentType;

    public Resource(String content, String contentType) {
        if (content == null) {
            throw new IllegalArgumentException("The resource content must not be null");
        }
        this.content = content;
        this.contentType = contentType;
    }

    public String getContent() {
        return this.content;
    }

    public String getContentType() {
        return this.contentType;
    }
}

