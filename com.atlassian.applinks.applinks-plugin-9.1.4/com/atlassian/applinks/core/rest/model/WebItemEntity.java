/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="webItem")
public class WebItemEntity {
    private String id;
    private String url;
    private String accessKey;
    private String iconUrl;
    private Integer iconHeight;
    private Integer iconWidth;
    private String label;
    private String tooltip;
    private String styleClass;

    private WebItemEntity() {
    }

    private WebItemEntity(String id, String url, String accessKey, String iconUrl, Integer iconHeight, Integer iconWidth, String label, String tooltip, String styleClass) {
        this.id = id;
        this.url = url;
        this.accessKey = accessKey;
        this.iconUrl = iconUrl;
        this.iconHeight = iconHeight;
        this.iconWidth = iconWidth;
        this.label = label;
        this.tooltip = tooltip;
        this.styleClass = styleClass;
    }

    public String getId() {
        return this.id;
    }

    public String getUrl() {
        return this.url;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public Integer getIconHeight() {
        return this.iconHeight;
    }

    public Integer getIconWidth() {
        return this.iconWidth;
    }

    public String getLabel() {
        return this.label;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public static class Builder {
        private String id;
        private String url;
        private String accessKey;
        private String iconUrl;
        private Integer iconHeight;
        private Integer iconWidth;
        private String label;
        private String tooltip;
        private String styleClass;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder iconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        public Builder iconHeight(Integer iconHeight) {
            this.iconHeight = iconHeight;
            return this;
        }

        public Builder iconWidth(Integer iconWidth) {
            this.iconWidth = iconWidth;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder styleClass(String styleClass) {
            this.styleClass = styleClass;
            return this;
        }

        public WebItemEntity build() {
            return new WebItemEntity(this.id, this.url, this.accessKey, this.iconUrl, this.iconHeight, this.iconWidth, this.label, this.tooltip, this.styleClass);
        }
    }
}

