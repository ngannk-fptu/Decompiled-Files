/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;

public interface TileScheduler {
    public Raster scheduleTile(OpImage var1, int var2, int var3);

    public Raster[] scheduleTiles(OpImage var1, Point[] var2);

    public TileRequest scheduleTiles(PlanarImage var1, Point[] var2, TileComputationListener[] var3);

    public void cancelTiles(TileRequest var1, Point[] var2);

    public void prefetchTiles(PlanarImage var1, Point[] var2);

    public void setParallelism(int var1);

    public int getParallelism();

    public void setPrefetchParallelism(int var1);

    public int getPrefetchParallelism();

    public void setPriority(int var1);

    public int getPriority();

    public void setPrefetchPriority(int var1);

    public int getPrefetchPriority();
}

