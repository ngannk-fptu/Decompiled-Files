/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import com.opensymphony.util.ClassLoaderUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileManager {
    private static Map files = Collections.synchronizedMap(new HashMap());
    protected static boolean reloadingConfigs = false;

    private FileManager() {
    }

    public static void setReloadingConfigs(boolean reloadingConfigs) {
        FileManager.reloadingConfigs = reloadingConfigs;
    }

    public static boolean isReloadingConfigs() {
        return reloadingConfigs;
    }

    public static boolean fileNeedsReloading(String fileName) {
        FileRevision revision = (FileRevision)files.get(fileName);
        if (revision == null && reloadingConfigs) {
            return true;
        }
        if (revision == null) {
            return false;
        }
        return revision.getLastModified() < revision.getFile().lastModified();
    }

    public static InputStream loadFile(String fileName, Class clazz) {
        InputStream is;
        URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);
        if (fileUrl == null) {
            return null;
        }
        try {
            is = fileUrl.openStream();
            if (is == null) {
                throw new IllegalArgumentException("No file '" + fileName + "' found as a resource");
            }
        }
        catch (IOException e) {
            throw new IllegalArgumentException("No file '" + fileName + "' found as a resource");
        }
        if (FileManager.isReloadingConfigs()) {
            File file = new File(fileUrl.getFile());
            if (!file.exists() || !file.canRead()) {
                file = null;
            }
            if (file != null) {
                long lastModified = file.lastModified();
                files.put(fileName, new FileRevision(file, lastModified));
            }
        }
        return is;
    }

    private static class FileRevision {
        private File file;
        private long lastModified;

        public FileRevision(File file, long lastUpdated) {
            this.file = file;
            this.lastModified = lastUpdated;
        }

        public File getFile() {
            return this.file;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public long getLastModified() {
            return this.lastModified;
        }
    }
}

