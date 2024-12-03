/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.SeekableOutputStream;
import com.sun.media.jai.codecimpl.CodecUtils;
import com.sun.media.jai.codecimpl.JaiI18N;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.OutputStream;

public class BMPImageEncoder
extends ImageEncoderImpl {
    private OutputStream output;
    private int version;
    private boolean isCompressed;
    private boolean isTopDown;
    private int w;
    private int h;
    private int compImageSize = 0;

    public BMPImageEncoder(OutputStream output, ImageEncodeParam param) {
        super(output, param);
        this.output = output;
        BMPEncodeParam bmpParam = param == null ? new BMPEncodeParam() : (BMPEncodeParam)param;
        this.version = bmpParam.getVersion();
        this.isCompressed = bmpParam.isCompressed();
        if (this.isCompressed && !(output instanceof SeekableOutputStream)) {
            throw new IllegalArgumentException(JaiI18N.getString("BMPImageEncoder6"));
        }
        this.isTopDown = bmpParam.isTopDown();
    }

    public void encode(RenderedImage im) throws IOException {
        int minX = im.getMinX();
        int minY = im.getMinY();
        this.w = im.getWidth();
        this.h = im.getHeight();
        int bitsPerPixel = 24;
        boolean isPalette = false;
        int paletteEntries = 0;
        IndexColorModel icm = null;
        SampleModel sm = im.getSampleModel();
        int numBands = sm.getNumBands();
        ColorModel cm = im.getColorModel();
        if (numBands != 1 && numBands != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("BMPImageEncoder1"));
        }
        int[] sampleSize = sm.getSampleSize();
        if (sampleSize[0] > 8) {
            throw new RuntimeException(JaiI18N.getString("BMPImageEncoder2"));
        }
        for (int i = 1; i < sampleSize.length; ++i) {
            if (sampleSize[i] == sampleSize[0]) continue;
            throw new RuntimeException(JaiI18N.getString("BMPImageEncoder3"));
        }
        int dataType = sm.getTransferType();
        if (dataType != 0 && !CodecUtils.isPackedByteImage(im)) {
            throw new RuntimeException(JaiI18N.getString("BMPImageEncoder0"));
        }
        int destScanlineBytes = this.w * numBands;
        int compression = 0;
        byte[] r = null;
        byte[] g = null;
        byte[] b = null;
        byte[] a = null;
        if (cm instanceof IndexColorModel) {
            isPalette = true;
            icm = (IndexColorModel)cm;
            paletteEntries = icm.getMapSize();
            if (paletteEntries <= 2) {
                bitsPerPixel = 1;
                destScanlineBytes = (int)Math.ceil((double)this.w / 8.0);
            } else if (paletteEntries <= 16) {
                bitsPerPixel = 4;
                destScanlineBytes = (int)Math.ceil((double)this.w / 2.0);
            } else if (paletteEntries <= 256) {
                bitsPerPixel = 8;
            } else {
                bitsPerPixel = 24;
                isPalette = false;
                paletteEntries = 0;
                destScanlineBytes = this.w * 3;
            }
            if (isPalette) {
                r = new byte[paletteEntries];
                g = new byte[paletteEntries];
                b = new byte[paletteEntries];
                a = new byte[paletteEntries];
                icm.getAlphas(a);
                icm.getReds(r);
                icm.getGreens(g);
                icm.getBlues(b);
            }
        } else if (numBands == 1) {
            isPalette = true;
            paletteEntries = 256;
            bitsPerPixel = sampleSize[0];
            destScanlineBytes = (int)Math.ceil((double)(this.w * bitsPerPixel) / 8.0);
            r = new byte[256];
            g = new byte[256];
            b = new byte[256];
            a = new byte[256];
            for (int i = 0; i < 256; ++i) {
                r[i] = (byte)i;
                g[i] = (byte)i;
                b[i] = (byte)i;
                a[i] = -1;
            }
        } else if (sm instanceof SinglePixelPackedSampleModel) {
            bitsPerPixel = DataBuffer.getDataTypeSize(sm.getDataType());
            destScanlineBytes = this.w * bitsPerPixel + 7 >> 3;
        }
        int fileSize = 0;
        int offset = 0;
        int headerSize = 0;
        int imageSize = 0;
        int xPelsPerMeter = 0;
        int yPelsPerMeter = 0;
        int colorsUsed = 0;
        int colorsImportant = paletteEntries;
        int padding = 0;
        int remainder = destScanlineBytes % 4;
        if (remainder != 0) {
            padding = 4 - remainder;
        }
        switch (this.version) {
            case 0: {
                offset = 26 + paletteEntries * 3;
                headerSize = 12;
                imageSize = (destScanlineBytes + padding) * this.h;
                fileSize = imageSize + offset;
                throw new RuntimeException(JaiI18N.getString("BMPImageEncoder5"));
            }
            case 1: {
                if (this.isCompressed && bitsPerPixel == 8) {
                    compression = 1;
                } else if (this.isCompressed && bitsPerPixel == 4) {
                    compression = 2;
                }
                offset = 54 + paletteEntries * 4;
                imageSize = (destScanlineBytes + padding) * this.h;
                fileSize = imageSize + offset;
                headerSize = 40;
                break;
            }
            case 2: {
                headerSize = 108;
                throw new RuntimeException(JaiI18N.getString("BMPImageEncoder5"));
            }
        }
        int redMask = 0;
        int blueMask = 0;
        int greenMask = 0;
        if (cm instanceof DirectColorModel) {
            redMask = ((DirectColorModel)cm).getRedMask();
            greenMask = ((DirectColorModel)cm).getGreenMask();
            blueMask = ((DirectColorModel)cm).getBlueMask();
            destScanlineBytes = this.w;
            compression = 3;
            fileSize += 12;
            offset += 12;
        }
        this.writeFileHeader(fileSize, offset);
        this.writeInfoHeader(headerSize, bitsPerPixel);
        this.writeDWord(compression);
        this.writeDWord(imageSize);
        this.writeDWord(xPelsPerMeter);
        this.writeDWord(yPelsPerMeter);
        this.writeDWord(colorsUsed);
        this.writeDWord(colorsImportant);
        if (compression == 3) {
            this.writeDWord(redMask);
            this.writeDWord(greenMask);
            this.writeDWord(blueMask);
        }
        if (compression == 3) {
            block15: for (int i = 0; i < this.h; ++i) {
                int row = minY + i;
                if (!this.isTopDown) {
                    row = minY + this.h - i - 1;
                }
                Rectangle srcRect = new Rectangle(minX, row, this.w, 1);
                Raster src = im.getData(srcRect);
                SampleModel sm1 = src.getSampleModel();
                int pos = 0;
                int startX = srcRect.x - src.getSampleModelTranslateX();
                int startY = srcRect.y - src.getSampleModelTranslateY();
                if (sm1 instanceof SinglePixelPackedSampleModel) {
                    SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm1;
                    pos = sppsm.getOffset(startX, startY);
                }
                switch (dataType) {
                    case 2: {
                        short[] sdata = ((DataBufferShort)src.getDataBuffer()).getData();
                        for (int m = 0; m < sdata.length; ++m) {
                            this.writeWord(sdata[m]);
                        }
                        continue block15;
                    }
                    case 1: {
                        short[] usdata = ((DataBufferUShort)src.getDataBuffer()).getData();
                        for (int m = 0; m < usdata.length; ++m) {
                            this.writeWord(usdata[m]);
                        }
                        continue block15;
                    }
                    case 3: {
                        int[] idata = ((DataBufferInt)src.getDataBuffer()).getData();
                        for (int m = 0; m < idata.length; ++m) {
                            this.writeDWord(idata[m]);
                        }
                        continue block15;
                    }
                }
            }
            return;
        }
        if (isPalette) {
            switch (this.version) {
                case 0: {
                    int i;
                    for (i = 0; i < paletteEntries; ++i) {
                        this.output.write(b[i]);
                        this.output.write(g[i]);
                        this.output.write(r[i]);
                    }
                    break;
                }
                default: {
                    int i;
                    for (i = 0; i < paletteEntries; ++i) {
                        this.output.write(b[i]);
                        this.output.write(g[i]);
                        this.output.write(r[i]);
                        this.output.write(a[i]);
                    }
                }
            }
        }
        int scanlineBytes = this.w * numBands;
        int[] pixels = new int[8 * scanlineBytes];
        byte[] bpixels = new byte[destScanlineBytes];
        if (!this.isTopDown) {
            int lastRow = minY + this.h;
            for (int row = lastRow - 1; row >= minY; row -= 8) {
                int rows = Math.min(8, row - minY + 1);
                Raster src = im.getData(new Rectangle(minX, row - rows + 1, this.w, rows));
                src.getPixels(minX, row - rows + 1, this.w, rows, pixels);
                int l = 0;
                int max = scanlineBytes * rows - 1;
                for (int i = 0; i < rows; ++i) {
                    l = max - (i + 1) * scanlineBytes + 1;
                    this.writePixels(l, scanlineBytes, bitsPerPixel, pixels, bpixels, padding, numBands, icm);
                }
            }
        } else {
            int lastRow = minY + this.h;
            for (int row = minY; row < lastRow; row += 8) {
                int rows = Math.min(8, lastRow - row);
                Raster src = im.getData(new Rectangle(minX, row, this.w, rows));
                src.getPixels(minX, row, this.w, rows, pixels);
                int l = 0;
                for (int i = 0; i < rows; ++i) {
                    this.writePixels(l, scanlineBytes, bitsPerPixel, pixels, bpixels, padding, numBands, icm);
                }
            }
        }
        if (this.isCompressed && (bitsPerPixel == 4 || bitsPerPixel == 8)) {
            this.output.write(0);
            this.output.write(1);
            this.incCompImageSize(2);
            imageSize = this.compImageSize;
            fileSize = this.compImageSize + offset;
            this.writeSize(fileSize, 2);
            this.writeSize(imageSize, 34);
        }
    }

    private void writePixels(int l, int scanlineBytes, int bitsPerPixel, int[] pixels, byte[] bpixels, int padding, int numBands, IndexColorModel icm) throws IOException {
        int pixel = 0;
        int k = 0;
        switch (bitsPerPixel) {
            case 1: {
                int j;
                for (j = 0; j < scanlineBytes / 8; ++j) {
                    bpixels[k++] = (byte)(pixels[l++] << 7 | pixels[l++] << 6 | pixels[l++] << 5 | pixels[l++] << 4 | pixels[l++] << 3 | pixels[l++] << 2 | pixels[l++] << 1 | pixels[l++]);
                }
                if (scanlineBytes % 8 > 0) {
                    pixel = 0;
                    for (j = 0; j < scanlineBytes % 8; ++j) {
                        pixel |= pixels[l++] << 7 - j;
                    }
                    bpixels[k++] = (byte)pixel;
                }
                this.output.write(bpixels, 0, (scanlineBytes + 7) / 8);
                break;
            }
            case 4: {
                if (this.isCompressed) {
                    byte[] bipixels = new byte[scanlineBytes];
                    for (int h = 0; h < scanlineBytes; ++h) {
                        bipixels[h] = (byte)pixels[l++];
                    }
                    this.encodeRLE4(bipixels, scanlineBytes);
                    break;
                }
                for (int j = 0; j < scanlineBytes / 2; ++j) {
                    pixel = pixels[l++] << 4 | pixels[l++];
                    bpixels[k++] = (byte)pixel;
                }
                if (scanlineBytes % 2 == 1) {
                    pixel = pixels[l] << 4;
                    bpixels[k++] = (byte)pixel;
                }
                this.output.write(bpixels, 0, (scanlineBytes + 1) / 2);
                break;
            }
            case 8: {
                if (this.isCompressed) {
                    for (int h = 0; h < scanlineBytes; ++h) {
                        bpixels[h] = (byte)pixels[l++];
                    }
                    this.encodeRLE8(bpixels, scanlineBytes);
                    break;
                }
                for (int j = 0; j < scanlineBytes; ++j) {
                    bpixels[j] = (byte)pixels[l++];
                }
                this.output.write(bpixels, 0, scanlineBytes);
                break;
            }
            case 24: {
                if (numBands == 3) {
                    for (int j = 0; j < scanlineBytes; j += 3) {
                        bpixels[k++] = (byte)pixels[l + 2];
                        bpixels[k++] = (byte)pixels[l + 1];
                        bpixels[k++] = (byte)pixels[l];
                        l += 3;
                    }
                    this.output.write(bpixels, 0, scanlineBytes);
                    break;
                }
                int entries = icm.getMapSize();
                byte[] r = new byte[entries];
                byte[] g = new byte[entries];
                byte[] b = new byte[entries];
                icm.getReds(r);
                icm.getGreens(g);
                icm.getBlues(b);
                for (int j = 0; j < scanlineBytes; ++j) {
                    int index = pixels[l];
                    bpixels[k++] = b[index];
                    bpixels[k++] = g[index];
                    bpixels[k++] = b[index];
                    ++l;
                }
                this.output.write(bpixels, 0, scanlineBytes * 3);
            }
        }
        if (!this.isCompressed || bitsPerPixel != 8 && bitsPerPixel != 4) {
            for (k = 0; k < padding; ++k) {
                this.output.write(0);
            }
        }
    }

    private void encodeRLE8(byte[] bpixels, int scanlineBytes) throws IOException {
        int runCount = 1;
        int absVal = -1;
        int j = -1;
        byte runVal = 0;
        byte nextVal = 0;
        runVal = bpixels[++j];
        byte[] absBuf = new byte[256];
        while (j < scanlineBytes - 1) {
            int b;
            int a;
            if ((nextVal = bpixels[++j]) == runVal) {
                if (absVal >= 3) {
                    this.output.write(0);
                    this.output.write(absVal);
                    this.incCompImageSize(2);
                    for (a = 0; a < absVal; ++a) {
                        this.output.write(absBuf[a]);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven(absVal)) {
                        this.output.write(0);
                        this.incCompImageSize(1);
                    }
                } else if (absVal > -1) {
                    for (b = 0; b < absVal; ++b) {
                        this.output.write(1);
                        this.output.write(absBuf[b]);
                        this.incCompImageSize(2);
                    }
                }
                absVal = -1;
                if (++runCount == 256) {
                    this.output.write(runCount - 1);
                    this.output.write(runVal);
                    this.incCompImageSize(2);
                    runCount = 1;
                }
            } else {
                if (runCount > 1) {
                    this.output.write(runCount);
                    this.output.write(runVal);
                    this.incCompImageSize(2);
                } else if (absVal < 0) {
                    absBuf[++absVal] = runVal;
                    absBuf[++absVal] = nextVal;
                } else if (absVal < 254) {
                    absBuf[++absVal] = nextVal;
                } else {
                    this.output.write(0);
                    this.output.write(absVal + 1);
                    this.incCompImageSize(2);
                    for (a = 0; a <= absVal; ++a) {
                        this.output.write(absBuf[a]);
                        this.incCompImageSize(1);
                    }
                    this.output.write(0);
                    this.incCompImageSize(1);
                    absVal = -1;
                }
                runVal = nextVal;
                runCount = 1;
            }
            if (j != scanlineBytes - 1) continue;
            if (absVal == -1) {
                this.output.write(runCount);
                this.output.write(runVal);
                this.incCompImageSize(2);
                runCount = 1;
            } else if (absVal >= 2) {
                this.output.write(0);
                this.output.write(absVal + 1);
                this.incCompImageSize(2);
                for (a = 0; a <= absVal; ++a) {
                    this.output.write(absBuf[a]);
                    this.incCompImageSize(1);
                }
                if (!this.isEven(absVal + 1)) {
                    this.output.write(0);
                    this.incCompImageSize(1);
                }
            } else if (absVal > -1) {
                for (b = 0; b <= absVal; ++b) {
                    this.output.write(1);
                    this.output.write(absBuf[b]);
                    this.incCompImageSize(2);
                }
            }
            this.output.write(0);
            this.output.write(0);
            this.incCompImageSize(2);
        }
    }

    private void encodeRLE4(byte[] bipixels, int scanlineBytes) throws IOException {
        int runCount = 2;
        int absVal = -1;
        int j = -1;
        int pixel = 0;
        int q = 0;
        byte runVal1 = 0;
        byte runVal2 = 0;
        byte nextVal1 = 0;
        byte nextVal2 = 0;
        byte[] absBuf = new byte[256];
        runVal1 = bipixels[++j];
        runVal2 = bipixels[++j];
        while (j < scanlineBytes - 2) {
            int n;
            int a;
            nextVal1 = bipixels[++j];
            nextVal2 = bipixels[++j];
            if (nextVal1 == runVal1) {
                int r;
                if (absVal >= 4) {
                    this.output.write(0);
                    this.output.write(absVal - 1);
                    this.incCompImageSize(2);
                    for (a = 0; a < absVal - 2; a += 2) {
                        pixel = absBuf[a] << 4 | absBuf[a + 1];
                        this.output.write((byte)pixel);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven(absVal - 1)) {
                        q = absBuf[absVal - 2] << 4 | 0;
                        this.output.write(q);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven((int)Math.ceil((absVal - 1) / 2))) {
                        this.output.write(0);
                        this.incCompImageSize(1);
                    }
                } else if (absVal > -1) {
                    this.output.write(2);
                    pixel = absBuf[0] << 4 | absBuf[1];
                    this.output.write(pixel);
                    this.incCompImageSize(2);
                }
                absVal = -1;
                if (nextVal2 == runVal2) {
                    if ((runCount += 2) == 256) {
                        this.output.write(runCount - 1);
                        pixel = runVal1 << 4 | runVal2;
                        this.output.write(pixel);
                        this.incCompImageSize(2);
                        runCount = 2;
                        if (j < scanlineBytes - 1) {
                            runVal1 = runVal2;
                            runVal2 = bipixels[++j];
                        } else {
                            this.output.write(1);
                            r = runVal2 << 4 | 0;
                            this.output.write(r);
                            this.incCompImageSize(2);
                            runCount = -1;
                        }
                    }
                } else {
                    pixel = runVal1 << 4 | runVal2;
                    this.output.write(++runCount);
                    this.output.write(pixel);
                    this.incCompImageSize(2);
                    runCount = 2;
                    runVal1 = nextVal2;
                    if (j < scanlineBytes - 1) {
                        runVal2 = bipixels[++j];
                    } else {
                        this.output.write(1);
                        r = nextVal2 << 4 | 0;
                        this.output.write(r);
                        this.incCompImageSize(2);
                        runCount = -1;
                    }
                }
            } else {
                if (runCount > 2) {
                    pixel = runVal1 << 4 | runVal2;
                    this.output.write(runCount);
                    this.output.write(pixel);
                    this.incCompImageSize(2);
                } else if (absVal < 0) {
                    absBuf[++absVal] = runVal1;
                    absBuf[++absVal] = runVal2;
                    absBuf[++absVal] = nextVal1;
                    absBuf[++absVal] = nextVal2;
                } else if (absVal < 253) {
                    absBuf[++absVal] = nextVal1;
                    absBuf[++absVal] = nextVal2;
                } else {
                    this.output.write(0);
                    this.output.write(absVal + 1);
                    this.incCompImageSize(2);
                    for (a = 0; a < absVal; a += 2) {
                        pixel = absBuf[a] << 4 | absBuf[a + 1];
                        this.output.write((byte)pixel);
                        this.incCompImageSize(1);
                    }
                    this.output.write(0);
                    this.incCompImageSize(1);
                    absVal = -1;
                }
                runVal1 = nextVal1;
                runVal2 = nextVal2;
                runCount = 2;
            }
            if (j < scanlineBytes - 2) continue;
            if (absVal == -1 && runCount >= 2) {
                if (j == scanlineBytes - 2) {
                    if (bipixels[++j] == runVal1) {
                        pixel = runVal1 << 4 | runVal2;
                        this.output.write(++runCount);
                        this.output.write(pixel);
                        this.incCompImageSize(2);
                    } else {
                        pixel = runVal1 << 4 | runVal2;
                        this.output.write(runCount);
                        this.output.write(pixel);
                        this.output.write(1);
                        pixel = bipixels[j] << 4 | 0;
                        this.output.write(pixel);
                        n = bipixels[j] << 4 | 0;
                        this.incCompImageSize(4);
                    }
                } else {
                    this.output.write(runCount);
                    pixel = runVal1 << 4 | runVal2;
                    this.output.write(pixel);
                    this.incCompImageSize(2);
                }
            } else if (absVal > -1) {
                if (j == scanlineBytes - 2) {
                    absBuf[++absVal] = bipixels[++j];
                }
                if (absVal >= 2) {
                    this.output.write(0);
                    this.output.write(absVal + 1);
                    this.incCompImageSize(2);
                    for (a = 0; a < absVal; a += 2) {
                        pixel = absBuf[a] << 4 | absBuf[a + 1];
                        this.output.write((byte)pixel);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven(absVal + 1)) {
                        q = absBuf[absVal] << 4 | 0;
                        this.output.write(q);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven((int)Math.ceil((absVal + 1) / 2))) {
                        this.output.write(0);
                        this.incCompImageSize(1);
                    }
                } else {
                    switch (absVal) {
                        case 0: {
                            this.output.write(1);
                            n = absBuf[0] << 4 | 0;
                            this.output.write(n);
                            this.incCompImageSize(2);
                            break;
                        }
                        case 1: {
                            this.output.write(2);
                            pixel = absBuf[0] << 4 | absBuf[1];
                            this.output.write(pixel);
                            this.incCompImageSize(2);
                        }
                    }
                }
            }
            this.output.write(0);
            this.output.write(0);
            this.incCompImageSize(2);
        }
    }

    private synchronized void incCompImageSize(int value) {
        this.compImageSize += value;
    }

    private boolean isEven(int number) {
        return number % 2 == 0;
    }

    private void writeFileHeader(int fileSize, int offset) throws IOException {
        this.output.write(66);
        this.output.write(77);
        this.writeDWord(fileSize);
        this.output.write(0);
        this.output.write(0);
        this.output.write(0);
        this.output.write(0);
        this.writeDWord(offset);
    }

    private void writeInfoHeader(int headerSize, int bitsPerPixel) throws IOException {
        this.writeDWord(headerSize);
        this.writeDWord(this.w);
        this.writeDWord(this.h);
        this.writeWord(1);
        this.writeWord(bitsPerPixel);
    }

    public void writeWord(int word) throws IOException {
        this.output.write(word & 0xFF);
        this.output.write((word & 0xFF00) >> 8);
    }

    public void writeDWord(int dword) throws IOException {
        this.output.write(dword & 0xFF);
        this.output.write((dword & 0xFF00) >> 8);
        this.output.write((dword & 0xFF0000) >> 16);
        this.output.write((dword & 0xFF000000) >> 24);
    }

    private void writeSize(int dword, int offset) throws IOException {
        ((SeekableOutputStream)this.output).seek(offset);
        this.writeDWord(dword);
    }
}

