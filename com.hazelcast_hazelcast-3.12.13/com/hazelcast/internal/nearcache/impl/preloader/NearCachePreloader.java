/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.preloader;

import com.hazelcast.config.NearCachePreloaderConfig;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.nearcache.impl.preloader.NearCachePreloaderLock;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.internal.util.BufferingInputStream;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.monitor.impl.NearCacheStatsImpl;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.collection.InflatableSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class NearCachePreloader<K> {
    private static final int MAGIC_BYTES = -365122482;
    private static final int LOG_OF_BUFFER_SIZE = 16;
    private static final int BUFFER_SIZE = 65536;
    private static final int LOAD_BATCH_SIZE = 100;
    private final ILogger logger = Logger.getLogger(NearCachePreloader.class);
    private final byte[] tmpBytes = new byte[4];
    private final String nearCacheName;
    private final NearCacheStatsImpl nearCacheStats;
    private final SerializationService serializationService;
    private final NearCachePreloaderLock lock;
    private final File storeFile;
    private final File tmpStoreFile;
    private ByteBuffer buf;
    private int lastWrittenBytes;
    private int lastKeyCount;

    public NearCachePreloader(String nearCacheName, NearCachePreloaderConfig preloaderConfig, NearCacheStatsImpl nearCacheStats, SerializationService serializationService) {
        this.nearCacheName = nearCacheName;
        this.nearCacheStats = nearCacheStats;
        this.serializationService = serializationService;
        String filename = NearCachePreloader.getFilename(preloaderConfig.getDirectory(), nearCacheName);
        this.lock = new NearCachePreloaderLock(this.logger, filename + ".lock");
        this.storeFile = new File(filename);
        this.tmpStoreFile = new File(filename + "~");
    }

    public void destroy() {
        this.lock.release();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadKeys(DataStructureAdapter<Object, ?> adapter) {
        BufferingInputStream bis;
        long startedNanos;
        block6: {
            if (!this.storeFile.exists()) {
                this.logger.info(String.format("Skipped loading keys of Near Cache %s since storage file doesn't exist (%s)", this.nearCacheName, this.storeFile.getAbsolutePath()));
                return;
            }
            startedNanos = System.nanoTime();
            bis = null;
            bis = new BufferingInputStream(new FileInputStream(this.storeFile), 65536);
            if (this.checkHeader(bis)) break block6;
            IOUtil.closeResource(bis);
            return;
        }
        try {
            int loadedKeys = this.loadKeySet(bis, adapter);
            long elapsedMillis = NearCachePreloader.getElapsedMillis(startedNanos);
            this.logger.info(String.format("Loaded %d keys of Near Cache %s in %d ms", loadedKeys, this.nearCacheName, elapsedMillis));
        }
        catch (Exception e) {
            try {
                this.logger.warning(String.format("Could not pre-load Near Cache %s (%s)", this.nearCacheName, this.storeFile.getAbsolutePath()), e);
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(bis);
                throw throwable;
            }
            IOUtil.closeResource(bis);
        }
        IOUtil.closeResource(bis);
    }

    private boolean checkHeader(BufferingInputStream bis) throws IOException {
        int magicBytes = this.readInt(bis);
        if (magicBytes != -365122482) {
            this.logger.warning(String.format("Found invalid header for Near Cache %s (%s)", this.nearCacheName, this.storeFile.getAbsolutePath()));
            return false;
        }
        int fileFormat = this.readInt(bis);
        if (fileFormat < 0 || fileFormat > FileFormat.values().length - 1) {
            this.logger.warning(String.format("Found invalid file format for Near Cache %s (%s)", this.nearCacheName, this.storeFile.getAbsolutePath()));
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void storeKeys(Iterator<K> iterator) {
        FileOutputStream fos;
        long startedNanos;
        block5: {
            startedNanos = System.nanoTime();
            fos = null;
            this.buf = ByteBuffer.allocate(65536);
            this.lastWrittenBytes = 0;
            this.lastKeyCount = 0;
            fos = new FileOutputStream(this.tmpStoreFile, false);
            this.writeInt(fos, -365122482);
            this.writeInt(fos, FileFormat.INTERLEAVED_LENGTH_FIELD.ordinal());
            this.writeKeySet(fos, fos.getChannel(), iterator);
            if (this.lastKeyCount != 0) break block5;
            IOUtil.deleteQuietly(this.storeFile);
            this.updatePersistenceStats(startedNanos);
            IOUtil.closeResource(fos);
            IOUtil.deleteQuietly(this.tmpStoreFile);
            return;
        }
        try {
            fos.flush();
            IOUtil.closeResource(fos);
            IOUtil.rename(this.tmpStoreFile, this.storeFile);
            this.updatePersistenceStats(startedNanos);
        }
        catch (Exception e) {
            try {
                this.logger.warning(String.format("Could not store keys of Near Cache %s (%s)", this.nearCacheName, this.storeFile.getAbsolutePath()), e);
                this.nearCacheStats.addPersistenceFailure(e);
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(fos);
                IOUtil.deleteQuietly(this.tmpStoreFile);
                throw throwable;
            }
            IOUtil.closeResource(fos);
            IOUtil.deleteQuietly(this.tmpStoreFile);
        }
        IOUtil.closeResource(fos);
        IOUtil.deleteQuietly(this.tmpStoreFile);
    }

    private void updatePersistenceStats(long startedNanos) {
        long elapsedMillis = NearCachePreloader.getElapsedMillis(startedNanos);
        this.nearCacheStats.addPersistence(elapsedMillis, this.lastWrittenBytes, this.lastKeyCount);
        this.logger.info(String.format("Stored %d keys of Near Cache %s in %d ms (%d kB)", this.lastKeyCount, this.nearCacheName, elapsedMillis, MemoryUnit.BYTES.toKiloBytes(this.lastWrittenBytes)));
    }

    private int loadKeySet(BufferingInputStream bis, DataStructureAdapter<Object, ?> adapter) throws IOException {
        int dataSize;
        byte[] payload;
        int loadedKeys = 0;
        InflatableSet.Builder builder = InflatableSet.newBuilder(100);
        while (IOUtil.readFullyOrNothing(bis, this.tmpBytes) && IOUtil.readFullyOrNothing(bis, payload = new byte[dataSize = Bits.readIntB(this.tmpBytes, 0)])) {
            HeapData key = new HeapData(payload);
            builder.add(this.serializationService.toObject(key));
            if (builder.size() == 100) {
                adapter.getAll(builder.build());
                builder = InflatableSet.newBuilder(100);
            }
            ++loadedKeys;
        }
        if (builder.size() > 0) {
            adapter.getAll(builder.build());
        }
        return loadedKeys;
    }

    private void writeKeySet(FileOutputStream fos, FileChannel outChannel, Iterator<K> iterator) throws IOException {
        while (iterator.hasNext()) {
            K key = iterator.next();
            Object dataKey = this.serializationService.toData(key);
            if (dataKey != null) {
                int transferredCount;
                int dataSize = dataKey.totalSize();
                this.writeInt(fos, dataSize);
                int position = 0;
                for (int remaining = dataSize; remaining > 0; remaining -= transferredCount) {
                    transferredCount = Math.min(65536 - this.buf.position(), remaining);
                    this.ensureBufHasRoom(fos, transferredCount);
                    this.buf.put(dataKey.toByteArray(), position, transferredCount);
                    position += transferredCount;
                }
                this.lastWrittenBytes += 4 + dataSize;
                ++this.lastKeyCount;
            }
            this.flushLocalBuffer(outChannel);
        }
    }

    private int readInt(BufferingInputStream bis) throws IOException {
        IOUtil.readFullyOrNothing(bis, this.tmpBytes);
        return Bits.readIntB(this.tmpBytes, 0);
    }

    private void writeInt(FileOutputStream fos, int dataSize) throws IOException {
        this.ensureBufHasRoom(fos, 4);
        Bits.writeIntB(this.tmpBytes, 0, dataSize);
        this.buf.put(this.tmpBytes);
    }

    private void ensureBufHasRoom(FileOutputStream fos, int expectedSize) throws IOException {
        if (this.buf.position() < 65536 - expectedSize) {
            return;
        }
        fos.write(this.buf.array());
        this.buf.position(0);
    }

    private void flushLocalBuffer(FileChannel outChannel) throws IOException {
        if (this.buf.position() == 0) {
            return;
        }
        this.buf.flip();
        while (this.buf.hasRemaining()) {
            outChannel.write(this.buf);
        }
        this.buf.clear();
    }

    private static String getFilename(String directory, String nearCacheName) {
        String filename = IOUtil.toFileName("nearCache-" + nearCacheName + ".store");
        if (StringUtil.isNullOrEmpty(directory)) {
            return filename;
        }
        return IOUtil.getPath(directory, filename);
    }

    private static long getElapsedMillis(long startedNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos);
    }

    private static enum FileFormat {
        INTERLEAVED_LENGTH_FIELD;

    }
}

