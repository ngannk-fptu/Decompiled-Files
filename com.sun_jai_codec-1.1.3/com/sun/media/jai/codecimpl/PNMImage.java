/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import com.sun.media.jai.codecimpl.util.ImagingException;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

class PNMImage
extends SimpleRenderedImage {
    private static final int PBM_ASCII = 49;
    private static final int PGM_ASCII = 50;
    private static final int PPM_ASCII = 51;
    private static final int PBM_RAW = 52;
    private static final int PGM_RAW = 53;
    private static final int PPM_RAW = 54;
    private static final int LINE_FEED = 10;
    private SeekableStream input;
    private byte[] lineSeparator;
    private int variant;
    private int maxValue;
    private Raster theTile = null;
    private int numBands;
    private int dataType;

    public PNMImage(SeekableStream input) {
        this.input = input;
        String ls = AccessController.doPrivileged(new GetPropertyAction("line.separator"));
        this.lineSeparator = ls.getBytes();
        try {
            if (this.input.read() != 80) {
                throw new RuntimeException(JaiI18N.getString("PNMImageDecoder0"));
            }
            this.variant = this.input.read();
            if (this.variant < 49 || this.variant > 54) {
                throw new RuntimeException(JaiI18N.getString("PNMImageDecoder1"));
            }
            this.width = this.readInteger(this.input);
            this.height = this.readInteger(this.input);
            this.maxValue = this.variant == 49 || this.variant == 52 ? 1 : this.readInteger(this.input);
        }
        catch (IOException e) {
            String message = JaiI18N.getString("PNMImageDecoder6");
            this.sendExceptionToListener(message, e);
        }
        if (this.isRaw(this.variant) && this.maxValue >= 256) {
            this.maxValue = 255;
        }
        this.tileWidth = this.width;
        this.tileHeight = this.height;
        this.numBands = this.variant == 51 || this.variant == 54 ? 3 : 1;
        this.dataType = this.maxValue < 256 ? 0 : (this.maxValue < 65536 ? 1 : 3);
        if (this.variant == 49 || this.variant == 52) {
            this.sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, 1);
            this.colorModel = ImageCodec.createGrayIndexColorModel(this.sampleModel, false);
        } else {
            int[] nArray;
            if (this.numBands == 1) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                int[] nArray3 = new int[3];
                nArray3[0] = 0;
                nArray3[1] = 1;
                nArray = nArray3;
                nArray3[2] = 2;
            }
            int[] bandOffsets = nArray;
            this.sampleModel = RasterFactory.createPixelInterleavedSampleModel(this.dataType, this.tileWidth, this.tileHeight, this.numBands, this.tileWidth * this.numBands, bandOffsets);
            this.colorModel = ImageCodec.createComponentColorModel(this.sampleModel);
        }
    }

    private boolean isRaw(int v) {
        return v >= 52;
    }

    private int readInteger(SeekableStream in) throws IOException {
        int b;
        int ret = 0;
        boolean foundDigit = false;
        while ((b = in.read()) != -1) {
            char c = (char)b;
            if (Character.isDigit(c)) {
                ret = ret * 10 + Character.digit(c, 10);
                foundDigit = true;
                continue;
            }
            if (c == '#') {
                int length = this.lineSeparator.length;
                while ((b = in.read()) != -1) {
                    boolean eol = false;
                    for (int i = 0; i < length; ++i) {
                        if (b != this.lineSeparator[i]) continue;
                        eol = true;
                        break;
                    }
                    if (!eol) continue;
                    break;
                }
                if (b == -1) break;
            }
            if (!foundDigit) continue;
            break;
        }
        return ret;
    }

    private Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster tile = Raster.createWritableRaster(this.sampleModel, org);
        Rectangle tileRect = tile.getBounds();
        try {
            block18: {
                block1 : switch (this.variant) {
                    case 49: 
                    case 52: {
                        DataBuffer dataBuffer = tile.getDataBuffer();
                        if (this.isRaw(this.variant)) {
                            byte[] buf = ((DataBufferByte)dataBuffer).getData();
                            this.input.readFully(buf, 0, buf.length);
                            break;
                        }
                        byte[] pixels = new byte[8 * this.width];
                        boolean offset = false;
                        for (int row = 0; row < this.tileHeight; row += 8) {
                            int rows = Math.min(8, this.tileHeight - row);
                            int len = (rows * this.width + 7) / 8;
                            for (int i = 0; i < rows * this.width; ++i) {
                                pixels[i] = (byte)this.readInteger(this.input);
                            }
                            this.sampleModel.setDataElements(tileRect.x, row, tileRect.width, rows, pixels, dataBuffer);
                        }
                        break;
                    }
                    case 50: 
                    case 51: 
                    case 53: 
                    case 54: {
                        int size = this.width * this.height * this.numBands;
                        switch (this.dataType) {
                            case 0: {
                                DataBufferByte bbuf = (DataBufferByte)tile.getDataBuffer();
                                byte[] byteArray = bbuf.getData();
                                if (this.isRaw(this.variant)) {
                                    this.input.readFully(byteArray);
                                    break block1;
                                }
                                for (int i = 0; i < size; ++i) {
                                    byteArray[i] = (byte)this.readInteger(this.input);
                                }
                                break block18;
                            }
                            case 1: {
                                DataBufferUShort sbuf = (DataBufferUShort)tile.getDataBuffer();
                                short[] shortArray = sbuf.getData();
                                for (int i = 0; i < size; ++i) {
                                    shortArray[i] = (short)this.readInteger(this.input);
                                }
                                break block18;
                            }
                            case 3: {
                                DataBufferInt ibuf = (DataBufferInt)tile.getDataBuffer();
                                int[] intArray = ibuf.getData();
                                for (int i = 0; i < size; ++i) {
                                    intArray[i] = this.readInteger(this.input);
                                }
                            }
                        }
                    }
                }
            }
            this.input.close();
        }
        catch (IOException e) {
            String message = JaiI18N.getString("PNMImageDecoder7");
            this.sendExceptionToListener(message, e);
        }
        return tile;
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("PNMImageDecoder4"));
        }
        if (this.theTile == null) {
            this.theTile = this.computeTile(tileX, tileY);
        }
        return this.theTile;
    }

    public void dispose() {
        this.theTile = null;
    }

    private void sendExceptionToListener(String message, Exception e) {
        ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
    }
}

