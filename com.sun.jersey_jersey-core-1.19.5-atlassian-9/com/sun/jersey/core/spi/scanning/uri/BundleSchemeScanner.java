/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.spi.scanning.uri.UriSchemeScanner;
import com.sun.jersey.core.util.Closing;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BundleSchemeScanner
implements UriSchemeScanner {
    @Override
    public Set<String> getSchemes() {
        return new HashSet<String>(Arrays.asList("bundle"));
    }

    @Override
    public void scan(final URI u, final ScannerListener sl) throws ScannerException {
        if (sl.onAccept(u.getPath())) {
            try {
                new Closing(new BufferedInputStream(u.toURL().openStream())).f(new Closing.Closure(){

                    @Override
                    public void f(InputStream in) throws IOException {
                        sl.onProcess(u.getPath(), in);
                    }
                });
            }
            catch (IOException ex) {
                throw new ScannerException("IO error when scanning bundle class " + u, ex);
            }
        }
    }
}

