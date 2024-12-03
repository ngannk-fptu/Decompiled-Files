/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.analytic.ExportScope;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;

public class PdfExportEvent {
    private final ExportScope exportScope;
    private final long spaceId;
    private final String spaceKey;
    private final String spaceName;
    private final long pageId;
    private final String pageTitle;
    private final String pageType;

    public PdfExportEvent(AbstractPage page) {
        this.exportScope = ExportScope.PAGE;
        Space space = page.getSpace();
        this.spaceId = space.getId();
        this.spaceKey = space.getKey();
        this.spaceName = space.getName();
        this.pageId = page.getId();
        this.pageTitle = page.getTitle();
        this.pageType = page.getType();
    }

    public PdfExportEvent(Space space) {
        this.exportScope = ExportScope.SPACE;
        this.spaceId = space.getId();
        this.spaceKey = space.getKey();
        this.spaceName = space.getName();
        this.pageId = 0L;
        this.pageTitle = null;
        this.pageType = null;
    }

    public ExportScope getExportScope() {
        return this.exportScope;
    }

    public long getSpaceId() {
        return this.spaceId;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public long getPageId() {
        return this.pageId;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public String getPageType() {
        return this.pageType;
    }
}

