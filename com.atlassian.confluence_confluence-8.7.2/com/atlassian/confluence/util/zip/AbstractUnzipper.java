/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.zip;

import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import com.atlassian.confluence.util.zip.FileUnzipper;
import com.atlassian.confluence.util.zip.Unzipper;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUnzipper
implements Unzipper {
    private static final Logger log = LoggerFactory.getLogger(FileUnzipper.class);
    protected File destDir;

    protected File saveEntry(InputStream is, ZipEntry entry) throws IOException {
        File file = new File(this.destDir, entry.getName());
        if (file.getCanonicalPath().equals(this.destDir.getCanonicalPath())) {
            return file;
        }
        if (!ConfluenceFileUtils.isChildOf(this.destDir, file)) {
            throw new IllegalArgumentException("The zip entry " + entry.getName() + " is not within the required destination directory " + this.destDir.getAbsolutePath());
        }
        if (entry.isDirectory()) {
            file.mkdirs();
        } else {
            File dir = new File(file.getParent());
            dir.mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file);){
                ByteStreams.copy((InputStream)is, (OutputStream)fos);
                fos.flush();
            }
            catch (IOException fnfe) {
                log.error("Error extracting a file to '" + this.destDir + File.separator + entry.getName() + "'. This destination is invalid for writing an extracted file stream to. ", (Throwable)fnfe);
                return null;
            }
        }
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ZipEntry[] entries(ZipInputStream zis) throws IOException {
        ArrayList<ZipEntry> entries = new ArrayList<ZipEntry>();
        try {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                entries.add(zipEntry);
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }
        finally {
            if (zis != null) {
                zis.close();
            }
        }
        return entries.toArray(new ZipEntry[entries.size()]);
    }
}

