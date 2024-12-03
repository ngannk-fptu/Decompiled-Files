/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.PNMEncodeParam;
import com.sun.media.jai.codecimpl.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class PNMImageEncoder
extends ImageEncoderImpl {
    private static final int PBM_ASCII = 49;
    private static final int PGM_ASCII = 50;
    private static final int PPM_ASCII = 51;
    private static final int PBM_RAW = 52;
    private static final int PGM_RAW = 53;
    private static final int PPM_RAW = 54;
    private static final int SPACE = 32;
    private static final String COMMENT = "# written by com.sun.media.jai.codecimpl.PNMImageEncoder";
    private byte[] lineSeparator;
    private int variant;
    private int maxValue;

    public PNMImageEncoder(OutputStream output, ImageEncodeParam param) {
        super(output, param);
        if (this.param == null) {
            this.param = new PNMEncodeParam();
        }
    }

    public void encode(RenderedImage im) throws IOException {
        boolean writeOptimal;
        boolean isPBMInverted;
        byte[] blues;
        byte[] greens;
        byte[] reds;
        int numBands;
        SampleModel sampleModel;
        int tileHeight;
        int height;
        int width;
        int minY;
        int minX;
        block61: {
            ComponentSampleModel csm;
            ColorModel colorModel;
            block60: {
                minX = im.getMinX();
                minY = im.getMinY();
                width = im.getWidth();
                height = im.getHeight();
                tileHeight = im.getTileHeight();
                sampleModel = im.getSampleModel();
                colorModel = im.getColorModel();
                String ls = AccessController.doPrivileged(new GetPropertyAction("line.separator"));
                this.lineSeparator = ls.getBytes();
                int dataType = sampleModel.getTransferType();
                if (dataType == 4 || dataType == 5) {
                    throw new RuntimeException(JaiI18N.getString("PNMImageEncoder0"));
                }
                int[] sampleSize = sampleModel.getSampleSize();
                numBands = sampleModel.getNumBands();
                reds = null;
                greens = null;
                blues = null;
                isPBMInverted = false;
                if (numBands == 1) {
                    if (colorModel instanceof IndexColorModel) {
                        IndexColorModel icm = (IndexColorModel)colorModel;
                        int mapSize = icm.getMapSize();
                        if (mapSize < 1 << sampleSize[0]) {
                            throw new RuntimeException(JaiI18N.getString("PNMImageEncoder1"));
                        }
                        if (sampleSize[0] == 1) {
                            this.variant = 52;
                            isPBMInverted = icm.getRed(1) + icm.getGreen(1) + icm.getBlue(1) > icm.getRed(0) + icm.getGreen(0) + icm.getBlue(0);
                        } else {
                            this.variant = 54;
                            reds = new byte[mapSize];
                            greens = new byte[mapSize];
                            blues = new byte[mapSize];
                            icm.getReds(reds);
                            icm.getGreens(greens);
                            icm.getBlues(blues);
                        }
                    } else {
                        this.variant = sampleSize[0] == 1 ? 52 : (sampleSize[0] <= 8 ? 53 : 50);
                    }
                } else if (numBands == 3) {
                    this.variant = sampleSize[0] <= 8 && sampleSize[1] <= 8 && sampleSize[2] <= 8 ? 54 : 51;
                } else {
                    throw new RuntimeException(JaiI18N.getString("PNMImageEncoder2"));
                }
                if (((PNMEncodeParam)this.param).getRaw()) {
                    if (!this.isRaw(this.variant)) {
                        boolean canUseRaw = true;
                        for (int i = 0; i < sampleSize.length; ++i) {
                            if (sampleSize[i] <= 8) continue;
                            canUseRaw = false;
                            break;
                        }
                        if (canUseRaw) {
                            this.variant += 3;
                        }
                    }
                } else if (this.isRaw(this.variant)) {
                    this.variant -= 3;
                }
                this.maxValue = (1 << sampleSize[0]) - 1;
                this.output.write(80);
                this.output.write(this.variant);
                this.output.write(this.lineSeparator);
                this.output.write(COMMENT.getBytes());
                this.output.write(this.lineSeparator);
                this.writeInteger(this.output, width);
                this.output.write(32);
                this.writeInteger(this.output, height);
                if (this.variant != 52 && this.variant != 49) {
                    this.output.write(this.lineSeparator);
                    this.writeInteger(this.output, this.maxValue);
                }
                if (this.variant == 52 || this.variant == 53 || this.variant == 54) {
                    this.output.write(10);
                }
                writeOptimal = false;
                if (this.variant != 52 || sampleModel.getTransferType() != 0 || !(sampleModel instanceof MultiPixelPackedSampleModel)) break block60;
                MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sampleModel;
                if (mppsm.getDataBitOffset() != 0 || mppsm.getPixelBitStride() != 1) break block61;
                writeOptimal = true;
                break block61;
            }
            if ((this.variant == 53 || this.variant == 54) && sampleModel instanceof ComponentSampleModel && !(colorModel instanceof IndexColorModel) && (csm = (ComponentSampleModel)sampleModel).getPixelStride() == numBands) {
                writeOptimal = true;
                if (this.variant == 54) {
                    int[] bandOffsets = csm.getBandOffsets();
                    for (int b = 0; b < numBands; ++b) {
                        if (bandOffsets[b] == b) continue;
                        writeOptimal = false;
                        break;
                    }
                }
            }
        }
        if (writeOptimal) {
            int bytesPerRow = this.variant == 52 ? (width + 7) / 8 : width * sampleModel.getNumBands();
            int numYTiles = im.getNumYTiles();
            Rectangle imageBounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
            Rectangle stripRect = new Rectangle(im.getMinX(), im.getMinTileY() * im.getTileHeight() + im.getTileGridYOffset(), im.getWidth(), im.getTileHeight());
            byte[] invertedData = null;
            if (isPBMInverted) {
                invertedData = new byte[bytesPerRow];
            }
            for (int j = 0; j < numYTiles; ++j) {
                int rowStride;
                if (j == numYTiles - 1) {
                    stripRect.height = im.getHeight() - stripRect.y;
                }
                Rectangle encodedRect = stripRect.intersection(imageBounds);
                Raster strip = im.getData(encodedRect);
                byte[] bdata = ((DataBufferByte)strip.getDataBuffer()).getData();
                int n = rowStride = this.variant == 52 ? ((MultiPixelPackedSampleModel)strip.getSampleModel()).getScanlineStride() : ((ComponentSampleModel)strip.getSampleModel()).getScanlineStride();
                if (rowStride == bytesPerRow && !isPBMInverted) {
                    this.output.write(bdata, 0, bdata.length);
                } else {
                    int offset = 0;
                    for (int i = 0; i < encodedRect.height; ++i) {
                        if (isPBMInverted) {
                            for (int k = 0; k < bytesPerRow; ++k) {
                                invertedData[k] = (byte)(~(bdata[offset + k] & 0xFF));
                            }
                            this.output.write(invertedData, 0, bytesPerRow);
                        } else {
                            this.output.write(bdata, offset, bytesPerRow);
                        }
                        offset += rowStride;
                    }
                }
                stripRect.y += tileHeight;
            }
            this.output.flush();
            return;
        }
        int[] pixels = new int[8 * width * numBands];
        byte[] bpixels = reds == null ? new byte[8 * width * numBands] : new byte[8 * width * 3];
        int count = 0;
        int lastRow = minY + height;
        block12: for (int row = minY; row < lastRow; row += 8) {
            int rows = Math.min(8, lastRow - row);
            int size = rows * width * numBands;
            Raster src = im.getData(new Rectangle(minX, row, width, rows));
            src.getPixels(minX, row, width, rows, pixels);
            if (isPBMInverted) {
                int k = 0;
                while (k < size) {
                    int n = k++;
                    pixels[n] = pixels[n] ^ 1;
                }
            }
            switch (this.variant) {
                case 49: 
                case 50: {
                    int i;
                    for (i = 0; i < size; ++i) {
                        if (count++ % 16 == 0) {
                            this.output.write(this.lineSeparator);
                        } else {
                            this.output.write(32);
                        }
                        this.writeInteger(this.output, pixels[i]);
                    }
                    this.output.write(this.lineSeparator);
                    continue block12;
                }
                case 51: {
                    int i;
                    if (reds == null) {
                        for (i = 0; i < size; ++i) {
                            if (count++ % 16 == 0) {
                                this.output.write(this.lineSeparator);
                            } else {
                                this.output.write(32);
                            }
                            this.writeInteger(this.output, pixels[i]);
                        }
                    } else {
                        for (i = 0; i < size; ++i) {
                            if (count++ % 16 == 0) {
                                this.output.write(this.lineSeparator);
                            } else {
                                this.output.write(32);
                            }
                            this.writeInteger(this.output, reds[pixels[i]] & 0xFF);
                            this.output.write(32);
                            this.writeInteger(this.output, greens[pixels[i]] & 0xFF);
                            this.output.write(32);
                            this.writeInteger(this.output, blues[pixels[i]] & 0xFF);
                        }
                    }
                    this.output.write(this.lineSeparator);
                    continue block12;
                }
                case 52: {
                    int i;
                    int kdst = 0;
                    int ksrc = 0;
                    for (i = 0; i < size / 8; ++i) {
                        int b = pixels[ksrc++] << 7 | pixels[ksrc++] << 6 | pixels[ksrc++] << 5 | pixels[ksrc++] << 4 | pixels[ksrc++] << 3 | pixels[ksrc++] << 2 | pixels[ksrc++] << 1 | pixels[ksrc++];
                        bpixels[kdst++] = (byte)b;
                    }
                    if (size % 8 > 0) {
                        int b = 0;
                        for (int i2 = 0; i2 < size % 8; ++i2) {
                            b |= pixels[size + i2] << 7 - i2;
                        }
                        bpixels[kdst++] = (byte)b;
                    }
                    this.output.write(bpixels, 0, (size + 7) / 8);
                    continue block12;
                }
                case 53: {
                    int i;
                    for (i = 0; i < size; ++i) {
                        bpixels[i] = (byte)pixels[i];
                    }
                    this.output.write(bpixels, 0, size);
                    continue block12;
                }
                case 54: {
                    int i;
                    if (reds == null) {
                        for (i = 0; i < size; ++i) {
                            bpixels[i] = (byte)(pixels[i] & 0xFF);
                        }
                    } else {
                        int j = 0;
                        for (i = 0; i < size; ++i) {
                            bpixels[j++] = reds[pixels[i]];
                            bpixels[j++] = greens[pixels[i]];
                            bpixels[j++] = blues[pixels[i]];
                        }
                    }
                    this.output.write(bpixels, 0, bpixels.length);
                }
            }
        }
        this.output.flush();
    }

    private void writeInteger(OutputStream output, int i) throws IOException {
        output.write(Integer.toString(i).getBytes());
    }

    private void writeByte(OutputStream output, byte b) throws IOException {
        output.write(Byte.toString(b).getBytes());
    }

    private boolean isRaw(int v) {
        return v >= 52;
    }
}

