/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.custom_apps.api;

import com.atlassian.plugins.navlink.consumer.menu.services.NavigationLinkComparator;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class CustomApp {
    private final String id;
    private final NavigationLink navigationLink;
    private final String sourceApplicationUrl;
    private final String sourceApplicationName;
    private final List<String> allowedGroups;
    private final boolean editable;
    private final boolean hide;

    public CustomApp(@Nonnull String id, @Nonnull NavigationLink link, String sourceApplicationUrl, String sourceApplicationName, boolean hide, List<String> allowedGroups, boolean editable) {
        this.id = (String)Preconditions.checkNotNull((Object)id);
        this.navigationLink = (NavigationLink)Preconditions.checkNotNull((Object)link);
        this.hide = hide;
        this.allowedGroups = allowedGroups != null ? Lists.newArrayList(allowedGroups) : Lists.newArrayList();
        this.sourceApplicationUrl = sourceApplicationUrl;
        this.sourceApplicationName = sourceApplicationName;
        this.editable = editable;
    }

    public CustomApp(@Nonnull String id, @Nonnull String displayName, @Nonnull String url, String sourceApplicationUrl, String sourceApplicationName, String sourceApplicationType, boolean hide, List<String> allowedGroups, boolean editable, boolean self) {
        this(id, ((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)((NavigationLinkBuilder)new NavigationLinkBuilder().key("custom-apps")).href((String)Preconditions.checkNotNull((Object)url))).label((String)Preconditions.checkNotNull((Object)displayName)).applicationType(sourceApplicationType)).self(self)).weight(NavigationLinkComparator.Weights.MAX.value())).build(), sourceApplicationUrl, sourceApplicationName, hide, allowedGroups, editable);
    }

    public CustomApp(@Nonnull String id, @Nonnull String displayName, @Nonnull String url, String sourceApplicationUrl, String sourceApplicationName, String sourceApplicationType, boolean hide, List<String> allowedGroups, boolean editable) {
        this(id, displayName, url, sourceApplicationUrl, sourceApplicationName, sourceApplicationType, hide, allowedGroups, editable, false);
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getDisplayName() {
        return this.navigationLink.getLabel();
    }

    @Nonnull
    public String getUrl() {
        return this.navigationLink.getHref();
    }

    public String getSourceApplicationUrl() {
        return this.sourceApplicationUrl;
    }

    public String getSourceApplicationName() {
        return this.sourceApplicationName;
    }

    public String getSourceApplicationType() {
        return this.navigationLink.getApplicationType();
    }

    public boolean getHide() {
        return this.hide;
    }

    public List<String> getAllowedGroups() {
        return this.allowedGroups;
    }

    public boolean getEditable() {
        return this.editable;
    }

    public boolean isSelf() {
        return this.navigationLink.isSelf();
    }

    public NavigationLink getNavigationLink() {
        return this.navigationLink;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.id, this.getDisplayName(), this.getUrl()});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CustomApp)) {
            return false;
        }
        CustomApp that = (CustomApp)obj;
        return Objects.equal((Object)this.getId(), (Object)that.getId()) && Objects.equal((Object)this.getDisplayName(), (Object)that.getDisplayName()) && Objects.equal((Object)this.getUrl(), (Object)that.getUrl());
    }

    public String toString() {
        return "CustomApp {id='" + this.id + "', displayName='" + this.getDisplayName() + "', url='" + this.getUrl() + "', sourceApplicationUrl='" + this.sourceApplicationUrl + "', sourceApplicationName='" + this.sourceApplicationName + "', sourceApplicationType='" + this.getSourceApplicationType() + "', hide='" + this.hide + ", editable='" + this.editable + "'}";
    }
}

