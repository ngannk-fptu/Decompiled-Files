/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.tomcat.util.scan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.compat.JreCompat;

public class JarFileUrlJar
implements Jar {
    private final JarFile jarFile;
    private final URL jarFileURL;
    private final boolean multiRelease;
    private Enumeration<JarEntry> entries;
    private Set<String> entryNamesSeen;
    private JarEntry entry = null;

    public JarFileUrlJar(URL url, boolean startsWithJar) throws IOException {
        if (startsWithJar) {
            JarURLConnection jarConn = (JarURLConnection)url.openConnection();
            jarConn.setUseCaches(false);
            this.jarFile = jarConn.getJarFile();
            this.jarFileURL = jarConn.getJarFileURL();
        } else {
            File f;
            try {
                f = new File(url.toURI());
            }
            catch (URISyntaxException e) {
                throw new IOException(e);
            }
            this.jarFile = JreCompat.getInstance().jarFileNewInstance(f);
            this.jarFileURL = url;
        }
        this.multiRelease = JreCompat.getInstance().jarFileIsMultiRelease(this.jarFile);
    }

    public URL getJarFileURL() {
        return this.jarFileURL;
    }

    public InputStream getInputStream(String name) throws IOException {
        ZipEntry entry = this.jarFile.getEntry(name);
        if (entry == null) {
            return null;
        }
        return this.jarFile.getInputStream(entry);
    }

    public long getLastModified(String name) throws IOException {
        ZipEntry entry = this.jarFile.getEntry(name);
        if (entry == null) {
            return -1L;
        }
        return entry.getTime();
    }

    public boolean exists(String name) throws IOException {
        ZipEntry entry = this.jarFile.getEntry(name);
        return entry != null;
    }

    public String getURL(String entry) {
        StringBuilder result = new StringBuilder("jar:");
        result.append(this.getJarFileURL().toExternalForm());
        result.append("!/");
        result.append(entry);
        return result.toString();
    }

    public void close() {
        if (this.jarFile != null) {
            try {
                this.jarFile.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public void nextEntry() {
        block6: {
            if (this.entries == null) {
                this.entries = this.jarFile.entries();
                if (this.multiRelease) {
                    this.entryNamesSeen = new HashSet<String>();
                }
            }
            if (this.multiRelease) {
                String name = null;
                while (this.entries.hasMoreElements()) {
                    this.entry = this.entries.nextElement();
                    name = this.entry.getName();
                    if (name.startsWith("META-INF/versions/")) {
                        int i = name.indexOf(47, 18);
                        if (i == -1) continue;
                        name = name.substring(i + 1);
                    }
                    if (name.length() == 0 || this.entryNamesSeen.contains(name)) continue;
                    this.entryNamesSeen.add(name);
                    this.entry = this.jarFile.getJarEntry(this.entry.getName());
                    break block6;
                }
                this.entry = null;
            } else {
                this.entry = this.entries.hasMoreElements() ? this.entries.nextElement() : null;
            }
        }
    }

    public String getEntryName() {
        if (this.entry == null) {
            return null;
        }
        return this.entry.getName();
    }

    public InputStream getEntryInputStream() throws IOException {
        if (this.entry == null) {
            return null;
        }
        return this.jarFile.getInputStream(this.entry);
    }

    public Manifest getManifest() throws IOException {
        return this.jarFile.getManifest();
    }

    public void reset() throws IOException {
        this.entries = null;
        this.entryNamesSeen = null;
        this.entry = null;
    }
}

