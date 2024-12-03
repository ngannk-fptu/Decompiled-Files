/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

public class ContentLocatorBuilder {
    private long id;
    private String contentType;
    private String spaceKey;
    private String title;

    public ContentLocatorBuilder withId(long id) {
        if (this.id != 0L) {
            throw new IllegalStateException("Can not set ID twice");
        }
        this.id = id;
        return this;
    }

    public ContentLocatorBuilder withContentType(String contentType) {
        if (this.contentType != null) {
            throw new IllegalStateException("Can not set content type twice");
        }
        this.contentType = contentType;
        return this;
    }

    public ContentLocatorBuilder withSpaceKey(String spaceKey) {
        if (this.spaceKey != null) {
            throw new IllegalStateException("Can not set space key twice");
        }
        this.spaceKey = spaceKey;
        return this;
    }

    public ContentLocatorBuilder withTitle(String title) {
        if (this.title != null) {
            throw new IllegalStateException("Can not set title twice");
        }
        this.title = title;
        return this;
    }
}

