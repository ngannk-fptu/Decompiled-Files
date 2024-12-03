/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning;

import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;

public interface Scanner {
    public void scan(ScannerListener var1) throws ScannerException;
}

