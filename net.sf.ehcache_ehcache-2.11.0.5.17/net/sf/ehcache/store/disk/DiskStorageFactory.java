/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store.disk;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.DiskStorePathManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.concurrent.ConcurrencyUtil;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.pool.sizeof.annotations.IgnoreSizeOf;
import net.sf.ehcache.store.disk.DiskStore;
import net.sf.ehcache.store.disk.ElementSubstituteFilter;
import net.sf.ehcache.store.disk.Segment;
import net.sf.ehcache.store.disk.ods.FileAllocationTree;
import net.sf.ehcache.store.disk.ods.Region;
import net.sf.ehcache.util.MemoryEfficientByteArrayOutputStream;
import net.sf.ehcache.util.PreferredLoaderObjectInputStream;
import net.sf.ehcache.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IgnoreSizeOf
public class DiskStorageFactory {
    private static final int SERIALIZATION_CONCURRENCY_DELAY = 250;
    private static final int SHUTDOWN_GRACE_PERIOD = 60;
    private static final int MEGABYTE = 0x100000;
    private static final int MAX_EVICT = 5;
    private static final int SAMPLE_SIZE = 30;
    private static final Logger LOG = LoggerFactory.getLogger((String)DiskStorageFactory.class.getName());
    protected volatile DiskStore store;
    private final BlockingQueue<Runnable> diskQueue;
    private final ScheduledThreadPoolExecutor diskWriter;
    private final long queueCapacity;
    private final File file;
    private final RandomAccessFile[] dataAccess;
    private final FileAllocationTree allocator;
    private final RegisteredEventListeners eventService;
    private volatile int elementSize;
    private final ElementSubstituteFilter onDiskFilter = new OnDiskFilter();
    private final AtomicInteger onDisk = new AtomicInteger();
    private final File indexFile;
    private final IndexWriteTask flushTask;
    private volatile int diskCapacity;
    private volatile boolean pinningEnabled;
    private final boolean diskPersistent;
    private final DiskStorePathManager diskStorePathManager;
    private final ClassLoader classLoader;

    public DiskStorageFactory(Ehcache cache, RegisteredEventListeners cacheEventNotificationService) {
        this.classLoader = cache.getCacheConfiguration().getClassLoader();
        this.diskStorePathManager = cache.getCacheManager().getDiskStorePathManager();
        this.file = this.diskStorePathManager.getFile(cache.getName(), ".data");
        this.indexFile = this.diskStorePathManager.getFile(cache.getName(), ".index");
        this.pinningEnabled = this.determineCachePinned(cache.getCacheConfiguration());
        this.diskPersistent = cache.getCacheConfiguration().isDiskPersistent();
        if (this.diskPersistent && this.diskStorePathManager.isAutoCreated()) {
            LOG.warn("Data in persistent disk stores is ignored for stores from automatically created directories.\nRemove diskPersistent or resolve the conflicting disk paths in cache configuration.\nDeleting data file " + this.file.getAbsolutePath());
            DiskStorageFactory.deleteFile(this.file);
        } else if (!this.diskPersistent) {
            DiskStorageFactory.deleteFile(this.file);
            DiskStorageFactory.deleteFile(this.indexFile);
        }
        try {
            this.dataAccess = DiskStorageFactory.allocateRandomAccessFiles(this.file, cache.getCacheConfiguration().getDiskAccessStripes());
        }
        catch (FileNotFoundException e) {
            throw new CacheException(e);
        }
        this.allocator = new FileAllocationTree(Long.MAX_VALUE, this.dataAccess[0]);
        this.diskWriter = new ScheduledThreadPoolExecutor(1, new ThreadFactory(){

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, DiskStorageFactory.this.file.getName());
                t.setDaemon(false);
                return t;
            }
        });
        this.diskQueue = this.diskWriter.getQueue();
        this.eventService = cache.getCacheEventNotificationService();
        this.queueCapacity = cache.getCacheConfiguration().getDiskSpoolBufferSizeMB() * 0x100000;
        this.diskCapacity = cache.getCacheConfiguration().getMaxElementsOnDisk();
        this.diskWriter.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.diskWriter.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        long expiryInterval = cache.getCacheConfiguration().getDiskExpiryThreadIntervalSeconds();
        this.diskWriter.scheduleWithFixedDelay(new DiskExpiryTask(), expiryInterval, expiryInterval, TimeUnit.SECONDS);
        this.flushTask = new IndexWriteTask(this.indexFile, cache.getCacheConfiguration().isClearOnFlush());
        if (!this.getDataFile().exists() || this.getDataFile().length() == 0L) {
            LOG.debug("Matching data file missing (or empty) for index file. Deleting index file " + this.indexFile);
            DiskStorageFactory.deleteFile(this.indexFile);
        } else if (this.getDataFile().exists() && this.indexFile.exists() && this.getDataFile().lastModified() > this.indexFile.lastModified() + TimeUnit.SECONDS.toMillis(1L)) {
            LOG.warn("The index for data file {} is out of date, probably due to an unclean shutdown. Deleting index file {}", (Object)this.getDataFile(), (Object)this.indexFile);
            DiskStorageFactory.deleteFile(this.indexFile);
        }
    }

    private boolean determineCachePinned(CacheConfiguration cacheConfiguration) {
        PinningConfiguration pinningConfiguration = cacheConfiguration.getPinningConfiguration();
        if (pinningConfiguration == null) {
            return false;
        }
        switch (pinningConfiguration.getStore()) {
            case LOCALMEMORY: {
                return false;
            }
            case INCACHE: {
                return true;
            }
        }
        throw new IllegalArgumentException();
    }

    private static RandomAccessFile[] allocateRandomAccessFiles(File f, int stripes) throws FileNotFoundException {
        int roundedStripes = stripes;
        while ((roundedStripes & roundedStripes - 1) != 0) {
            ++roundedStripes;
        }
        RandomAccessFile[] result = new RandomAccessFile[roundedStripes];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new RandomAccessFile(f, "rw");
        }
        return result;
    }

    private RandomAccessFile getDataAccess(Object key) {
        return this.dataAccess[ConcurrencyUtil.selectLock(key, this.dataAccess.length)];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getOnDiskSizeInBytes() {
        RandomAccessFile randomAccessFile = this.dataAccess[0];
        synchronized (randomAccessFile) {
            try {
                return this.dataAccess[0].length();
            }
            catch (IOException e) {
                LOG.warn("Exception trying to determine store size", (Throwable)e);
                return 0L;
            }
        }
    }

    public void bind(DiskStore store) {
        this.store = store;
        this.loadIndex();
    }

    public void free(Lock lock, DiskSubstitute substitute) {
        this.free(lock, substitute, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void free(Lock lock, DiskSubstitute substitute, boolean faultFailure) {
        if (substitute instanceof DiskMarker) {
            if (!faultFailure) {
                this.onDisk.decrementAndGet();
            }
            DiskFreeTask free = new DiskFreeTask(lock, (DiskMarker)substitute);
            if (lock.tryLock()) {
                try {
                    free.call();
                }
                finally {
                    lock.unlock();
                }
            } else {
                this.schedule(free);
            }
        }
    }

    protected void markUsed(DiskMarker marker) {
        this.allocator.mark(new Region(marker.getPosition(), marker.getPosition() + (long)marker.getSize() - 1L));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void shrinkDataFile() {
        RandomAccessFile randomAccessFile = this.dataAccess[0];
        synchronized (randomAccessFile) {
            try {
                this.dataAccess[0].setLength(this.allocator.getFileSize());
            }
            catch (IOException e) {
                LOG.error("Exception trying to shrink data file to size", (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void shutdown() throws IOException {
        this.diskWriter.shutdown();
        for (int i = 0; i < 60; ++i) {
            try {
                if (this.diskWriter.awaitTermination(1L, TimeUnit.SECONDS)) break;
                LOG.info("Waited " + (i + 1) + " seconds for shutdown of [" + this.file.getName() + "]");
                continue;
            }
            catch (InterruptedException e) {
                LOG.warn("Received exception while waiting for shutdown", (Throwable)e);
            }
        }
        RandomAccessFile[] randomAccessFileArray = this.dataAccess;
        int n = randomAccessFileArray.length;
        for (int i = 0; i < n; ++i) {
            RandomAccessFile raf;
            RandomAccessFile randomAccessFile = raf = randomAccessFileArray[i];
            synchronized (randomAccessFile) {
                raf.close();
                continue;
            }
        }
        if (!this.diskPersistent) {
            DiskStorageFactory.deleteFile(this.file);
            DiskStorageFactory.deleteFile(this.indexFile);
        }
    }

    protected void delete() {
        DiskStorageFactory.deleteFile(this.file);
        this.allocator.clear();
    }

    protected <U> Future<U> schedule(Callable<U> call) {
        return this.diskWriter.submit(call);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Element read(DiskMarker marker) throws IOException, ClassNotFoundException {
        RandomAccessFile data;
        byte[] buffer = new byte[marker.getSize()];
        RandomAccessFile randomAccessFile = data = this.getDataAccess(marker.getKey());
        synchronized (randomAccessFile) {
            data.seek(marker.getPosition());
            data.readFully(buffer);
        }
        try (PreferredLoaderObjectInputStream objstr = new PreferredLoaderObjectInputStream(new ByteArrayInputStream(buffer), this.classLoader);){
            Element element = (Element)objstr.readObject();
            return element;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DiskMarker write(Element element) throws IOException {
        RandomAccessFile data;
        int bufferLength;
        MemoryEfficientByteArrayOutputStream buffer = this.serializeElement(element);
        this.elementSize = bufferLength = buffer.size();
        DiskMarker marker = this.alloc(element, bufferLength);
        RandomAccessFile randomAccessFile = data = this.getDataAccess(element.getObjectKey());
        synchronized (randomAccessFile) {
            data.seek(marker.getPosition());
            data.write(buffer.toByteArray(), 0, bufferLength);
        }
        return marker;
    }

    private MemoryEfficientByteArrayOutputStream serializeElement(Element element) throws IOException {
        try {
            return MemoryEfficientByteArrayOutputStream.serialize(element);
        }
        catch (ConcurrentModificationException e) {
            throw new CacheException("Failed to serialize element due to ConcurrentModificationException. This is frequently the result of inappropriately sharing thread unsafe object (eg. ArrayList, HashMap, etc) between threads", e);
        }
    }

    private DiskMarker alloc(Element element, int size) throws IOException {
        Region r = this.allocator.alloc(size);
        return this.createMarker(r.start(), size, element);
    }

    protected void free(DiskMarker marker) {
        this.allocator.free(new Region(marker.getPosition(), marker.getPosition() + (long)marker.getSize() - 1L));
    }

    public boolean bufferFull() {
        return (long)(this.diskQueue.size() * this.elementSize) > this.queueCapacity;
    }

    public File getDataFile() {
        return this.file;
    }

    public void expireElements() {
        new DiskExpiryTask().run();
    }

    protected static void deleteFile(File f) {
        if (!f.delete()) {
            LOG.debug("Failed to delete file {}", (Object)f.getName());
        }
    }

    public DiskSubstitute create(Element element) throws IllegalArgumentException {
        return new Placeholder(element);
    }

    public Element retrieve(DiskSubstitute object) {
        if (object instanceof DiskMarker) {
            try {
                DiskMarker marker = (DiskMarker)object;
                return this.read(marker);
            }
            catch (IOException e) {
                throw new CacheException(e);
            }
            catch (ClassNotFoundException e) {
                throw new CacheException(e);
            }
        }
        if (object instanceof Placeholder) {
            return ((Placeholder)object).getElement();
        }
        return null;
    }

    public Element retrieve(DiskSubstitute object, Segment segment) {
        if (object instanceof DiskMarker) {
            try {
                DiskMarker marker = (DiskMarker)object;
                Element e = this.read(marker);
                marker.hit(e);
                return e;
            }
            catch (IOException e) {
                throw new CacheException(e);
            }
            catch (ClassNotFoundException e) {
                throw new CacheException(e);
            }
        }
        if (object instanceof Placeholder) {
            return ((Placeholder)object).getElement();
        }
        return null;
    }

    public boolean created(Object object) {
        if (object instanceof DiskSubstitute) {
            return ((DiskSubstitute)object).getFactory() == this;
        }
        return false;
    }

    public void unbind() {
        try {
            this.flushTask.call();
        }
        catch (Throwable t) {
            LOG.error("Could not flush disk cache. Initial cause was " + t.getMessage(), t);
        }
        try {
            this.shutdown();
            if (this.diskStorePathManager.isAutoCreated()) {
                DiskStorageFactory.deleteFile(this.indexFile);
                this.delete();
            }
        }
        catch (IOException e) {
            LOG.error("Could not shut down disk cache. Initial cause was " + e.getMessage(), (Throwable)e);
        }
    }

    public Future<Void> flush() {
        return this.schedule(this.flushTask);
    }

    private DiskMarker createMarker(long position, int size, Element element) {
        return new DiskMarker(this, position, size, element);
    }

    private boolean isPinningEnabled() {
        return this.pinningEnabled;
    }

    int evict(int count) {
        if (this.isPinningEnabled()) {
            return 0;
        }
        int evicted = 0;
        for (int i = 0; i < count; ++i) {
            Element evictedElement;
            DiskSubstitute target = this.getDiskEvictionTarget(null, count);
            if (target == null || (evictedElement = this.store.evictElement(target.getKey(), null)) == null) continue;
            ++evicted;
        }
        return evicted;
    }

    public int getOnDiskSize() {
        return this.onDisk.get();
    }

    public void setOnDiskCapacity(int capacity) {
        this.diskCapacity = capacity;
    }

    int getDiskCapacity() {
        return this.diskCapacity == 0 ? Integer.MAX_VALUE : this.diskCapacity;
    }

    private void onDiskEvict(int size, Object keyHint) {
        if (this.diskCapacity > 0 && !this.isPinningEnabled()) {
            Element element;
            DiskSubstitute target;
            int overflow = size - this.diskCapacity;
            for (int i = 0; i < Math.min(5, overflow) && ((target = this.getDiskEvictionTarget(keyHint, size)) == null || (element = this.store.evictElement(target.getKey(), target)) == null || this.onDisk.get() > this.diskCapacity); ++i) {
            }
        }
    }

    private DiskSubstitute getDiskEvictionTarget(Object keyHint, int size) {
        List<DiskSubstitute> sample = this.store.getRandomSample(this.onDiskFilter, Math.min(30, size), keyHint);
        DiskSubstitute target = null;
        DiskSubstitute hintTarget = null;
        for (DiskSubstitute substitute : sample) {
            if (target != null && substitute.getHitCount() >= target.getHitCount()) continue;
            if (substitute.getKey().equals(keyHint)) {
                hintTarget = substitute;
                continue;
            }
            target = substitute;
        }
        return target != null ? target : hintTarget;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private void loadIndex() {
        block15: {
            if (!this.indexFile.exists()) {
                return;
            }
            try {
                ois = new PreferredLoaderObjectInputStream(new FileInputStream(this.indexFile), this.classLoader);
                key = ois.readObject();
                value = ois.readObject();
                marker = (DiskMarker)value;
                while (true) {
                    marker.bindFactory(this);
                    this.markUsed(marker);
                    if (!this.store.putRawIfAbsent(key, marker)) ** break block13
                    this.onDisk.incrementAndGet();
                    break block14;
                    break;
                }
                {
                    catch (Throwable var5_7) {
                        ois.close();
                        throw var5_7;
                    }
                }
                {
                    block14: {
                        ois.close();
                        return;
                    }
                    key = ois.readObject();
                    marker = (DiskMarker)ois.readObject();
                    continue;
                }
                catch (EOFException ois) {
                    break block15;
                }
                catch (Exception e) {
                    DiskStorageFactory.LOG.warn("Index file {} is corrupt, deleting and ignoring it : {}", (Object)this.indexFile, (Object)e);
                    e.printStackTrace();
                    this.store.removeAll();
                    DiskStorageFactory.deleteFile(this.indexFile);
                }
            }
            finally {
                this.shrinkDataFile();
            }
        }
    }

    public File getIndexFile() {
        return this.indexFile;
    }

    class IndexWriteTask
    implements Callable<Void> {
        private final File index;
        private final boolean clearOnFlush;

        IndexWriteTask(File index, boolean clear) {
            this.index = index;
            this.clearOnFlush = clear;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public synchronized Void call() throws IOException, InterruptedException {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.index));){
                for (Object key : DiskStorageFactory.this.store.keySet()) {
                    Object o = DiskStorageFactory.this.store.unretrievedGet(key);
                    if (o instanceof Placeholder && !((Placeholder)o).failedToFlush && (o = new PersistentDiskWriteTask((Placeholder)o).call()) == null) {
                        o = DiskStorageFactory.this.store.unretrievedGet(key);
                    }
                    if (!(o instanceof DiskMarker)) continue;
                    DiskMarker marker = (DiskMarker)o;
                    oos.writeObject(key);
                    oos.writeObject(marker);
                }
            }
            return null;
        }
    }

    private final class PersistentDiskWriteTask
    extends DiskWriteTask {
        PersistentDiskWriteTask(Placeholder p) {
            super(p);
        }

        @Override
        public DiskMarker call() {
            DiskMarker result = super.call();
            if (result != null) {
                int disk = DiskStorageFactory.this.onDisk.incrementAndGet();
                DiskStorageFactory.this.onDiskEvict(disk, this.getPlaceholder().getKey());
            }
            return result;
        }
    }

    private class OnDiskFilter
    implements ElementSubstituteFilter {
        private OnDiskFilter() {
        }

        @Override
        public boolean allows(Object object) {
            if (!DiskStorageFactory.this.created(object)) {
                return false;
            }
            return object instanceof DiskMarker;
        }
    }

    private final class DiskExpiryTask
    implements Runnable {
        private DiskExpiryTask() {
        }

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            for (Object key : DiskStorageFactory.this.store.keySet()) {
                Object value = DiskStorageFactory.this.store.unretrievedGet(key);
                if (!DiskStorageFactory.this.created(value) || !(value instanceof DiskMarker)) continue;
                this.checkExpiry((DiskMarker)value, now);
            }
        }

        private void checkExpiry(DiskMarker marker, long now) {
            if (marker.getExpirationTime() < now) {
                DiskStorageFactory.this.store.evict(marker.getKey(), marker);
            }
        }
    }

    public static class DiskMarker
    extends DiskSubstitute
    implements Serializable {
        @IgnoreSizeOf
        private final Object key;
        private final long position;
        private final int size;
        private volatile long hitCount;
        private volatile long expiry;

        DiskMarker(DiskStorageFactory factory, long position, int size, Element element) {
            super(factory);
            this.position = position;
            this.size = size;
            this.key = element.getObjectKey();
            this.hitCount = element.getHitCount();
            this.expiry = TimeUtil.toMillis(TimeUtil.toSecs(element.getExpirationTime()));
        }

        DiskMarker(DiskStorageFactory factory, long position, int size, Object key, long hits) {
            super(factory);
            this.position = position;
            this.size = size;
            this.key = key;
            this.hitCount = hits;
        }

        @Override
        Object getKey() {
            return this.key;
        }

        @Override
        long getHitCount() {
            return this.hitCount;
        }

        private long getPosition() {
            return this.position;
        }

        public int getSize() {
            return this.size;
        }

        @Override
        public void installed() {
        }

        @Override
        public long getExpirationTime() {
            return this.expiry;
        }

        void hit(Element e) {
            ++this.hitCount;
            this.expiry = e.getExpirationTime();
        }

        void updateStats(Element e) {
            this.hitCount = e.getHitCount();
            this.expiry = e.getExpirationTime();
        }
    }

    final class Placeholder
    extends DiskSubstitute {
        @IgnoreSizeOf
        private final Object key;
        private final Element element;
        private volatile boolean failedToFlush;

        Placeholder(Element element) {
            super(DiskStorageFactory.this);
            this.key = element.getObjectKey();
            this.element = element;
        }

        boolean hasFailedToFlush() {
            return this.failedToFlush;
        }

        private void setFailedToFlush(boolean failedToFlush) {
            this.failedToFlush = failedToFlush;
        }

        @Override
        public void installed() {
            DiskStorageFactory.this.schedule(new PersistentDiskWriteTask(this));
        }

        @Override
        Object getKey() {
            return this.key;
        }

        @Override
        long getHitCount() {
            return this.getElement().getHitCount();
        }

        @Override
        long getExpirationTime() {
            return this.getElement().getExpirationTime();
        }

        Element getElement() {
            return this.element;
        }
    }

    public static abstract class DiskSubstitute {
        protected volatile transient long onHeapSize;
        @IgnoreSizeOf
        private volatile transient DiskStorageFactory factory;

        public DiskSubstitute() {
            this.factory = null;
        }

        DiskSubstitute(DiskStorageFactory factory) {
            this.factory = factory;
        }

        abstract Object getKey();

        abstract long getHitCount();

        abstract long getExpirationTime();

        abstract void installed();

        public final DiskStorageFactory getFactory() {
            return this.factory;
        }

        void bindFactory(DiskStorageFactory factory) {
            this.factory = factory;
        }
    }

    private final class DiskFreeTask
    implements Callable<Void> {
        private final Lock lock;
        private final DiskMarker marker;

        private DiskFreeTask(Lock lock, DiskMarker marker) {
            this.lock = lock;
            this.marker = marker;
        }

        @Override
        public Void call() {
            this.lock.lock();
            try {
                DiskStorageFactory.this.free(this.marker);
            }
            finally {
                this.lock.unlock();
            }
            return null;
        }
    }

    abstract class DiskWriteTask
    implements Callable<DiskMarker> {
        private final Placeholder placeholder;

        DiskWriteTask(Placeholder p) {
            this.placeholder = p;
        }

        Placeholder getPlaceholder() {
            return this.placeholder;
        }

        @Override
        public DiskMarker call() {
            try {
                if (DiskStorageFactory.this.store.containsKey(this.placeholder.getKey())) {
                    DiskMarker marker = DiskStorageFactory.this.write(this.placeholder.getElement());
                    if (marker != null && DiskStorageFactory.this.store.fault(this.placeholder.getKey(), this.placeholder, marker)) {
                        return marker;
                    }
                    return null;
                }
                return null;
            }
            catch (Throwable e) {
                LOG.error("Disk Write of " + this.placeholder.getKey() + " failed: ", e);
                DiskStorageFactory.this.store.evict(this.placeholder.getKey(), this.placeholder);
                return null;
            }
        }
    }
}

