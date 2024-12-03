/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.analytics;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class TrashRemovalStatistic {
    private int purgedPageCnt = 0;
    private int purgedAttachmentCnt = 0;
    private long purgedAttachmentTotalSizeInBytes = 0L;

    public TrashRemovalStatistic() {
        this(0, 0, 0L);
    }

    public TrashRemovalStatistic(int purgedPageCnt, int purgedAttachmentCnt, long purgedAttachmentTotalSizeInBytes) {
        this.purgedPageCnt = purgedPageCnt;
        this.purgedAttachmentCnt = purgedAttachmentCnt;
        this.purgedAttachmentTotalSizeInBytes = purgedAttachmentTotalSizeInBytes;
    }

    public void pageOrBlogDeleted(AbstractPage deletedPageOrBlog) {
        this.purgedPageCnt += deletedPageOrBlog.getVersion();
    }

    public void attachmentDeleted(List<Attachment> deletedVersions) {
        this.purgedAttachmentCnt += deletedVersions.size();
        deletedVersions.forEach(deletedVersion -> this.purgedAttachmentTotalSizeInBytes += deletedVersion.getFileSize());
    }

    public int getPurgedPageCnt() {
        return this.purgedPageCnt;
    }

    public int getPurgedAttachmentCnt() {
        return this.purgedAttachmentCnt;
    }

    public long getPurgedAttachmentTotalSizeInBytes() {
        return this.purgedAttachmentTotalSizeInBytes;
    }
}

