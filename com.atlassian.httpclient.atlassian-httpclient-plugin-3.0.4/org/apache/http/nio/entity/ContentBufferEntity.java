/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.entity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.nio.entity.ContentInputStream;
import org.apache.http.nio.util.ContentInputBuffer;
import org.apache.http.util.Args;

public class ContentBufferEntity
extends BasicHttpEntity {
    private final HttpEntity wrappedEntity;

    public ContentBufferEntity(HttpEntity entity, ContentInputBuffer buffer) {
        Args.notNull(entity, "HTTP entity");
        this.wrappedEntity = entity;
        this.setContent(new ContentInputStream(buffer));
    }

    @Override
    public boolean isChunked() {
        return this.wrappedEntity.isChunked();
    }

    @Override
    public long getContentLength() {
        return this.wrappedEntity.getContentLength();
    }

    @Override
    public Header getContentType() {
        return this.wrappedEntity.getContentType();
    }

    @Override
    public Header getContentEncoding() {
        return this.wrappedEntity.getContentEncoding();
    }
}

