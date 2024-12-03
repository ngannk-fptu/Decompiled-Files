/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.Raster;
import java.util.EventListener;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileRequest;

public interface TileComputationListener
extends EventListener {
    public void tileComputed(Object var1, TileRequest[] var2, PlanarImage var3, int var4, int var5, Raster var6);

    public void tileCancelled(Object var1, TileRequest[] var2, PlanarImage var3, int var4, int var5);

    public void tileComputationFailure(Object var1, TileRequest[] var2, PlanarImage var3, int var4, int var5, Throwable var6);
}

