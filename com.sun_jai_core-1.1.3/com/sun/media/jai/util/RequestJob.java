/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.Job;
import com.sun.media.jai.util.Request;
import com.sun.media.jai.util.SunTileScheduler;
import java.awt.Point;
import java.awt.image.Raster;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;

final class RequestJob
implements Job {
    final SunTileScheduler scheduler;
    final PlanarImage owner;
    final int tileX;
    final int tileY;
    final Raster[] tiles;
    final int offset;
    boolean done = false;
    Exception exception = null;

    RequestJob(SunTileScheduler scheduler, PlanarImage owner, int tileX, int tileY, Raster[] tiles, int offset) {
        this.scheduler = scheduler;
        this.owner = owner;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tiles = tiles;
        this.offset = offset;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void compute() {
        block22: {
            Map map = this.scheduler.tileRequests;
            // MONITORENTER : map
            Object tileID = SunTileScheduler.tileKey(this.owner, this.tileX, this.tileY);
            List reqList = (List)this.scheduler.tileRequests.remove(tileID);
            this.scheduler.tileJobs.remove(tileID);
            // MONITOREXIT : map
            if (reqList != null && !reqList.isEmpty()) {
                Point p = new Point(this.tileX, this.tileY);
                Integer tileStatus = new Integer(1);
                Iterator reqIter = reqList.iterator();
                while (reqIter.hasNext()) {
                    Request r = (Request)reqIter.next();
                    r.tileStatus.put(p, tileStatus);
                }
                try {
                    try {
                        this.tiles[this.offset] = this.owner.getTile(this.tileX, this.tileY);
                    }
                    catch (Exception e) {
                        this.exception = e;
                        Object var7_8 = null;
                        int numReq = reqList.size();
                        Set listeners = SunTileScheduler.getListeners(reqList);
                        if (listeners == null || listeners.isEmpty()) break block22;
                        TileRequest[] requests = reqList.toArray(new TileRequest[0]);
                        tileStatus = new Integer(this.exception == null ? 2 : 4);
                        for (int i = 0; i < numReq; ++i) {
                            ((Request)requests[i]).tileStatus.put(p, tileStatus);
                        }
                        Iterator iter = listeners.iterator();
                        if (this.exception == null) {
                            while (iter.hasNext()) {
                                TileComputationListener listener = (TileComputationListener)iter.next();
                                listener.tileComputed(this.scheduler, requests, this.owner, this.tileX, this.tileY, this.tiles[this.offset]);
                            }
                            break block22;
                        } else {
                            while (iter.hasNext()) {
                                TileComputationListener listener = (TileComputationListener)iter.next();
                                listener.tileComputationFailure(this.scheduler, requests, this.owner, this.tileX, this.tileY, this.exception);
                            }
                        }
                    }
                    Object var7_7 = null;
                    int numReq = reqList.size();
                    Set listeners = SunTileScheduler.getListeners(reqList);
                    if (listeners == null || listeners.isEmpty()) break block22;
                    TileRequest[] requests = reqList.toArray(new TileRequest[0]);
                    tileStatus = new Integer(this.exception == null ? 2 : 4);
                    for (int i = 0; i < numReq; ++i) {
                        ((Request)requests[i]).tileStatus.put(p, tileStatus);
                    }
                    Iterator iter = listeners.iterator();
                    if (this.exception == null) {
                        while (iter.hasNext()) {
                            TileComputationListener listener = (TileComputationListener)iter.next();
                            listener.tileComputed(this.scheduler, requests, this.owner, this.tileX, this.tileY, this.tiles[this.offset]);
                        }
                    } else {
                        while (iter.hasNext()) {
                            TileComputationListener listener = (TileComputationListener)iter.next();
                            listener.tileComputationFailure(this.scheduler, requests, this.owner, this.tileX, this.tileY, this.exception);
                        }
                    }
                }
                catch (Throwable throwable) {
                    Object var7_9 = null;
                    int numReq = reqList.size();
                    Set listeners = SunTileScheduler.getListeners(reqList);
                    if (listeners == null) throw throwable;
                    if (listeners.isEmpty()) throw throwable;
                    TileRequest[] requests = reqList.toArray(new TileRequest[0]);
                    tileStatus = new Integer(this.exception == null ? 2 : 4);
                    for (int i = 0; i < numReq; ++i) {
                        ((Request)requests[i]).tileStatus.put(p, tileStatus);
                    }
                    Iterator iter = listeners.iterator();
                    if (this.exception == null) {
                        while (iter.hasNext()) {
                            TileComputationListener listener = (TileComputationListener)iter.next();
                            listener.tileComputed(this.scheduler, requests, this.owner, this.tileX, this.tileY, this.tiles[this.offset]);
                        }
                        throw throwable;
                    }
                    while (iter.hasNext()) {
                        TileComputationListener listener = (TileComputationListener)iter.next();
                        listener.tileComputationFailure(this.scheduler, requests, this.owner, this.tileX, this.tileY, this.exception);
                    }
                    throw throwable;
                }
            }
        }
        this.done = true;
    }

    public boolean notDone() {
        return !this.done;
    }

    public PlanarImage getOwner() {
        return this.owner;
    }

    public boolean isBlocking() {
        return false;
    }

    public Exception getException() {
        return this.exception;
    }

    public String toString() {
        String tString = "null";
        if (this.tiles[this.offset] != null) {
            tString = this.tiles[this.offset].toString();
        }
        return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + ": owner = " + this.owner.toString() + " tileX = " + Integer.toString(this.tileX) + " tileY = " + Integer.toString(this.tileY) + " tile = " + tString;
    }
}

