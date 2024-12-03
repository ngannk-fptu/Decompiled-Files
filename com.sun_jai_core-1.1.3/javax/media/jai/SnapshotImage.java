/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.util.HashSet;
import java.util.Iterator;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.Snapshot;
import javax.media.jai.SnapshotProxy;

public class SnapshotImage
extends PlanarImage
implements TileObserver {
    private PlanarImage source;
    private Snapshot tail = null;
    private HashSet activeTiles = new HashSet();

    public SnapshotImage(PlanarImage source) {
        super(new ImageLayout(source), null, null);
        this.source = source;
        if (source instanceof WritableRenderedImage) {
            WritableRenderedImage wri = (WritableRenderedImage)((Object)source);
            wri.addTileObserver(this);
            Point[] pts = wri.getWritableTileIndices();
            if (pts != null) {
                int num = pts.length;
                for (int i = 0; i < num; ++i) {
                    Point p = pts[i];
                    this.activeTiles.add(new Point(p.x, p.y));
                }
            }
        }
    }

    protected PlanarImage getTrueSource() {
        return this.source;
    }

    void setTail(Snapshot tail) {
        this.tail = tail;
    }

    Snapshot getTail() {
        return this.tail;
    }

    private Raster createTileCopy(int tileX, int tileY) {
        int x = this.tileXToX(tileX);
        int y = this.tileYToY(tileY);
        Point p = new Point(x, y);
        WritableRaster tile = RasterFactory.createWritableRaster(this.sampleModel, p);
        this.source.copyData(tile);
        return tile;
    }

    public PlanarImage createSnapshot() {
        if (this.source instanceof WritableRenderedImage) {
            Snapshot snap = new Snapshot(this);
            Iterator iter = this.activeTiles.iterator();
            while (iter.hasNext()) {
                Point p = (Point)iter.next();
                Raster tile = this.createTileCopy(p.x, p.y);
                snap.addTile(tile, p.x, p.y);
            }
            if (this.tail == null) {
                this.tail = snap;
            } else {
                this.tail.setNext(snap);
                snap.setPrev(this.tail);
                this.tail = snap;
            }
            return new SnapshotProxy(snap);
        }
        return this.source;
    }

    public void tileUpdate(WritableRenderedImage source, int tileX, int tileY, boolean willBeWritable) {
        if (willBeWritable) {
            if (this.tail != null && !this.tail.hasTile(tileX, tileY)) {
                this.tail.addTile(this.createTileCopy(tileX, tileY), tileX, tileY);
            }
            this.activeTiles.add(new Point(tileX, tileY));
        } else {
            this.activeTiles.remove(new Point(tileX, tileY));
        }
    }

    public Raster getTile(int tileX, int tileY) {
        return this.source.getTile(tileX, tileY);
    }
}

