/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.zip;

import com.atlassian.confluence.util.zip.StreamUnzipper;
import com.atlassian.confluence.util.zip.Unzipper;
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
implements Unzipper {
    private static final Logger log = LoggerFactory.getLogger(FileUnzipper.class);
    private final File zipFile;
    private final File destDir;

    public FileUnzipper(File zipFile, File destDir) {
        this.zipFile = zipFile;
        this.destDir = destDir;
    }

    @Override
    public void unzip() throws IOException {
        if (this.zipFile == null) {
            log.error("Zip file is null, cannot extract");
        } else if (!this.zipFile.isFile()) {
            log.error("[{}] is not a file, cannot extract", (Object)this.zipFile.getAbsolutePath());
        } else {
            this.getStreamUnzipper().unzip();
        }
    }

    @Override
    public ZipEntry[] entries() throws IOException {
        return this.getStreamUnzipper().entries();
    }

    @Override
    public File unzipFileInArchive(String fileName) throws IOException {
        if (this.zipFile == null) {
            log.error("Zip file is null, cannot extract entry [{}]", (Object)fileName);
            return null;
        }
        if (!this.zipFile.isFile()) {
            log.error("[{}] is not a file, cannot extract entry [{}]", (Object)this.zipFile.getAbsolutePath(), (Object)fileName);
            return null;
        }
        if (StringUtils.isBlank((CharSequence)fileName)) {
            log.error("Cannot extract entry with blank name from [{}]", (Object)this.zipFile.getAbsolutePath());
            return null;
        }
        File result = this.getStreamUnzipper().unzipFileInArchive(fileName);
        if (result == null) {
            log.error("Entry [{}] could not be found in [{}]", (Object)fileName, (Object)this.zipFile.getAbsolutePath());
        }
        return result;
    }

    private StreamUnzipper getStreamUnzipper() throws FileNotFoundException {
        return new StreamUnzipper(new BufferedInputStream(new FileInputStream(this.zipFile)), this.destDir);
    }
}

