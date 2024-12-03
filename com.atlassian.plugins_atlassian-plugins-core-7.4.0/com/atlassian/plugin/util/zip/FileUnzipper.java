/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util.zip;

import com.atlassian.plugin.util.zip.AbstractUnzipper;
import com.atlassian.plugin.util.zip.StreamUnzipper;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUnzipper
extends AbstractUnzipper {
    private static final Logger log = LoggerFactory.getLogger(FileUnzipper.class);
    private File zipFile;
    private File destDir;

    public FileUnzipper(File zipFile, File destDir) {
        this.zipFile = zipFile;
        this.destDir = destDir;
    }

    @Override
    public void unzip() throws IOException {
        if (this.zipFile == null || !this.zipFile.isFile()) {
            return;
        }
        this.getStreamUnzipper().unzip();
    }

    @Override
    public ZipEntry[] entries() throws IOException {
        return this.getStreamUnzipper().entries();
    }

    @Override
    public File unzipFileInArchive(String fileName) throws IOException {
        if (this.zipFile == null || !this.zipFile.isFile() || StringUtils.isEmpty((CharSequence)fileName)) {
            return null;
        }
        File result = this.getStreamUnzipper().unzipFileInArchive(fileName);
        if (result == null) {
            log.error("The file: {} could not be found in the archive: {}", (Object)fileName, (Object)this.zipFile.getAbsolutePath());
        }
        return result;
    }

    private StreamUnzipper getStreamUnzipper() throws FileNotFoundException {
        return new StreamUnzipper(new BufferedInputStream(new FileInputStream(this.zipFile)), this.destDir);
    }
}

