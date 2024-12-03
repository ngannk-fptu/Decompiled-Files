/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import org.hibernate.boot.archive.scan.spi.ScanParameters;

public class StandardScanParameters
implements ScanParameters {
    public static final StandardScanParameters INSTANCE = new StandardScanParameters();

    private StandardScanParameters() {
    }
}

