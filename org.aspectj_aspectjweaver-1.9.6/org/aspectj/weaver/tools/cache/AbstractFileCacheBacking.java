/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.aspectj.weaver.tools.cache.AbstractCacheBacking;

public abstract class AbstractFileCacheBacking
extends AbstractCacheBacking {
    public static final String WEAVED_CLASS_CACHE_DIR = "aj.weaving.cache.dir";
    private final File cacheDirectory;

    protected AbstractFileCacheBacking(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
        if (this.cacheDirectory == null) {
            throw new IllegalStateException("No cache directory specified");
        }
    }

    public File getCacheDirectory() {
        return this.cacheDirectory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void writeClassBytes(String key, byte[] bytes) throws Exception {
        File dir = this.getCacheDirectory();
        File file = new File(dir, key);
        FileOutputStream out = new FileOutputStream(file);
        try {
            out.write(bytes);
        }
        finally {
            this.close(out, file);
        }
    }

    protected void delete(File file) {
        if (file.exists() && !file.delete() && this.logger != null && this.logger.isTraceEnabled()) {
            this.logger.error("Error deleting file " + file.getAbsolutePath());
        }
    }

    protected void close(OutputStream out, File file) {
        block3: {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    if (this.logger == null || !this.logger.isTraceEnabled()) break block3;
                    this.logger.error("Failed (" + e.getClass().getSimpleName() + ") to close write file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                }
            }
        }
    }

    protected void close(InputStream in, File file) {
        block3: {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    if (this.logger == null || !this.logger.isTraceEnabled()) break block3;
                    this.logger.error("Failed (" + e.getClass().getSimpleName() + ") to close read file " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                }
            }
        }
    }
}

