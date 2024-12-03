/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.WritableRaster;
import javax.media.jai.BorderExtender;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;

public final class BorderExtenderConstant
extends BorderExtender {
    private double[] constants;

    public BorderExtenderConstant(double[] constants) {
        this.constants = constants;
    }

    /*
     * WARNING - void declaration
     */
    private int clamp(int band, int min, int max) {
        void var5_5;
        double c;
        int length = this.constants.length;
        if (length == 1) {
            c = this.constants[0];
        } else if (band < length) {
            c = this.constants[band];
        } else {
            throw new UnsupportedOperationException(JaiI18N.getString("BorderExtenderConstant0"));
        }
        return var5_5 > (double)min ? (var5_5 > (double)max ? max : (int)var5_5) : min;
    }

    public final double[] getConstants() {
        return this.constants;
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
        int dataType = raster.getSampleModel().getDataType();
        if (dataType == 4) {
            float[] fBandData = new float[numBands];
            for (int b = 0; b < numBands; ++b) {
                fBandData[b] = b < this.constants.length ? (float)this.constants[b] : 0.0f;
            }
            float[] fData = new float[width * numBands];
            int index = 0;
            for (int i = 0; i < width; ++i) {
                for (int b = 0; b < numBands; ++b) {
                    fData[index++] = fBandData[b];
                }
            }
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
        } else if (dataType == 5) {
            double[] dBandData = new double[numBands];
            for (int b = 0; b < numBands; ++b) {
                dBandData[b] = b < this.constants.length ? this.constants[b] : 0.0;
            }
            double[] dData = new double[width * numBands];
            int index = 0;
            for (int i = 0; i < width; ++i) {
                for (int b = 0; b < numBands; ++b) {
                    dData[index++] = dBandData[b];
                }
            }
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
        } else {
            int[] iBandData = new int[numBands];
            switch (dataType) {
                case 0: {
                    int b;
                    for (b = 0; b < numBands; ++b) {
                        iBandData[b] = this.clamp(b, 0, 255);
                    }
                    break;
                }
                case 2: {
                    int b;
                    for (b = 0; b < numBands; ++b) {
                        iBandData[b] = this.clamp(b, Short.MIN_VALUE, Short.MAX_VALUE);
                    }
                    break;
                }
                case 1: {
                    int b;
                    for (b = 0; b < numBands; ++b) {
                        iBandData[b] = this.clamp(b, 0, 65535);
                    }
                    break;
                }
                case 3: {
                    int b;
                    for (b = 0; b < numBands; ++b) {
                        iBandData[b] = this.clamp(b, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
                }
            }
            int[] iData = new int[width * numBands];
            int index = 0;
            for (int i = 0; i < width; ++i) {
                for (int b = 0; b < numBands; ++b) {
                    iData[index++] = iBandData[b];
                }
            }
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
        }
    }
}

