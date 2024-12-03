/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileComputationListener;

public interface TileRequest {
    public static final int TILE_STATUS_PENDING = 0;
    public static final int TILE_STATUS_PROCESSING = 1;
    public static final int TILE_STATUS_COMPUTED = 2;
    public static final int TILE_STATUS_CANCELLED = 3;
    public static final int TILE_STATUS_FAILED = 4;

    public PlanarImage getImage();

    public Point[] getTileIndices();

    public TileComputationListener[] getTileListeners();

    public boolean isStatusAvailable();

    public int getTileStatus(int var1, int var2);

    public void cancelTiles(Point[] var1);
}

