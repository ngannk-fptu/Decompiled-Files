/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JaiI18N;
import com.sun.media.jai.util.Job;
import com.sun.media.jai.util.Request;
import com.sun.media.jai.util.RequestJob;
import com.sun.media.jai.util.TileJob;
import com.sun.media.jai.util.WorkerThread;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;
import javax.media.jai.TileScheduler;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public final class SunTileScheduler
implements TileScheduler {
    private static final int NUM_THREADS_DEFAULT = 2;
    private static final int NUM_PREFETCH_THREADS_DEFAULT = 1;
    private static int numInstances = 0;
    private static String name = JaiI18N.getString("SunTileSchedulerName");
    private ThreadGroup rootGroup;
    private ThreadGroup standardGroup;
    private ThreadGroup prefetchGroup;
    private int parallelism = 2;
    private int prefetchParallelism = 1;
    private int priority = 5;
    private int prefetchPriority = 1;
    private LinkedList queue = null;
    private LinkedList prefetchQueue = null;
    private Vector workers = new Vector();
    private Vector prefetchWorkers = new Vector();
    private int numWorkerThreads = 0;
    private int numPrefetchThreads = 0;
    private Map tilesInProgress = new HashMap();
    Map tileRequests = new HashMap();
    Map tileJobs = new HashMap();
    private String nameOfThisInstance;

    static Object tileKey(PlanarImage owner, int tileX, int tileY) {
        long idx = (long)tileY * (long)owner.getNumXTiles() + (long)tileX;
        BigInteger imageID = (BigInteger)owner.getImageID();
        byte[] buf = imageID.toByteArray();
        int length = buf.length;
        byte[] buf1 = new byte[length + 8];
        System.arraycopy(buf, 0, buf1, 0, length);
        int i = 7;
        int j = 0;
        while (i >= 0) {
            buf1[length++] = (byte)(idx >> j);
            --i;
            j += 8;
        }
        return new BigInteger(buf1);
    }

    static Set getListeners(List reqList) {
        int numReq = reqList.size();
        HashSet listeners = null;
        for (int j = 0; j < numReq; ++j) {
            Request req = (Request)reqList.get(j);
            if (req.listeners == null || req.listeners.isEmpty()) continue;
            if (listeners == null) {
                listeners = new HashSet();
            }
            listeners.addAll(req.listeners);
        }
        return listeners;
    }

    private static String getStackTraceString(Throwable e) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteStream);
        e.printStackTrace(printStream);
        printStream.flush();
        String stackTraceString = byteStream.toString();
        printStream.close();
        return stackTraceString;
    }

    public SunTileScheduler(int parallelism, int priority, int prefetchParallelism, int prefetchPriority) {
        this();
        this.setParallelism(parallelism);
        this.setPriority(priority);
        this.setPrefetchParallelism(prefetchParallelism);
        this.setPrefetchPriority(prefetchPriority);
    }

    public SunTileScheduler() {
        this.queue = new LinkedList();
        this.prefetchQueue = new LinkedList();
        this.nameOfThisInstance = name + numInstances;
        this.rootGroup = new ThreadGroup(this.nameOfThisInstance);
        this.rootGroup.setDaemon(true);
        this.standardGroup = new ThreadGroup(this.rootGroup, this.nameOfThisInstance + "Standard");
        this.standardGroup.setDaemon(true);
        this.prefetchGroup = new ThreadGroup(this.rootGroup, this.nameOfThisInstance + "Prefetch");
        this.prefetchGroup.setDaemon(true);
        ++numInstances;
    }

    Exception compute(PlanarImage owner, Point[] tileIndices, Raster[] tiles, int offset, int numTiles, Request request) {
        Exception exception = null;
        int j = offset;
        if (request == null || request.listeners == null) {
            int i = 0;
            while (i < numTiles) {
                Point p = tileIndices[j];
                try {
                    tiles[j] = owner.getTile(p.x, p.y);
                }
                catch (Exception e) {
                    exception = e;
                    break;
                }
                ++i;
                ++j;
            }
        } else {
            TileRequest[] reqs = new Request[]{request};
            int i = 0;
            while (i < numTiles) {
                Point p = tileIndices[j];
                Integer tileStatus = new Integer(1);
                request.tileStatus.put(p, tileStatus);
                try {
                    tiles[j] = owner.getTile(p.x, p.y);
                    Iterator iter = request.listeners.iterator();
                    while (iter.hasNext()) {
                        tileStatus = new Integer(2);
                        request.tileStatus.put(p, tileStatus);
                        TileComputationListener listener = (TileComputationListener)iter.next();
                        listener.tileComputed(this, reqs, owner, p.x, p.y, tiles[j]);
                    }
                }
                catch (Exception e) {
                    exception = e;
                    break;
                }
                ++i;
                ++j;
            }
        }
        if (exception != null && request != null && request.listeners != null) {
            int lastOffset = j;
            int numFailed = numTiles - (lastOffset - offset);
            int k = lastOffset;
            for (int i = 0; i < numFailed; ++i) {
                Integer tileStatus = new Integer(4);
                request.tileStatus.put(tileIndices[k++], tileStatus);
            }
            TileRequest[] reqs = new Request[]{request};
            int k2 = lastOffset;
            for (int i = 0; i < numFailed; ++i) {
                Point p = tileIndices[k2++];
                Iterator iter = request.listeners.iterator();
                while (iter.hasNext()) {
                    TileComputationListener listener = (TileComputationListener)iter.next();
                    listener.tileComputationFailure(this, reqs, owner, p.x, p.y, exception);
                }
            }
        }
        return exception;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Raster scheduleTile(OpImage owner, int tileX, int tileY) {
        if (owner == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler1"));
        }
        Raster tile = null;
        Object tileID = SunTileScheduler.tileKey(owner, tileX, tileY);
        boolean computeTile = false;
        Object[] cache = null;
        Object[] objectArray = this.tilesInProgress;
        synchronized (this.tilesInProgress) {
            computeTile = !this.tilesInProgress.containsKey(tileID);
            if (computeTile) {
                cache = new Object[1];
                this.tilesInProgress.put(tileID, cache);
            } else {
                cache = (Object[])this.tilesInProgress.get(tileID);
            }
            // ** MonitorExit[objectArray] (shouldn't be in output)
            if (computeTile) {
                try {
                    try {
                        try {
                            tile = owner.computeTile(tileX, tileY);
                        }
                        catch (OutOfMemoryError e) {
                            TileCache tileCache = owner.getTileCache();
                            if (tileCache != null) {
                                tileCache.flush();
                                System.gc();
                            }
                            tile = owner.computeTile(tileX, tileY);
                        }
                        Object var11_14 = null;
                        Object[] objectArray2 = cache;
                    }
                    catch (Throwable e) {
                        if (e instanceof Error) {
                            throw (Error)e;
                        }
                        if (e instanceof RuntimeException) {
                            this.sendExceptionToListener(JaiI18N.getString("SunTileScheduler6"), e);
                        } else {
                            String message = JaiI18N.getString("SunTileScheduler6");
                            this.sendExceptionToListener(message, new ImagingException(message, e));
                        }
                        Object var11_15 = null;
                        Object[] objectArray2 = cache;
                        synchronized (cache) {
                            cache[0] = tile != null ? tile : new Object();
                            cache.notifyAll();
                            Map map = this.tilesInProgress;
                            synchronized (map) {
                                this.tilesInProgress.remove(tileID);
                            }
                            // ** MonitorExit[objectArray2] (shouldn't be in output)
                            return tile;
                        }
                    }
                }
                catch (Throwable throwable) {
                    Object var11_16 = null;
                    Object[] objectArray4 = cache;
                    synchronized (cache) {
                        cache[0] = tile != null ? tile : new Object();
                        cache.notifyAll();
                        Map map = this.tilesInProgress;
                        synchronized (map) {
                            this.tilesInProgress.remove(tileID);
                        }
                        // ** MonitorExit[objectArray4] (shouldn't be in output)
                        throw throwable;
                    }
                }
                synchronized (cache) {
                    cache[0] = tile != null ? tile : new Object();
                    cache.notifyAll();
                    Map map = this.tilesInProgress;
                    synchronized (map) {
                        this.tilesInProgress.remove(tileID);
                    }
                    // ** MonitorExit[var12_17] (shouldn't be in output)
                    return tile;
                }
            }
            objectArray = cache;
            synchronized (cache) {
                if (cache[0] == null) {
                    try {
                        cache.wait();
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                }
                if (!(cache[0] instanceof Raster)) throw new RuntimeException(JaiI18N.getString("SunTileScheduler5"));
                return (Raster)cache[0];
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object scheduleJob(PlanarImage owner, Point[] tileIndices, boolean isBlocking, boolean isPrefetch, TileComputationListener[] listeners) {
        Raster[] tiles;
        if (owner == null || tileIndices == null) {
            throw new IllegalArgumentException();
        }
        if ((isBlocking || isPrefetch) && listeners != null) {
            throw new IllegalArgumentException();
        }
        if (isBlocking && isPrefetch) {
            throw new IllegalArgumentException();
        }
        int numTiles = tileIndices.length;
        Object returnValue = tiles = new Raster[numTiles];
        int numThreads = 0;
        Job[] jobs = null;
        int numJobs = 0;
        Vector vector = this.getWorkers(isPrefetch);
        synchronized (vector) {
            numThreads = this.getNumThreads(isPrefetch);
            if (numThreads > 0) {
                if (numTiles <= numThreads || !isBlocking && !isPrefetch) {
                    jobs = new Job[numTiles];
                    if (!isBlocking && !isPrefetch) {
                        Request request = new Request(this, owner, tileIndices, listeners);
                        returnValue = request;
                        while (numJobs < numTiles) {
                            Point p = tileIndices[numJobs];
                            Object tileID = SunTileScheduler.tileKey(owner, p.x, p.y);
                            Map map = this.tileRequests;
                            synchronized (map) {
                                List<Request> reqList = null;
                                if (this.tileRequests.containsKey(tileID)) {
                                    reqList = (List)this.tileRequests.get(tileID);
                                    reqList.add(request);
                                    --numTiles;
                                } else {
                                    reqList = new ArrayList<Request>();
                                    reqList.add(request);
                                    this.tileRequests.put(tileID, reqList);
                                    jobs[numJobs] = new RequestJob(this, owner, p.x, p.y, tiles, numJobs);
                                    this.tileJobs.put(tileID, jobs[numJobs]);
                                    this.addJob(jobs[numJobs++], false);
                                }
                            }
                        }
                    } else {
                        while (numJobs < numTiles) {
                            jobs[numJobs] = new TileJob(this, isBlocking, owner, tileIndices, tiles, numJobs, 1);
                            this.addJob(jobs[numJobs++], isPrefetch);
                        }
                    }
                } else {
                    float frac = 1.0f / (2.0f * (float)numThreads);
                    int minTilesPerThread = numThreads == 1 ? numTiles : Math.min(Math.max(1, (int)(frac * (float)numTiles / 2.0f + 0.5f)), numTiles);
                    int maxNumJobs = numThreads == 1 ? 1 : (int)((float)numTiles / (float)minTilesPerThread + 0.5f);
                    jobs = new TileJob[maxNumJobs];
                    int numTilesQueued = 0;
                    int numTilesLeft = numTiles - numTilesQueued;
                    while (numTilesLeft > 0) {
                        int numTilesInThread = (int)(frac * (float)numTilesLeft + 0.5f);
                        if (numTilesInThread < minTilesPerThread) {
                            numTilesInThread = minTilesPerThread;
                        }
                        if (numTilesInThread > numTilesLeft) {
                            numTilesInThread = numTilesLeft;
                        }
                        if ((numTilesLeft -= numTilesInThread) < minTilesPerThread) {
                            numTilesInThread += numTilesLeft;
                            numTilesLeft = 0;
                        }
                        jobs[numJobs] = new TileJob(this, isBlocking, owner, tileIndices, tiles, numTilesQueued, numTilesInThread);
                        this.addJob(jobs[numJobs++], isPrefetch);
                        numTilesQueued += numTilesInThread;
                    }
                }
            }
        }
        if (numThreads != 0) {
            if (isBlocking) {
                LinkedList jobQueue = this.getQueue(isPrefetch);
                for (int i = 0; i < numJobs; ++i) {
                    SunTileScheduler minTilesPerThread = this;
                    synchronized (minTilesPerThread) {
                        while (jobs[i].notDone()) {
                            try {
                                this.wait();
                            }
                            catch (InterruptedException ie) {}
                        }
                    }
                    Exception e = jobs[i].getException();
                    if (e == null) continue;
                    String message = JaiI18N.getString("SunTileScheduler7");
                    this.sendExceptionToListener(message, new ImagingException(message, e));
                }
            }
        } else {
            Exception e;
            Request request = null;
            if (!isBlocking && !isPrefetch) {
                request = new Request(this, owner, tileIndices, listeners);
                returnValue = request;
            }
            if ((e = this.compute(owner, tileIndices, tiles, 0, numTiles, request)) != null) {
                String message = JaiI18N.getString("SunTileScheduler7");
                this.sendExceptionToListener(message, new ImagingException(message, e));
            }
        }
        return returnValue;
    }

    public Raster[] scheduleTiles(OpImage owner, Point[] tileIndices) {
        if (owner == null || tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler0"));
        }
        return (Raster[])this.scheduleJob(owner, tileIndices, true, false, null);
    }

    public TileRequest scheduleTiles(PlanarImage target, Point[] tileIndices, TileComputationListener[] tileListeners) {
        if (target == null || tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler4"));
        }
        return (TileRequest)this.scheduleJob(target, tileIndices, false, false, tileListeners);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancelTiles(TileRequest request, Point[] tileIndices) {
        if (request == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler3"));
        }
        Request req = (Request)request;
        Map map = this.tileRequests;
        synchronized (map) {
            Point[] indices;
            List reqIndexList = req.indices;
            if (tileIndices != null && tileIndices.length > 0) {
                List<Point> tileIndexList = Arrays.asList(tileIndices);
                tileIndexList.retainAll(reqIndexList);
                indices = tileIndexList.toArray(new Point[0]);
            } else {
                indices = reqIndexList.toArray(new Point[0]);
            }
            int numTiles = indices.length;
            Integer tileStatus = new Integer(3);
            for (int i = 0; i < numTiles; ++i) {
                Point p = indices[i];
                Object tileID = SunTileScheduler.tileKey(req.image, p.x, p.y);
                List reqList = (List)this.tileRequests.get(tileID);
                if (reqList == null) continue;
                reqList.remove(req);
                if (reqList.isEmpty()) {
                    LinkedList linkedList = this.queue;
                    synchronized (linkedList) {
                        Object job = this.tileJobs.remove(tileID);
                        if (job != null) {
                            this.queue.remove(job);
                        }
                    }
                    this.tileRequests.remove(tileID);
                }
                req.tileStatus.put(p, tileStatus);
                if (req.listeners == null) continue;
                TileRequest[] reqArray = new TileRequest[]{req};
                Iterator iter = req.listeners.iterator();
                while (iter.hasNext()) {
                    TileComputationListener listener = (TileComputationListener)iter.next();
                    listener.tileCancelled(this, reqArray, req.image, p.x, p.y);
                }
            }
        }
    }

    public void prefetchTiles(PlanarImage owner, Point[] tileIndices) {
        if (owner == null || tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler0"));
        }
        this.scheduleJob(owner, tileIndices, false, true, null);
    }

    public void setParallelism(int parallelism) {
        if (parallelism < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler2"));
        }
        this.parallelism = parallelism;
    }

    public int getParallelism() {
        return this.parallelism;
    }

    public void setPrefetchParallelism(int parallelism) {
        if (parallelism < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler2"));
        }
        this.prefetchParallelism = parallelism;
    }

    public int getPrefetchParallelism() {
        return this.prefetchParallelism;
    }

    public void setPriority(int priority) {
        this.priority = Math.max(Math.min(priority, 10), 1);
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPrefetchPriority(int priority) {
        this.prefetchPriority = Math.max(Math.min(priority, 10), 1);
    }

    public int getPrefetchPriority() {
        return this.prefetchPriority;
    }

    private void createThreadGroup(boolean isPrefetch) {
        if (this.rootGroup == null || this.rootGroup.isDestroyed()) {
            this.rootGroup = new ThreadGroup(this.nameOfThisInstance);
            this.rootGroup.setDaemon(true);
        }
        if (isPrefetch && (this.prefetchGroup == null || this.prefetchGroup.isDestroyed())) {
            this.prefetchGroup = new ThreadGroup(this.rootGroup, this.nameOfThisInstance + "Prefetch");
            this.prefetchGroup.setDaemon(true);
        }
        if (!isPrefetch && (this.standardGroup == null || this.standardGroup.isDestroyed())) {
            this.standardGroup = new ThreadGroup(this.rootGroup, this.nameOfThisInstance + "Standard");
            this.standardGroup.setDaemon(true);
        }
        Vector thr = this.getWorkers(isPrefetch);
        int size = thr.size();
        for (int i = size - 1; i >= 0; --i) {
            Thread t = (Thread)thr.get(i);
            if (t.isAlive()) continue;
            thr.remove(t);
        }
        if (isPrefetch) {
            this.numPrefetchThreads = thr.size();
        } else {
            this.numWorkerThreads = thr.size();
        }
    }

    private int getNumThreads(boolean isPrefetch) {
        int prty;
        int prll;
        int nthr;
        this.createThreadGroup(isPrefetch);
        Vector thr = this.getWorkers(isPrefetch);
        if (isPrefetch) {
            nthr = this.numPrefetchThreads;
            prll = this.prefetchParallelism;
            prty = this.prefetchPriority;
        } else {
            nthr = this.numWorkerThreads;
            prll = this.parallelism;
            prty = this.priority;
        }
        if (nthr > 0 && ((Thread)thr.get(0)).getPriority() != prty) {
            int size = thr.size();
            for (int i = 0; i < size; ++i) {
                Thread t = (Thread)thr.get(i);
                if (t == null || t.getThreadGroup() == null) continue;
                t.setPriority(prty);
            }
        }
        if (nthr < prll) {
            while (nthr < prll) {
                WorkerThread t = new WorkerThread(isPrefetch ? this.prefetchGroup : this.standardGroup, this, isPrefetch);
                t.setPriority(prty);
                thr.add(t);
                ++nthr;
            }
        } else {
            while (nthr > prll) {
                this.addJob(WorkerThread.TERMINATE, isPrefetch);
                --nthr;
            }
        }
        if (isPrefetch) {
            this.numPrefetchThreads = nthr;
        } else {
            this.numWorkerThreads = nthr;
        }
        return nthr;
    }

    Vector getWorkers(boolean isPrefetch) {
        return isPrefetch ? this.workers : this.prefetchWorkers;
    }

    LinkedList getQueue(boolean isPrefetch) {
        return isPrefetch ? this.prefetchQueue : this.queue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addJob(Object job, boolean isPrefetch) {
        LinkedList jobQueue;
        if (job == null || job != WorkerThread.TERMINATE && !(job instanceof Job)) {
            throw new IllegalArgumentException();
        }
        LinkedList linkedList = jobQueue = this.getQueue(isPrefetch);
        synchronized (jobQueue) {
            if (isPrefetch || jobQueue.isEmpty() || job instanceof RequestJob) {
                jobQueue.addLast(job);
            } else {
                boolean inserted = false;
                for (int idx = jobQueue.size() - 1; idx >= 0; --idx) {
                    if (!(jobQueue.get(idx) instanceof TileJob)) continue;
                    jobQueue.add(idx + 1, job);
                    inserted = true;
                    break;
                }
                if (!inserted) {
                    jobQueue.addFirst(job);
                }
            }
            jobQueue.notify();
            // ** MonitorExit[var4_4] (shouldn't be in output)
            return;
        }
    }

    protected void finalize() throws Throwable {
        this.terminateAll(false);
        this.terminateAll(true);
        super.finalize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void terminateAll(boolean isPrefetch) {
        Vector vector = this.getWorkers(isPrefetch);
        synchronized (vector) {
            int numThreads = isPrefetch ? this.numPrefetchThreads : this.numWorkerThreads;
            for (int i = 0; i < numThreads; ++i) {
                this.addJob(WorkerThread.TERMINATE, isPrefetch);
                if (isPrefetch) {
                    --this.numPrefetchThreads;
                    continue;
                }
                --this.numWorkerThreads;
            }
        }
    }

    void sendExceptionToListener(String message, Throwable e) {
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
        listener.errorOccurred(message, e, this, false);
    }
}

