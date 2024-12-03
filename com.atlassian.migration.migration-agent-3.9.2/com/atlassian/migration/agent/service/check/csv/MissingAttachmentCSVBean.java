/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.csv;

import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentDto;
import com.atlassian.migration.agent.service.check.csv.AbstractCheckResultCSVBean;
import lombok.Generated;

public class MissingAttachmentCSVBean
implements AbstractCheckResultCSVBean {
    private String spaceKey;
    private Long pageId;
    private Long attachmentId;
    private String path;
    private String name;
    private String url;

    public MissingAttachmentCSVBean(MissingAttachmentDto missingAttachmentDto) {
        this.spaceKey = missingAttachmentDto.getSpaceKey();
        this.pageId = missingAttachmentDto.getPageId();
        this.attachmentId = missingAttachmentDto.getAttachmentId();
        this.path = missingAttachmentDto.getPath();
        this.name = missingAttachmentDto.getName();
        this.url = missingAttachmentDto.getUrl();
    }

    @Generated
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Generated
    public Long getPageId() {
        return this.pageId;
    }

    @Generated
    public Long getAttachmentId() {
        return this.attachmentId;
    }

    @Generated
    public String getPath() {
        return this.path;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getUrl() {
        return this.url;
    }
}

