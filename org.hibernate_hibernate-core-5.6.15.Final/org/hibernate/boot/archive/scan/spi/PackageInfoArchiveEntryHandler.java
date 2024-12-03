/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

import org.hibernate.boot.archive.scan.internal.PackageDescriptorImpl;
import org.hibernate.boot.archive.scan.internal.ScanResultCollector;
import org.hibernate.boot.archive.scan.spi.PackageDescriptor;
import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveEntry;
import org.hibernate.boot.archive.spi.ArchiveEntryHandler;

public class PackageInfoArchiveEntryHandler
implements ArchiveEntryHandler {
    private final ScanResultCollector resultCollector;

    public PackageInfoArchiveEntryHandler(ScanResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    @Override
    public void handleEntry(ArchiveEntry entry, ArchiveContext context) {
        if (entry.getNameWithinArchive().equals("package-info.class")) {
            return;
        }
        this.resultCollector.handlePackage(this.toPackageDescriptor(entry), context.isRootUrl());
    }

    protected PackageDescriptor toPackageDescriptor(ArchiveEntry entry) {
        String packageInfoFilePath = entry.getNameWithinArchive();
        String packageName = packageInfoFilePath.substring(0, packageInfoFilePath.lastIndexOf(47)).replace('/', '.');
        return new PackageDescriptorImpl(packageName, entry.getStreamAccess());
    }
}

