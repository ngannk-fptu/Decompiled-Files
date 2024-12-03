/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;
import javax.media.jai.TileScheduler;

class Request
implements TileRequest {
    private final TileScheduler scheduler;
    final PlanarImage image;
    final List indices;
    final Set listeners;
    final Hashtable tileStatus;

    Request(TileScheduler scheduler, PlanarImage image, Point[] tileIndices, TileComputationListener[] tileListeners) {
        if (scheduler == null) {
            throw new IllegalArgumentException();
        }
        this.scheduler = scheduler;
        if (image == null) {
            throw new IllegalArgumentException();
        }
        this.image = image;
        if (tileIndices == null || tileIndices.length == 0) {
            throw new IllegalArgumentException();
        }
        this.indices = Arrays.asList(tileIndices);
        if (tileListeners != null) {
            int numListeners = tileListeners.length;
            if (numListeners > 0) {
                this.listeners = new HashSet(numListeners);
                for (int i = 0; i < numListeners; ++i) {
                    this.listeners.add(tileListeners[i]);
                }
            } else {
                this.listeners = null;
            }
        } else {
            this.listeners = null;
        }
        this.tileStatus = new Hashtable(tileIndices.length);
    }

    public PlanarImage getImage() {
        return this.image;
    }

    public Point[] getTileIndices() {
        return this.indices.toArray(new Point[0]);
    }

    public TileComputationListener[] getTileListeners() {
        return this.listeners.toArray(new TileComputationListener[0]);
    }

    public boolean isStatusAvailable() {
        return true;
    }

    public int getTileStatus(int tileX, int tileY) {
        Point p = new Point(tileX, tileY);
        int status = this.tileStatus.containsKey(p) ? (Integer)this.tileStatus.get(p) : 0;
        return status;
    }

    public void cancelTiles(Point[] tileIndices) {
        this.scheduler.cancelTiles(this, tileIndices);
    }
}

