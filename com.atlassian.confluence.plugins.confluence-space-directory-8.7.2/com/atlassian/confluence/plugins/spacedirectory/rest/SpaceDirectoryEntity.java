/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.entities.DateEntity
 *  com.atlassian.confluence.plugins.rest.entities.LabelEntityList
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.spacedirectory.rest;

import com.atlassian.confluence.plugins.rest.entities.DateEntity;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityList;
import com.atlassian.confluence.plugins.spacedirectory.rest.UserEntityList;
import com.atlassian.plugins.rest.common.Link;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="concise-space-entity")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SpaceDirectoryEntity {
    @XmlAttribute
    private String key;
    @XmlAttribute
    private String name;
    @XmlAttribute(name="effective-user")
    private String effectiveUser;
    @XmlAttribute
    private String title;
    @XmlElement(name="link")
    private List<Link> links;
    @XmlElement(name="description")
    private String description;
    @XmlElement(name="admins")
    private UserEntityList admins;
    @XmlElement(name="lastModifiedDate")
    private DateEntity lastModifiedDate;
    @XmlElement(name="createdDate")
    private DateEntity createdDate;
    @XmlElement
    private String wikiLink;
    @XmlElement
    private LabelEntityList labels;
    @XmlElement(name="logo")
    private Link logo;
    @XmlAttribute(name="favourite")
    private Boolean favourite;
    @XmlAttribute(name="adminEllipsis")
    private Boolean adminEllipsis;
    @XmlElement(name="summaryPath")
    private String summaryPath;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.title = name;
    }

    public String getTitle() {
        return this.title;
    }

    public List<Link> getLinks() {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        return this.links;
    }

    public void addLink(Link link) {
        this.getLinks().add(link);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntityList getAdmins() {
        return this.admins;
    }

    public void setAdmins(UserEntityList admins) {
        this.admins = admins;
    }

    public DateEntity getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(DateEntity lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public DateEntity getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(DateEntity createdDate) {
        this.createdDate = createdDate;
    }

    public String getWikiLink() {
        return this.wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getEffectiveUser() {
        return this.effectiveUser;
    }

    public void setEffectiveUser(String effectiveUser) {
        this.effectiveUser = effectiveUser;
    }

    public LabelEntityList getLabels() {
        return this.labels;
    }

    public void setLabels(LabelEntityList labels) {
        this.labels = labels;
    }

    public Link getLogo() {
        return this.logo;
    }

    public void setLogo(Link logo) {
        this.logo = logo;
    }

    public Boolean getFavourite() {
        return this.favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Boolean getAdminEllipsis() {
        return this.adminEllipsis;
    }

    public void setAdminEllipsis(Boolean adminEllipsis) {
        this.adminEllipsis = adminEllipsis;
    }

    public String getSummaryPath() {
        return this.summaryPath;
    }

    public void setSummaryPath(String summaryPath) {
        this.summaryPath = summaryPath;
    }
}

