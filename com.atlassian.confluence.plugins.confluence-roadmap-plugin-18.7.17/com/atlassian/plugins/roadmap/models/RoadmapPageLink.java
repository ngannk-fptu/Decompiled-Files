/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.roadmap.models;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoadmapPageLink
implements Serializable {
    @XmlElement
    private String id;
    @XmlElement
    private String spaceKey;
    @XmlElement
    private String title;
    @XmlElement
    private String type;
    @XmlElement
    private String wikiLink;

    public RoadmapPageLink() {
    }

    public RoadmapPageLink(AbstractPage abstractPage) {
        this.id = abstractPage.getIdAsString();
        this.title = abstractPage.getTitle();
        this.spaceKey = abstractPage.getSpaceKey();
        this.wikiLink = abstractPage.getLinkWikiMarkup();
        this.type = abstractPage instanceof Page ? "page" : "blogpost";
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWikiLink() {
        return this.wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof RoadmapPageLink)) {
            return false;
        }
        RoadmapPageLink other = (RoadmapPageLink)obj;
        return this.id != null && this.id.equals(other.getId());
    }

    public String toString() {
        return "Page id: " + this.id;
    }
}

