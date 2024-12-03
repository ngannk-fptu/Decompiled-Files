/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  javax.annotation.Nullable
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.DelegatingAttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricBuilder;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric;
import com.atlassian.util.profiling.MetricTag;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.util.unit.DataSize;

public class IpdAttachmentDataFileSystem
implements DelegatingAttachmentDataFileSystem {
    private static final String ATTACHMENT_SAVE_LATENCY_METRIC_NAME = "attachment.save.latency";
    private static final long BYTES_IN_MB = 0x100000L;
    private final AttachmentDataFileSystem delegate;
    private final Clock clock;
    private final IpdStatsMetric attachmentWriteLatencyMetric;

    public IpdAttachmentDataFileSystem(AttachmentDataFileSystem delegate, IpdMainRegistry ipdMainRegistry, Clock clock) {
        this.delegate = delegate;
        this.clock = clock;
        this.attachmentWriteLatencyMetric = ipdMainRegistry.createRegistry(IpdMetricBuilder::asWorkInProgress).statsMetric(ATTACHMENT_SAVE_LATENCY_METRIC_NAME, new MetricTag.RequiredMetricTag[0]);
    }

    @Override
    public AttachmentDataFileSystem getDelegate() {
        return this.delegate;
    }

    @Override
    public boolean dataExistsForAttachment(AttachmentRef attachment) {
        return this.delegate.dataExistsForAttachment(attachment);
    }

    @Override
    public void moveAttachment(AttachmentRef oldAttachment, AttachmentRef newAttachment, AttachmentRef.Container newContentEntity) {
        this.delegate.moveAttachment(oldAttachment, newAttachment, newContentEntity);
    }

    @Override
    public boolean saveAttachmentData(AttachmentRef attachment, AttachmentDataStream dataStream, boolean overwrite, DataSize expectedFileSize) {
        long bytes;
        Instant start = Instant.now(this.clock);
        boolean result = this.delegate.saveAttachmentData(attachment, dataStream, overwrite, expectedFileSize);
        Duration duration = Duration.between(start, Instant.now(this.clock));
        if (result && (bytes = expectedFileSize.toBytes()) > 0L) {
            long durationInMs = duration.toMillis();
            double numberOfMb = (double)bytes / 1048576.0;
            long saveTimeOf1MbInMs = Math.round((double)durationInMs / numberOfMb);
            this.attachmentWriteLatencyMetric.update(Long.valueOf(saveTimeOf1MbInMs));
        }
        return result;
    }

    @Override
    public void deleteAllAttachmentVersions(AttachmentRef attachment, AttachmentRef.Container contentEntity) {
        this.delegate.deleteAllAttachmentVersions(attachment, contentEntity);
    }

    @Override
    public void moveDataForAttachmentVersion(AttachmentRef sourceAttachmentVersion, AttachmentRef targetAttachmentVersion) {
        this.delegate.moveDataForAttachmentVersion(sourceAttachmentVersion, targetAttachmentVersion);
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container contentEntity) {
        this.delegate.deleteSingleAttachmentVersion(attachment, contentEntity);
    }

    @Override
    public void deleteSingleAttachmentVersion(AttachmentRef attachment, AttachmentRef.Container contentEntity, AttachmentDataStreamType dataStreamType) {
        this.delegate.deleteSingleAttachmentVersion(attachment, contentEntity, dataStreamType);
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, AttachmentDataStreamType dataStreamType) {
        return this.delegate.getAttachmentData(attachment, dataStreamType);
    }

    @Override
    public AttachmentDataStream getAttachmentData(AttachmentRef attachment, AttachmentDataStreamType dataStreamType, Optional<RangeRequest> range) {
        return this.delegate.getAttachmentData(attachment, dataStreamType, range);
    }

    @Override
    public void moveAttachments(AttachmentRef.Container contentEntity, AttachmentRef.Space oldSpace, AttachmentRef.Space newSpace) {
        this.delegate.moveAttachments(contentEntity, oldSpace, newSpace);
    }

    @Override
    public void prepareForMigrationTo() {
        this.delegate.prepareForMigrationTo();
    }

    @Override
    public AttachmentDataFile<FilesystemPath> getAttachmentDataFile(long attachmentId, @Nullable Long containerId, @Nullable Long spaceId, Integer attachmentVersion, AttachmentDataStreamType dataStreamType) {
        return this.delegate.getAttachmentDataFile(attachmentId, containerId, spaceId, attachmentVersion, dataStreamType);
    }
}

