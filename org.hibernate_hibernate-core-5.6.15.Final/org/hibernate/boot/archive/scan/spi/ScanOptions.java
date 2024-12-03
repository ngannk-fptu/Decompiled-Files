/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

public interface ScanOptions {
    public boolean canDetectUnlistedClassesInRoot();

    public boolean canDetectUnlistedClassesInNonRoot();

    @Deprecated
    public boolean canDetectHibernateMappingFiles();
}

