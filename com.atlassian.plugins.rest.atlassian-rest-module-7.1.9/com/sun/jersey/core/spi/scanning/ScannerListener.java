/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning;

import java.io.IOException;
import java.io.InputStream;

public interface ScannerListener {
    public boolean onAccept(String var1);

    public void onProcess(String var1, InputStream var2) throws IOException;
}

