/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.SnapshotImage;
import javax.media.jai.TileCopy;

final class Snapshot
extends PlanarImage {
    SnapshotImage parent;
    Snapshot next;
    Snapshot prev;
    Hashtable tiles = new Hashtable();
    boolean disposed = false;

    Snapshot(SnapshotImage parent) {
        super(new ImageLayout(parent), null, null);
        this.parent = parent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Raster getTile(int tileX, int tileY) {
        SnapshotImage snapshotImage = this.parent;
        synchronized (snapshotImage) {
            TileCopy tc = (TileCopy)this.tiles.get(new Point(tileX, tileY));
            if (tc != null) {
                return tc.tile;
            }
            if (this.next != null) {
                return this.next.getTile(tileX, tileY);
            }
            return this.parent.getTrueSource().getTile(tileX, tileY);
        }
    }

    void setNext(Snapshot next) {
        this.next = next;
    }

    void setPrev(Snapshot prev) {
        this.prev = prev;
    }

    boolean hasTile(int tileX, int tileY) {
        TileCopy tc = (TileCopy)this.tiles.get(new Point(tileX, tileY));
        return tc != null;
    }

    void addTile(Raster tile, int tileX, int tileY) {
        TileCopy tc = new TileCopy(tile, tileX, tileY);
        this.tiles.put(new Point(tileX, tileY), tc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        SnapshotImage snapshotImage = this.parent;
        synchronized (snapshotImage) {
            if (this.disposed) {
                return;
            }
            this.disposed = true;
            if (this.parent.getTail() == this) {
                this.parent.setTail(this.prev);
            }
            if (this.prev != null) {
                this.prev.setNext(this.next);
            }
            if (this.next != null) {
                this.next.setPrev(this.prev);
            }
            if (this.prev != null) {
                Enumeration enumeration = this.tiles.elements();
                while (enumeration.hasMoreElements()) {
                    TileCopy tc = (TileCopy)enumeration.nextElement();
                    if (this.prev.hasTile(tc.tileX, tc.tileY)) continue;
                    this.prev.addTile(tc.tile, tc.tileX, tc.tileY);
                }
            }
            this.parent = null;
            this.prev = null;
            this.next = null;
            this.tiles = null;
        }
    }
}

