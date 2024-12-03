/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.SpaceToSidMapId;
import java.io.Serializable;

public class DenormalisedSpacePermission
implements Serializable,
NotExportable {
    private SpaceToSidMapId spaceToSidMapId;

    public DenormalisedSpacePermission() {
    }

    public DenormalisedSpacePermission(long spaceId, long sidId) {
        this.spaceToSidMapId = new SpaceToSidMapId(spaceId, sidId);
    }

    public DenormalisedSpacePermission(SpaceToSidMapId spaceToSidMapId) {
        this.spaceToSidMapId = spaceToSidMapId;
    }

    public SpaceToSidMapId getSpaceToSidMapId() {
        return this.spaceToSidMapId;
    }

    public void setSpaceToSidMapId(SpaceToSidMapId spaceToSidMapId) {
        this.spaceToSidMapId = spaceToSidMapId;
    }
}

