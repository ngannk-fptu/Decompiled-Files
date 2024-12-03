/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.ContentToSidMapId;
import java.io.Serializable;

public class DenormalisedContentViewPermission
implements Serializable,
NotExportable {
    public static final String TABLE_NAME = "DENORMALISED_CONTENT_VIEW_PERMISSIONS";
    private ContentToSidMapId contentToSidMapId;

    public DenormalisedContentViewPermission() {
    }

    public DenormalisedContentViewPermission(long contentId, long sidId) {
        this.contentToSidMapId = new ContentToSidMapId(contentId, sidId);
    }

    public ContentToSidMapId getContentToSidMapId() {
        return this.contentToSidMapId;
    }

    public void setContentToSidMapId(ContentToSidMapId contentToSidMapId) {
        this.contentToSidMapId = contentToSidMapId;
    }
}

