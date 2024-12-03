/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.tomcat.util.scan.AbstractInputStreamJar;
import org.apache.tomcat.util.scan.NonClosingJarInputStream;

public class UrlJar
extends AbstractInputStreamJar {
    public UrlJar(URL jarFileURL) {
        super(jarFileURL);
    }

    public void close() {
        this.closeStream();
    }

    @Override
    protected NonClosingJarInputStream createJarInputStream() throws IOException {
        JarURLConnection jarConn = (JarURLConnection)this.getJarFileURL().openConnection();
        URL resourceURL = jarConn.getJarFileURL();
        URLConnection resourceConn = resourceURL.openConnection();
        resourceConn.setUseCaches(false);
        return new NonClosingJarInputStream(resourceConn.getInputStream());
    }
}

