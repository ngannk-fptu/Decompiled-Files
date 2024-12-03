/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.aspectj.util.FileUtil;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.cache.AbstractIndexedFileCacheBacking;
import org.aspectj.weaver.tools.cache.CachedClassEntry;
import org.aspectj.weaver.tools.cache.CachedClassReference;
import org.aspectj.weaver.tools.cache.WeavedClassCache;

public abstract class AsynchronousFileCacheBacking
extends AbstractIndexedFileCacheBacking {
    private static final BlockingQueue<AsyncCommand> commandsQ = new LinkedBlockingQueue<AsyncCommand>();
    private static final ExecutorService execService = Executors.newSingleThreadExecutor();
    private static Future<?> commandsRunner;
    protected final Map<String, AbstractIndexedFileCacheBacking.IndexEntry> index;
    protected final Map<String, AbstractIndexedFileCacheBacking.IndexEntry> exposedIndex;
    protected final Map<String, byte[]> bytesMap;
    protected final Map<String, byte[]> exposedBytes;

    protected AsynchronousFileCacheBacking(File cacheDir) {
        super(cacheDir);
        this.index = this.readIndex(cacheDir, this.getIndexFile());
        this.exposedIndex = Collections.unmodifiableMap(this.index);
        this.bytesMap = this.readClassBytes(this.index, cacheDir);
        this.exposedBytes = Collections.unmodifiableMap(this.bytesMap);
    }

    @Override
    protected Map<String, AbstractIndexedFileCacheBacking.IndexEntry> getIndex() {
        return this.index;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CachedClassEntry get(CachedClassReference ref, byte[] originalBytes) {
        byte[] bytes;
        AbstractIndexedFileCacheBacking.IndexEntry indexEntry;
        String key = ref.getKey();
        Map<String, AbstractIndexedFileCacheBacking.IndexEntry> map = this.index;
        synchronized (map) {
            indexEntry = this.index.get(key);
            if (indexEntry == null) {
                return null;
            }
        }
        if (AsynchronousFileCacheBacking.crc(originalBytes) != indexEntry.crcClass) {
            if (this.logger != null && this.logger.isTraceEnabled()) {
                this.logger.debug("get(" + this.getCacheDirectory() + ") mismatched original class bytes CRC for " + key);
            }
            this.remove(key);
            return null;
        }
        if (indexEntry.ignored) {
            return new CachedClassEntry(ref, WeavedClassCache.ZERO_BYTES, CachedClassEntry.EntryType.IGNORED);
        }
        Map<String, byte[]> map2 = this.bytesMap;
        synchronized (map2) {
            bytes = this.bytesMap.remove(key);
            if (bytes == null) {
                return null;
            }
        }
        if (indexEntry.generated) {
            return new CachedClassEntry(ref, bytes, CachedClassEntry.EntryType.GENERATED);
        }
        return new CachedClassEntry(ref, bytes, CachedClassEntry.EntryType.WEAVED);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void put(CachedClassEntry entry, byte[] originalBytes) {
        String key = entry.getKey();
        byte[] bytes = entry.isIgnored() ? null : entry.getBytes();
        Map<String, AbstractIndexedFileCacheBacking.IndexEntry> map = this.index;
        synchronized (map) {
            AbstractIndexedFileCacheBacking.IndexEntry indexEntry = this.index.get(key);
            if (indexEntry != null) {
                return;
            }
            indexEntry = AsynchronousFileCacheBacking.createIndexEntry(entry, originalBytes);
            this.index.put(key, indexEntry);
        }
        if (!AsynchronousFileCacheBacking.postCacheCommand(new InsertCommand(this, key, bytes)) && this.logger != null && this.logger.isTraceEnabled()) {
            this.logger.error("put(" + this.getCacheDirectory() + ") Failed to post insert command for " + key);
        }
        if (this.logger != null && this.logger.isTraceEnabled()) {
            this.logger.debug("put(" + this.getCacheDirectory() + ")[" + key + "] inserted");
        }
    }

    @Override
    public void remove(CachedClassReference ref) {
        this.remove(ref.getKey());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected AbstractIndexedFileCacheBacking.IndexEntry remove(String key) {
        AbstractIndexedFileCacheBacking.IndexEntry entry;
        Map<String, AbstractIndexedFileCacheBacking.IndexEntry> map = this.index;
        synchronized (map) {
            entry = this.index.remove(key);
        }
        map = this.bytesMap;
        synchronized (map) {
            this.bytesMap.remove(key);
        }
        if (!AsynchronousFileCacheBacking.postCacheCommand(new RemoveCommand(this, key)) && this.logger != null && this.logger.isTraceEnabled()) {
            this.logger.error("remove(" + this.getCacheDirectory() + ") Failed to post remove command for " + key);
        }
        if (entry != null) {
            if (!key.equals(entry.key)) {
                if (this.logger != null && this.logger.isTraceEnabled()) {
                    this.logger.error("remove(" + this.getCacheDirectory() + ") Mismatched keys: " + key + " / " + entry.key);
                }
            } else if (this.logger != null && this.logger.isTraceEnabled()) {
                this.logger.debug("remove(" + this.getCacheDirectory() + ")[" + key + "] removed");
            }
        }
        return entry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<AbstractIndexedFileCacheBacking.IndexEntry> getIndexEntries() {
        Map<String, AbstractIndexedFileCacheBacking.IndexEntry> map = this.index;
        synchronized (map) {
            if (this.index.isEmpty()) {
                return Collections.emptyList();
            }
            return new ArrayList<AbstractIndexedFileCacheBacking.IndexEntry>(this.index.values());
        }
    }

    public Map<String, AbstractIndexedFileCacheBacking.IndexEntry> getIndexMap() {
        return this.exposedIndex;
    }

    public Map<String, byte[]> getBytesMap() {
        return this.exposedBytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Map<String, AbstractIndexedFileCacheBacking.IndexEntry> map = this.index;
        synchronized (map) {
            this.index.clear();
        }
        if (!AsynchronousFileCacheBacking.postCacheCommand(new ClearCommand(this)) && this.logger != null && this.logger.isTraceEnabled()) {
            this.logger.error("Failed to post clear command for " + this.getIndexFile());
        }
    }

    protected void executeCommand(AsyncCommand cmd) throws Exception {
        if (cmd instanceof ClearCommand) {
            this.executeClearCommand();
        } else if (cmd instanceof UpdateIndexCommand) {
            this.executeUpdateIndexCommand();
        } else if (cmd instanceof InsertCommand) {
            this.executeInsertCommand((InsertCommand)cmd);
        } else if (cmd instanceof RemoveCommand) {
            this.executeRemoveCommand((RemoveCommand)cmd);
        } else {
            throw new UnsupportedOperationException("Unknown command: " + cmd);
        }
    }

    protected void executeClearCommand() throws Exception {
        FileUtil.deleteContents(this.getIndexFile());
        FileUtil.deleteContents(this.getCacheDirectory());
    }

    protected void executeUpdateIndexCommand() throws Exception {
        this.writeIndex(this.getIndexFile(), this.getIndexEntries());
    }

    protected void executeInsertCommand(InsertCommand cmd) throws Exception {
        this.writeIndex(this.getIndexFile(), this.getIndexEntries());
        byte[] bytes = cmd.getClassBytes();
        if (bytes != null) {
            this.writeClassBytes(cmd.getKey(), bytes);
        }
    }

    protected void executeRemoveCommand(RemoveCommand cmd) throws Exception {
        Exception err = null;
        try {
            this.removeClassBytes(cmd.getKey());
        }
        catch (Exception e) {
            err = e;
        }
        this.writeIndex(this.getIndexFile(), this.getIndexEntries());
        if (err != null) {
            throw err;
        }
    }

    protected abstract void removeClassBytes(String var1) throws Exception;

    protected abstract Map<String, byte[]> readClassBytes(Map<String, AbstractIndexedFileCacheBacking.IndexEntry> var1, File var2);

    public String toString() {
        return this.getClass().getSimpleName() + "[" + String.valueOf(this.getCacheDirectory()) + "]";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static final <T extends AsynchronousFileCacheBacking> T createBacking(File cacheDir, AsynchronousFileCacheBackingCreator<T> creator) {
        final Trace trace = TraceFactory.getTraceFactory().getTrace(AsynchronousFileCacheBacking.class);
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            if (trace != null && trace.isTraceEnabled()) {
                trace.error("Unable to create cache directory at " + cacheDir.getAbsolutePath());
            }
            return null;
        }
        if (!cacheDir.canWrite()) {
            if (trace != null && trace.isTraceEnabled()) {
                trace.error("Cache directory is not writable at " + cacheDir.getAbsolutePath());
            }
            return null;
        }
        T backing = creator.create(cacheDir);
        ExecutorService executorService = execService;
        synchronized (executorService) {
            if (commandsRunner == null) {
                commandsRunner = execService.submit(new Runnable(){

                    @Override
                    public void run() {
                        block4: while (true) {
                            try {
                                while (true) {
                                    AsyncCommand cmd = (AsyncCommand)commandsQ.take();
                                    try {
                                        AsynchronousFileCacheBacking cache = cmd.getCache();
                                        cache.executeCommand(cmd);
                                        continue block4;
                                    }
                                    catch (Exception e) {
                                        if (trace == null || !trace.isTraceEnabled()) continue;
                                        trace.error("Failed (" + e.getClass().getSimpleName() + ") to execute " + cmd + ": " + e.getMessage(), e);
                                        continue;
                                    }
                                    break;
                                }
                            }
                            catch (InterruptedException e) {
                                if (trace != null && trace.isTraceEnabled()) {
                                    trace.warn("Interrupted");
                                }
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }
                });
            }
        }
        if (!AsynchronousFileCacheBacking.postCacheCommand(new UpdateIndexCommand((AsynchronousFileCacheBacking)backing)) && trace != null && trace.isTraceEnabled()) {
            trace.warn("Failed to offer update index command to " + cacheDir.getAbsolutePath());
        }
        return backing;
    }

    public static final boolean postCacheCommand(AsyncCommand cmd) {
        return commandsQ.offer(cmd);
    }

    public static class InsertCommand
    extends KeyedCommand {
        private final byte[] bytes;

        public InsertCommand(AsynchronousFileCacheBacking cache, String keyValue, byte[] classBytes) {
            super(cache, keyValue);
            this.bytes = classBytes;
        }

        public final byte[] getClassBytes() {
            return this.bytes;
        }
    }

    public static class RemoveCommand
    extends KeyedCommand {
        public RemoveCommand(AsynchronousFileCacheBacking cache, String keyValue) {
            super(cache, keyValue);
        }
    }

    public static abstract class KeyedCommand
    extends AbstractCommand {
        private final String key;

        protected KeyedCommand(AsynchronousFileCacheBacking cache, String keyValue) {
            super(cache);
            if (LangUtil.isEmpty(keyValue)) {
                throw new IllegalStateException("No key value");
            }
            this.key = keyValue;
        }

        public final String getKey() {
            return this.key;
        }

        @Override
        public String toString() {
            return super.toString() + "[" + this.getKey() + "]";
        }
    }

    public static class UpdateIndexCommand
    extends AbstractCommand {
        public UpdateIndexCommand(AsynchronousFileCacheBacking cache) {
            super(cache);
        }
    }

    public static class ClearCommand
    extends AbstractCommand {
        public ClearCommand(AsynchronousFileCacheBacking cache) {
            super(cache);
        }
    }

    public static abstract class AbstractCommand
    implements AsyncCommand {
        private final AsynchronousFileCacheBacking cache;

        protected AbstractCommand(AsynchronousFileCacheBacking backing) {
            this.cache = backing;
            if (this.cache == null) {
                throw new IllegalStateException("No backing cache specified");
            }
        }

        @Override
        public final AsynchronousFileCacheBacking getCache() {
            return this.cache;
        }

        public String toString() {
            return this.getClass().getSimpleName() + "[" + this.getCache() + "]";
        }
    }

    public static interface AsyncCommand {
        public AsynchronousFileCacheBacking getCache();
    }

    public static interface AsynchronousFileCacheBackingCreator<T extends AsynchronousFileCacheBacking> {
        public T create(File var1);
    }
}

