/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.WritableRaster;
import javax.media.jai.BorderExtender;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;

public final class BorderExtenderZero
extends BorderExtender {
    BorderExtenderZero() {
    }

    public final void extend(WritableRaster raster, PlanarImage im) {
        if (raster == null || im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int width = raster.getWidth();
        int height = raster.getHeight();
        int numBands = raster.getNumBands();
        int minX = raster.getMinX();
        int maxX = minX + width;
        int minY = raster.getMinY();
        int maxY = minY + height;
        int validMinX = Math.max(im.getMinX(), minX);
        int validMaxX = Math.min(im.getMaxX(), maxX);
        int validMinY = Math.max(im.getMinY(), minY);
        int validMaxY = Math.min(im.getMaxY(), maxY);
        switch (raster.getSampleModel().getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                int[] iData = new int[width * numBands];
                if (validMinX > validMaxX || validMinY > validMaxY) {
                    for (int row = minY; row < maxY; ++row) {
                        raster.setPixels(minX, row, width, 1, iData);
                    }
                } else {
                    int row;
                    for (row = minY; row < validMinY; ++row) {
                        raster.setPixels(minX, row, width, 1, iData);
                    }
                    for (row = validMinY; row < validMaxY; ++row) {
                        if (minX < validMinX) {
                            raster.setPixels(minX, row, validMinX - minX, 1, iData);
                        }
                        if (validMaxX >= maxX) continue;
                        raster.setPixels(validMaxX, row, maxX - validMaxX, 1, iData);
                    }
                    for (row = validMaxY; row < maxY; ++row) {
                        raster.setPixels(minX, row, width, 1, iData);
                    }
                }
                break;
            }
            case 4: {
                float[] fData = new float[width * numBands];
                if (validMinX > validMaxX || validMinY > validMaxY) {
                    for (int row = minY; row < maxY; ++row) {
                        raster.setPixels(minX, row, width, 1, fData);
                    }
                } else {
                    int row;
                    for (row = minY; row < validMinY; ++row) {
                        raster.setPixels(minX, row, width, 1, fData);
                    }
                    for (row = validMinY; row < validMaxY; ++row) {
                        if (minX < validMinX) {
                            raster.setPixels(minX, row, validMinX - minX, 1, fData);
                        }
                        if (validMaxX >= maxX) continue;
                        raster.setPixels(validMaxX, row, maxX - validMaxX, 1, fData);
                    }
                    for (row = validMaxY; row < maxY; ++row) {
                        raster.setPixels(minX, row, width, 1, fData);
                    }
                }
                break;
            }
            case 5: {
                double[] dData = new double[width * numBands];
                if (validMinX > validMaxX || validMinY > validMaxY) {
                    for (int row = minY; row < maxY; ++row) {
                        raster.setPixels(minX, row, width, 1, dData);
                    }
                } else {
                    int row;
                    for (row = minY; row < validMinY; ++row) {
                        raster.setPixels(minX, row, width, 1, dData);
                    }
                    for (row = validMinY; row < validMaxY; ++row) {
                        if (minX < validMinX) {
                            raster.setPixels(minX, row, validMinX - minX, 1, dData);
                        }
                        if (validMaxX >= maxX) continue;
                        raster.setPixels(validMaxX, row, maxX - validMaxX, 1, dData);
                    }
                    for (row = validMaxY; row < maxY; ++row) {
                        raster.setPixels(minX, row, width, 1, dData);
                    }
                }
                break;
            }
        }
    }
}

