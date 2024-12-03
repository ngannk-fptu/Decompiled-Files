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

public final class BorderExtenderCopy
extends BorderExtender {
    BorderExtenderCopy() {
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
        int size = Math.max(width, height);
        switch (raster.getSampleModel().getDataType()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                int row;
                int col;
                int[] iData = new int[size * numBands];
                if (minX < validMinX) {
                    rect.x = validMinX;
                    rect.y = validMinY;
                    rect.width = 1;
                    rect.height = validMaxY - validMinY;
                    if (rect.height > 0) {
                        Raster leftEdge = im.getData(rect);
                        leftEdge.getPixels(validMinX, validMinY, 1, rect.height, iData);
                        for (col = minX; col < validMinX; ++col) {
                            raster.setPixels(col, validMinY, 1, rect.height, iData);
                        }
                    }
                }
                if (validMaxX < maxX) {
                    rect.x = validMaxX - 1;
                    rect.y = validMinY;
                    rect.width = 1;
                    rect.height = validMaxY - validMinY;
                    if (rect.height > 0) {
                        Raster rightEdge = im.getData(rect);
                        rightEdge.getPixels(validMaxX - 1, validMinY, 1, rect.height, iData);
                        for (col = validMaxX; col < maxX; ++col) {
                            raster.setPixels(col, validMinY, 1, rect.height, iData);
                        }
                    }
                }
                if (minY < validMinY) {
                    rect.x = minX;
                    rect.y = validMinY;
                    rect.width = width;
                    rect.height = 1;
                    Raster topRow = im.getExtendedData(rect, this);
                    topRow.getPixels(minX, validMinY, width, 1, iData);
                    for (row = minY; row < validMinY; ++row) {
                        raster.setPixels(minX, row, width, 1, iData);
                    }
                }
                if (validMaxY >= maxY) break;
                rect.x = minX;
                rect.y = validMaxY - 1;
                rect.width = width;
                rect.height = 1;
                Raster bottomRow = im.getExtendedData(rect, this);
                bottomRow.getPixels(minX, validMaxY - 1, width, 1, iData);
                for (row = validMaxY; row < maxY; ++row) {
                    raster.setPixels(minX, row, width, 1, iData);
                }
                break;
            }
            case 4: {
                int row;
                int col;
                float[] fData = new float[size * numBands];
                if (minX < validMinX) {
                    rect.x = validMinX;
                    rect.y = validMinY;
                    rect.width = 1;
                    rect.height = validMaxY - validMinY;
                    if (rect.height > 0) {
                        Raster leftEdge = im.getData(rect);
                        leftEdge.getPixels(validMinX, validMinY, 1, rect.height, fData);
                        for (col = minX; col < validMinX; ++col) {
                            raster.setPixels(col, validMinY, 1, rect.height, fData);
                        }
                    }
                }
                if (validMaxX < maxX) {
                    rect.x = validMaxX - 1;
                    rect.y = validMinY;
                    rect.width = 1;
                    rect.height = validMaxY - validMinY;
                    if (rect.height > 0) {
                        Raster rightEdge = im.getData(rect);
                        rightEdge.getPixels(validMaxX - 1, validMinY, 1, rect.height, fData);
                        for (col = validMaxX; col < maxX; ++col) {
                            raster.setPixels(col, validMinY, 1, rect.height, fData);
                        }
                    }
                }
                if (minY < validMinY) {
                    rect.x = minX;
                    rect.y = validMinY;
                    rect.width = width;
                    rect.height = 1;
                    Raster topRow = im.getExtendedData(rect, this);
                    topRow.getPixels(minX, validMinY, width, 1, fData);
                    for (row = minY; row < validMinY; ++row) {
                        raster.setPixels(minX, row, width, 1, fData);
                    }
                }
                if (validMaxY >= maxY) break;
                rect.x = minX;
                rect.y = validMaxY - 1;
                rect.width = width;
                rect.height = 1;
                Raster bottomRow = im.getExtendedData(rect, this);
                bottomRow.getPixels(minX, validMaxY - 1, width, 1, fData);
                for (row = validMaxY; row < maxY; ++row) {
                    raster.setPixels(minX, row, width, 1, fData);
                }
                break;
            }
            case 5: {
                int row;
                int col;
                double[] dData = new double[size * numBands];
                if (minX < validMinX) {
                    rect.x = validMinX;
                    rect.y = validMinY;
                    rect.width = 1;
                    rect.height = validMaxY - validMinY;
                    if (rect.height > 0) {
                        Raster leftEdge = im.getData(rect);
                        leftEdge.getPixels(validMinX, validMinY, 1, rect.height, dData);
                        for (col = minX; col < validMinX; ++col) {
                            raster.setPixels(col, validMinY, 1, rect.height, dData);
                        }
                    }
                }
                if (validMaxX < maxX) {
                    rect.x = validMaxX - 1;
                    rect.y = validMinY;
                    rect.width = 1;
                    rect.height = validMaxY - validMinY;
                    if (rect.height > 0) {
                        Raster rightEdge = im.getData(rect);
                        rightEdge.getPixels(validMaxX - 1, validMinY, 1, rect.height, dData);
                        for (col = validMaxX; col < maxX; ++col) {
                            raster.setPixels(col, validMinY, 1, rect.height, dData);
                        }
                    }
                }
                if (minY < validMinY) {
                    rect.x = minX;
                    rect.y = validMinY;
                    rect.width = width;
                    rect.height = 1;
                    Raster topRow = im.getExtendedData(rect, this);
                    topRow.getPixels(minX, validMinY, width, 1, dData);
                    for (row = minY; row < validMinY; ++row) {
                        raster.setPixels(minX, row, width, 1, dData);
                    }
                }
                if (validMaxY >= maxY) break;
                rect.x = minX;
                rect.y = validMaxY - 1;
                rect.width = width;
                rect.height = 1;
                Raster bottomRow = im.getExtendedData(rect, this);
                bottomRow.getPixels(minX, validMaxY - 1, width, 1, dData);
                for (row = validMaxY; row < maxY; ++row) {
                    raster.setPixels(minX, row, width, 1, dData);
                }
                break;
            }
        }
    }
}

