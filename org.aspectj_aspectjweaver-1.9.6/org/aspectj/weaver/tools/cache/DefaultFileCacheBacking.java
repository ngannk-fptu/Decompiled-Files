/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.Map;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FileUtil;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.tools.cache.AbstractIndexedFileCacheBacking;
import org.aspectj.weaver.tools.cache.CacheBacking;
import org.aspectj.weaver.tools.cache.CachedClassEntry;
import org.aspectj.weaver.tools.cache.CachedClassReference;
import org.aspectj.weaver.tools.cache.WeavedClassCache;

public class DefaultFileCacheBacking
extends AbstractIndexedFileCacheBacking {
    private final Map<String, AbstractIndexedFileCacheBacking.IndexEntry> index = this.readIndex();
    private static final Object LOCK = new Object();

    protected DefaultFileCacheBacking(File cacheDir) {
        super(cacheDir);
    }

    public static final DefaultFileCacheBacking createBacking(File cacheDir) {
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                MessageUtil.error("Unable to create cache directory at " + cacheDir.getName());
                return null;
            }
        } else if (!cacheDir.isDirectory()) {
            MessageUtil.error("Not a cache directory at " + cacheDir.getName());
            return null;
        }
        if (!cacheDir.canWrite()) {
            MessageUtil.error("Cache directory is not writable at " + cacheDir.getName());
            return null;
        }
        return new DefaultFileCacheBacking(cacheDir);
    }

    @Override
    protected Map<String, AbstractIndexedFileCacheBacking.IndexEntry> getIndex() {
        return this.index;
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
    private void removeIndexEntry(String key) {
        Object object = LOCK;
        synchronized (object) {
            this.index.remove(key);
            this.writeIndex();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addIndexEntry(AbstractIndexedFileCacheBacking.IndexEntry ie) {
        Object object = LOCK;
        synchronized (object) {
            this.index.put(ie.key, ie);
            this.writeIndex();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Map<String, AbstractIndexedFileCacheBacking.IndexEntry> readIndex() {
        Object object = LOCK;
        synchronized (object) {
            return super.readIndex();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void writeIndex() {
        Object object = LOCK;
        synchronized (object) {
            super.writeIndex();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        File cacheDir = this.getCacheDirectory();
        int numDeleted = 0;
        Object object = LOCK;
        synchronized (object) {
            numDeleted = FileUtil.deleteContents(cacheDir);
        }
        if (numDeleted > 0 && this.logger != null && this.logger.isTraceEnabled()) {
            this.logger.info("clear(" + cacheDir + ") deleted");
        }
    }

    public static CacheBacking createBacking(String scope) {
        String cache = System.getProperty("aj.weaving.cache.dir");
        if (cache == null) {
            return null;
        }
        File cacheDir = new File(cache, scope);
        return DefaultFileCacheBacking.createBacking(cacheDir);
    }

    @Override
    public String[] getKeys(final String regex) {
        File cacheDirectory = this.getCacheDirectory();
        Object[] files = cacheDirectory.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String s) {
                return s.matches(regex);
            }
        });
        if (LangUtil.isEmpty(files)) {
            return EMPTY_KEYS;
        }
        String[] keys = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            keys[i] = ((File)files[i]).getName();
        }
        return keys;
    }

    @Override
    public CachedClassEntry get(CachedClassReference ref, byte[] originalBytes) {
        byte[] bytes;
        File cacheDirectory = this.getCacheDirectory();
        String refKey = ref.getKey();
        File cacheFile = new File(cacheDirectory, refKey);
        AbstractIndexedFileCacheBacking.IndexEntry ie = this.index.get(refKey);
        if (ie == null) {
            this.delete(cacheFile);
            return null;
        }
        if (DefaultFileCacheBacking.crc(originalBytes) != ie.crcClass) {
            this.delete(cacheFile);
            return null;
        }
        if (ie.ignored) {
            return new CachedClassEntry(ref, WeavedClassCache.ZERO_BYTES, CachedClassEntry.EntryType.IGNORED);
        }
        if (cacheFile.canRead() && (bytes = this.read(cacheFile, ie.crcWeaved)) != null) {
            if (!ie.generated) {
                return new CachedClassEntry(ref, bytes, CachedClassEntry.EntryType.WEAVED);
            }
            return new CachedClassEntry(ref, bytes, CachedClassEntry.EntryType.GENERATED);
        }
        return null;
    }

    @Override
    public void put(CachedClassEntry entry, byte[] originalBytes) {
        boolean writeEntryBytes;
        File cacheDirectory = this.getCacheDirectory();
        String refKey = entry.getKey();
        AbstractIndexedFileCacheBacking.IndexEntry ie = this.index.get(refKey);
        File cacheFile = new File(cacheDirectory, refKey);
        if (ie != null && (ie.ignored != entry.isIgnored() || ie.generated != entry.isGenerated() || DefaultFileCacheBacking.crc(originalBytes) != ie.crcClass)) {
            this.delete(cacheFile);
            writeEntryBytes = true;
        } else {
            boolean bl = writeEntryBytes = !cacheFile.exists();
        }
        if (writeEntryBytes) {
            ie = DefaultFileCacheBacking.createIndexEntry(entry, originalBytes);
            if (!entry.isIgnored()) {
                ie.crcWeaved = this.write(cacheFile, entry.getBytes());
            }
            this.addIndexEntry(ie);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(CachedClassReference ref) {
        File cacheDirectory = this.getCacheDirectory();
        String refKey = ref.getKey();
        File cacheFile = new File(cacheDirectory, refKey);
        Object object = LOCK;
        synchronized (object) {
            this.removeIndexEntry(refKey);
            this.delete(cacheFile);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void delete(File file) {
        Object object = LOCK;
        synchronized (object) {
            super.delete(file);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected byte[] read(File file, long expectedCRC) {
        byte[] bytes = null;
        Object object = LOCK;
        synchronized (object) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                bytes = FileUtil.readAsByteArray(fis);
            }
            catch (Exception e) {
                if (this.logger != null && this.logger.isTraceEnabled()) {
                    this.logger.warn("read(" + file.getAbsolutePath() + ") failed (" + e.getClass().getSimpleName() + ") to read contents: " + e.getMessage(), e);
                }
            }
            finally {
                this.close(fis, file);
            }
            if (bytes == null || DefaultFileCacheBacking.crc(bytes) != expectedCRC) {
                this.delete(file);
                return null;
            }
        }
        return bytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected long write(File file, byte[] bytes) {
        Object object = LOCK;
        synchronized (object) {
            if (file.exists()) {
                return -1L;
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                ((OutputStream)out).write(bytes);
            }
            catch (Exception e) {
                long l;
                try {
                    if (this.logger != null && this.logger.isTraceEnabled()) {
                        this.logger.warn("write(" + file.getAbsolutePath() + ") failed (" + e.getClass().getSimpleName() + ") to write contents: " + e.getMessage(), e);
                    }
                    this.delete(file);
                    l = -1L;
                }
                catch (Throwable throwable) {
                    this.close(out, file);
                    throw throwable;
                }
                this.close(out, file);
                return l;
            }
            this.close(out, file);
            return DefaultFileCacheBacking.crc(bytes);
        }
    }
}

