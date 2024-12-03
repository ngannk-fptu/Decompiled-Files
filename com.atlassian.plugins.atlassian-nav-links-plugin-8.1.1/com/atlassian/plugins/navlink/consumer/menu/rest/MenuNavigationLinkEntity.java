/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.navlink.consumer.menu.rest;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.links.SourceType;
import com.atlassian.plugins.navlink.producer.navigation.rest.MenuItemKey;
import com.google.common.base.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="key")
@XmlAccessorType(value=XmlAccessType.PROPERTY)
public class MenuNavigationLinkEntity {
    private MenuItemKey key;
    private String link;
    private String label;
    private String description;
    private String tooltip;
    private String iconUrl;
    private boolean local;
    private boolean self;
    private String applicationType;

    public MenuNavigationLinkEntity() {
    }

    public MenuNavigationLinkEntity(NavigationLink navLink) {
        this.key = MenuNavigationLinkEntity.retrieveKey(navLink);
        this.link = navLink.getHref();
        this.label = navLink.getLabel();
        this.tooltip = navLink.getTooltip();
        this.iconUrl = navLink.getIconUrl();
        this.local = navLink.getSource().type() == SourceType.LOCAL;
        this.self = navLink.isSelf();
        this.applicationType = navLink.getApplicationType();
    }

    @Nullable
    private static MenuItemKey retrieveKey(@Nonnull NavigationLink navLink) {
        String key = navLink.getKey();
        return key != null ? new MenuItemKey(key) : null;
    }

    public MenuItemKey getKey() {
        return this.key;
    }

    public void setKey(MenuItemKey key) {
        this.key = key;
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

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getApplicationType() {
        return this.applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isSelf() {
        return this.self;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MenuNavigationLinkEntity that = (MenuNavigationLinkEntity)o;
        return Objects.equal((Object)this.key, (Object)that.key) && Objects.equal((Object)this.link, (Object)that.link) && Objects.equal((Object)this.label, (Object)that.label) && Objects.equal((Object)this.description, (Object)that.description) && Objects.equal((Object)this.tooltip, (Object)that.tooltip) && Objects.equal((Object)this.iconUrl, (Object)that.iconUrl) && Objects.equal((Object)this.local, (Object)that.local) && Objects.equal((Object)this.self, (Object)that.self) && Objects.equal((Object)this.local, (Object)that.local) && Objects.equal((Object)this.applicationType, (Object)that.applicationType);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.key, this.link, this.label, this.description, this.tooltip, this.iconUrl, this.local, this.self, this.applicationType});
    }

    public String toString() {
        return "MenuNavigationLinkEntity{, key=" + this.key + ", link='" + this.link + '\'' + ", label='" + this.label + '\'' + ", description='" + this.description + '\'' + ", tooltip='" + this.tooltip + '\'' + ", iconUrl='" + this.iconUrl + '\'' + ", local=" + this.local + ", self=" + this.self + ", applicationType=" + this.applicationType + '}';
    }
}

