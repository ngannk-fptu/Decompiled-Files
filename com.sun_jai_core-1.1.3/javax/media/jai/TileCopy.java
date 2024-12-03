/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.Raster;

final class TileCopy {
    Raster tile;
    int tileX;
    int tileY;

    TileCopy(Raster tile, int tileX, int tileY) {
        this.tile = tile;
        this.tileX = tileX;
        this.tileY = tileY;
    }
}

