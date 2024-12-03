/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.schedule;

import java.util.Objects;

public class AttachmentVersionRemovalSummary {
    private long attachmentsRemovedByGlobalRules;
    private long attachmentSizeRemovedByGlobalRules;
    private long attachmentsRemovedBySpaceRules;
    private long attachmentSizeRemovedBySpaceRules;

    public AttachmentVersionRemovalSummary(long attachmentsRemovedByGlobalRules, long attachmentSizeRemovedByGlobalRules, long attachmentsRemovedBySpaceRules, long attachmentSizeRemovedBySpaceRules) {
        this.attachmentsRemovedByGlobalRules = attachmentsRemovedByGlobalRules;
        this.attachmentSizeRemovedByGlobalRules = attachmentSizeRemovedByGlobalRules;
        this.attachmentsRemovedBySpaceRules = attachmentsRemovedBySpaceRules;
        this.attachmentSizeRemovedBySpaceRules = attachmentSizeRemovedBySpaceRules;
    }

    public long getAttachmentsRemovedByGlobalRules() {
        return this.attachmentsRemovedByGlobalRules;
    }

    public void setAttachmentsRemovedByGlobalRules(long attachmentsRemovedByGlobalRules) {
        this.attachmentsRemovedByGlobalRules = attachmentsRemovedByGlobalRules;
    }

    public long getAttachmentSizeRemovedByGlobalRules() {
        return this.attachmentSizeRemovedByGlobalRules;
    }

    public void setAttachmentSizeRemovedByGlobalRules(long attachmentSizeRemovedByGlobalRules) {
        this.attachmentSizeRemovedByGlobalRules = attachmentSizeRemovedByGlobalRules;
    }

    public long getAttachmentsRemovedBySpaceRules() {
        return this.attachmentsRemovedBySpaceRules;
    }

    public void setAttachmentsRemovedBySpaceRules(long attachmentsRemovedBySpaceRules) {
        this.attachmentsRemovedBySpaceRules = attachmentsRemovedBySpaceRules;
    }

    public long getAttachmentSizeRemovedBySpaceRules() {
        return this.attachmentSizeRemovedBySpaceRules;
    }

    public void setAttachmentSizeRemovedBySpaceRules(long attachmentSizeRemovedBySpaceRules) {
        this.attachmentSizeRemovedBySpaceRules = attachmentSizeRemovedBySpaceRules;
    }

    public int hashCode() {
        return Objects.hash(this.attachmentsRemovedByGlobalRules, this.attachmentSizeRemovedByGlobalRules, this.attachmentsRemovedBySpaceRules, this.attachmentSizeRemovedBySpaceRules);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AttachmentVersionRemovalSummary)) {
            return false;
        }
        AttachmentVersionRemovalSummary attachmentVersionRemovalSummary = (AttachmentVersionRemovalSummary)obj;
        return Objects.equals(attachmentVersionRemovalSummary.attachmentsRemovedByGlobalRules, this.attachmentsRemovedByGlobalRules) && Objects.equals(attachmentVersionRemovalSummary.attachmentSizeRemovedByGlobalRules, this.attachmentSizeRemovedByGlobalRules) && Objects.equals(attachmentVersionRemovalSummary.attachmentsRemovedBySpaceRules, this.attachmentsRemovedBySpaceRules) && Objects.equals(attachmentVersionRemovalSummary.attachmentSizeRemovedBySpaceRules, this.attachmentSizeRemovedBySpaceRules);
    }
}

