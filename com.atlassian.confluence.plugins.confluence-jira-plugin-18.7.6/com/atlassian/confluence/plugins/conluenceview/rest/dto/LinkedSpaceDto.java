/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.conluenceview.rest.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement
public class LinkedSpaceDto
implements Serializable {
    String spaceKey;
    String spaceName;
    String spaceUrl;
    String spaceIcon;

    private LinkedSpaceDto(Builder builder) {
        this.spaceIcon = builder.spaceIcon;
        this.spaceKey = builder.spaceKey;
        this.spaceName = builder.spaceName;
        this.spaceUrl = builder.spaceUrl;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getSpaceIcon() {
        return this.spaceIcon;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public String getSpaceUrl() {
        return this.spaceUrl;
    }

    public static final class Builder {
        private String spaceIcon;
        private String spaceKey;
        private String spaceName;
        private String spaceUrl;

        private Builder() {
        }

        public Builder withSpaceIcon(String val) {
            this.spaceIcon = val;
            return this;
        }

        public Builder withSpaceKey(String val) {
            this.spaceKey = val;
            return this;
        }

        public Builder withSpaceName(String val) {
            this.spaceName = val;
            return this;
        }

        public Builder withSpaceUrl(String val) {
            this.spaceUrl = val;
            return this;
        }

        public LinkedSpaceDto build() {
            return new LinkedSpaceDto(this);
        }
    }
}

