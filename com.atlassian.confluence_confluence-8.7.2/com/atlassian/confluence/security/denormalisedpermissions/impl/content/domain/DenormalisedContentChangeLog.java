/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain;

import com.atlassian.confluence.core.NotExportable;
import java.io.Serializable;

public class DenormalisedContentChangeLog
implements Serializable,
NotExportable {
    public static final String TABLE_NAME = "DENORMALISED_CONTENT_CHANGE_LOG";
    private long id;
    private Long contentId;
    private Long contentPermissionSetId;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getContentPermissionSetId() {
        return this.contentPermissionSetId;
    }

    public void setContentPermissionSetId(Long contentPermissionSetId) {
        this.contentPermissionSetId = contentPermissionSetId;
    }

    public String toString() {
        return "DenormalisedContentChangeLog{id=" + this.id + ", contentId=" + this.contentId + ", contentPermissionSetId=" + this.contentPermissionSetId + "}";
    }
}

