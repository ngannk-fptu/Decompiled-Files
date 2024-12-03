/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.navlink.producer.contentlinks.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="content-link")
@XmlAccessorType(value=XmlAccessType.PROPERTY)
public class ContentLinkEntity {
    public static ContentLinkEntity EXAMPLE = new ContentLinkEntity("http://my.jira.server/secure/browse/MY_PROJECT", "My Awesome Project Home", "Go to the home page for My Awesome Project", false);
    private String link;
    private String label;
    private String tooltip;
    private boolean custom;

    public ContentLinkEntity() {
    }

    public ContentLinkEntity(String link, String label, String tooltip, boolean isCustom) {
        this.link = link;
        this.label = label;
        this.tooltip = tooltip;
        this.custom = isCustom;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean isCustom() {
        return this.custom;
    }

    public void setCustom(boolean isCustom) {
        this.custom = isCustom;
    }

    public String toString() {
        return "NavigationShortcut{, link='" + this.link + '\'' + ", label='" + this.label + '\'' + ", tooltip='" + this.tooltip + '\'' + ", custom='" + this.custom + '\'' + '}';
    }
}

