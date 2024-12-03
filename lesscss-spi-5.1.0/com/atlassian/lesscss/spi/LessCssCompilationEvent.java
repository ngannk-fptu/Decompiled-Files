/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lesscss.spi;

import java.net.URI;

public class LessCssCompilationEvent {
    private final URI lessResourceUri;

    public LessCssCompilationEvent(URI lessResourceUri) {
        this.lessResourceUri = lessResourceUri;
    }

    public URI getLessResourceUri() {
        return this.lessResourceUri;
    }
}

