/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.tools.cache.AbstractFileCacheBacking;
import org.aspectj.weaver.tools.cache.CachedClassEntry;

public abstract class AbstractIndexedFileCacheBacking
extends AbstractFileCacheBacking {
    public static final String INDEX_FILE = "cache.idx";
    protected static final IndexEntry[] EMPTY_INDEX = new IndexEntry[0];
    protected static final String[] EMPTY_KEYS = new String[0];
    private final File indexFile;

    protected AbstractIndexedFileCacheBacking(File cacheDir) {
        super(cacheDir);
        this.indexFile = new File(cacheDir, INDEX_FILE);
    }

    public File getIndexFile() {
        return this.indexFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getKeys(String regex) {
        Map<String, IndexEntry> index = this.getIndex();
        if (index == null || index.isEmpty()) {
            return EMPTY_KEYS;
        }
        LinkedList<String> matches = new LinkedList<String>();
        Map<String, IndexEntry> map = index;
        synchronized (map) {
            for (String key : index.keySet()) {
                if (!key.matches(regex)) continue;
                matches.add(key);
            }
        }
        if (matches.isEmpty()) {
            return EMPTY_KEYS;
        }
        return matches.toArray(new String[matches.size()]);
    }

    protected Map<String, IndexEntry> readIndex() {
        return this.readIndex(this.getCacheDirectory(), this.getIndexFile());
    }

    protected void writeIndex() {
        this.writeIndex(this.getIndexFile());
    }

    protected void writeIndex(File file) {
        block2: {
            try {
                this.writeIndex(file, this.getIndex());
            }
            catch (Exception e) {
                if (this.logger == null || !this.logger.isTraceEnabled()) break block2;
                this.logger.warn("writeIndex(" + file + ") " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            }
        }
    }

    protected abstract Map<String, IndexEntry> getIndex();

    protected Map<String, IndexEntry> readIndex(File cacheDir, File cacheFile) {
        TreeMap<String, IndexEntry> indexMap = new TreeMap<String, IndexEntry>();
        Object[] idxValues = this.readIndex(cacheFile);
        if (LangUtil.isEmpty(idxValues)) {
            if (this.logger != null && this.logger.isTraceEnabled()) {
                this.logger.debug("readIndex(" + cacheFile + ") no index entries");
            }
            return indexMap;
        }
        for (Object ie : idxValues) {
            IndexEntry resEntry = this.resolveIndexMapEntry(cacheDir, (IndexEntry)ie);
            if (resEntry != null) {
                indexMap.put(resEntry.key, resEntry);
                continue;
            }
            if (this.logger == null || !this.logger.isTraceEnabled()) continue;
            this.logger.debug("readIndex(" + cacheFile + ") skip " + ((IndexEntry)ie).key);
        }
        return indexMap;
    }

    protected IndexEntry resolveIndexMapEntry(File cacheDir, IndexEntry ie) {
        return ie;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public IndexEntry[] readIndex(File indexFile) {
        IndexEntry[] indexEntryArray;
        if (!indexFile.canRead()) {
            return EMPTY_INDEX;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(indexFile));
            indexEntryArray = (IndexEntry[])ois.readObject();
        }
        catch (Exception e) {
            try {
                if (this.logger != null && this.logger.isTraceEnabled()) {
                    this.logger.error("Failed (" + e.getClass().getSimpleName() + ") to read index from " + indexFile.getAbsolutePath() + " : " + e.getMessage(), e);
                }
                this.delete(indexFile);
            }
            catch (Throwable throwable) {
                this.close(ois, indexFile);
                throw throwable;
            }
            this.close(ois, indexFile);
            return EMPTY_INDEX;
        }
        this.close(ois, indexFile);
        return indexEntryArray;
    }

    protected void writeIndex(File indexFile, Map<String, ? extends IndexEntry> index) throws IOException {
        this.writeIndex(indexFile, LangUtil.isEmpty(index) ? Collections.emptyList() : index.values());
    }

    protected void writeIndex(File indexFile, IndexEntry ... entries) throws IOException {
        this.writeIndex(indexFile, LangUtil.isEmpty(entries) ? Collections.emptyList() : Arrays.asList(entries));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void writeIndex(File indexFile, Collection<? extends IndexEntry> entries) throws IOException {
        IndexEntry[] entryValues;
        File indexDir = indexFile.getParentFile();
        if (!indexDir.exists() && !indexDir.mkdirs()) {
            throw new IOException("Failed to create path to " + indexFile.getAbsolutePath());
        }
        int numEntries = LangUtil.isEmpty(entries) ? 0 : entries.size();
        IndexEntry[] indexEntryArray = entryValues = numEntries <= 0 ? null : entries.toArray(new IndexEntry[numEntries]);
        if (LangUtil.isEmpty(entryValues)) {
            if (indexFile.exists() && !indexFile.delete()) {
                throw new StreamCorruptedException("Failed to clean up index file at " + indexFile.getAbsolutePath());
            }
            return;
        }
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(indexFile), 4096));
        try {
            oos.writeObject(entryValues);
        }
        finally {
            this.close(oos, indexFile);
        }
    }

    public static final IndexEntry createIndexEntry(CachedClassEntry classEntry, byte[] originalBytes) {
        if (classEntry == null) {
            return null;
        }
        IndexEntry indexEntry = new IndexEntry();
        indexEntry.key = classEntry.getKey();
        indexEntry.generated = classEntry.isGenerated();
        indexEntry.ignored = classEntry.isIgnored();
        indexEntry.crcClass = AbstractIndexedFileCacheBacking.crc(originalBytes);
        if (!classEntry.isIgnored()) {
            indexEntry.crcWeaved = AbstractIndexedFileCacheBacking.crc(classEntry.getBytes());
        }
        return indexEntry;
    }

    public static class IndexEntry
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = 756391290557029363L;
        public String key;
        public boolean generated;
        public boolean ignored;
        public long crcClass;
        public long crcWeaved;

        public IndexEntry clone() {
            try {
                return (IndexEntry)this.getClass().cast(super.clone());
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException("Failed to clone: " + this.toString() + ": " + e.getMessage(), e);
            }
        }

        public int hashCode() {
            return (int)((long)(this.key.hashCode() + (this.generated ? 1 : 0) + (this.ignored ? 1 : 0)) + this.crcClass + this.crcWeaved);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            IndexEntry other = (IndexEntry)obj;
            return this.key.equals(other.key) && this.ignored == other.ignored && this.generated == other.generated && this.crcClass == other.crcClass && this.crcWeaved == other.crcWeaved;
        }

        public String toString() {
            return this.key + "[" + (this.generated ? "generated" : "ignored") + "];crcClass=0x" + Long.toHexString(this.crcClass) + ";crcWeaved=0x" + Long.toHexString(this.crcWeaved);
        }
    }
}

