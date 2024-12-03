/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete;

import com.atlassian.confluence.pages.persistence.dao.bulk.DefaultBulkOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.IdListAttachmentDeleteOptions;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AttachmentDeleteOptions
extends DefaultBulkOptions {
    private Set<AttachmentRef> attachmentRefs;
    private long attachmentContainerId;

    private AttachmentDeleteOptions(DefaultBulkOptions defaultBulkOptions, long attachmentContainerId, Set<AttachmentRef> attachmentRefs) {
        super(defaultBulkOptions);
        this.attachmentContainerId = attachmentContainerId;
        this.attachmentRefs = attachmentRefs == null ? Collections.EMPTY_SET : attachmentRefs;
    }

    protected AttachmentDeleteOptions(DefaultBulkOptions defaultBulkOptions) {
        this(defaultBulkOptions, -1L, Collections.EMPTY_SET);
    }

    public Set<AttachmentRef> getAttachmentRefs() {
        return this.attachmentRefs;
    }

    public boolean isLatestVersion() {
        return this.attachmentRefs.stream().allMatch(attachmentRef -> attachmentRef.getAttachmentVersion() == -1);
    }

    public long getAttachmentContainerId() {
        return this.attachmentContainerId;
    }

    public static AttachmentDeleteOptionsBuilder get() {
        return new AttachmentDeleteOptionsBuilder();
    }

    public static class IdListAttachmentDeleteOptionsBuilder
    extends DefaultBulkOptions.BaseBuilder<IdListAttachmentDeleteOptionsBuilder> {
        private List<Long> ids;

        public IdListAttachmentDeleteOptionsBuilder withIds(List<Long> ids) {
            this.ids = ids;
            return this;
        }

        public IdListAttachmentDeleteOptions build() {
            return new IdListAttachmentDeleteOptions(this.buildDefault(), this.ids);
        }

        @Override
        protected IdListAttachmentDeleteOptionsBuilder builder() {
            return this;
        }
    }

    public static class AttachmentDeleteOptionsBuilder
    extends DefaultBulkOptions.BaseBuilder<AttachmentDeleteOptionsBuilder> {
        private Set<AttachmentRef> attachmentRefs = new HashSet<AttachmentRef>();
        private long attachmentContainerId;

        AttachmentDeleteOptionsBuilder() {
        }

        public AttachmentDeleteOptionsBuilder withDefaultOptions(DefaultBulkOptions options) {
            return (AttachmentDeleteOptionsBuilder)((AttachmentDeleteOptionsBuilder)((AttachmentDeleteOptionsBuilder)((AttachmentDeleteOptionsBuilder)this.withMaxProcessedEntries(options.getMaxProcessedEntries())).withBatchSize(options.getBatchSize())).withUser(options.getUser())).withProgressMeter(options.getProgressMeter());
        }

        public AttachmentDeleteOptionsBuilder withContainerId(long attachmentContainerId) {
            this.attachmentContainerId = attachmentContainerId;
            return this;
        }

        public AttachmentDeleteOptionsBuilder withAttachmentBy(String attachmentName) {
            this.withAttachmentBy(attachmentName, -1, "");
            return this;
        }

        public AttachmentDeleteOptionsBuilder withAttachmentBy(String attachmentName, int attachmentVersion) {
            this.withAttachmentBy(attachmentName, attachmentVersion, "");
            return this;
        }

        public AttachmentDeleteOptionsBuilder withAttachmentBy(String attachmentName, int attachmentVersion, String mineType) {
            AttachmentRef attachmentRef = new AttachmentRef();
            attachmentRef.attachmentName = attachmentName;
            attachmentRef.attachmentVersion = attachmentVersion;
            attachmentRef.mimeType = mineType;
            this.attachmentRefs.add(attachmentRef);
            return this;
        }

        public IdListAttachmentDeleteOptionsBuilder withIds(List<Long> ids) {
            IdListAttachmentDeleteOptionsBuilder innerBuilder = new IdListAttachmentDeleteOptionsBuilder();
            innerBuilder.withIds(ids).withProgressMeter(this.progressMeter);
            return innerBuilder;
        }

        @Override
        protected AttachmentDeleteOptionsBuilder builder() {
            return this;
        }

        public AttachmentDeleteOptions build() {
            return new AttachmentDeleteOptions(this.buildDefault(), this.attachmentContainerId, this.attachmentRefs);
        }
    }

    public static class AttachmentRef {
        String attachmentName;
        int attachmentVersion;
        String mimeType;

        public String getAttachmentName() {
            return this.attachmentName;
        }

        public int getAttachmentVersion() {
            return this.attachmentVersion;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AttachmentRef that = (AttachmentRef)o;
            return this.attachmentVersion == that.attachmentVersion && Objects.equals(this.attachmentName, that.attachmentName) && Objects.equals(this.mimeType, that.mimeType);
        }

        public int hashCode() {
            return Objects.hash(this.attachmentName, this.attachmentVersion, this.mimeType);
        }
    }
}

