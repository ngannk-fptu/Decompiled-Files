/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import org.hibernate.boot.archive.spi.InputStreamAccess;

public interface ArchiveEntry {
    public String getName();

    public String getNameWithinArchive();

    public InputStreamAccess getStreamAccess();
}

