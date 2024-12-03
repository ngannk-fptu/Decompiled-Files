/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveEntry;

public interface ArchiveEntryHandler {
    public void handleEntry(ArchiveEntry var1, ArchiveContext var2);
}

