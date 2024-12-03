/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.CacheDiagnostics;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JaiI18N;
import com.sun.media.jai.util.SunCachedTile;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.TileCache;
import javax.media.jai.util.ImagingListener;

public final class SunTileCache
extends Observable
implements TileCache,
CacheDiagnostics {
    private static final long DEFAULT_MEMORY_CAPACITY = 0x1000000L;
    private static final int DEFAULT_HASHTABLE_CAPACITY = 1009;
    private static final float LOAD_FACTOR = 0.5f;
    private Hashtable cache;
    private SortedSet cacheSortedSet;
    private long memoryCapacity;
    private long memoryUsage = 0L;
    private float memoryThreshold = 0.75f;
    private long timeStamp = 0L;
    private Comparator comparator = null;
    private SunCachedTile first = null;
    private SunCachedTile last = null;
    private long tileCount = 0L;
    private long hitCount = 0L;
    private long missCount = 0L;
    private boolean diagnostics = false;
    private static final int ADD = 0;
    private static final int REMOVE = 1;
    private static final int REMOVE_FROM_FLUSH = 2;
    private static final int REMOVE_FROM_MEMCON = 3;
    private static final int UPDATE_FROM_ADD = 4;
    private static final int UPDATE_FROM_GETTILE = 5;
    private static final int ABOUT_TO_REMOVE = 6;

    public static EnumeratedParameter[] getCachedTileActions() {
        return new EnumeratedParameter[]{new EnumeratedParameter("add", 0), new EnumeratedParameter("remove", 1), new EnumeratedParameter("remove_by_flush", 2), new EnumeratedParameter("remove_by_memorycontrol", 3), new EnumeratedParameter("timestamp_update_by_add", 4), new EnumeratedParameter("timestamp_update_by_gettile", 5), new EnumeratedParameter("preremove", 6)};
    }

    public SunTileCache() {
        this(0x1000000L);
    }

    public SunTileCache(long memoryCapacity) {
        if (memoryCapacity < 0L) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileCache"));
        }
        this.memoryCapacity = memoryCapacity;
        this.cache = new Hashtable(1009, 0.5f);
    }

    public void add(RenderedImage owner, int tileX, int tileY, Raster tile) {
        this.add(owner, tileX, tileY, tile, null);
    }

    public synchronized void add(RenderedImage owner, int tileX, int tileY, Raster tile, Object tileCacheMetric) {
        if (this.memoryCapacity == 0L) {
            return;
        }
        Object key = SunCachedTile.hashKey(owner, tileX, tileY);
        SunCachedTile ct = (SunCachedTile)this.cache.get(key);
        if (ct != null) {
            ct.timeStamp = this.timeStamp++;
            if (ct != this.first) {
                if (ct == this.last) {
                    this.last = ct.previous;
                    this.last.next = null;
                } else {
                    ct.previous.next = ct.next;
                    ct.next.previous = ct.previous;
                }
                ct.previous = null;
                ct.next = this.first;
                this.first.previous = ct;
                this.first = ct;
            }
            ++this.hitCount;
            if (this.diagnostics) {
                ct.action = 4;
                this.setChanged();
                this.notifyObservers(ct);
            }
        } else {
            ct = new SunCachedTile(owner, tileX, tileY, tile, tileCacheMetric);
            if (this.memoryUsage + ct.memorySize > this.memoryCapacity && ct.memorySize > (long)((float)this.memoryCapacity * this.memoryThreshold)) {
                return;
            }
            ct.timeStamp = this.timeStamp++;
            ct.previous = null;
            ct.next = this.first;
            if (this.first == null && this.last == null) {
                this.first = ct;
                this.last = ct;
            } else {
                this.first.previous = ct;
                this.first = ct;
            }
            if (this.cache.put(ct.key, ct) == null) {
                this.memoryUsage += ct.memorySize;
                ++this.tileCount;
                if (this.cacheSortedSet != null) {
                    this.cacheSortedSet.add(ct);
                }
                if (this.diagnostics) {
                    ct.action = 0;
                    this.setChanged();
                    this.notifyObservers(ct);
                }
            }
            if (this.memoryUsage > this.memoryCapacity) {
                this.memoryControl();
            }
        }
    }

    public synchronized void remove(RenderedImage owner, int tileX, int tileY) {
        if (this.memoryCapacity == 0L) {
            return;
        }
        Object key = SunCachedTile.hashKey(owner, tileX, tileY);
        SunCachedTile ct = (SunCachedTile)this.cache.get(key);
        if (ct != null) {
            ct.action = 6;
            this.setChanged();
            this.notifyObservers(ct);
            ct = (SunCachedTile)this.cache.remove(key);
            if (ct != null) {
                this.memoryUsage -= ct.memorySize;
                --this.tileCount;
                if (this.cacheSortedSet != null) {
                    this.cacheSortedSet.remove(ct);
                }
                if (ct == this.first) {
                    if (ct == this.last) {
                        this.first = null;
                        this.last = null;
                    } else {
                        this.first = ct.next;
                        this.first.previous = null;
                    }
                } else if (ct == this.last) {
                    this.last = ct.previous;
                    this.last.next = null;
                } else {
                    ct.previous.next = ct.next;
                    ct.next.previous = ct.previous;
                }
                if (this.diagnostics) {
                    ct.action = 1;
                    this.setChanged();
                    this.notifyObservers(ct);
                }
                ct.previous = null;
                ct.next = null;
                ct = null;
            }
        }
    }

    public synchronized Raster getTile(RenderedImage owner, int tileX, int tileY) {
        Raster tile = null;
        if (this.memoryCapacity == 0L) {
            return null;
        }
        Object key = SunCachedTile.hashKey(owner, tileX, tileY);
        SunCachedTile ct = (SunCachedTile)this.cache.get(key);
        if (ct == null) {
            ++this.missCount;
        } else {
            tile = ct.getTile();
            ct.timeStamp = this.timeStamp++;
            if (ct != this.first) {
                if (ct == this.last) {
                    this.last = ct.previous;
                    this.last.next = null;
                } else {
                    ct.previous.next = ct.next;
                    ct.next.previous = ct.previous;
                }
                ct.previous = null;
                ct.next = this.first;
                this.first.previous = ct;
                this.first = ct;
            }
            ++this.hitCount;
            if (this.diagnostics) {
                ct.action = 5;
                this.setChanged();
                this.notifyObservers(ct);
            }
        }
        return tile;
    }

    public synchronized Raster[] getTiles(RenderedImage owner) {
        Raster[] tiles = null;
        if (this.memoryCapacity == 0L) {
            return null;
        }
        int size = Math.min(owner.getNumXTiles() * owner.getNumYTiles(), (int)this.tileCount);
        if (size > 0) {
            int minTx = owner.getMinTileX();
            int minTy = owner.getMinTileY();
            int maxTx = minTx + owner.getNumXTiles();
            int maxTy = minTy + owner.getNumYTiles();
            Vector<Raster> temp = new Vector<Raster>(10, 20);
            for (int y = minTy; y < maxTy; ++y) {
                for (int x = minTx; x < maxTx; ++x) {
                    Raster raster = null;
                    Object key = SunCachedTile.hashKey(owner, x, y);
                    SunCachedTile ct = (SunCachedTile)this.cache.get(key);
                    if (ct == null) {
                        raster = null;
                        ++this.missCount;
                    } else {
                        raster = ct.getTile();
                        ++this.timeStamp;
                        ct.timeStamp = ct.timeStamp;
                        if (ct != this.first) {
                            if (ct == this.last) {
                                this.last = ct.previous;
                                this.last.next = null;
                            } else {
                                ct.previous.next = ct.next;
                                ct.next.previous = ct.previous;
                            }
                            ct.previous = null;
                            ct.next = this.first;
                            this.first.previous = ct;
                            this.first = ct;
                        }
                        ++this.hitCount;
                        if (this.diagnostics) {
                            ct.action = 5;
                            this.setChanged();
                            this.notifyObservers(ct);
                        }
                    }
                    if (raster == null) continue;
                    temp.add(raster);
                }
            }
            int tmpsize = temp.size();
            if (tmpsize > 0) {
                tiles = temp.toArray(new Raster[tmpsize]);
            }
        }
        return tiles;
    }

    public void removeTiles(RenderedImage owner) {
        if (this.memoryCapacity > 0L) {
            int minTx = owner.getMinTileX();
            int minTy = owner.getMinTileY();
            int maxTx = minTx + owner.getNumXTiles();
            int maxTy = minTy + owner.getNumYTiles();
            for (int y = minTy; y < maxTy; ++y) {
                for (int x = minTx; x < maxTx; ++x) {
                    this.remove(owner, x, y);
                }
            }
        }
    }

    public synchronized void addTiles(RenderedImage owner, Point[] tileIndices, Raster[] tiles, Object tileCacheMetric) {
        if (this.memoryCapacity == 0L) {
            return;
        }
        for (int i = 0; i < tileIndices.length; ++i) {
            int tileX = tileIndices[i].x;
            int tileY = tileIndices[i].y;
            Raster tile = tiles[i];
            Object key = SunCachedTile.hashKey(owner, tileX, tileY);
            SunCachedTile ct = (SunCachedTile)this.cache.get(key);
            if (ct != null) {
                ++this.timeStamp;
                ct.timeStamp = ct.timeStamp;
                if (ct != this.first) {
                    if (ct == this.last) {
                        this.last = ct.previous;
                        this.last.next = null;
                    } else {
                        ct.previous.next = ct.next;
                        ct.next.previous = ct.previous;
                    }
                    ct.previous = null;
                    ct.next = this.first;
                    this.first.previous = ct;
                    this.first = ct;
                }
                ++this.hitCount;
                if (!this.diagnostics) continue;
                ct.action = 4;
                this.setChanged();
                this.notifyObservers(ct);
                continue;
            }
            ct = new SunCachedTile(owner, tileX, tileY, tile, tileCacheMetric);
            if (this.memoryUsage + ct.memorySize > this.memoryCapacity && ct.memorySize > (long)((float)this.memoryCapacity * this.memoryThreshold)) {
                return;
            }
            ++this.timeStamp;
            ct.timeStamp = ct.timeStamp;
            ct.previous = null;
            ct.next = this.first;
            if (this.first == null && this.last == null) {
                this.first = ct;
                this.last = ct;
            } else {
                this.first.previous = ct;
                this.first = ct;
            }
            if (this.cache.put(ct.key, ct) == null) {
                this.memoryUsage += ct.memorySize;
                ++this.tileCount;
                if (this.cacheSortedSet != null) {
                    this.cacheSortedSet.add(ct);
                }
                if (this.diagnostics) {
                    ct.action = 0;
                    this.setChanged();
                    this.notifyObservers(ct);
                }
            }
            if (this.memoryUsage <= this.memoryCapacity) continue;
            this.memoryControl();
        }
    }

    public synchronized Raster[] getTiles(RenderedImage owner, Point[] tileIndices) {
        if (this.memoryCapacity == 0L) {
            return null;
        }
        Raster[] tiles = new Raster[tileIndices.length];
        for (int i = 0; i < tiles.length; ++i) {
            int tileX = tileIndices[i].x;
            int tileY = tileIndices[i].y;
            Object key = SunCachedTile.hashKey(owner, tileX, tileY);
            SunCachedTile ct = (SunCachedTile)this.cache.get(key);
            if (ct == null) {
                tiles[i] = null;
                ++this.missCount;
                continue;
            }
            tiles[i] = ct.getTile();
            ++this.timeStamp;
            ct.timeStamp = ct.timeStamp;
            if (ct != this.first) {
                if (ct == this.last) {
                    this.last = ct.previous;
                    this.last.next = null;
                } else {
                    ct.previous.next = ct.next;
                    ct.next.previous = ct.previous;
                }
                ct.previous = null;
                ct.next = this.first;
                this.first.previous = ct;
                this.first = ct;
            }
            ++this.hitCount;
            if (!this.diagnostics) continue;
            ct.action = 5;
            this.setChanged();
            this.notifyObservers(ct);
        }
        return tiles;
    }

    public synchronized void flush() {
        Enumeration keys = this.cache.keys();
        this.hitCount = 0L;
        this.missCount = 0L;
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            SunCachedTile ct = (SunCachedTile)this.cache.remove(key);
            if (ct == null) continue;
            this.memoryUsage -= ct.memorySize;
            --this.tileCount;
            if (ct == this.first) {
                if (ct == this.last) {
                    this.first = null;
                    this.last = null;
                } else {
                    this.first = ct.next;
                    this.first.previous = null;
                }
            } else if (ct == this.last) {
                this.last = ct.previous;
                this.last.next = null;
            } else {
                ct.previous.next = ct.next;
                ct.next.previous = ct.previous;
            }
            ct.previous = null;
            ct.next = null;
            if (!this.diagnostics) continue;
            ct.action = 2;
            this.setChanged();
            this.notifyObservers(ct);
        }
        if (this.memoryCapacity > 0L) {
            this.cache = new Hashtable(1009, 0.5f);
        }
        if (this.cacheSortedSet != null) {
            this.cacheSortedSet.clear();
            this.cacheSortedSet = Collections.synchronizedSortedSet(new TreeSet(this.comparator));
        }
        this.tileCount = 0L;
        this.timeStamp = 0L;
        this.memoryUsage = 0L;
    }

    public int getTileCapacity() {
        return 0;
    }

    public void setTileCapacity(int tileCapacity) {
    }

    public long getMemoryCapacity() {
        return this.memoryCapacity;
    }

    public void setMemoryCapacity(long memoryCapacity) {
        if (memoryCapacity < 0L) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileCache"));
        }
        if (memoryCapacity == 0L) {
            this.flush();
        }
        this.memoryCapacity = memoryCapacity;
        if (this.memoryUsage > memoryCapacity) {
            this.memoryControl();
        }
    }

    public void enableDiagnostics() {
        this.diagnostics = true;
    }

    public void disableDiagnostics() {
        this.diagnostics = false;
    }

    public long getCacheTileCount() {
        return this.tileCount;
    }

    public long getCacheMemoryUsed() {
        return this.memoryUsage;
    }

    public long getCacheHitCount() {
        return this.hitCount;
    }

    public long getCacheMissCount() {
        return this.missCount;
    }

    public void resetCounts() {
        this.hitCount = 0L;
        this.missCount = 0L;
    }

    public void setMemoryThreshold(float mt) {
        if (mt < 0.0f || mt > 1.0f) {
            throw new IllegalArgumentException(JaiI18N.getString("SunTileCache"));
        }
        this.memoryThreshold = mt;
        this.memoryControl();
    }

    public float getMemoryThreshold() {
        return this.memoryThreshold;
    }

    public String toString() {
        return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + ": memoryCapacity = " + Long.toHexString(this.memoryCapacity) + " memoryUsage = " + Long.toHexString(this.memoryUsage) + " #tilesInCache = " + Integer.toString(this.cache.size());
    }

    public Object getCachedObject() {
        return this.cache;
    }

    public synchronized void memoryControl() {
        if (this.cacheSortedSet == null) {
            this.standard_memory_control();
        } else {
            this.custom_memory_control();
        }
    }

    private final void standard_memory_control() {
        long limit = (long)((float)this.memoryCapacity * this.memoryThreshold);
        while (this.memoryUsage > limit && this.last != null) {
            SunCachedTile ct = (SunCachedTile)this.cache.get(this.last.key);
            if (ct == null) continue;
            ct = (SunCachedTile)this.cache.remove(this.last.key);
            this.memoryUsage -= this.last.memorySize;
            --this.tileCount;
            this.last = this.last.previous;
            if (this.last != null) {
                this.last.next.previous = null;
                this.last.next = null;
            } else {
                this.first = null;
            }
            if (!this.diagnostics) continue;
            ct.action = 3;
            this.setChanged();
            this.notifyObservers(ct);
        }
    }

    private final void custom_memory_control() {
        long limit = (long)((float)this.memoryCapacity * this.memoryThreshold);
        Iterator iter = this.cacheSortedSet.iterator();
        while (iter.hasNext() && this.memoryUsage > limit) {
            SunCachedTile ct;
            block12: {
                block10: {
                    block11: {
                        ct = (SunCachedTile)iter.next();
                        this.memoryUsage -= ct.memorySize;
                        --this.tileCount;
                        try {
                            iter.remove();
                        }
                        catch (ConcurrentModificationException e) {
                            ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
                            listener.errorOccurred(JaiI18N.getString("SunTileCache0"), e, this, false);
                        }
                        if (ct != this.first) break block10;
                        if (ct != this.last) break block11;
                        this.first = null;
                        this.last = null;
                        break block12;
                    }
                    this.first = ct.next;
                    if (this.first == null) break block12;
                    this.first.previous = null;
                    this.first.next = ct.next.next;
                    break block12;
                }
                if (ct == this.last) {
                    this.last = ct.previous;
                    if (this.last != null) {
                        this.last.next = null;
                        this.last.previous = ct.previous.previous;
                    }
                } else {
                    SunCachedTile ptr = this.first.next;
                    while (ptr != null) {
                        if (ptr == ct) {
                            if (ptr.previous != null) {
                                ptr.previous.next = ptr.next;
                            }
                            if (ptr.next == null) break;
                            ptr.next.previous = ptr.previous;
                            break;
                        }
                        ptr = ptr.next;
                    }
                }
            }
            this.cache.remove(ct.key);
            if (!this.diagnostics) continue;
            ct.action = 3;
            this.setChanged();
            this.notifyObservers(ct);
        }
        if (this.memoryUsage > limit) {
            this.standard_memory_control();
        }
    }

    public synchronized void setTileComparator(Comparator c) {
        this.comparator = c;
        if (this.comparator == null) {
            if (this.cacheSortedSet != null) {
                this.cacheSortedSet.clear();
                this.cacheSortedSet = null;
            }
        } else {
            this.cacheSortedSet = Collections.synchronizedSortedSet(new TreeSet(this.comparator));
            Enumeration keys = this.cache.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object ct = this.cache.get(key);
                this.cacheSortedSet.add(ct);
            }
        }
    }

    public Comparator getTileComparator() {
        return this.comparator;
    }

    public void dump() {
        System.out.println("first = " + this.first);
        System.out.println("last  = " + this.last);
        Iterator iter = this.cacheSortedSet.iterator();
        int k = 0;
        while (iter.hasNext()) {
            SunCachedTile ct = (SunCachedTile)iter.next();
            System.out.println(k++);
            System.out.println(ct);
        }
    }

    void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
        listener.errorOccurred(message, e, this, false);
    }
}

