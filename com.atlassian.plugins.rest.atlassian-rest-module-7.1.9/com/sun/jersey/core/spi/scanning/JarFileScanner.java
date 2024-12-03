/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning;

import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.util.Closing;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public final class JarFileScanner {
    public static void scan(File f, final String parent, final ScannerListener sl) throws IOException {
        new Closing(new FileInputStream(f)).f(new Closing.Closure(){

            @Override
            public void f(InputStream in) throws IOException {
                JarFileScanner.scan(in, parent, sl);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void scan(InputStream in, String parent, ScannerListener sl) throws IOException {
        try (JarInputStream jarIn = null;){
            jarIn = new JarInputStream(in);
            JarEntry e = jarIn.getNextJarEntry();
            while (e != null) {
                if (!e.isDirectory() && e.getName().startsWith(parent) && sl.onAccept(e.getName())) {
                    sl.onProcess(e.getName(), jarIn);
                }
                jarIn.closeEntry();
                e = jarIn.getNextJarEntry();
            }
        }
    }
}

