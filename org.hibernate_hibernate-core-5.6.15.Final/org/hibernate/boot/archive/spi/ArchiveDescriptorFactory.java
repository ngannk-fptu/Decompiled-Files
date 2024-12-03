/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import java.net.URL;
import org.hibernate.boot.archive.spi.ArchiveDescriptor;

public interface ArchiveDescriptorFactory {
    public ArchiveDescriptor buildArchiveDescriptor(URL var1);

    public ArchiveDescriptor buildArchiveDescriptor(URL var1, String var2);

    public URL getJarURLFromURLEntry(URL var1, String var2) throws IllegalArgumentException;

    @Deprecated
    public URL getURLFromPath(String var1);
}

