/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.png;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import org.apache.xmlgraphics.image.codec.png.ChunkStream;
import org.apache.xmlgraphics.image.codec.png.IDATOutputStream;
import org.apache.xmlgraphics.image.codec.png.PNGEncodeParam;
import org.apache.xmlgraphics.image.codec.util.ImageEncoderImpl;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;

public class PNGImageEncoder
extends ImageEncoderImpl {
    private static final int PNG_COLOR_GRAY = 0;
    private static final int PNG_COLOR_RGB = 2;
    private static final int PNG_COLOR_PALETTE = 3;
    private static final int PNG_COLOR_GRAY_ALPHA = 4;
    private static final int PNG_COLOR_RGB_ALPHA = 6;
    private static final byte[] MAGIC = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private PNGEncodeParam param;
    private RenderedImage image;
    private int width;
    private int height;
    private int bitDepth;
    private int bitShift;
    private int numBands;
    private int colorType;
    private int bpp;
    private boolean skipAlpha;
    private boolean compressGray;
    private boolean interlace;
    private byte[] redPalette;
    private byte[] greenPalette;
    private byte[] bluePalette;
    private byte[] alphaPalette;
    private DataOutputStream dataOutput;
    private byte[] prevRow;
    private byte[] currRow;
    private byte[][] filteredRows;
    private static final float[] SRGB_CHROMA = new float[]{0.3127f, 0.329f, 0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f};

    public PNGImageEncoder(OutputStream output, PNGEncodeParam param) {
        super(output, param);
        if (param != null) {
            this.param = param;
        }
        this.dataOutput = new DataOutputStream(output);
    }

    private void writeMagic() throws IOException {
        this.dataOutput.write(MAGIC);
    }

    private void writeIHDR() throws IOException {
        try (ChunkStream cs = new ChunkStream("IHDR");){
            cs.writeInt(this.width);
            cs.writeInt(this.height);
            cs.writeByte((byte)this.bitDepth);
            cs.writeByte((byte)this.colorType);
            cs.writeByte(0);
            cs.writeByte(0);
            cs.writeByte(this.interlace ? 1 : 0);
            cs.writeToStream(this.dataOutput);
        }
    }

    private static int clamp(int val, int maxValue) {
        return val > maxValue ? maxValue : val;
    }

    private void encodePass(OutputStream os, Raster ras, int xOffset, int yOffset, int xSkip, int ySkip) throws IOException {
        int minX = ras.getMinX();
        int minY = ras.getMinY();
        int width = ras.getWidth();
        int height = ras.getHeight();
        int samplesPerByte = 8 / this.bitDepth;
        int numSamples = width * this.numBands;
        int[] samples = new int[numSamples];
        int pixels = (numSamples - (xOffset *= this.numBands) + (xSkip *= this.numBands) - 1) / xSkip;
        int bytesPerRow = pixels * this.numBands;
        if (this.bitDepth < 8) {
            bytesPerRow = (bytesPerRow + samplesPerByte - 1) / samplesPerByte;
        } else if (this.bitDepth == 16) {
            bytesPerRow *= 2;
        }
        if (bytesPerRow == 0) {
            return;
        }
        this.currRow = new byte[bytesPerRow + this.bpp];
        this.prevRow = new byte[bytesPerRow + this.bpp];
        this.filteredRows = new byte[5][bytesPerRow + this.bpp];
        int maxValue = (1 << this.bitDepth) - 1;
        for (int row = minY + yOffset; row < minY + height; row += ySkip) {
            ras.getPixels(minX, row, width, 1, samples);
            if (this.compressGray) {
                int shift = 8 - this.bitDepth;
                int i = 0;
                while (i < width) {
                    int n = i++;
                    samples[n] = samples[n] >> shift;
                }
            }
            int count = this.bpp;
            int pos = 0;
            int tmp = 0;
            switch (this.bitDepth) {
                case 1: 
                case 2: 
                case 4: {
                    int mask = samplesPerByte - 1;
                    for (int s = xOffset; s < numSamples; s += xSkip) {
                        int val = PNGImageEncoder.clamp(samples[s] >> this.bitShift, maxValue);
                        tmp = tmp << this.bitDepth | val;
                        if (pos++ != mask) continue;
                        this.currRow[count++] = (byte)tmp;
                        tmp = 0;
                        pos = 0;
                    }
                    if (pos == 0) break;
                    this.currRow[count++] = (byte)(tmp <<= (samplesPerByte - pos) * this.bitDepth);
                    break;
                }
                case 8: {
                    int b;
                    for (int s = xOffset; s < numSamples; s += xSkip) {
                        for (b = 0; b < this.numBands; ++b) {
                            this.currRow[count++] = (byte)PNGImageEncoder.clamp(samples[s + b] >> this.bitShift, maxValue);
                        }
                    }
                    break;
                }
                case 16: {
                    int b;
                    for (int s = xOffset; s < numSamples; s += xSkip) {
                        for (b = 0; b < this.numBands; ++b) {
                            int val = PNGImageEncoder.clamp(samples[s + b] >> this.bitShift, maxValue);
                            this.currRow[count++] = (byte)(val >> 8);
                            this.currRow[count++] = (byte)(val & 0xFF);
                        }
                    }
                    break;
                }
            }
            int filterType = this.param.filterRow(this.currRow, this.prevRow, this.filteredRows, bytesPerRow, this.bpp);
            os.write(filterType);
            os.write(this.filteredRows[filterType], this.bpp, bytesPerRow);
            byte[] swap = this.currRow;
            this.currRow = this.prevRow;
            this.prevRow = swap;
        }
    }

    private void writeIDAT() throws IOException {
        IDATOutputStream ios = new IDATOutputStream(this.dataOutput, 8192);
        DeflaterOutputStream dos = new DeflaterOutputStream((OutputStream)ios, new Deflater(9));
        Raster ras = this.image.getData(new Rectangle(this.image.getMinX(), this.image.getMinY(), this.image.getWidth(), this.image.getHeight()));
        if (this.skipAlpha) {
            int numBands = ras.getNumBands() - 1;
            int[] bandList = new int[numBands];
            for (int i = 0; i < numBands; ++i) {
                bandList[i] = i;
            }
            ras = ras.createChild(0, 0, ras.getWidth(), ras.getHeight(), 0, 0, bandList);
        }
        if (this.interlace) {
            this.encodePass(dos, ras, 0, 0, 8, 8);
            this.encodePass(dos, ras, 4, 0, 8, 8);
            this.encodePass(dos, ras, 0, 4, 4, 8);
            this.encodePass(dos, ras, 2, 0, 4, 4);
            this.encodePass(dos, ras, 0, 2, 2, 4);
            this.encodePass(dos, ras, 1, 0, 2, 2);
            this.encodePass(dos, ras, 0, 1, 1, 2);
        } else {
            this.encodePass(dos, ras, 0, 0, 1, 1);
        }
        dos.finish();
        dos.close();
        ios.flush();
        ios.close();
    }

    private void writeIEND() throws IOException {
        try (ChunkStream cs = new ChunkStream("IEND");){
            cs.writeToStream(this.dataOutput);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeCHRM() throws IOException {
        if (this.param.isChromaticitySet() || this.param.isSRGBIntentSet()) {
            try (ChunkStream cs = new ChunkStream("cHRM");){
                float[] chroma = !this.param.isSRGBIntentSet() ? this.param.getChromaticity() : SRGB_CHROMA;
                for (int i = 0; i < 8; ++i) {
                    cs.writeInt((int)(chroma[i] * 100000.0f));
                }
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    private void writeGAMA() throws IOException {
        if (this.param.isGammaSet() || this.param.isSRGBIntentSet()) {
            try (ChunkStream cs = new ChunkStream("gAMA");){
                float gamma = !this.param.isSRGBIntentSet() ? this.param.getGamma() : 0.45454544f;
                cs.writeInt((int)(gamma * 100000.0f));
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    private void writeICCP() throws IOException {
        if (this.param.isICCProfileDataSet()) {
            try (ChunkStream cs = new ChunkStream("iCCP");){
                byte[] iccProfileData = this.param.getICCProfileData();
                cs.write(iccProfileData);
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeSBIT() throws IOException {
        if (this.param.isSignificantBitsSet()) {
            try (ChunkStream cs = new ChunkStream("sBIT");){
                int[] significantBits;
                for (int significantBit : significantBits = this.param.getSignificantBits()) {
                    cs.writeByte(significantBit);
                }
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    private void writeSRGB() throws IOException {
        if (this.param.isSRGBIntentSet()) {
            try (ChunkStream cs = new ChunkStream("sRGB");){
                int intent = this.param.getSRGBIntent();
                cs.write(intent);
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    private void writePLTE() throws IOException {
        if (this.redPalette == null) {
            return;
        }
        try (ChunkStream cs = new ChunkStream("PLTE");){
            for (int i = 0; i < this.redPalette.length; ++i) {
                cs.writeByte(this.redPalette[i]);
                cs.writeByte(this.greenPalette[i]);
                cs.writeByte(this.bluePalette[i]);
            }
            cs.writeToStream(this.dataOutput);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeBKGD() throws IOException {
        if (this.param.isBackgroundSet()) {
            try (ChunkStream cs = new ChunkStream("bKGD");){
                switch (this.colorType) {
                    case 0: 
                    case 4: {
                        int gray = ((PNGEncodeParam.Gray)this.param).getBackgroundGray();
                        cs.writeShort(gray);
                        break;
                    }
                    case 3: {
                        int index = ((PNGEncodeParam.Palette)this.param).getBackgroundPaletteIndex();
                        cs.writeByte(index);
                        break;
                    }
                    case 2: 
                    case 6: {
                        int[] rgb = ((PNGEncodeParam.RGB)this.param).getBackgroundRGB();
                        cs.writeShort(rgb[0]);
                        cs.writeShort(rgb[1]);
                        cs.writeShort(rgb[2]);
                    }
                }
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeHIST() throws IOException {
        if (this.param.isPaletteHistogramSet()) {
            try (ChunkStream cs = new ChunkStream("hIST");){
                int[] hist;
                for (int aHist : hist = this.param.getPaletteHistogram()) {
                    cs.writeShort(aHist);
                }
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeTRNS() throws IOException {
        if (this.param.isTransparencySet() && this.colorType != 4 && this.colorType != 6) {
            try (ChunkStream cs = new ChunkStream("tRNS");){
                if (this.param instanceof PNGEncodeParam.Palette) {
                    byte[] t;
                    for (byte aT : t = ((PNGEncodeParam.Palette)this.param).getPaletteTransparency()) {
                        cs.writeByte(aT);
                    }
                } else if (this.param instanceof PNGEncodeParam.Gray) {
                    int t = ((PNGEncodeParam.Gray)this.param).getTransparentGray();
                    cs.writeShort(t);
                } else if (this.param instanceof PNGEncodeParam.RGB) {
                    int[] t = ((PNGEncodeParam.RGB)this.param).getTransparentRGB();
                    cs.writeShort(t[0]);
                    cs.writeShort(t[1]);
                    cs.writeShort(t[2]);
                }
                cs.writeToStream(this.dataOutput);
            }
        }
        if (this.colorType == 3) {
            int lastEntry;
            int nonOpaque;
            for (nonOpaque = lastEntry = Math.min(255, this.alphaPalette.length - 1); nonOpaque >= 0 && this.alphaPalette[nonOpaque] == -1; --nonOpaque) {
            }
            if (nonOpaque >= 0) {
                try (ChunkStream cs = new ChunkStream("tRNS");){
                    for (int i = 0; i <= nonOpaque; ++i) {
                        cs.writeByte(this.alphaPalette[i]);
                    }
                    cs.writeToStream(this.dataOutput);
                }
            }
        }
    }

    private void writePHYS() throws IOException {
        if (this.param.isPhysicalDimensionSet()) {
            try (ChunkStream cs = new ChunkStream("pHYs");){
                int[] dims = this.param.getPhysicalDimension();
                cs.writeInt(dims[0]);
                cs.writeInt(dims[1]);
                cs.writeByte((byte)dims[2]);
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    private void writeSPLT() throws IOException {
        if (this.param.isSuggestedPaletteSet()) {
            try (ChunkStream cs = new ChunkStream("sPLT");){
                System.out.println("sPLT not supported yet.");
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeTIME() throws IOException {
        if (this.param.isModificationTimeSet()) {
            try (ChunkStream cs = new ChunkStream("tIME");){
                Date date = this.param.getModificationTime();
                TimeZone gmt = TimeZone.getTimeZone("GMT");
                GregorianCalendar cal = new GregorianCalendar(gmt);
                cal.setTime(date);
                int year = cal.get(1);
                int month = cal.get(2);
                int day = cal.get(5);
                int hour = cal.get(11);
                int minute = cal.get(12);
                int second = cal.get(13);
                cs.writeShort(year);
                cs.writeByte(month + 1);
                cs.writeByte(day);
                cs.writeByte(hour);
                cs.writeByte(minute);
                cs.writeByte(second);
                cs.writeToStream(this.dataOutput);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeTEXT() throws IOException {
        if (this.param.isTextSet()) {
            String[] text = this.param.getText();
            for (int i = 0; i < text.length / 2; ++i) {
                byte[] keyword = text[2 * i].getBytes("UTF-8");
                byte[] value = text[2 * i + 1].getBytes("UTF-8");
                try (ChunkStream cs = new ChunkStream("tEXt");){
                    cs.write(keyword, 0, Math.min(keyword.length, 79));
                    cs.write(0);
                    cs.write(value);
                    cs.writeToStream(this.dataOutput);
                    continue;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeZTXT() throws IOException {
        if (this.param.isCompressedTextSet()) {
            String[] text = this.param.getCompressedText();
            for (int i = 0; i < text.length / 2; ++i) {
                byte[] keyword = text[2 * i].getBytes("UTF-8");
                byte[] value = text[2 * i + 1].getBytes("UTF-8");
                try (ChunkStream cs = new ChunkStream("zTXt");){
                    cs.write(keyword, 0, Math.min(keyword.length, 79));
                    cs.write(0);
                    cs.write(0);
                    try (DeflaterOutputStream dos = new DeflaterOutputStream(cs);){
                        dos.write(value);
                        dos.finish();
                    }
                    cs.writeToStream(this.dataOutput);
                    continue;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writePrivateChunks() throws IOException {
        int numChunks = this.param.getNumPrivateChunks();
        for (int i = 0; i < numChunks; ++i) {
            String type = this.param.getPrivateChunkType(i);
            byte[] data = this.param.getPrivateChunkData(i);
            try (ChunkStream cs = new ChunkStream(type);){
                cs.write(data);
                cs.writeToStream(this.dataOutput);
                continue;
            }
        }
    }

    private PNGEncodeParam.Gray createGrayParam(byte[] redPalette, byte[] greenPalette, byte[] bluePalette, byte[] alphaPalette) {
        PNGEncodeParam.Gray param = new PNGEncodeParam.Gray();
        int numTransparent = 0;
        int grayFactor = 255 / ((1 << this.bitDepth) - 1);
        int entries = 1 << this.bitDepth;
        for (int i = 0; i < entries; ++i) {
            byte red = redPalette[i];
            if (red != i * grayFactor || red != greenPalette[i] || red != bluePalette[i]) {
                return null;
            }
            byte alpha = alphaPalette[i];
            if (alpha == 0) {
                param.setTransparentGray(i);
                if (++numTransparent <= 1) continue;
                return null;
            }
            if (alpha == -1) continue;
            return null;
        }
        return param;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void encode(RenderedImage im) throws IOException {
        this.image = im;
        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
        SampleModel sampleModel = this.image.getSampleModel();
        int[] sampleSize = sampleModel.getSampleSize();
        this.bitDepth = -1;
        this.bitShift = 0;
        if (this.param instanceof PNGEncodeParam.Gray) {
            PNGEncodeParam.Gray paramg = (PNGEncodeParam.Gray)this.param;
            if (paramg.isBitDepthSet()) {
                this.bitDepth = paramg.getBitDepth();
            }
            if (paramg.isBitShiftSet()) {
                this.bitShift = paramg.getBitShift();
            }
        }
        if (this.bitDepth == -1) {
            this.bitDepth = sampleSize[0];
            for (int i = 1; i < sampleSize.length; ++i) {
                if (sampleSize[i] == this.bitDepth) continue;
                throw new RuntimeException(PropertyUtil.getString("PNGImageEncoder0"));
            }
            if (this.bitDepth > 2 && this.bitDepth < 4) {
                this.bitDepth = 4;
            } else if (this.bitDepth > 4 && this.bitDepth < 8) {
                this.bitDepth = 8;
            } else if (this.bitDepth > 8 && this.bitDepth < 16) {
                this.bitDepth = 16;
            } else if (this.bitDepth > 16) {
                throw new RuntimeException(PropertyUtil.getString("PNGImageEncoder1"));
            }
        }
        this.numBands = sampleModel.getNumBands();
        this.bpp = this.numBands * (this.bitDepth == 16 ? 2 : 1);
        ColorModel colorModel = this.image.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            if (this.bitDepth < 1 || this.bitDepth > 8) {
                throw new RuntimeException(PropertyUtil.getString("PNGImageEncoder2"));
            }
            if (sampleModel.getNumBands() != 1) {
                throw new RuntimeException(PropertyUtil.getString("PNGImageEncoder3"));
            }
            IndexColorModel icm = (IndexColorModel)colorModel;
            int size = icm.getMapSize();
            this.redPalette = new byte[size];
            this.greenPalette = new byte[size];
            this.bluePalette = new byte[size];
            this.alphaPalette = new byte[size];
            icm.getReds(this.redPalette);
            icm.getGreens(this.greenPalette);
            icm.getBlues(this.bluePalette);
            icm.getAlphas(this.alphaPalette);
            this.bpp = 1;
            if (this.param == null) {
                this.param = this.createGrayParam(this.redPalette, this.greenPalette, this.bluePalette, this.alphaPalette);
            }
            if (this.param == null) {
                this.param = new PNGEncodeParam.Palette();
            }
            if (this.param instanceof PNGEncodeParam.Palette) {
                PNGEncodeParam.Palette parami = (PNGEncodeParam.Palette)this.param;
                if (parami.isPaletteSet()) {
                    int[] palette = parami.getPalette();
                    size = palette.length / 3;
                    int index = 0;
                    for (int i = 0; i < size; ++i) {
                        this.redPalette[i] = (byte)palette[index++];
                        this.greenPalette[i] = (byte)palette[index++];
                        this.bluePalette[i] = (byte)palette[index++];
                        this.alphaPalette[i] = -1;
                    }
                }
                this.colorType = 3;
            } else {
                if (!(this.param instanceof PNGEncodeParam.Gray)) throw new RuntimeException(PropertyUtil.getString("PNGImageEncoder4"));
                this.alphaPalette = null;
                this.bluePalette = null;
                this.greenPalette = null;
                this.redPalette = null;
                this.colorType = 0;
            }
        } else if (this.numBands == 1) {
            if (this.param == null) {
                this.param = new PNGEncodeParam.Gray();
            }
            this.colorType = 0;
        } else if (this.numBands == 2) {
            if (this.param == null) {
                this.param = new PNGEncodeParam.Gray();
            }
            if (this.param.isTransparencySet()) {
                this.skipAlpha = true;
                this.numBands = 1;
                if (sampleSize[0] == 8 && this.bitDepth < 8) {
                    this.compressGray = true;
                }
                this.bpp = this.bitDepth == 16 ? 2 : 1;
                this.colorType = 0;
            } else {
                if (this.bitDepth < 8) {
                    this.bitDepth = 8;
                }
                this.colorType = 4;
            }
        } else if (this.numBands == 3) {
            if (this.param == null) {
                this.param = new PNGEncodeParam.RGB();
            }
            this.colorType = 2;
        } else if (this.numBands == 4) {
            if (this.param == null) {
                this.param = new PNGEncodeParam.RGB();
            }
            if (this.param.isTransparencySet()) {
                this.skipAlpha = true;
                this.numBands = 3;
                this.bpp = this.bitDepth == 16 ? 6 : 3;
                this.colorType = 2;
            } else {
                this.colorType = 6;
            }
        }
        this.interlace = this.param.getInterlacing();
        this.writeMagic();
        this.writeIHDR();
        this.writeCHRM();
        this.writeGAMA();
        this.writeICCP();
        this.writeSBIT();
        this.writeSRGB();
        this.writePLTE();
        this.writeHIST();
        this.writeTRNS();
        this.writeBKGD();
        this.writePHYS();
        this.writeSPLT();
        this.writeTIME();
        this.writeTEXT();
        this.writeZTXT();
        this.writePrivateChunks();
        this.writeIDAT();
        this.writeIEND();
        this.dataOutput.flush();
        this.dataOutput.close();
    }
}

