/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.SupportUtility;
import com.google.common.io.ByteStreams;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtility {
    private Map<String, File> files = new LinkedHashMap<String, File>();
    private static final Logger log = LoggerFactory.getLogger(SupportUtility.class);
    private static final FileHandler NOOP_FILE_HANDLER = f -> f;
    private final FileHandler handler;

    public ZipUtility() {
        this(NOOP_FILE_HANDLER);
    }

    public ZipUtility(FileHandler fileHandler) {
        this.handler = fileHandler;
    }

    public void add(String pathInZip, File file) {
        if (file == null || !file.exists()) {
            log.info("File does not exist: {}", (Object)file);
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File entry : files) {
                    this.add(pathInZip + "/" + entry.getName(), entry);
                }
            }
        } else {
            if (this.handler != null) {
                file = this.handler.handle(file);
            }
            if (file != null) {
                this.files.put(pathInZip, file);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void zip(OutputStream destination) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(destination));
        try {
            out.setComment("zip, created by Confluence");
            for (Map.Entry<String, File> entry : this.files.entrySet()) {
                String path = entry.getKey();
                File file = entry.getValue();
                if (file == null) {
                    log.warn("Not adding file for path: " + path + ". File is null.");
                    continue;
                }
                if (!file.exists()) {
                    log.info("File does not exist " + file.getAbsolutePath() + " (as path " + path + ").");
                    continue;
                }
                log.debug("adding entry: " + file.getAbsolutePath() + ", as " + path);
                ZipEntry zentry = new ZipEntry(path);
                zentry.setTime(file.lastModified());
                out.putNextEntry(zentry);
                try {
                    ByteStreams.copy((InputStream)FileUtils.openInputStream((File)file), (OutputStream)out);
                }
                catch (IOException e) {
                    log.error("Could not copy file " + file, (Throwable)e);
                    throw e;
                }
                out.closeEntry();
            }
        }
        finally {
            out.finish();
            out.close();
        }
    }

    public static interface FileHandler {
        public File handle(File var1);
    }
}

