/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import java.net.URI;
import java.util.Set;

public interface UriSchemeScanner {
    public Set<String> getSchemes();

    public void scan(URI var1, ScannerListener var2) throws ScannerException;
}

