/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

import org.hibernate.boot.archive.spi.InputStreamAccess;

public interface ClassDescriptor {
    public String getName();

    public Categorization getCategorization();

    public InputStreamAccess getStreamAccess();

    public static enum Categorization {
        MODEL,
        CONVERTER,
        OTHER;

    }
}

