/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain;

import java.io.Serializable;
import java.util.Objects;

public class SpaceToSidMapId
implements Serializable {
    private long spaceId;
    private long sidId;

    public SpaceToSidMapId() {
    }

    public SpaceToSidMapId(long spaceId, long sidId) {
        this.spaceId = spaceId;
        this.sidId = sidId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpaceToSidMapId that = (SpaceToSidMapId)o;
        return this.spaceId == that.spaceId && this.sidId == that.sidId;
    }

    public int hashCode() {
        return Objects.hash(this.spaceId, this.sidId);
    }

    public long getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    public long getSidId() {
        return this.sidId;
    }

    public void setSidId(long sidId) {
        this.sidId = sidId;
    }
}

