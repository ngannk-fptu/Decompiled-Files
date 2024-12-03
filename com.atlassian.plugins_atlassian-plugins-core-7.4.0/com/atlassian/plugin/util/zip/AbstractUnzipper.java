/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util.zip;

import com.atlassian.plugin.util.zip.FileUnzipper;
import com.atlassian.plugin.util.zip.Unzipper;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUnzipper
implements Unzipper {
    protected static Logger log = LoggerFactory.getLogger(FileUnzipper.class);
    protected File destDir;

    protected File saveEntry(InputStream is, ZipEntry entry) throws IOException {
        File file = new File(this.destDir, AbstractUnzipper.normaliseAndVerify(entry.getName()));
        if (entry.isDirectory()) {
            file.mkdirs();
        } else {
            File dir = new File(file.getParent());
            dir.mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file);){
                IOUtils.copy((InputStream)is, (OutputStream)fos);
                fos.flush();
            }
            catch (FileNotFoundException fnfe) {
                log.error("Error extracting a file to '{}{}{}'. This destination is invalid for writing an extracted file stream to.", new Object[]{this.destDir, File.separator, entry.getName()});
                return null;
            }
        }
        file.setLastModified(entry.getTime());
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
            IOUtils.closeQuietly((InputStream)zis);
        }
        return entries.toArray(new ZipEntry[0]);
    }

    @Override
    public void conditionalUnzip() throws IOException {
        Map<String, Long> zipContentsAndLastModified = Arrays.stream(this.entries()).collect(Collectors.toMap(ZipEntry::getName, ZipEntry::getTime));
        Map<String, Long> targetDirContents = this.getContentsOfTargetDir(this.destDir);
        if (!targetDirContents.equals(zipContentsAndLastModified)) {
            if (this.destDir.exists()) {
                FileUtils.cleanDirectory((File)this.destDir);
            }
            this.unzip();
        } else {
            log.debug("Target directory contents match zip contents. Do nothing.");
        }
    }

    @VisibleForTesting
    static String normaliseAndVerify(String name) {
        String normalised = FilenameUtils.normalizeNoEndSeparator((String)name);
        if (StringUtils.isBlank((CharSequence)normalised)) {
            throw new IllegalArgumentException("Path name " + name + " is illegal");
        }
        return normalised;
    }

    private Map<String, Long> getContentsOfTargetDir(File dir) {
        if (!dir.isDirectory()) {
            return Collections.emptyMap();
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyMap();
        }
        HashMap<String, Long> targetDirContents = new HashMap<String, Long>();
        for (File child : files) {
            if (log.isDebugEnabled()) {
                log.debug("Examining entry in zip: {}", (Object)child);
            }
            targetDirContents.put(child.getName(), child.lastModified());
        }
        return targetDirContents;
    }
}

