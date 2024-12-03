/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain;

import com.atlassian.confluence.core.NotExportable;

public class DenormalisedSpaceChangeLog
implements NotExportable {
    private long id;
    private Long spaceId;
    public static final String TABLE_NAME = "DENORMALISED_SPACE_CHANGE_LOG";

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String toString() {
        return "DenormalisedSpaceChangeLog{id=" + this.id + ", spaceId=" + this.spaceId + "}";
    }

    public Long getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }
}

