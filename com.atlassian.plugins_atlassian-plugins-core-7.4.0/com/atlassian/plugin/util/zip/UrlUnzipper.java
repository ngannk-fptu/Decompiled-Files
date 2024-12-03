/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util.zip;

import com.atlassian.plugin.util.zip.AbstractUnzipper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UrlUnzipper
extends AbstractUnzipper {
    private URL zipUrl;

    public UrlUnzipper(URL zipUrl, File destDir) {
        this.zipUrl = zipUrl;
        this.destDir = destDir;
    }

    @Override
    public void unzip() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(this.zipUrl.openStream());){
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                this.saveEntry(zis, zipEntry);
            }
        }
    }

    @Override
    public File unzipFileInArchive(String fileName) {
        throw new UnsupportedOperationException("Feature not implemented.");
    }

    @Override
    public ZipEntry[] entries() throws IOException {
        return this.entries(new ZipInputStream(this.zipUrl.openStream()));
    }
}

