/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.pagehierarchy.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.pagehierarchy.rest.CopyPageHierarchyRequest;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

@EventName(value="confluence.bulkoperations.copy")
public class CopyAnalyticsEvent {
    private final boolean copyAttachments;
    private final boolean copyPermissions;
    private final boolean copyLabels;
    private final boolean prefix;
    private final boolean searchReplace;
    private final int pages;
    private final boolean sameSpace;

    public CopyAnalyticsEvent(CopyPageHierarchyRequest request, int pages, boolean sameSpace) {
        this.copyAttachments = request.isCopyAttachments();
        this.copyPermissions = request.isCopyPermissions();
        this.copyLabels = request.isCopyLabels();
        this.prefix = StringUtils.isNotBlank((CharSequence)request.getTitleOptions().getPrefix());
        this.searchReplace = StringUtils.isNotBlank((CharSequence)request.getTitleOptions().getSearch());
        this.pages = pages;
        this.sameSpace = sameSpace;
    }

    public String toString() {
        return Objects.toString(this);
    }

    public boolean getCopyAttachments() {
        return this.copyAttachments;
    }

    public boolean getCopyPermissions() {
        return this.copyPermissions;
    }

    public boolean getCopyLabels() {
        return this.copyLabels;
    }

    public boolean getPrefix() {
        return this.prefix;
    }

    public boolean getSearchReplace() {
        return this.searchReplace;
    }

    public int getPages() {
        return this.pages;
    }

    public boolean getSameSpace() {
        return this.sameSpace;
    }
}

