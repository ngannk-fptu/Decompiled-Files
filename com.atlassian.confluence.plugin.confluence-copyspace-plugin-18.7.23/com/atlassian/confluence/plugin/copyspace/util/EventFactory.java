/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.util;

import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionFailureDescriptor;
import com.atlassian.confluence.plugin.copyspace.api.event.analytics.CopySpaceFailedEvent;
import com.atlassian.confluence.plugin.copyspace.api.event.analytics.CopySpaceStartedEvent;
import com.atlassian.confluence.plugin.copyspace.api.event.analytics.CopySpaceSuccessEvent;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;

public class EventFactory {
    private EventFactory() {
    }

    public static CopySpaceStartedEvent createCopySpaceStartedEvent(CopySpaceContext context) {
        return new CopySpaceStartedEvent(context.getUuid(), context.getOriginalSpaceId(), context.getOriginalSpaceKey(), context.isCopyComments(), context.isCopyLabels(), context.isCopyAttachments(), context.isCopyMetadata(), context.isPreserveWatchers(), context.isCopyBlogPosts(), context.isCopyPages(), context.getPagesCount(), context.getCommentsCount(), context.getBlogPostsCount(), context.getAttachmentsCount());
    }

    public static CopySpaceSuccessEvent createCopySpaceSuccessEvent(CopySpaceContext context) {
        return new CopySpaceSuccessEvent(context.getUuid(), context.getOriginalSpaceId(), context.getOriginalSpaceKey(), context.isCopyComments(), context.isCopyLabels(), context.isCopyAttachments(), context.isCopyMetadata(), context.isPreserveWatchers(), context.isCopyBlogPosts(), context.isCopyPages(), context.getPagesCount(), context.getCommentsCount(), context.getBlogPostsCount(), context.getAttachmentsCount(), context.getStartTimestamp());
    }

    public static CopySpaceFailedEvent createCopySpaceFailedEvent(CopySpaceContext context, ExecutionFailureDescriptor executionFailureDescriptor) {
        return new CopySpaceFailedEvent(context.getUuid(), context.getOriginalSpaceId(), context.getOriginalSpaceKey(), context.isCopyComments(), context.isCopyLabels(), context.isCopyAttachments(), context.isCopyMetadata(), context.isPreserveWatchers(), context.isCopyBlogPosts(), context.isCopyPages(), context.getPagesCount(), context.getCommentsCount(), context.getBlogPostsCount(), context.getAttachmentsCount(), context.getStartTimestamp(), executionFailureDescriptor);
    }
}

