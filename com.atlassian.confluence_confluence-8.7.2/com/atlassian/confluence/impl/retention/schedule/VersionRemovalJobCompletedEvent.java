/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.schedule;

import com.atlassian.confluence.impl.retention.schedule.AttachmentVersionRemovalSummary;
import com.atlassian.confluence.impl.retention.schedule.PageVersionRemovalSummary;
import com.atlassian.confluence.impl.retention.schedule.VersionRemovalJobType;
import java.util.Objects;

public class VersionRemovalJobCompletedEvent {
    private VersionRemovalJobType type;
    private PageVersionRemovalSummary pageVersionRemovalSummary;
    private AttachmentVersionRemovalSummary attachmentVersionRemovalSummary;

    public VersionRemovalJobCompletedEvent(VersionRemovalJobType type, PageVersionRemovalSummary pageVersionRemovalSummary, AttachmentVersionRemovalSummary attachmentVersionRemovalSummary) {
        this.type = type;
        this.pageVersionRemovalSummary = pageVersionRemovalSummary;
        this.attachmentVersionRemovalSummary = attachmentVersionRemovalSummary;
    }

    public VersionRemovalJobType getType() {
        return this.type;
    }

    public void setType(VersionRemovalJobType type) {
        this.type = type;
    }

    public PageVersionRemovalSummary getPageVersionRemovalSummary() {
        return this.pageVersionRemovalSummary;
    }

    public void setPageVersionRemovalSummary(PageVersionRemovalSummary pageVersionRemovalSummary) {
        this.pageVersionRemovalSummary = pageVersionRemovalSummary;
    }

    public AttachmentVersionRemovalSummary getAttachmentVersionRemovalSummary() {
        return this.attachmentVersionRemovalSummary;
    }

    public void setAttachmentVersionRemovalSummary(AttachmentVersionRemovalSummary attachmentVersionRemovalSummary) {
        this.attachmentVersionRemovalSummary = attachmentVersionRemovalSummary;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.pageVersionRemovalSummary, this.attachmentVersionRemovalSummary});
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VersionRemovalJobCompletedEvent)) {
            return false;
        }
        VersionRemovalJobCompletedEvent versionRemovalJobCompletedEvent = (VersionRemovalJobCompletedEvent)obj;
        return Objects.equals((Object)versionRemovalJobCompletedEvent.type, (Object)this.type) && Objects.equals(versionRemovalJobCompletedEvent.pageVersionRemovalSummary, this.pageVersionRemovalSummary) && Objects.equals(versionRemovalJobCompletedEvent.attachmentVersionRemovalSummary, this.attachmentVersionRemovalSummary);
    }
}

