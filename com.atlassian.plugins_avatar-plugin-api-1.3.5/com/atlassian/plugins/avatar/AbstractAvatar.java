/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.avatar;

import com.atlassian.plugins.avatar.Avatar;

public abstract class AbstractAvatar
implements Avatar {
    private final String ownerId;
    private final String contentType;
    private final int size;

    public AbstractAvatar(String ownerId, String contentType, int size) {
        this.ownerId = ownerId;
        this.contentType = contentType;
        this.size = size;
    }

    @Override
    public String getOwnerId() {
        return this.ownerId;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }
}

