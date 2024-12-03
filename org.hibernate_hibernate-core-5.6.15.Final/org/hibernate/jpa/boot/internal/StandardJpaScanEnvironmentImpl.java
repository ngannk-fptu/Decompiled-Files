/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.boot.internal;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;

public class StandardJpaScanEnvironmentImpl
implements ScanEnvironment {
    private final PersistenceUnitDescriptor persistenceUnitDescriptor;
    private final List<String> explicitlyListedClassNames;
    private final List<String> explicitlyListedMappingFiles;

    public StandardJpaScanEnvironmentImpl(PersistenceUnitDescriptor persistenceUnitDescriptor) {
        this.persistenceUnitDescriptor = persistenceUnitDescriptor;
        this.explicitlyListedClassNames = persistenceUnitDescriptor.getManagedClassNames() == null ? Collections.emptyList() : persistenceUnitDescriptor.getManagedClassNames();
        this.explicitlyListedMappingFiles = persistenceUnitDescriptor.getMappingFileNames() == null ? Collections.emptyList() : persistenceUnitDescriptor.getMappingFileNames();
    }

    @Override
    public URL getRootUrl() {
        return this.persistenceUnitDescriptor.getPersistenceUnitRootUrl();
    }

    @Override
    public List<URL> getNonRootUrls() {
        return this.persistenceUnitDescriptor.getJarFileUrls();
    }

    @Override
    public List<String> getExplicitlyListedClassNames() {
        return this.explicitlyListedClassNames;
    }

    @Override
    public List<String> getExplicitlyListedMappingFiles() {
        return this.explicitlyListedMappingFiles;
    }
}

