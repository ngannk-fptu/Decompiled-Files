/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

import java.io.File;
import java.io.IOException;
import org.apache.tomcat.Jar;

public interface JarScannerCallback {
    public void scan(Jar var1, String var2, boolean var3) throws IOException;

    public void scan(File var1, String var2, boolean var3) throws IOException;

    public void scanWebInfClasses() throws IOException;
}

