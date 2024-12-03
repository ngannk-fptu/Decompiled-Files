/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.sun.jersey.spi.scanning.servlet;

import com.sun.jersey.core.spi.scanning.JarFileScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.util.Closing;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.servlet.ServletContext;

public class WebAppResourcesScanner
implements Scanner {
    private final String[] paths;
    private final ServletContext sc;

    public WebAppResourcesScanner(String[] paths, ServletContext sc) {
        this.paths = paths;
        this.sc = sc;
    }

    @Override
    public void scan(ScannerListener cfl) {
        for (String path : this.paths) {
            this.scan(path, cfl);
        }
    }

    private void scan(String root, final ScannerListener cfl) {
        Set resourcePaths = this.sc.getResourcePaths(root);
        if (resourcePaths == null) {
            return;
        }
        for (final String resourcePath : resourcePaths) {
            if (resourcePath.endsWith("/")) {
                this.scan(resourcePath, cfl);
                continue;
            }
            if (resourcePath.endsWith(".jar")) {
                try {
                    new Closing(this.sc.getResourceAsStream(resourcePath)).f(new Closing.Closure(){

                        @Override
                        public void f(InputStream in) throws IOException {
                            JarFileScanner.scan(in, "", cfl);
                        }
                    });
                    continue;
                }
                catch (IOException ex) {
                    throw new ScannerException("IO error scanning jar " + resourcePath, ex);
                }
            }
            if (!cfl.onAccept(resourcePath)) continue;
            try {
                new Closing(this.sc.getResourceAsStream(resourcePath)).f(new Closing.Closure(){

                    @Override
                    public void f(InputStream in) throws IOException {
                        cfl.onProcess(resourcePath, in);
                    }
                });
            }
            catch (IOException ex) {
                throw new ScannerException("IO error scanning resource " + resourcePath, ex);
            }
        }
    }
}

