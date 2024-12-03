/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import java.util.Set;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
import org.hibernate.boot.archive.scan.spi.PackageDescriptor;
import org.hibernate.boot.archive.scan.spi.ScanResult;

public class ScanResultImpl
implements ScanResult {
    private final Set<PackageDescriptor> packageDescriptorSet;
    private final Set<ClassDescriptor> classDescriptorSet;
    private final Set<MappingFileDescriptor> mappingFileSet;

    public ScanResultImpl(Set<PackageDescriptor> packageDescriptorSet, Set<ClassDescriptor> classDescriptorSet, Set<MappingFileDescriptor> mappingFileSet) {
        this.packageDescriptorSet = packageDescriptorSet;
        this.classDescriptorSet = classDescriptorSet;
        this.mappingFileSet = mappingFileSet;
    }

    @Override
    public Set<PackageDescriptor> getLocatedPackages() {
        return this.packageDescriptorSet;
    }

    @Override
    public Set<ClassDescriptor> getLocatedClasses() {
        return this.classDescriptorSet;
    }

    @Override
    public Set<MappingFileDescriptor> getLocatedMappingFiles() {
        return this.mappingFileSet;
    }
}

