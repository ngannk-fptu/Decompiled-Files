/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.archive.scan.internal.NoopEntryHandler;
import org.hibernate.boot.archive.scan.internal.ScanResultCollector;
import org.hibernate.boot.archive.scan.spi.ClassFileArchiveEntryHandler;
import org.hibernate.boot.archive.scan.spi.NonClassFileArchiveEntryHandler;
import org.hibernate.boot.archive.scan.spi.PackageInfoArchiveEntryHandler;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.ScanParameters;
import org.hibernate.boot.archive.scan.spi.ScanResult;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.archive.spi.ArchiveEntry;
import org.hibernate.boot.archive.spi.ArchiveEntryHandler;
import org.hibernate.boot.archive.spi.JarFileEntryUrlAdjuster;

public abstract class AbstractScannerImpl
implements Scanner {
    private final ArchiveDescriptorFactory archiveDescriptorFactory;
    private final Map<URL, ArchiveDescriptorInfo> archiveDescriptorCache = new HashMap<URL, ArchiveDescriptorInfo>();

    protected AbstractScannerImpl(ArchiveDescriptorFactory archiveDescriptorFactory) {
        this.archiveDescriptorFactory = archiveDescriptorFactory;
    }

    @Override
    public ScanResult scan(ScanEnvironment environment, ScanOptions options, ScanParameters parameters) {
        ArchiveContextImpl context;
        ScanResultCollector collector = new ScanResultCollector(environment, options, parameters);
        if (environment.getNonRootUrls() != null) {
            context = new ArchiveContextImpl(false, collector);
            for (URL url : environment.getNonRootUrls()) {
                ArchiveDescriptor descriptor = this.buildArchiveDescriptor(url, environment, false);
                descriptor.visitArchive(context);
            }
        }
        if (environment.getRootUrl() != null) {
            context = new ArchiveContextImpl(true, collector);
            ArchiveDescriptor descriptor = this.buildArchiveDescriptor(environment.getRootUrl(), environment, true);
            descriptor.visitArchive(context);
        }
        return collector.toScanResult();
    }

    private ArchiveDescriptor buildArchiveDescriptor(URL url, ScanEnvironment environment, boolean isRootUrl) {
        ArchiveDescriptor descriptor;
        ArchiveDescriptorInfo descriptorInfo = this.archiveDescriptorCache.get(url);
        if (descriptorInfo == null) {
            if (!isRootUrl && this.archiveDescriptorFactory instanceof JarFileEntryUrlAdjuster) {
                url = ((JarFileEntryUrlAdjuster)((Object)this.archiveDescriptorFactory)).adjustJarFileEntryUrl(url, environment.getRootUrl());
            }
            descriptor = this.archiveDescriptorFactory.buildArchiveDescriptor(url);
            this.archiveDescriptorCache.put(url, new ArchiveDescriptorInfo(descriptor, isRootUrl));
        } else {
            this.validateReuse(descriptorInfo, isRootUrl);
            descriptor = descriptorInfo.archiveDescriptor;
        }
        return descriptor;
    }

    protected URL resolveNonRootUrl(URL url) {
        return null;
    }

    protected void validateReuse(ArchiveDescriptorInfo descriptor, boolean root) {
        throw new IllegalStateException("ArchiveDescriptor reused; can URLs be processed multiple times?");
    }

    public static class ArchiveContextImpl
    implements ArchiveContext {
        private final boolean isRootUrl;
        private final ClassFileArchiveEntryHandler classEntryHandler;
        private final PackageInfoArchiveEntryHandler packageEntryHandler;
        private final ArchiveEntryHandler fileEntryHandler;

        public ArchiveContextImpl(boolean isRootUrl, ScanResultCollector scanResultCollector) {
            this.isRootUrl = isRootUrl;
            this.classEntryHandler = new ClassFileArchiveEntryHandler(scanResultCollector);
            this.packageEntryHandler = new PackageInfoArchiveEntryHandler(scanResultCollector);
            this.fileEntryHandler = new NonClassFileArchiveEntryHandler(scanResultCollector);
        }

        @Override
        public boolean isRootUrl() {
            return this.isRootUrl;
        }

        @Override
        public ArchiveEntryHandler obtainArchiveEntryHandler(ArchiveEntry entry) {
            String nameWithinArchive = entry.getNameWithinArchive();
            if (nameWithinArchive.endsWith("package-info.class")) {
                return this.packageEntryHandler;
            }
            if (nameWithinArchive.endsWith("module-info.class")) {
                return NoopEntryHandler.NOOP_INSTANCE;
            }
            if (nameWithinArchive.endsWith(".class")) {
                return this.classEntryHandler;
            }
            return this.fileEntryHandler;
        }
    }

    protected static class ArchiveDescriptorInfo {
        public final ArchiveDescriptor archiveDescriptor;
        public final boolean isRoot;

        public ArchiveDescriptorInfo(ArchiveDescriptor archiveDescriptor, boolean isRoot) {
            this.archiveDescriptor = archiveDescriptor;
            this.isRoot = isRoot;
        }
    }
}

