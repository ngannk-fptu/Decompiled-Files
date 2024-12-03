/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.boot.archive.scan.internal.ScanResultImpl;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
import org.hibernate.boot.archive.scan.spi.PackageDescriptor;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.ScanParameters;
import org.hibernate.boot.archive.scan.spi.ScanResult;

public class ScanResultCollector {
    private final ScanEnvironment environment;
    private final ScanOptions options;
    private final Set<ClassDescriptor> discoveredClasses;
    private final Set<PackageDescriptor> discoveredPackages;
    private final Set<MappingFileDescriptor> discoveredMappingFiles;

    public ScanResultCollector(ScanEnvironment environment, ScanOptions options, ScanParameters parameters) {
        this.environment = environment;
        this.options = options;
        if (environment.getExplicitlyListedClassNames() == null) {
            throw new IllegalArgumentException("ScanEnvironment#getExplicitlyListedClassNames should not return null");
        }
        if (environment.getExplicitlyListedMappingFiles() == null) {
            throw new IllegalArgumentException("ScanEnvironment#getExplicitlyListedMappingFiles should not return null");
        }
        this.discoveredPackages = new HashSet<PackageDescriptor>();
        this.discoveredClasses = new HashSet<ClassDescriptor>();
        this.discoveredMappingFiles = new HashSet<MappingFileDescriptor>();
    }

    public void handleClass(ClassDescriptor classDescriptor, boolean rootUrl) {
        if (!this.isListedOrDetectable(classDescriptor.getName(), rootUrl)) {
            return;
        }
        this.discoveredClasses.add(classDescriptor);
    }

    protected boolean isListedOrDetectable(String name, boolean rootUrl) {
        if (rootUrl) {
            return this.options.canDetectUnlistedClassesInRoot() || this.environment.getExplicitlyListedClassNames().contains(name);
        }
        return this.options.canDetectUnlistedClassesInNonRoot() || this.environment.getExplicitlyListedClassNames().contains(name);
    }

    public void handlePackage(PackageDescriptor packageDescriptor, boolean rootUrl) {
        if (!this.isListedOrDetectable(packageDescriptor.getName(), rootUrl)) {
            return;
        }
        this.discoveredPackages.add(packageDescriptor);
    }

    public void handleMappingFile(MappingFileDescriptor mappingFileDescriptor, boolean rootUrl) {
        if (this.acceptAsMappingFile(mappingFileDescriptor, rootUrl)) {
            this.discoveredMappingFiles.add(mappingFileDescriptor);
        }
    }

    private boolean acceptAsMappingFile(MappingFileDescriptor mappingFileDescriptor, boolean rootUrl) {
        if (mappingFileDescriptor.getName().endsWith("hbm.xml")) {
            return this.options.canDetectHibernateMappingFiles();
        }
        if (mappingFileDescriptor.getName().endsWith("META-INF/orm.xml")) {
            if (this.environment.getExplicitlyListedMappingFiles().contains("META-INF/orm.xml")) {
                return rootUrl;
            }
            return true;
        }
        return this.environment.getExplicitlyListedMappingFiles().contains(mappingFileDescriptor.getName());
    }

    public ScanResult toScanResult() {
        return new ScanResultImpl(Collections.unmodifiableSet(this.discoveredPackages), Collections.unmodifiableSet(this.discoveredClasses), Collections.unmodifiableSet(this.discoveredMappingFiles));
    }
}

