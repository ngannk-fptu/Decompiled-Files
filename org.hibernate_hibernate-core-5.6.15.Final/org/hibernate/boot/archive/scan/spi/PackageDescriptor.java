/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

import org.hibernate.boot.archive.spi.InputStreamAccess;

public interface PackageDescriptor {
    public String getName();

    public InputStreamAccess getStreamAccess();
}

