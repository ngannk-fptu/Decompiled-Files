/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;

final class BandCombineOpImage
extends PointOpImage {
    private double[][] matrix;

    public BandCombineOpImage(RenderedImage source, Map config, ImageLayout layout, double[][] matrix) {
        super(source, layout, config, true);
        this.matrix = matrix;
        int numBands = matrix.length;
        if (this.getSampleModel().getNumBands() != numBands) {
            this.sampleModel = RasterFactory.createComponentSampleModel(this.sampleModel, this.sampleModel.getDataType(), this.tileWidth, this.tileHeight, numBands);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        RasterFormatTag[] formatTags = this.getFormatTags();
        RasterAccessor s = new RasterAccessor(sources[0], destRect, formatTags[0], this.getSourceImage(0).getColorModel());
        RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[1], this.getColorModel());
        switch (d.getDataType()) {
            case 0: {
                this.computeRectByte(s, d);
                break;
            }
            case 1: {
                this.computeRectUShort(s, d);
                break;
            }
            case 2: {
                this.computeRectShort(s, d);
                break;
            }
            case 3: {
                this.computeRectInt(s, d);
                break;
            }
            case 4: {
                this.computeRectFloat(s, d);
                break;
            }
            case 5: {
                this.computeRectDouble(s, d);
            }
        }
        if (d.isDataCopy()) {
            d.clampDataArrays();
            d.copyDataToRaster();
        }
    }

    private void computeRectByte(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int sbands = src.getNumBands();
        int[] sBandOffsets = src.getBandOffsets();
        byte[][] sData = src.getByteDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dbands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        byte[][] dData = dst.getByteDataArrays();
        int sso = 0;
        int dso = 0;
        for (int h = 0; h < dheight; ++h) {
            int spo = sso;
            int dpo = dso;
            for (int w = 0; w < dwidth; ++w) {
                for (int b = 0; b < dbands; ++b) {
                    float sum = 0.0f;
                    double[] mat = this.matrix[b];
                    for (int k = 0; k < sbands; ++k) {
                        sum += (float)mat[k] * (float)(sData[k][spo + sBandOffsets[k]] & 0xFF);
                    }
                    dData[b][dpo + dBandOffsets[b]] = ImageUtil.clampRoundByte(sum + (float)mat[sbands]);
                }
                spo += sPixelStride;
                dpo += dPixelStride;
            }
            sso += sLineStride;
            dso += dLineStride;
        }
    }

    private void computeRectUShort(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int sbands = src.getNumBands();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dbands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();
        int sso = 0;
        int dso = 0;
        for (int h = 0; h < dheight; ++h) {
            int spo = sso;
            int dpo = dso;
            for (int w = 0; w < dwidth; ++w) {
                for (int b = 0; b < dbands; ++b) {
                    float sum = 0.0f;
                    double[] mat = this.matrix[b];
                    for (int k = 0; k < sbands; ++k) {
                        sum += (float)mat[k] * (float)(sData[k][spo + sBandOffsets[k]] & 0xFFFF);
                    }
                    dData[b][dpo + dBandOffsets[b]] = ImageUtil.clampRoundUShort(sum + (float)this.matrix[b][sbands]);
                }
                spo += sPixelStride;
                dpo += dPixelStride;
            }
            sso += sLineStride;
            dso += dLineStride;
        }
    }

    private void computeRectShort(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int sbands = src.getNumBands();
        int[] sBandOffsets = src.getBandOffsets();
        short[][] sData = src.getShortDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dbands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        short[][] dData = dst.getShortDataArrays();
        int sso = 0;
        int dso = 0;
        for (int h = 0; h < dheight; ++h) {
            int spo = sso;
            int dpo = dso;
            for (int w = 0; w < dwidth; ++w) {
                for (int b = 0; b < dbands; ++b) {
                    float sum = 0.0f;
                    double[] mat = this.matrix[b];
                    for (int k = 0; k < sbands; ++k) {
                        sum += (float)mat[k] * (float)sData[k][spo + sBandOffsets[k]];
                    }
                    dData[b][dpo + dBandOffsets[b]] = ImageUtil.clampRoundUShort(sum + (float)this.matrix[b][sbands]);
                }
                spo += sPixelStride;
                dpo += dPixelStride;
            }
            sso += sLineStride;
            dso += dLineStride;
        }
    }

    private void computeRectInt(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int sbands = src.getNumBands();
        int[] sBandOffsets = src.getBandOffsets();
        int[][] sData = src.getIntDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dbands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        int[][] dData = dst.getIntDataArrays();
        int sso = 0;
        int dso = 0;
        for (int h = 0; h < dheight; ++h) {
            int spo = sso;
            int dpo = dso;
            for (int w = 0; w < dwidth; ++w) {
                for (int b = 0; b < dbands; ++b) {
                    float sum = 0.0f;
                    double[] mat = this.matrix[b];
                    for (int k = 0; k < sbands; ++k) {
                        sum += (float)mat[k] * (float)sData[k][spo + sBandOffsets[k]];
                    }
                    dData[b][dpo + dBandOffsets[b]] = ImageUtil.clampRoundInt(sum + (float)this.matrix[b][sbands]);
                }
                spo += sPixelStride;
                dpo += dPixelStride;
            }
            sso += sLineStride;
            dso += dLineStride;
        }
    }

    private void computeRectFloat(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int sbands = src.getNumBands();
        int[] sBandOffsets = src.getBandOffsets();
        float[][] sData = src.getFloatDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dbands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        float[][] dData = dst.getFloatDataArrays();
        int sso = 0;
        int dso = 0;
        for (int h = 0; h < dheight; ++h) {
            int spo = sso;
            int dpo = dso;
            for (int w = 0; w < dwidth; ++w) {
                for (int b = 0; b < dbands; ++b) {
                    float sum = 0.0f;
                    double[] mat = this.matrix[b];
                    for (int k = 0; k < sbands; ++k) {
                        sum += (float)mat[k] * sData[k][spo + sBandOffsets[k]];
                    }
                    dData[b][dpo + dBandOffsets[b]] = sum + (float)this.matrix[b][sbands];
                }
                spo += sPixelStride;
                dpo += dPixelStride;
            }
            sso += sLineStride;
            dso += dLineStride;
        }
    }

    private void computeRectDouble(RasterAccessor src, RasterAccessor dst) {
        int sLineStride = src.getScanlineStride();
        int sPixelStride = src.getPixelStride();
        int sbands = src.getNumBands();
        int[] sBandOffsets = src.getBandOffsets();
        double[][] sData = src.getDoubleDataArrays();
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int dbands = dst.getNumBands();
        int dLineStride = dst.getScanlineStride();
        int dPixelStride = dst.getPixelStride();
        int[] dBandOffsets = dst.getBandOffsets();
        double[][] dData = dst.getDoubleDataArrays();
        int sso = 0;
        int dso = 0;
        for (int h = 0; h < dheight; ++h) {
            int spo = sso;
            int dpo = dso;
            for (int w = 0; w < dwidth; ++w) {
                for (int b = 0; b < dbands; ++b) {
                    double sum = 0.0;
                    double[] mat = this.matrix[b];
                    for (int k = 0; k < sbands; ++k) {
                        sum += mat[k] * sData[k][spo + sBandOffsets[k]];
                    }
                    dData[b][dpo + dBandOffsets[b]] = sum + this.matrix[b][sbands];
                }
                spo += sPixelStride;
                dpo += dPixelStride;
            }
            sso += sLineStride;
            dso += dLineStride;
        }
    }
}

