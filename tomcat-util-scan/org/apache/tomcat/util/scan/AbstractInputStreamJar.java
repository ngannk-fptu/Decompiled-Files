/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.scan.NonClosingJarInputStream;

public abstract class AbstractInputStreamJar
implements Jar {
    private final URL jarFileURL;
    private NonClosingJarInputStream jarInputStream = null;
    private JarEntry entry = null;
    private Boolean multiRelease = null;
    private Map<String, String> mrMap = null;

    public AbstractInputStreamJar(URL jarFileUrl) {
        this.jarFileURL = jarFileUrl;
    }

    public URL getJarFileURL() {
        return this.jarFileURL;
    }

    public void nextEntry() {
        if (this.jarInputStream == null) {
            try {
                this.reset();
            }
            catch (IOException e) {
                this.entry = null;
                return;
            }
        }
        try {
            this.entry = this.jarInputStream.getNextJarEntry();
            if (this.multiRelease.booleanValue()) {
                while (this.entry != null && (this.mrMap.containsKey(this.entry.getName()) || this.entry.getName().startsWith("META-INF/versions/") && !this.mrMap.containsValue(this.entry.getName()))) {
                    this.entry = this.jarInputStream.getNextJarEntry();
                }
            } else {
                while (this.entry != null && this.entry.getName().startsWith("META-INF/versions/")) {
                    this.entry = this.jarInputStream.getNextJarEntry();
                }
            }
        }
        catch (IOException ioe) {
            this.entry = null;
        }
    }

    public String getEntryName() {
        if (this.entry == null) {
            return null;
        }
        return this.entry.getName();
    }

    public InputStream getEntryInputStream() throws IOException {
        return this.jarInputStream;
    }

    public InputStream getInputStream(String name) throws IOException {
        this.gotoEntry(name);
        if (this.entry == null) {
            return null;
        }
        this.entry = null;
        return this.jarInputStream;
    }

    public long getLastModified(String name) throws IOException {
        this.gotoEntry(name);
        if (this.entry == null) {
            return -1L;
        }
        return this.entry.getTime();
    }

    public boolean exists(String name) throws IOException {
        this.gotoEntry(name);
        return this.entry != null;
    }

    public String getURL(String entry) {
        StringBuilder result = new StringBuilder("jar:");
        result.append(this.getJarFileURL().toExternalForm());
        result.append("!/");
        result.append(entry);
        return result.toString();
    }

    public Manifest getManifest() throws IOException {
        this.reset();
        return this.jarInputStream.getManifest();
    }

    public void reset() throws IOException {
        this.closeStream();
        this.entry = null;
        this.jarInputStream = this.createJarInputStream();
        if (this.multiRelease == null) {
            String mrValue;
            Manifest manifest;
            this.multiRelease = JreCompat.isJre9Available() ? ((manifest = this.jarInputStream.getManifest()) == null ? Boolean.FALSE : ((mrValue = manifest.getMainAttributes().getValue("Multi-Release")) == null ? Boolean.FALSE : Boolean.valueOf(mrValue))) : Boolean.FALSE;
            if (this.multiRelease.booleanValue() && this.mrMap == null) {
                this.populateMrMap();
            }
        }
    }

    protected void closeStream() {
        if (this.jarInputStream != null) {
            try {
                this.jarInputStream.reallyClose();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    protected abstract NonClosingJarInputStream createJarInputStream() throws IOException;

    private void gotoEntry(String name) throws IOException {
        boolean needsReset = true;
        if (this.multiRelease == null) {
            this.reset();
            needsReset = false;
        }
        if (this.multiRelease.booleanValue()) {
            String mrName = this.mrMap.get(name);
            if (mrName != null) {
                name = mrName;
            }
        } else if (name.startsWith("META-INF/versions/")) {
            this.entry = null;
            return;
        }
        if (this.entry != null && name.equals(this.entry.getName())) {
            return;
        }
        if (needsReset) {
            this.reset();
        }
        JarEntry jarEntry = this.jarInputStream.getNextJarEntry();
        while (jarEntry != null) {
            if (name.equals(jarEntry.getName())) {
                this.entry = jarEntry;
                break;
            }
            jarEntry = this.jarInputStream.getNextJarEntry();
        }
    }

    private void populateMrMap() throws IOException {
        int targetVersion = JreCompat.getInstance().jarFileRuntimeMajorVersion();
        HashMap<String, Integer> mrVersions = new HashMap<String, Integer>();
        JarEntry jarEntry = this.jarInputStream.getNextJarEntry();
        while (jarEntry != null) {
            int i;
            String name = jarEntry.getName();
            if (name.startsWith("META-INF/versions/") && name.endsWith(".class") && (i = name.indexOf(47, 18)) > 0) {
                String baseName = name.substring(i + 1);
                int version = Integer.parseInt(name.substring(18, i));
                if (version <= targetVersion) {
                    Integer mappedVersion = (Integer)mrVersions.get(baseName);
                    if (mappedVersion == null) {
                        mrVersions.put(baseName, version);
                    } else if (version > mappedVersion) {
                        mrVersions.put(baseName, version);
                    }
                }
            }
            jarEntry = this.jarInputStream.getNextJarEntry();
        }
        this.mrMap = new HashMap<String, String>();
        for (Map.Entry mrVersion : mrVersions.entrySet()) {
            this.mrMap.put((String)mrVersion.getKey(), "META-INF/versions/" + ((Integer)mrVersion.getValue()).toString() + "/" + (String)mrVersion.getKey());
        }
        this.closeStream();
        this.jarInputStream = this.createJarInputStream();
    }
}

