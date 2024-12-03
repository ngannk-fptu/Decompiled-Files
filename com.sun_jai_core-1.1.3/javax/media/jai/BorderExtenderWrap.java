/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.WritableRaster;
import javax.media.jai.BorderExtender;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;

public class BorderExtenderWrap
extends BorderExtender {
    BorderExtenderWrap() {
    }

    public final void extend(WritableRaster raster, PlanarImage im) {
        if (raster == null || im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int width = raster.getWidth();
        int height = raster.getHeight();
        int minX = raster.getMinX();
        int maxX = minX + width;
        int minY = raster.getMinY();
        int maxY = minY + height;
        int imMinX = im.getMinX();
        int imMinY = im.getMinY();
        int imWidth = im.getWidth();
        int imHeight = im.getHeight();
        Rectangle rect = new Rectangle();
        int minTileX = PlanarImage.XToTileX(minX, imMinX, imWidth);
        int maxTileX = PlanarImage.XToTileX(maxX - 1, imMinX, imWidth);
        int minTileY = PlanarImage.YToTileY(minY, imMinY, imHeight);
        int maxTileY = PlanarImage.YToTileY(maxY - 1, imMinY, imHeight);
        for (int tileY = minTileY; tileY <= maxTileY; ++tileY) {
            int ty = tileY * imHeight + imMinY;
            for (int tileX = minTileX; tileX <= maxTileX; ++tileX) {
                int tx = tileX * imWidth + imMinX;
                if (tileX == 0 && tileY == 0) continue;
                rect.x = tx;
                rect.y = ty;
                rect.width = imWidth;
                rect.height = imHeight;
                int xOffset = 0;
                if (rect.x < minX) {
                    xOffset = minX - rect.x;
                    rect.x = minX;
                    rect.width -= xOffset;
                }
                int yOffset = 0;
                if (rect.y < minY) {
                    yOffset = minY - rect.y;
                    rect.y = minY;
                    rect.height -= yOffset;
                }
                if (rect.x + rect.width > maxX) {
                    rect.width = maxX - rect.x;
                }
                if (rect.y + rect.height > maxY) {
                    rect.height = maxY - rect.y;
                }
                WritableRaster child = RasterFactory.createWritableChild(raster, rect.x, rect.y, rect.width, rect.height, imMinX + xOffset, imMinY + yOffset, null);
                im.copyData(child);
            }
        }
    }
}

