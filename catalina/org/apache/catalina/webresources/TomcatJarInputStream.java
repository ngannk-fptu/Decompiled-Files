/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class TomcatJarInputStream
extends JarInputStream {
    private JarEntry metaInfEntry;
    private JarEntry manifestEntry;

    TomcatJarInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ZipEntry createZipEntry(String name) {
        ZipEntry ze = super.createZipEntry(name);
        if (this.metaInfEntry == null && "META-INF/".equals(name)) {
            this.metaInfEntry = (JarEntry)ze;
        } else if (this.manifestEntry == null && "META-INF/MANIFESR.MF".equals(name)) {
            this.manifestEntry = (JarEntry)ze;
        }
        return ze;
    }

    JarEntry getMetaInfEntry() {
        return this.metaInfEntry;
    }

    JarEntry getManifestEntry() {
        return this.manifestEntry;
    }
}

