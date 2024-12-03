/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.Job;
import com.sun.media.jai.util.SunTileScheduler;
import java.awt.Point;
import java.awt.image.Raster;
import javax.media.jai.PlanarImage;

final class TileJob
implements Job {
    final SunTileScheduler scheduler;
    final boolean isBlocking;
    final PlanarImage owner;
    final Point[] tileIndices;
    final Raster[] tiles;
    final int offset;
    final int numTiles;
    boolean done = false;
    Exception exception = null;

    TileJob(SunTileScheduler scheduler, boolean isBlocking, PlanarImage owner, Point[] tileIndices, Raster[] tiles, int offset, int numTiles) {
        this.scheduler = scheduler;
        this.isBlocking = isBlocking;
        this.owner = owner;
        this.tileIndices = tileIndices;
        this.tiles = tiles;
        this.offset = offset;
        this.numTiles = numTiles;
    }

    public void compute() {
        this.exception = this.scheduler.compute(this.owner, this.tileIndices, this.tiles, this.offset, this.numTiles, null);
        this.done = true;
    }

    public boolean notDone() {
        return !this.done;
    }

    public PlanarImage getOwner() {
        return this.owner;
    }

    public boolean isBlocking() {
        return this.isBlocking;
    }

    public Exception getException() {
        return this.exception;
    }
}

