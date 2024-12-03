/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import org.hibernate.boot.archive.spi.ArchiveEntry;
import org.hibernate.boot.archive.spi.ArchiveEntryHandler;

public interface ArchiveContext {
    public boolean isRootUrl();

    public ArchiveEntryHandler obtainArchiveEntryHandler(ArchiveEntry var1);
}

