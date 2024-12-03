/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning;

import com.sun.jersey.core.spi.scanning.JarFileScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.util.Closing;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilesScanner
implements Scanner {
    private final File[] files;

    public FilesScanner(File[] files) {
        this.files = files;
    }

    @Override
    public void scan(ScannerListener cfl) {
        for (File f : this.files) {
            this.scan(f, cfl);
        }
    }

    private void scan(File f, ScannerListener cfl) {
        if (f.isDirectory()) {
            this.scanDir(f, cfl);
        } else if (f.getName().endsWith(".jar") || f.getName().endsWith(".zip")) {
            try {
                JarFileScanner.scan(f, "", cfl);
            }
            catch (IOException ex) {
                throw new ScannerException("IO error when scanning jar file " + f, ex);
            }
        }
    }

    private void scanDir(File root, final ScannerListener cfl) {
        for (final File child : root.listFiles()) {
            if (child.isDirectory()) {
                this.scanDir(child, cfl);
                continue;
            }
            if (child.getName().endsWith(".jar")) {
                try {
                    JarFileScanner.scan(child, "", cfl);
                    continue;
                }
                catch (IOException ex) {
                    throw new ScannerException("IO error when scanning jar file " + child, ex);
                }
            }
            if (!cfl.onAccept(child.getName())) continue;
            try {
                new Closing(new BufferedInputStream(new FileInputStream(child))).f(new Closing.Closure(){

                    @Override
                    public void f(InputStream in) throws IOException {
                        cfl.onProcess(child.getName(), in);
                    }
                });
            }
            catch (IOException ex) {
                throw new ScannerException("IO error when scanning file " + child, ex);
            }
        }
    }
}

