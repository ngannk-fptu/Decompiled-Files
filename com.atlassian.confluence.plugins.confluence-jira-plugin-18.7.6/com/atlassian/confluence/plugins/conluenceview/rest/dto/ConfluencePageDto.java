/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.conluenceview.rest.dto;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement
public class ConfluencePageDto {
    private Long pageId;
    private String pageTitle;
    private String pageUrl;
    private Date lastModified;
    private List<String> labels;
    private String author;
    private String lastModifier;

    private ConfluencePageDto(Builder builder) {
        this.author = builder.author;
        this.pageId = builder.pageId;
        this.pageTitle = builder.pageTitle;
        this.pageUrl = builder.pageUrl;
        this.lastModified = builder.lastModified;
        this.labels = builder.labels;
        this.lastModifier = builder.lastModifier;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getPageId() {
        return this.pageId;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getLastModifier() {
        return this.lastModifier;
    }

    public static final class Builder {
        private String author;
        private Long pageId;
        private String pageTitle;
        private String pageUrl;
        private Date lastModified;
        private List<String> labels;
        private String lastModifier;

        private Builder() {
        }

        public Builder withAuthor(String val) {
            this.author = val;
            return this;
        }

        public Builder withPageId(Long val) {
            this.pageId = val;
            return this;
        }

        public Builder withPageTitle(String val) {
            this.pageTitle = val;
            return this;
        }

        public Builder withPageUrl(String val) {
            this.pageUrl = val;
            return this;
        }

        public Builder withLastModified(Date val) {
            this.lastModified = val;
            return this;
        }

        public Builder withLabels(List<String> val) {
            this.labels = val;
            return this;
        }

        public Builder withLastModifier(String val) {
            this.lastModifier = val;
            return this;
        }

        public ConfluencePageDto build() {
            return new ConfluencePageDto(this);
        }
    }
}

