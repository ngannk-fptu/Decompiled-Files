/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.zip;

import com.atlassian.confluence.util.zip.AbstractUnzipper;
import com.atlassian.confluence.util.zip.StreamUnzipper;
import java.io.BufferedInputStream;
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
    public File unzipFileInArchive(String fileName) throws IOException {
        StreamUnzipper streamUnzipper = new StreamUnzipper(new BufferedInputStream(this.zipUrl.openStream()), this.destDir);
        return streamUnzipper.unzipFileInArchive(fileName);
    }

    @Override
    public ZipEntry[] entries() throws IOException {
        return this.entries(new ZipInputStream(this.zipUrl.openStream()));
    }
}

