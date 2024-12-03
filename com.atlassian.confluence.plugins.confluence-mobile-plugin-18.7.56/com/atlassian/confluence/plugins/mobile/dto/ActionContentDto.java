/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.plugins.mobile.dto.AbstractPageDto;
import com.atlassian.confluence.plugins.mobile.dto.AttachmentDto;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import org.codehaus.jackson.annotate.JsonProperty;

public class ActionContentDto {
    @JsonProperty
    private AbstractPageDto page;
    @JsonProperty
    private AttachmentDto attachment;
    @JsonProperty
    private CommentDto comment;
    @JsonProperty
    private SpaceDto space;

    public AbstractPageDto getPage() {
        return this.page;
    }

    public void setPage(AbstractPageDto page) {
        this.page = page;
    }

    public AttachmentDto getAttachment() {
        return this.attachment;
    }

    public void setAttachment(AttachmentDto attachment) {
        this.attachment = attachment;
    }

    public CommentDto getComment() {
        return this.comment;
    }

    public void setComment(CommentDto comment) {
        this.comment = comment;
    }

    public SpaceDto getSpace() {
        return this.space;
    }

    public void setSpace(SpaceDto space) {
        this.space = space;
    }
}

