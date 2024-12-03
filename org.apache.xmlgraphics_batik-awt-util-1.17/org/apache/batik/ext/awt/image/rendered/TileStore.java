/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;

public interface TileStore {
    public void setTile(int var1, int var2, Raster var3);

    public Raster getTile(int var1, int var2);

    public Raster getTileNoCompute(int var1, int var2);
}

