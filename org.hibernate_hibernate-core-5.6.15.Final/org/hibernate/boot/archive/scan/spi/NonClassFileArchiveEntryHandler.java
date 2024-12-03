/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

import org.hibernate.boot.archive.scan.internal.MappingFileDescriptorImpl;
import org.hibernate.boot.archive.scan.internal.ScanResultCollector;
import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveEntry;
import org.hibernate.boot.archive.spi.ArchiveEntryHandler;

public class NonClassFileArchiveEntryHandler
implements ArchiveEntryHandler {
    private final ScanResultCollector resultCollector;

    public NonClassFileArchiveEntryHandler(ScanResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    @Override
    public void handleEntry(ArchiveEntry entry, ArchiveContext context) {
        this.resultCollector.handleMappingFile(new MappingFileDescriptorImpl(entry.getNameWithinArchive(), entry.getStreamAccess()), context.isRootUrl());
    }
}

