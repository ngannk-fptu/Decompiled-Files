/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.embedded.EmbeddedResourceParser;
import java.util.Properties;

public class EmbeddedResource {
    protected final EmbeddedResourceParser parser;
    protected final String filename;
    protected final String type;
    protected final String page;
    protected final String space;
    protected final String url;
    protected final String originalText;
    protected Properties properties;

    public EmbeddedResource(EmbeddedResourceParser parser) {
        this.originalText = parser.getOriginalText();
        this.parser = parser;
        this.url = parser.getResource();
        this.filename = parser.getFilename();
        this.type = parser.getType();
        this.space = parser.getSpace();
        this.page = parser.getPage();
    }

    public EmbeddedResource(String originalText) {
        this(new EmbeddedResourceParser(originalText));
    }

    public static boolean matchesType(EmbeddedResourceParser parser) throws IllegalArgumentException {
        return parser.getType() == null || parser.getType().equals("");
    }

    public boolean isExternal() {
        return this.parser.isExternal();
    }

    public boolean isInternal() {
        return !this.isExternal();
    }

    public String getUrl() {
        return this.url;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getType() {
        return this.type;
    }

    public String getOriginalLinkText() {
        return this.originalText;
    }

    public String getSpace() {
        return this.space;
    }

    public String getPage() {
        return this.page;
    }

    public String toString() {
        return "EmbeddedResource[" + this.originalText + "]";
    }

    public Properties getProperties() {
        return this.properties;
    }
}

