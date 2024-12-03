/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

import java.util.Set;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
import org.hibernate.boot.archive.scan.spi.PackageDescriptor;

public interface ScanResult {
    public Set<PackageDescriptor> getLocatedPackages();

    public Set<ClassDescriptor> getLocatedClasses();

    public Set<MappingFileDescriptor> getLocatedMappingFiles();
}

