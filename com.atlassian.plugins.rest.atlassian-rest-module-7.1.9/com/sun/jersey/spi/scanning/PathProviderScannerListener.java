/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.scanning;

import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

public final class PathProviderScannerListener
extends AnnotationScannerListener {
    public PathProviderScannerListener() {
        super(Path.class, Provider.class);
    }

    public PathProviderScannerListener(ClassLoader classloader) {
        super(classloader, Path.class, Provider.class);
    }
}

