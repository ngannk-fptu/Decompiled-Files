/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.core.spi.scanning.JarFileScanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.spi.scanning.uri.UriSchemeScanner;
import com.sun.jersey.core.util.Closing;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JarZipSchemeScanner
implements UriSchemeScanner {
    @Override
    public Set<String> getSchemes() {
        return new HashSet<String>(Arrays.asList("jar", "zip"));
    }

    @Override
    public void scan(URI u, final ScannerListener cfl) {
        String ssp = u.getRawSchemeSpecificPart();
        String jarUrlString = ssp.substring(0, ssp.lastIndexOf(33));
        final String parent = ssp.substring(ssp.lastIndexOf(33) + 2);
        try {
            this.closing(jarUrlString).f(new Closing.Closure(){

                @Override
                public void f(InputStream in) throws IOException {
                    JarFileScanner.scan(in, parent, cfl);
                }
            });
        }
        catch (IOException ex) {
            throw new ScannerException("IO error when scanning jar " + u, ex);
        }
    }

    protected Closing closing(String jarUrlString) throws IOException {
        try {
            return new Closing(new URL(jarUrlString).openStream());
        }
        catch (MalformedURLException ex) {
            return new Closing(new FileInputStream(UriComponent.decode(jarUrlString, UriComponent.Type.PATH)));
        }
    }
}

