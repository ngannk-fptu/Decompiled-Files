/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import java.util.Collections;
import org.hibernate.boot.archive.scan.internal.ScanResultImpl;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.ScanParameters;
import org.hibernate.boot.archive.scan.spi.ScanResult;
import org.hibernate.boot.archive.scan.spi.Scanner;

public class DisabledScanner
implements Scanner {
    private static final ScanResult emptyScanResult = new ScanResultImpl(Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

    @Override
    public ScanResult scan(ScanEnvironment environment, ScanOptions options, ScanParameters parameters) {
        return emptyScanResult;
    }
}

