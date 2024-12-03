/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.Map;
import java.util.TreeMap;
import org.aspectj.util.FileUtil;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.tools.cache.AbstractIndexedFileCacheBacking;
import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking;

public class FlatFileCacheBacking
extends AsynchronousFileCacheBacking {
    private static final AsynchronousFileCacheBacking.AsynchronousFileCacheBackingCreator<FlatFileCacheBacking> defaultCreator = new AsynchronousFileCacheBacking.AsynchronousFileCacheBackingCreator<FlatFileCacheBacking>(){

        @Override
        public FlatFileCacheBacking create(File cacheDir) {
            return new FlatFileCacheBacking(cacheDir);
        }
    };

    public FlatFileCacheBacking(File cacheDir) {
        super(cacheDir);
    }

    public static final FlatFileCacheBacking createBacking(File cacheDir) {
        return FlatFileCacheBacking.createBacking(cacheDir, defaultCreator);
    }

    @Override
    protected Map<String, byte[]> readClassBytes(Map<String, AbstractIndexedFileCacheBacking.IndexEntry> indexMap, File cacheDir) {
        return this.readClassBytes(indexMap, cacheDir.listFiles());
    }

    protected Map<String, byte[]> readClassBytes(Map<String, AbstractIndexedFileCacheBacking.IndexEntry> indexMap, File[] files) {
        TreeMap<String, byte[]> result = new TreeMap<String, byte[]>();
        if (LangUtil.isEmpty(files)) {
            return result;
        }
        for (File file : files) {
            String key;
            if (!file.isFile() || "cache.idx".equalsIgnoreCase(key = file.getName())) continue;
            AbstractIndexedFileCacheBacking.IndexEntry entry = indexMap.get(key);
            if (entry == null || entry.ignored) {
                if (this.logger != null && this.logger.isTraceEnabled()) {
                    this.logger.info("readClassBytes(" + key + ") remove orphan/ignored: " + file.getAbsolutePath());
                }
                FileUtil.deleteContents(file);
                continue;
            }
            try {
                byte[] bytes = FileUtil.readAsByteArray(file);
                long crc = FlatFileCacheBacking.crc(bytes);
                if (crc != entry.crcWeaved) {
                    throw new StreamCorruptedException("Mismatched CRC - expected=" + entry.crcWeaved + "/got=" + crc);
                }
                result.put(key, bytes);
                if (this.logger == null || !this.logger.isTraceEnabled()) continue;
                this.logger.debug("readClassBytes(" + key + ") cached from " + file.getAbsolutePath());
            }
            catch (IOException e) {
                if (this.logger != null && this.logger.isTraceEnabled()) {
                    this.logger.error("Failed (" + e.getClass().getSimpleName() + ") to read bytes from " + file.getAbsolutePath() + ": " + e.getMessage());
                }
                indexMap.remove(key);
                FileUtil.deleteContents(file);
            }
        }
        return result;
    }

    @Override
    protected AbstractIndexedFileCacheBacking.IndexEntry resolveIndexMapEntry(File cacheDir, AbstractIndexedFileCacheBacking.IndexEntry ie) {
        File cacheEntry = new File(cacheDir, ie.key);
        if (ie.ignored || cacheEntry.canRead()) {
            return ie;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void writeClassBytes(String key, byte[] bytes) throws Exception {
        File dir = this.getCacheDirectory();
        File file = new File(dir, key);
        try (FileOutputStream out = new FileOutputStream(file);){
            out.write(bytes);
        }
    }

    @Override
    protected void removeClassBytes(String key) throws Exception {
        File dir = this.getCacheDirectory();
        File file = new File(dir, key);
        if (file.exists() && !file.delete()) {
            throw new StreamCorruptedException("Failed to delete " + file.getAbsolutePath());
        }
    }
}

