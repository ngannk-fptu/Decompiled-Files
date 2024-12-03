/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.tomcat.util.scan.AbstractInputStreamJar;
import org.apache.tomcat.util.scan.NonClosingJarInputStream;

public class JarFileUrlNestedJar
extends AbstractInputStreamJar {
    private final JarFile warFile;
    private final JarEntry jarEntry;

    public JarFileUrlNestedJar(URL url) throws IOException {
        super(url);
        JarURLConnection jarConn = (JarURLConnection)url.openConnection();
        jarConn.setUseCaches(false);
        this.warFile = jarConn.getJarFile();
        String urlAsString = url.toString();
        int pathStart = urlAsString.indexOf("!/") + 2;
        String jarPath = urlAsString.substring(pathStart);
        this.jarEntry = this.warFile.getJarEntry(jarPath);
    }

    public void close() {
        this.closeStream();
        if (this.warFile != null) {
            try {
                this.warFile.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    protected NonClosingJarInputStream createJarInputStream() throws IOException {
        return new NonClosingJarInputStream(this.warFile.getInputStream(this.jarEntry));
    }
}

