/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain;

import java.io.Serializable;
import java.util.Objects;

public class ContentToSidMapId
implements Serializable {
    private long contentId;
    private long sidId;

    public ContentToSidMapId() {
    }

    public ContentToSidMapId(long contentId, long sidId) {
        this.contentId = contentId;
        this.sidId = sidId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentToSidMapId that = (ContentToSidMapId)o;
        return this.contentId == that.contentId && this.sidId == that.sidId;
    }

    public int hashCode() {
        return Objects.hash(this.contentId, this.sidId);
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public long getSidId() {
        return this.sidId;
    }

    public void setSidId(long sidId) {
        this.sidId = sidId;
    }
}

