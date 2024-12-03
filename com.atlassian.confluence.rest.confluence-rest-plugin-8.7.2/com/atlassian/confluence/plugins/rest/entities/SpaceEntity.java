/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.entities.DateEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityExpander;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserProperties;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.Expander;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="space")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=SpaceEntityExpander.class)
public class SpaceEntity
extends SearchResultEntity {
    @XmlAttribute
    private String expand;
    @XmlAttribute
    private String key;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String title;
    @XmlElement(name="link")
    private List<Link> links;
    @XmlElement(name="description")
    private String description;
    @XmlElement(name="lastModifiedDate")
    private DateEntity lastModifiedDate;
    @XmlElement(name="createdDate")
    private DateEntity createdDate;
    @XmlElement
    private String wikiLink;
    @XmlElement(name="rootpages")
    @Expandable(value="rootpages")
    private ContentEntityList rootPages;
    @XmlElement(name="userproperties")
    @Expandable(value="userproperties")
    private SpaceEntityUserProperties userProperties;
    @XmlElement(name="home")
    @Expandable(value="home")
    private ContentEntity home;
    @XmlAttribute
    private String type = "space";

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public ContentEntityList getRootPages() {
        return this.rootPages;
    }

    public ContentEntity getHome() {
        return this.home;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
        this.title = name;
    }

    public String getTitle() {
        return this.name;
    }

    public void setHome(ContentEntity home) {
        this.home = home;
    }

    public void setRootPages(ContentEntityList rootPages) {
        this.rootPages = rootPages;
    }

    public SpaceEntityUserProperties getUserProperties() {
        return this.userProperties;
    }

    public void setUserProperties(SpaceEntityUserProperties userProperties) {
        this.userProperties = userProperties;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLastModifiedDate(DateEntity lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public DateEntity getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setCreatedDate(DateEntity createdDate) {
        this.createdDate = createdDate;
    }

    public DateEntity getCreatedDate() {
        return this.createdDate;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getWikiLink() {
        return this.wikiLink;
    }

    public void addLink(Link link) {
        this.getLinks().add(link);
    }

    public List<Link> getLinks() {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        return this.links;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpaceEntity.class.getSimpleName() + "[", "]").add("expand='" + this.expand + "'").add("key='" + this.key + "'").add("name='" + this.name + "'").add("title='" + this.title + "'").add("links=" + this.links).add("description='" + this.description + "'").add("lastModifiedDate=" + this.lastModifiedDate).add("createdDate=" + this.createdDate).add("wikiLink='" + this.wikiLink + "'").add("rootPages=" + this.rootPages).add("userProperties=" + this.userProperties).add("home=" + this.home).add("type='" + this.type + "'").toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.createdDate == null ? 0 : this.createdDate.hashCode());
        result = 31 * result + (this.description == null ? 0 : this.description.hashCode());
        result = 31 * result + (this.key == null ? 0 : this.key.hashCode());
        result = 31 * result + (this.lastModifiedDate == null ? 0 : this.lastModifiedDate.hashCode());
        result = 31 * result + (this.links == null ? 0 : this.links.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        result = 31 * result + (this.wikiLink == null ? 0 : this.wikiLink.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SpaceEntity other = (SpaceEntity)obj;
        if (this.createdDate == null ? other.createdDate != null : !this.createdDate.equals(other.createdDate)) {
            return false;
        }
        if (this.description == null ? other.description != null : !this.description.equals(other.description)) {
            return false;
        }
        if (this.key == null ? other.key != null : !this.key.equals(other.key)) {
            return false;
        }
        if (this.lastModifiedDate == null ? other.lastModifiedDate != null : !this.lastModifiedDate.equals(other.lastModifiedDate)) {
            return false;
        }
        if (this.links == null ? other.links != null : !this.links.equals(other.links)) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        if (this.type == null ? other.type != null : !this.type.equals(other.type)) {
            return false;
        }
        return !(this.wikiLink == null ? other.wikiLink != null : !this.wikiLink.equals(other.wikiLink));
    }
}

