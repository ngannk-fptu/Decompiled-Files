/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.check.attachment;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MissingAttachmentDto {
    private final String spaceKey;
    private final Long pageId;
    private final String name;
    private final Long attachmentId;
    private final String path;
    private final String url;

    @JsonCreator
    public MissingAttachmentDto(@JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="pageId") Long pageId, @JsonProperty(value="name") String name, @JsonProperty(value="attachmentId") Long attachmentId, @JsonProperty(value="path") String path, @JsonProperty(value="url") String url) {
        this.spaceKey = spaceKey;
        this.pageId = pageId;
        this.name = name;
        this.attachmentId = attachmentId;
        this.path = path;
        this.url = url;
    }

    @Generated
    public static MissingAttachmentDtoBuilder builder() {
        return new MissingAttachmentDtoBuilder();
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
    public String getName() {
        return this.name;
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
    public String getUrl() {
        return this.url;
    }

    @Generated
    public String toString() {
        return "MissingAttachmentDto(spaceKey=" + this.getSpaceKey() + ", pageId=" + this.getPageId() + ", name=" + this.getName() + ", attachmentId=" + this.getAttachmentId() + ", path=" + this.getPath() + ", url=" + this.getUrl() + ")";
    }

    @Generated
    public static class MissingAttachmentDtoBuilder {
        @Generated
        private String spaceKey;
        @Generated
        private Long pageId;
        @Generated
        private String name;
        @Generated
        private Long attachmentId;
        @Generated
        private String path;
        @Generated
        private String url;

        @Generated
        MissingAttachmentDtoBuilder() {
        }

        @Generated
        public MissingAttachmentDtoBuilder spaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        @Generated
        public MissingAttachmentDtoBuilder pageId(Long pageId) {
            this.pageId = pageId;
            return this;
        }

        @Generated
        public MissingAttachmentDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public MissingAttachmentDtoBuilder attachmentId(Long attachmentId) {
            this.attachmentId = attachmentId;
            return this;
        }

        @Generated
        public MissingAttachmentDtoBuilder path(String path) {
            this.path = path;
            return this;
        }

        @Generated
        public MissingAttachmentDtoBuilder url(String url) {
            this.url = url;
            return this;
        }

        @Generated
        public MissingAttachmentDto build() {
            return new MissingAttachmentDto(this.spaceKey, this.pageId, this.name, this.attachmentId, this.path, this.url);
        }

        @Generated
        public String toString() {
            return "MissingAttachmentDto.MissingAttachmentDtoBuilder(spaceKey=" + this.spaceKey + ", pageId=" + this.pageId + ", name=" + this.name + ", attachmentId=" + this.attachmentId + ", path=" + this.path + ", url=" + this.url + ")";
        }
    }
}

