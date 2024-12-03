/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.ia.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PageNodeBean {
    @XmlElement
    String pageId;
    @XmlElement
    String title;
    @XmlElement
    String url;
    @XmlElement
    boolean active;

    public PageNodeBean(String pageId, String title, String url, boolean active) {
        this.pageId = pageId;
        this.title = title;
        this.url = url;
        this.active = active;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageNodeBean)) {
            return false;
        }
        PageNodeBean that = (PageNodeBean)o;
        if (this.active != that.active) {
            return false;
        }
        if (this.pageId != null ? !this.pageId.equals(that.pageId) : that.pageId != null) {
            return false;
        }
        if (this.title != null ? !this.title.equals(that.title) : that.title != null) {
            return false;
        }
        return !(this.url != null ? !this.url.equals(that.url) : that.url != null);
    }

    public int hashCode() {
        int result = this.pageId != null ? this.pageId.hashCode() : 0;
        result = 31 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 31 * result + (this.url != null ? this.url.hashCode() : 0);
        result = 31 * result + (this.active ? 1 : 0);
        return result;
    }
}

