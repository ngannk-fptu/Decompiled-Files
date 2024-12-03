/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LinkExtractorDto {
    @JsonProperty
    private Long pageId;
    @JsonProperty
    private Long commentId;

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private LinkExtractorDto() {
        this(LinkExtractorDto.builder());
    }

    private LinkExtractorDto(Builder builder) {
        this.pageId = builder.pageId;
        this.commentId = builder.commentId;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public Long getCommentId() {
        return this.commentId;
    }

    public static final class Builder {
        private Long pageId;
        private Long commentId;

        public LinkExtractorDto build() {
            return new LinkExtractorDto(this);
        }

        public Builder pageId(Long pageId) {
            this.pageId = pageId;
            return this;
        }

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }
    }
}

