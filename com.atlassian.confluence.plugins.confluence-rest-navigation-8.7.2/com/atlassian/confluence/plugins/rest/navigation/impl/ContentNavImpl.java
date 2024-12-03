/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.Navigation$ContentNav
 *  com.atlassian.confluence.api.nav.Navigation$ContentRestrictionByOperationNav
 *  com.atlassian.confluence.api.nav.Navigation$ExperimentalContentNav
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.atlassian.confluence.plugins.rest.navigation.impl.ContentRestrictionByOperationNavImpl;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.List;

class ContentNavImpl
extends DelegatingPathBuilder
implements Navigation.ContentNav,
Navigation.ExperimentalContentNav {
    private static final List<ContentStatus> NAVIGABLE_STATUSES = ImmutableList.of((Object)ContentStatus.CURRENT, (Object)ContentStatus.HISTORICAL, (Object)ContentStatus.TRASHED, (Object)ContentStatus.DRAFT);
    private static final String PATH_SEPARATOR = "/";
    private final ContentSelector contentSelector;
    private final AbstractNav baseBuilder;

    public static Navigation.ContentNav build(Content content, AbstractNav builder) {
        return ContentNavImpl.build(content.getSelector(), builder);
    }

    public static ContentNavImpl build(ContentSelector selector, AbstractNav builder) {
        ContentStatus status = selector.getStatus();
        if (selector.getId() == null || status != null && !NAVIGABLE_STATUSES.contains(status)) {
            return null;
        }
        return new ContentNavImpl(selector, builder);
    }

    private ContentNavImpl(ContentSelector selector, AbstractNav baseBuilder) {
        super("/content/" + selector.getId().serialise(), baseBuilder);
        this.contentSelector = selector;
        this.baseBuilder = baseBuilder;
        ContentStatus status = selector.getStatus();
        if (status != null && !ContentStatus.CURRENT.equals((Object)status)) {
            this.addParam("status", status.serialise());
            if (ContentStatus.HISTORICAL.equals((Object)status)) {
                this.addParam("version", selector.getVersion());
            }
        }
    }

    @VisibleForTesting
    ContentNavImpl asCurrent() {
        AbstractNav baseClone = this.baseBuilder.copy();
        baseClone.getParams().clear();
        return new ContentNavImpl(this.contentSelector.asCurrent(), baseClone);
    }

    public Navigation.Builder history() {
        return new DelegatingPathBuilder("/history", this.asCurrent());
    }

    public Navigation.Builder label() {
        return new DelegatingPathBuilder("/label", this.asCurrent());
    }

    public AbstractNav children(Depth depth) {
        Object version;
        boolean isChildPath = depth == Depth.ROOT;
        String subPath = isChildPath ? "/child" : "/descendant";
        DelegatingPathBuilder pathBuilder = new DelegatingPathBuilder(subPath, this.asCurrent());
        if (isChildPath && (version = this.getParams().get("version")) != null) {
            pathBuilder.addParam("parentVersion", version);
        }
        return pathBuilder;
    }

    public Navigation.Builder children(ContentType type, Depth depth) {
        return new DelegatingPathBuilder(PATH_SEPARATOR + type.getType(), this.children(depth));
    }

    public Navigation.Builder property(JsonContentProperty property) {
        return new DelegatingPathBuilder("/property/" + property.getKey(), this.asCurrent());
    }

    public Navigation.Builder properties() {
        return new DelegatingPathBuilder("/property", this.asCurrent());
    }

    public Navigation.ContentRestrictionByOperationNav restrictionByOperation() {
        return new ContentRestrictionByOperationNavImpl(this.asCurrent());
    }

    public Navigation.Builder restrictions() {
        return new DelegatingPathBuilder("/restriction", this.asCurrent());
    }

    public Navigation.Builder version(Version version) {
        return new DelegatingPathBuilder("/version/" + version.getNumber(), this.asCurrent());
    }
}

