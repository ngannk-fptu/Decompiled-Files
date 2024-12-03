/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import org.hibernate.boot.archive.internal.ArchiveHelper;
import org.hibernate.boot.archive.internal.ByteArrayInputStreamAccess;
import org.hibernate.boot.archive.spi.ArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.archive.spi.InputStreamAccess;
import org.hibernate.internal.util.StringHelper;

public abstract class AbstractArchiveDescriptor
implements ArchiveDescriptor {
    private final ArchiveDescriptorFactory archiveDescriptorFactory;
    private final URL archiveUrl;
    private final String entryBasePrefix;

    protected AbstractArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL archiveUrl, String entryBasePrefix) {
        this.archiveDescriptorFactory = archiveDescriptorFactory;
        this.archiveUrl = archiveUrl;
        this.entryBasePrefix = AbstractArchiveDescriptor.normalizeEntryBasePrefix(entryBasePrefix);
    }

    private static String normalizeEntryBasePrefix(String entryBasePrefix) {
        if (StringHelper.isEmpty(entryBasePrefix) || entryBasePrefix.length() == 1) {
            return null;
        }
        return entryBasePrefix.startsWith("/") ? entryBasePrefix.substring(1) : entryBasePrefix;
    }

    protected ArchiveDescriptorFactory getArchiveDescriptorFactory() {
        return this.archiveDescriptorFactory;
    }

    protected URL getArchiveUrl() {
        return this.archiveUrl;
    }

    protected String getEntryBasePrefix() {
        return this.entryBasePrefix;
    }

    protected String extractRelativeName(ZipEntry zipEntry) {
        String entryName = this.extractName(zipEntry);
        return this.entryBasePrefix != null && entryName.contains(this.entryBasePrefix) ? entryName.substring(this.entryBasePrefix.length()) : entryName;
    }

    protected String extractName(ZipEntry zipEntry) {
        return this.normalizePathName(zipEntry.getName());
    }

    protected String normalizePathName(String pathName) {
        return pathName.startsWith("/") ? pathName.substring(1) : pathName;
    }

    protected InputStreamAccess buildByteBasedInputStreamAccess(String name, InputStream inputStream) {
        byte[] bytes = ArchiveHelper.getBytesFromInputStreamSafely(inputStream);
        return new ByteArrayInputStreamAccess(name, bytes);
    }
}

