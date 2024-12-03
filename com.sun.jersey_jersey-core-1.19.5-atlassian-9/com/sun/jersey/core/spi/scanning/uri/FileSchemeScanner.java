/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.spi.scanning.uri.UriSchemeScanner;
import com.sun.jersey.core.util.Closing;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class FileSchemeScanner
implements UriSchemeScanner {
    @Override
    public Set<String> getSchemes() {
        return Collections.singleton("file");
    }

    @Override
    public void scan(URI u, ScannerListener cfl) {
        File f = new File(u.getPath());
        if (f.isDirectory()) {
            this.scanDirectory(f, cfl);
        }
    }

    private void scanDirectory(File root, final ScannerListener cfl) {
        for (final File child : root.listFiles()) {
            if (child.isDirectory()) {
                this.scanDirectory(child, cfl);
                continue;
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
                throw new ScannerException("IO error when scanning jar file " + child, ex);
            }
        }
    }
}

