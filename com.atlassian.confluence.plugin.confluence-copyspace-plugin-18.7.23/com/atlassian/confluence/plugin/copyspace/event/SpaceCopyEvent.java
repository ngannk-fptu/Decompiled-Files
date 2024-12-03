/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.space.SpaceEvent
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugin.copyspace.event;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.spaces.Space;

public class SpaceCopyEvent
extends SpaceEvent {
    private final boolean copyComments;
    private final boolean copyLabels;
    private final boolean copyAttachments;
    private final boolean keepMetaData;
    private final boolean preserveWatchers;
    private final boolean copyBlogposts;
    private final boolean copyPages;
    private final String originalSpaceKey;

    public SpaceCopyEvent(Object src, Space space, boolean copyComments, boolean copyLabels, boolean copyAttachments, boolean keepMetaData, boolean preserveWatchers, boolean copyBlogposts, boolean copyPages, String originalSpaceKey) {
        super(src, space);
        this.copyComments = copyComments;
        this.copyLabels = copyLabels;
        this.copyAttachments = copyAttachments;
        this.keepMetaData = keepMetaData;
        this.preserveWatchers = preserveWatchers;
        this.copyBlogposts = copyBlogposts;
        this.copyPages = copyPages;
        this.originalSpaceKey = originalSpaceKey;
    }

    public boolean isCopyComments() {
        return this.copyComments;
    }

    public boolean isCopyLabels() {
        return this.copyLabels;
    }

    public boolean isCopyAttachments() {
        return this.copyAttachments;
    }

    public boolean isKeepMetaData() {
        return this.keepMetaData;
    }

    public boolean isPreserveWatchers() {
        return this.preserveWatchers;
    }

    public boolean isCopyBlogposts() {
        return this.copyBlogposts;
    }

    public boolean isCopyPages() {
        return this.copyPages;
    }

    public String getOriginalSpaceKey() {
        return this.originalSpaceKey;
    }
}

