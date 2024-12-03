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
public class SpaceBean {
    @XmlElement
    final String logoUrl;
    @XmlElement
    final String homeUrl;
    @XmlElement
    final String name;
    @XmlElement
    final String key;
    @XmlElement
    final String description;
    @XmlElement
    final String browseSpaceUrl;
    @XmlElement
    final boolean isPersonal;
    @XmlElement
    final boolean isPersonalSpaceBelongsToUser;

    public SpaceBean(String key, String name, String description, String homeUrl, String logoUrl, String browseSpaceUrl, boolean isPersonal, boolean isPersonalSpaceBelongsToUser) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.homeUrl = homeUrl;
        this.logoUrl = logoUrl;
        this.browseSpaceUrl = browseSpaceUrl;
        this.isPersonal = isPersonal;
        this.isPersonalSpaceBelongsToUser = isPersonalSpaceBelongsToUser;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getHomeUrl() {
        return this.homeUrl;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public String getBrowseSpaceUrl() {
        return this.browseSpaceUrl;
    }

    public boolean isPersonal() {
        return this.isPersonal;
    }

    public boolean isPersonalSpaceBelongsToUser() {
        return this.isPersonalSpaceBelongsToUser;
    }
}

