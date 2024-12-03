/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import javax.media.jai.CachedTile;
import javax.media.jai.PlanarImage;
import javax.media.jai.remote.SerializableRenderedImage;

final class SunCachedTile
implements CachedTile {
    Raster tile;
    WeakReference owner;
    int tileX;
    int tileY;
    Object tileCacheMetric;
    long timeStamp;
    Object key;
    long memorySize;
    SunCachedTile previous;
    SunCachedTile next;
    int action = 0;

    SunCachedTile(RenderedImage owner, int tileX, int tileY, Raster tile, Object tileCacheMetric) {
        this.owner = new WeakReference<RenderedImage>(owner);
        this.tile = tile;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileCacheMetric = tileCacheMetric;
        this.key = SunCachedTile.hashKey(owner, tileX, tileY);
        DataBuffer db = tile.getDataBuffer();
        this.memorySize = (long)DataBuffer.getDataTypeSize(db.getDataType()) / 8L * (long)db.getSize() * (long)db.getNumBanks();
    }

    static Object hashKey(RenderedImage owner, int tileX, int tileY) {
        long idx = (long)tileY * (long)owner.getNumXTiles() + (long)tileX;
        BigInteger imageID = null;
        if (owner instanceof PlanarImage) {
            imageID = (BigInteger)((PlanarImage)owner).getImageID();
        } else if (owner instanceof SerializableRenderedImage) {
            imageID = (BigInteger)((SerializableRenderedImage)owner).getImageID();
        }
        if (imageID != null) {
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
        return new Long((long)owner.hashCode() << 32 | (idx &= 0xFFFFFFFFL));
    }

    public String toString() {
        RenderedImage o = this.getOwner();
        String ostring = o == null ? "null" : o.toString();
        Raster t = this.getTile();
        String tstring = t == null ? "null" : t.toString();
        return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + ": owner = " + ostring + " tileX = " + Integer.toString(this.tileX) + " tileY = " + Integer.toString(this.tileY) + " tile = " + tstring + " key = " + (this.key instanceof Long ? Long.toHexString((Long)this.key) : this.key.toString()) + " memorySize = " + Long.toString(this.memorySize) + " timeStamp = " + Long.toString(this.timeStamp);
    }

    public Raster getTile() {
        return this.tile;
    }

    public RenderedImage getOwner() {
        return (RenderedImage)this.owner.get();
    }

    public long getTileTimeStamp() {
        return this.timeStamp;
    }

    public Object getTileCacheMetric() {
        return this.tileCacheMetric;
    }

    public long getTileSize() {
        return this.memorySize;
    }

    public int getAction() {
        return this.action;
    }
}

