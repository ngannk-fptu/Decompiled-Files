/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import org.apache.batik.ext.awt.image.rendered.LRUCache;

public class TileLRUMember
implements LRUCache.LRUObj {
    private static final boolean DEBUG = false;
    protected LRUCache.LRUNode myNode = null;
    protected Reference wRaster = null;
    protected Raster hRaster = null;

    public TileLRUMember() {
    }

    public TileLRUMember(Raster ras) {
        this.setRaster(ras);
    }

    public void setRaster(Raster ras) {
        this.hRaster = ras;
        this.wRaster = new SoftReference<Raster>(ras);
    }

    public boolean checkRaster() {
        if (this.hRaster != null) {
            return true;
        }
        return this.wRaster != null && this.wRaster.get() != null;
    }

    public Raster retrieveRaster() {
        if (this.hRaster != null) {
            return this.hRaster;
        }
        if (this.wRaster == null) {
            return null;
        }
        this.hRaster = (Raster)this.wRaster.get();
        if (this.hRaster == null) {
            this.wRaster = null;
        }
        return this.hRaster;
    }

    @Override
    public LRUCache.LRUNode lruGet() {
        return this.myNode;
    }

    @Override
    public void lruSet(LRUCache.LRUNode nde) {
        this.myNode = nde;
    }

    @Override
    public void lruRemove() {
        this.myNode = null;
        this.hRaster = null;
    }
}

