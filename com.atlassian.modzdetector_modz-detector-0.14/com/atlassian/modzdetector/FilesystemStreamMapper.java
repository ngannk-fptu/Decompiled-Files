/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.modzdetector;

import com.atlassian.modzdetector.StreamMapper;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesystemStreamMapper
implements StreamMapper {
    private static final Logger log = LoggerFactory.getLogger(FilesystemStreamMapper.class);
    private static final char REGISTRY_SEPARATOR_CHAR = '/';
    private final File base;
    private final int basePathLength;
    private FileFilter filter;

    public FilesystemStreamMapper(File base, FileFilter filter) {
        if (!base.isDirectory() && base.canRead()) {
            throw new IllegalArgumentException("base must be a readable directory");
        }
        this.base = base;
        this.basePathLength = (base.getAbsolutePath() + "/").length();
        this.filter = filter;
    }

    public InputStream mapStream(String prefix, String resourceName) {
        if ("fs.".equals(prefix)) {
            try {
                File file = new File(this.base, resourceName);
                if (this.filter.accept(file)) {
                    return new FileInputStream(file);
                }
                log.warn("The given file cannot be mapped because it has been filtered out. File path: " + file.getAbsolutePath());
                return null;
            }
            catch (FileNotFoundException ignore) {
                return null;
            }
        }
        throw new IllegalStateException("Only filesystems supported.");
    }

    public String getResourcePath(String resourceKey) {
        if (resourceKey.startsWith("fs.")) {
            String unixPath = resourceKey.substring("fs.".length());
            return unixPath.replace('/', File.separatorChar);
        }
        throw new IllegalArgumentException("only filesystem resources are supported: " + resourceKey);
    }

    public String getResourceKey(File file) {
        return "fs." + file.getAbsolutePath().substring(this.basePathLength);
    }
}

