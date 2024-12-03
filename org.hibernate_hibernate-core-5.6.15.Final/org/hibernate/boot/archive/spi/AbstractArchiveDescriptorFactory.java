/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import java.net.URL;
import org.hibernate.boot.archive.internal.ArchiveHelper;
import org.hibernate.boot.archive.spi.ArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;

public abstract class AbstractArchiveDescriptorFactory
implements ArchiveDescriptorFactory {
    @Override
    public ArchiveDescriptor buildArchiveDescriptor(URL url) {
        return this.buildArchiveDescriptor(url, "");
    }

    @Override
    public URL getJarURLFromURLEntry(URL url, String entry) throws IllegalArgumentException {
        return ArchiveHelper.getJarURLFromURLEntry(url, entry);
    }

    @Override
    public URL getURLFromPath(String jarPath) {
        return ArchiveHelper.getURLFromPath(jarPath);
    }
}

