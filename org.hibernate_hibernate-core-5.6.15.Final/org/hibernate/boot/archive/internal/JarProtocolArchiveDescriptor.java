/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.internal;

import java.net.URL;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;

public class JarProtocolArchiveDescriptor
implements ArchiveDescriptor {
    private final ArchiveDescriptor delegateDescriptor;

    public JarProtocolArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL url, String incomingEntry) {
        if (incomingEntry != null && incomingEntry.length() > 0) {
            throw new IllegalArgumentException("jar:jar: not supported: " + url);
        }
        String urlFile = url.getFile();
        int subEntryIndex = urlFile.lastIndexOf(33);
        if (subEntryIndex == -1) {
            throw new AssertionFailure("JAR URL does not contain '!/' :" + url);
        }
        String subEntry = subEntryIndex + 1 >= urlFile.length() ? "" : urlFile.substring(subEntryIndex + 1);
        URL fileUrl = archiveDescriptorFactory.getJarURLFromURLEntry(url, subEntry);
        this.delegateDescriptor = archiveDescriptorFactory.buildArchiveDescriptor(fileUrl, subEntry);
    }

    @Override
    public void visitArchive(ArchiveContext context) {
        this.delegateDescriptor.visitArchive(context);
    }
}

