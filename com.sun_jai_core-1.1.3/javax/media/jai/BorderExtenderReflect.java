/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.media.jai.BorderExtender;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;

public final class BorderExtenderReflect
extends BorderExtender {
    BorderExtenderReflect() {
    }

    private void flipX(WritableRaster raster) {
        int minX = raster.getMinX();
        int minY = raster.getMinY();
        int height = raster.getHeight();
        int width = raster.getWidth();
        int maxX = minX + width - 1;
        int numBands = raster.getNumBands();
        switch (raster.getSampleModel().getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                int[] iData0 = new int[height * numBands];
                int[] iData1 = new int[height * numBands];
                for (int i = 0; i < width / 2; ++i) {
                    raster.getPixels(minX + i, minY, 1, height, iData0);
                    raster.getPixels(maxX - i, minY, 1, height, iData1);
                    raster.setPixels(minX + i, minY, 1, height, iData1);
                    raster.setPixels(maxX - i, minY, 1, height, iData0);
                }
                break;
            }
            case 4: {
                float[] fData0 = new float[height * numBands];
                float[] fData1 = new float[height * numBands];
                for (int i = 0; i < width / 2; ++i) {
                    raster.getPixels(minX + i, minY, 1, height, fData0);
                    raster.getPixels(maxX - i, minY, 1, height, fData1);
                    raster.setPixels(minX + i, minY, 1, height, fData1);
                    raster.setPixels(maxX - i, minY, 1, height, fData0);
                }
                break;
            }
            case 5: {
                double[] dData0 = new double[height * numBands];
                double[] dData1 = new double[height * numBands];
                for (int i = 0; i < width / 2; ++i) {
                    raster.getPixels(minX + i, minY, 1, height, dData0);
                    raster.getPixels(maxX - i, minY, 1, height, dData1);
                    raster.setPixels(minX + i, minY, 1, height, dData1);
                    raster.setPixels(maxX - i, minY, 1, height, dData0);
                }
                break;
            }
        }
    }

    private void flipY(WritableRaster raster) {
        int minX = raster.getMinX();
        int minY = raster.getMinY();
        int height = raster.getHeight();
        int width = raster.getWidth();
        int maxY = minY + height - 1;
        int numBands = raster.getNumBands();
        switch (raster.getSampleModel().getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                int[] iData0 = new int[width * numBands];
                int[] iData1 = new int[width * numBands];
                for (int i = 0; i < height / 2; ++i) {
                    raster.getPixels(minX, minY + i, width, 1, iData0);
                    raster.getPixels(minX, maxY - i, width, 1, iData1);
                    raster.setPixels(minX, minY + i, width, 1, iData1);
                    raster.setPixels(minX, maxY - i, width, 1, iData0);
                }
                break;
            }
            case 4: {
                float[] fData0 = new float[width * numBands];
                float[] fData1 = new float[width * numBands];
                for (int i = 0; i < height / 2; ++i) {
                    raster.getPixels(minX, minY + i, width, 1, fData0);
                    raster.getPixels(minX, maxY - i, width, 1, fData1);
                    raster.setPixels(minX, minY + i, width, 1, fData1);
                    raster.setPixels(minX, maxY - i, width, 1, fData0);
                }
                break;
            }
            case 5: {
                double[] dData0 = new double[width * numBands];
                double[] dData1 = new double[width * numBands];
                for (int i = 0; i < height / 2; ++i) {
                    raster.getPixels(minX, minY + i, width, 1, dData0);
                    raster.getPixels(minX, maxY - i, width, 1, dData1);
                    raster.setPixels(minX, minY + i, width, 1, dData1);
                    raster.setPixels(minX, maxY - i, width, 1, dData0);
                }
                break;
            }
        }
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
        int validMinX = Math.max(imMinX, minX);
        int validMaxX = Math.min(imMinX + imWidth, maxX);
        int validMinY = Math.max(imMinY, minY);
        int validMaxY = Math.min(imMinY + imHeight, maxY);
        if (validMinX > validMaxX || validMinY > validMaxY) {
            if (validMinX > validMaxX) {
                if (minX == validMinX) {
                    minX = im.getMaxX() - 1;
                } else {
                    maxX = im.getMinX();
                }
            }
            if (validMinY > validMaxY) {
                if (minY == validMinY) {
                    minY = im.getMaxY() - 1;
                } else {
                    maxY = im.getMinY();
                }
            }
            WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY, maxX - minX, maxY - minY);
            this.extend(wr, im);
            Raster child = wr.createChild(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), raster.getMinX(), raster.getMinY(), null);
            JDKWorkarounds.setRect(raster, child, 0, 0);
            return;
        }
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
                boolean flipX = Math.abs(tileX) % 2 == 1;
                boolean flipY = Math.abs(tileY) % 2 == 1;
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
                int imX = flipX ? (xOffset == 0 ? imMinX + imWidth - rect.width : imMinX) : imMinX + xOffset;
                int imY = flipY ? (yOffset == 0 ? imMinY + imHeight - rect.height : imMinY) : imMinY + yOffset;
                WritableRaster child = RasterFactory.createWritableChild(raster, rect.x, rect.y, rect.width, rect.height, imX, imY, null);
                im.copyData(child);
                if (flipX) {
                    this.flipX(child);
                }
                if (!flipY) continue;
                this.flipY(child);
            }
        }
    }
}

