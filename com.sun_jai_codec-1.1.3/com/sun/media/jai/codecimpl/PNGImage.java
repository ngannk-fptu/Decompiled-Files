/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.PNGChunk;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.Color;
import java.awt.Point;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class PNGImage
extends SimpleRenderedImage {
    public static final int PNG_COLOR_GRAY = 0;
    public static final int PNG_COLOR_RGB = 2;
    public static final int PNG_COLOR_PALETTE = 3;
    public static final int PNG_COLOR_GRAY_ALPHA = 4;
    public static final int PNG_COLOR_RGB_ALPHA = 6;
    private static final String[] colorTypeNames = new String[]{"Grayscale", "Error", "Truecolor", "Index", "Grayscale with alpha", "Error", "Truecolor with alpha"};
    public static final int PNG_FILTER_NONE = 0;
    public static final int PNG_FILTER_SUB = 1;
    public static final int PNG_FILTER_UP = 2;
    public static final int PNG_FILTER_AVERAGE = 3;
    public static final int PNG_FILTER_PAETH = 4;
    private static final int RED_OFFSET = 2;
    private static final int GREEN_OFFSET = 1;
    private static final int BLUE_OFFSET = 0;
    private int[][] bandOffsets = new int[][]{null, {0}, {0, 1}, {0, 1, 2}, {0, 1, 2, 3}};
    private int bitDepth;
    private int colorType;
    private int compressionMethod;
    private int filterMethod;
    private int interlaceMethod;
    private int paletteEntries;
    private byte[] redPalette;
    private byte[] greenPalette;
    private byte[] bluePalette;
    private byte[] alphaPalette;
    private int bkgdRed;
    private int bkgdGreen;
    private int bkgdBlue;
    private int grayTransparentAlpha;
    private int redTransparentAlpha;
    private int greenTransparentAlpha;
    private int blueTransparentAlpha;
    private int maxOpacity;
    private int[] significantBits = null;
    private boolean hasBackground = false;
    private boolean suppressAlpha = false;
    private boolean expandPalette = false;
    private boolean output8BitGray = false;
    private boolean outputHasAlphaPalette = false;
    private boolean performGammaCorrection = false;
    private boolean expandGrayAlpha = false;
    private boolean generateEncodeParam = false;
    private PNGDecodeParam decodeParam = null;
    private PNGEncodeParam encodeParam = null;
    private boolean emitProperties = true;
    private float fileGamma = 0.45455f;
    private float userExponent = 1.0f;
    private float displayExponent = 2.2f;
    private float[] chromaticity = null;
    private int sRGBRenderingIntent = -1;
    private ICC_Profile iccProfile = null;
    private String iccProfileName = null;
    private int postProcess = 0;
    private static final int POST_NONE = 0;
    private static final int POST_GAMMA = 1;
    private static final int POST_GRAY_LUT = 2;
    private static final int POST_GRAY_LUT_ADD_TRANS = 3;
    private static final int POST_PALETTE_TO_RGB = 4;
    private static final int POST_PALETTE_TO_RGBA = 5;
    private static final int POST_ADD_GRAY_TRANS = 6;
    private static final int POST_ADD_RGB_TRANS = 7;
    private static final int POST_REMOVE_GRAY_TRANS = 8;
    private static final int POST_REMOVE_RGB_TRANS = 9;
    private static final int POST_EXP_MASK = 16;
    private static final int POST_GRAY_ALPHA_EXP = 16;
    private static final int POST_GAMMA_EXP = 17;
    private static final int POST_GRAY_LUT_ADD_TRANS_EXP = 19;
    private static final int POST_ADD_GRAY_TRANS_EXP = 22;
    private Vector streamVec = new Vector();
    private DataInputStream dataStream;
    private int bytesPerPixel;
    private int inputBands;
    private int outputBands;
    private int chunkIndex = 0;
    private Vector textKeys = new Vector();
    private Vector textStrings = new Vector();
    private Vector ztextKeys = new Vector();
    private Vector ztextStrings = new Vector();
    private WritableRaster theTile;
    private int[] gammaLut = null;
    private final byte[][] expandBits = new byte[][]{null, {0, -1}, {0, 85, -86, -1}, null, {0, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1}};
    private int[] grayLut = null;
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$PNGImageDecoder;

    private void initGammaLut(int bits) {
        double exp = (double)this.userExponent / (double)(this.fileGamma * this.displayExponent);
        int numSamples = 1 << bits;
        int maxOutSample = bits == 16 ? 65535 : 255;
        this.gammaLut = new int[numSamples];
        for (int i = 0; i < numSamples; ++i) {
            double gbright = (double)i / (double)(numSamples - 1);
            double gamma = Math.pow(gbright, exp);
            int igamma = (int)(gamma * (double)maxOutSample + 0.5);
            if (igamma > maxOutSample) {
                igamma = maxOutSample;
            }
            this.gammaLut[i] = igamma;
        }
    }

    private void initGrayLut(int bits) {
        int len = 1 << bits;
        this.grayLut = new int[len];
        if (this.performGammaCorrection) {
            for (int i = 0; i < len; ++i) {
                this.grayLut[i] = this.gammaLut[i];
            }
        } else {
            for (int i = 0; i < len; ++i) {
                this.grayLut[i] = this.expandBits[bits][i];
            }
        }
    }

    public PNGImage(InputStream stream, PNGDecodeParam decodeParam) throws IOException {
        String message;
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        DataInputStream distream = new DataInputStream(stream);
        if (decodeParam == null) {
            decodeParam = new PNGDecodeParam();
        }
        this.decodeParam = decodeParam;
        this.suppressAlpha = decodeParam.getSuppressAlpha();
        this.expandPalette = decodeParam.getExpandPalette();
        this.output8BitGray = decodeParam.getOutput8BitGray();
        this.expandGrayAlpha = decodeParam.getExpandGrayAlpha();
        if (decodeParam.getPerformGammaCorrection()) {
            this.userExponent = decodeParam.getUserExponent();
            this.displayExponent = decodeParam.getDisplayExponent();
            this.performGammaCorrection = true;
            this.output8BitGray = true;
        }
        this.generateEncodeParam = decodeParam.getGenerateEncodeParam();
        if (this.emitProperties) {
            this.properties.put("file_type", "PNG v. 1.0");
        }
        try {
            long magic = distream.readLong();
            if (magic != -8552249625308161526L) {
                String msg = JaiI18N.getString("PNGImageDecoder0");
                throw new RuntimeException(msg);
            }
        }
        catch (Exception e) {
            message = JaiI18N.getString("PNGImageDecoder1");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
        }
        block4: while (true) {
            try {
                while (true) {
                    PNGChunk chunk;
                    String chunkType;
                    if ((chunkType = PNGImage.getChunkType(distream)).equals("IHDR")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_IHDR_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("PLTE")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_PLTE_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("IDAT")) {
                        chunk = PNGImage.readChunk(distream);
                        this.streamVec.add(new ByteArrayInputStream(chunk.getData()));
                        continue;
                    }
                    if (chunkType.equals("IEND")) {
                        PNGChunk chunk2 = PNGImage.readChunk(distream);
                        this.parse_IEND_chunk(chunk2);
                        break block4;
                    }
                    if (chunkType.equals("bKGD")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_bKGD_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("cHRM")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_cHRM_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("gAMA")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_gAMA_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("hIST")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_hIST_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("iCCP")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_iCCP_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("pHYs")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_pHYs_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("sBIT")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_sBIT_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("sRGB")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_sRGB_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("tEXt")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_tEXt_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("tIME")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_tIME_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("tRNS")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_tRNS_chunk(chunk);
                        continue;
                    }
                    if (chunkType.equals("zTXt")) {
                        chunk = PNGImage.readChunk(distream);
                        this.parse_zTXt_chunk(chunk);
                        continue;
                    }
                    chunk = PNGImage.readChunk(distream);
                    String type = chunk.getTypeString();
                    byte[] data = chunk.getData();
                    if (this.encodeParam != null) {
                        this.encodeParam.addPrivateChunk(type, data);
                    }
                    if (!this.emitProperties) continue;
                    String key = "chunk_" + this.chunkIndex++ + ":" + type;
                    this.properties.put(key.toLowerCase(), data);
                }
            }
            catch (Exception e) {
                message = JaiI18N.getString("PNGImageDecoder2");
                ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
                continue;
            }
            break;
        }
        if (this.significantBits == null) {
            this.significantBits = new int[this.inputBands];
            for (int i = 0; i < this.inputBands; ++i) {
                this.significantBits[i] = this.bitDepth;
            }
            if (this.emitProperties) {
                this.properties.put("significant_bits", this.significantBits);
            }
        }
    }

    private static String getChunkType(DataInputStream distream) {
        try {
            distream.mark(8);
            int length = distream.readInt();
            int type = distream.readInt();
            distream.reset();
            String typeString = new String();
            typeString = typeString + (char)(type >> 24);
            typeString = typeString + (char)(type >> 16 & 0xFF);
            typeString = typeString + (char)(type >> 8 & 0xFF);
            typeString = typeString + (char)(type & 0xFF);
            return typeString;
        }
        catch (Exception e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PNGImageDecoder20"), e, class$com$sun$media$jai$codecimpl$PNGImageDecoder == null ? (class$com$sun$media$jai$codecimpl$PNGImageDecoder = PNGImage.class$("com.sun.media.jai.codecimpl.PNGImageDecoder")) : class$com$sun$media$jai$codecimpl$PNGImageDecoder, false);
            return null;
        }
    }

    private static PNGChunk readChunk(DataInputStream distream) {
        try {
            int length = distream.readInt();
            int type = distream.readInt();
            byte[] data = new byte[length];
            distream.readFully(data);
            int crc = distream.readInt();
            return new PNGChunk(length, type, data, crc);
        }
        catch (Exception e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PNGImageDecoder21"), e, class$com$sun$media$jai$codecimpl$PNGImageDecoder == null ? (class$com$sun$media$jai$codecimpl$PNGImageDecoder = PNGImage.class$("com.sun.media.jai.codecimpl.PNGImageDecoder")) : class$com$sun$media$jai$codecimpl$PNGImageDecoder, false);
            return null;
        }
    }

    private void parse_IHDR_chunk(PNGChunk chunk) {
        this.tileWidth = this.width = chunk.getInt4(0);
        this.tileHeight = this.height = chunk.getInt4(4);
        this.bitDepth = chunk.getInt1(8);
        if (this.bitDepth != 1 && this.bitDepth != 2 && this.bitDepth != 4 && this.bitDepth != 8 && this.bitDepth != 16) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder3"));
        }
        this.maxOpacity = (1 << this.bitDepth) - 1;
        this.colorType = chunk.getInt1(9);
        if (this.colorType != 0 && this.colorType != 2 && this.colorType != 3 && this.colorType != 4 && this.colorType != 6) {
            System.out.println(JaiI18N.getString("PNGImageDecoder4"));
        }
        if (this.colorType == 2 && this.bitDepth < 8) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder5"));
        }
        if (this.colorType == 3 && this.bitDepth == 16) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder6"));
        }
        if (this.colorType == 4 && this.bitDepth < 8) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder7"));
        }
        if (this.colorType == 6 && this.bitDepth < 8) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder8"));
        }
        if (this.emitProperties) {
            this.properties.put("color_type", colorTypeNames[this.colorType]);
        }
        if (this.generateEncodeParam) {
            this.encodeParam = this.colorType == 3 ? new PNGEncodeParam.Palette() : (this.colorType == 0 || this.colorType == 4 ? new PNGEncodeParam.Gray() : new PNGEncodeParam.RGB());
            this.decodeParam.setEncodeParam(this.encodeParam);
        }
        if (this.encodeParam != null) {
            this.encodeParam.setBitDepth(this.bitDepth);
        }
        if (this.emitProperties) {
            this.properties.put("bit_depth", new Integer(this.bitDepth));
        }
        if (this.performGammaCorrection) {
            float gamma = 0.45454544f * (this.displayExponent / this.userExponent);
            if (this.encodeParam != null) {
                this.encodeParam.setGamma(gamma);
            }
            if (this.emitProperties) {
                this.properties.put("gamma", new Float(gamma));
            }
        }
        this.compressionMethod = chunk.getInt1(10);
        if (this.compressionMethod != 0) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder9"));
        }
        this.filterMethod = chunk.getInt1(11);
        if (this.filterMethod != 0) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder10"));
        }
        this.interlaceMethod = chunk.getInt1(12);
        if (this.interlaceMethod == 0) {
            if (this.encodeParam != null) {
                this.encodeParam.setInterlacing(false);
            }
            if (this.emitProperties) {
                this.properties.put("interlace_method", "None");
            }
        } else if (this.interlaceMethod == 1) {
            if (this.encodeParam != null) {
                this.encodeParam.setInterlacing(true);
            }
            if (this.emitProperties) {
                this.properties.put("interlace_method", "Adam7");
            }
        } else {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder11"));
        }
        this.bytesPerPixel = this.bitDepth == 16 ? 2 : 1;
        switch (this.colorType) {
            case 0: {
                this.inputBands = 1;
                this.outputBands = 1;
                if (this.output8BitGray && this.bitDepth < 8) {
                    this.postProcess = 2;
                    break;
                }
                if (this.performGammaCorrection) {
                    this.postProcess = 1;
                    break;
                }
                this.postProcess = 0;
                break;
            }
            case 2: {
                this.inputBands = 3;
                this.bytesPerPixel *= 3;
                this.outputBands = 3;
                if (this.performGammaCorrection) {
                    this.postProcess = 1;
                    break;
                }
                this.postProcess = 0;
                break;
            }
            case 3: {
                this.inputBands = 1;
                this.bytesPerPixel = 1;
                int n = this.outputBands = this.expandPalette ? 3 : 1;
                if (this.expandPalette) {
                    this.postProcess = 4;
                    break;
                }
                this.postProcess = 0;
                break;
            }
            case 4: {
                this.inputBands = 2;
                this.bytesPerPixel *= 2;
                if (this.suppressAlpha) {
                    this.outputBands = 1;
                    this.postProcess = 8;
                    break;
                }
                this.postProcess = this.performGammaCorrection ? 1 : 0;
                if (this.expandGrayAlpha) {
                    this.postProcess |= 0x10;
                    this.outputBands = 4;
                    break;
                }
                this.outputBands = 2;
                break;
            }
            case 6: {
                this.inputBands = 4;
                this.bytesPerPixel *= 4;
                int n = this.outputBands = !this.suppressAlpha ? 4 : 3;
                this.postProcess = this.suppressAlpha ? 9 : (this.performGammaCorrection ? 1 : 0);
            }
        }
    }

    private void parse_IEND_chunk(PNGChunk chunk) throws Exception {
        int textLen = this.textKeys.size();
        String[] textArray = new String[2 * textLen];
        for (int i = 0; i < textLen; ++i) {
            String key = (String)this.textKeys.elementAt(i);
            String val = (String)this.textStrings.elementAt(i);
            textArray[2 * i] = key;
            textArray[2 * i + 1] = val;
            if (!this.emitProperties) continue;
            String uniqueKey = "text_" + i + ":" + key;
            this.properties.put(uniqueKey.toLowerCase(), val);
        }
        if (this.encodeParam != null) {
            this.encodeParam.setText(textArray);
        }
        int ztextLen = this.ztextKeys.size();
        String[] ztextArray = new String[2 * ztextLen];
        for (int i = 0; i < ztextLen; ++i) {
            String key = (String)this.ztextKeys.elementAt(i);
            String val = (String)this.ztextStrings.elementAt(i);
            ztextArray[2 * i] = key;
            ztextArray[2 * i + 1] = val;
            if (!this.emitProperties) continue;
            String uniqueKey = "ztext_" + i + ":" + key;
            this.properties.put(uniqueKey.toLowerCase(), val);
        }
        if (this.encodeParam != null) {
            this.encodeParam.setCompressedText(ztextArray);
        }
        if (this.sRGBRenderingIntent != -1 && this.iccProfile != null) {
            this.iccProfile = null;
        }
        if (this.encodeParam != null && this.iccProfile != null) {
            this.encodeParam.setICCProfileData(this.iccProfile.getData());
            this.encodeParam.setICCProfileName(this.iccProfileName);
        }
        SequenceInputStream seqStream = new SequenceInputStream(this.streamVec.elements());
        InflaterInputStream infStream = new InflaterInputStream(seqStream, new Inflater());
        this.dataStream = new DataInputStream(infStream);
        int depth = this.bitDepth;
        if (this.colorType == 0 && this.bitDepth < 8 && this.output8BitGray) {
            depth = 8;
        }
        if (this.colorType == 3 && this.expandPalette) {
            depth = 8;
        }
        int bytesPerRow = (this.outputBands * this.width * depth + 7) / 8;
        int scanlineStride = depth == 16 ? bytesPerRow / 2 : bytesPerRow;
        this.theTile = this.createRaster(this.width, this.height, this.outputBands, scanlineStride, depth);
        if (this.performGammaCorrection && this.gammaLut == null) {
            this.initGammaLut(this.bitDepth);
        }
        if (this.postProcess == 2 || this.postProcess == 3 || this.postProcess == 19) {
            this.initGrayLut(this.bitDepth);
        }
        this.decodeImage(this.interlaceMethod == 1);
        this.sampleModel = this.theTile.getSampleModel();
        if (this.colorType == 3 && !this.expandPalette) {
            this.colorModel = this.outputHasAlphaPalette ? new IndexColorModel(this.bitDepth, this.paletteEntries, this.redPalette, this.greenPalette, this.bluePalette, this.alphaPalette) : new IndexColorModel(this.bitDepth, this.paletteEntries, this.redPalette, this.greenPalette, this.bluePalette);
        } else if (this.colorType == 0 && this.bitDepth < 8 && !this.output8BitGray) {
            byte[] palette = this.expandBits[this.bitDepth];
            this.colorModel = new IndexColorModel(this.bitDepth, palette.length, palette, palette, palette);
        } else {
            this.colorModel = ImageCodec.createComponentColorModel(this.sampleModel, this.iccProfile == null ? null : new ICC_ColorSpace(this.iccProfile));
        }
    }

    private void parse_PLTE_chunk(PNGChunk chunk) {
        this.paletteEntries = chunk.getLength() / 3;
        this.redPalette = new byte[this.paletteEntries];
        this.greenPalette = new byte[this.paletteEntries];
        this.bluePalette = new byte[this.paletteEntries];
        int pltIndex = 0;
        if (this.performGammaCorrection) {
            if (this.gammaLut == null) {
                this.initGammaLut(this.bitDepth == 16 ? 16 : 8);
            }
            for (int i = 0; i < this.paletteEntries; ++i) {
                byte r = chunk.getByte(pltIndex++);
                byte g = chunk.getByte(pltIndex++);
                byte b = chunk.getByte(pltIndex++);
                this.redPalette[i] = (byte)this.gammaLut[r & 0xFF];
                this.greenPalette[i] = (byte)this.gammaLut[g & 0xFF];
                this.bluePalette[i] = (byte)this.gammaLut[b & 0xFF];
            }
        } else {
            for (int i = 0; i < this.paletteEntries; ++i) {
                this.redPalette[i] = chunk.getByte(pltIndex++);
                this.greenPalette[i] = chunk.getByte(pltIndex++);
                this.bluePalette[i] = chunk.getByte(pltIndex++);
            }
        }
    }

    private void parse_bKGD_chunk(PNGChunk chunk) {
        this.hasBackground = true;
        switch (this.colorType) {
            case 3: {
                int bkgdIndex = chunk.getByte(0) & 0xFF;
                this.bkgdRed = this.redPalette[bkgdIndex] & 0xFF;
                this.bkgdGreen = this.greenPalette[bkgdIndex] & 0xFF;
                this.bkgdBlue = this.bluePalette[bkgdIndex] & 0xFF;
                if (this.encodeParam == null) break;
                ((PNGEncodeParam.Palette)this.encodeParam).setBackgroundPaletteIndex(bkgdIndex);
                break;
            }
            case 0: 
            case 4: {
                int bkgdGray;
                this.bkgdGreen = this.bkgdBlue = (bkgdGray = chunk.getInt2(0));
                this.bkgdRed = this.bkgdBlue;
                if (this.encodeParam == null) break;
                ((PNGEncodeParam.Gray)this.encodeParam).setBackgroundGray(bkgdGray);
                break;
            }
            case 2: 
            case 6: {
                int mask = (1 << this.bitDepth) - 1;
                this.bkgdRed = chunk.getInt2(0) & mask;
                this.bkgdGreen = chunk.getInt2(2) & mask;
                this.bkgdBlue = chunk.getInt2(4) & mask;
                int[] bkgdRGB = new int[]{this.bkgdRed, this.bkgdGreen, this.bkgdBlue};
                if (this.encodeParam == null) break;
                ((PNGEncodeParam.RGB)this.encodeParam).setBackgroundRGB(bkgdRGB);
            }
        }
        int r = 0;
        int g = 0;
        int b = 0;
        if (this.bitDepth < 8) {
            r = this.expandBits[this.bitDepth][this.bkgdRed];
            g = this.expandBits[this.bitDepth][this.bkgdGreen];
            b = this.expandBits[this.bitDepth][this.bkgdBlue];
        } else if (this.bitDepth == 8) {
            r = this.bkgdRed;
            g = this.bkgdGreen;
            b = this.bkgdBlue;
        } else if (this.bitDepth == 16) {
            r = this.bkgdRed >> 8;
            g = this.bkgdGreen >> 8;
            b = this.bkgdBlue >> 8;
        }
        if (this.emitProperties) {
            this.properties.put("background_color", new Color(r, g, b));
        }
    }

    private void parse_cHRM_chunk(PNGChunk chunk) {
        if (this.sRGBRenderingIntent != -1) {
            return;
        }
        this.chromaticity = new float[8];
        this.chromaticity[0] = (float)chunk.getInt4(0) / 100000.0f;
        this.chromaticity[1] = (float)chunk.getInt4(4) / 100000.0f;
        this.chromaticity[2] = (float)chunk.getInt4(8) / 100000.0f;
        this.chromaticity[3] = (float)chunk.getInt4(12) / 100000.0f;
        this.chromaticity[4] = (float)chunk.getInt4(16) / 100000.0f;
        this.chromaticity[5] = (float)chunk.getInt4(20) / 100000.0f;
        this.chromaticity[6] = (float)chunk.getInt4(24) / 100000.0f;
        this.chromaticity[7] = (float)chunk.getInt4(28) / 100000.0f;
        if (this.encodeParam != null) {
            this.encodeParam.setChromaticity(this.chromaticity);
        }
        if (this.emitProperties) {
            this.properties.put("white_point_x", new Float(this.chromaticity[0]));
            this.properties.put("white_point_y", new Float(this.chromaticity[1]));
            this.properties.put("red_x", new Float(this.chromaticity[2]));
            this.properties.put("red_y", new Float(this.chromaticity[3]));
            this.properties.put("green_x", new Float(this.chromaticity[4]));
            this.properties.put("green_y", new Float(this.chromaticity[5]));
            this.properties.put("blue_x", new Float(this.chromaticity[6]));
            this.properties.put("blue_y", new Float(this.chromaticity[7]));
        }
    }

    private void parse_gAMA_chunk(PNGChunk chunk) {
        float exp;
        if (this.sRGBRenderingIntent != -1) {
            return;
        }
        this.fileGamma = (float)chunk.getInt4(0) / 100000.0f;
        float f = exp = this.performGammaCorrection ? this.displayExponent / this.userExponent : 1.0f;
        if (this.encodeParam != null) {
            this.encodeParam.setGamma(this.fileGamma * exp);
        }
        if (this.emitProperties) {
            this.properties.put("gamma", new Float(this.fileGamma * exp));
        }
    }

    private void parse_hIST_chunk(PNGChunk chunk) {
        if (this.redPalette == null) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder18"));
        }
        int length = this.redPalette.length;
        int[] hist = new int[length];
        for (int i = 0; i < length; ++i) {
            hist[i] = chunk.getInt2(2 * i);
        }
        if (this.encodeParam != null) {
            this.encodeParam.setPaletteHistogram(hist);
        }
    }

    private void parse_iCCP_chunk(PNGChunk chunk) {
        byte b;
        byte[] data = new byte[80];
        int pos = 0;
        while (pos < 79 && (b = chunk.getByte(pos)) != 0) {
            data[pos++] = b;
        }
        data[pos] = 0;
        String name = new String(data);
        byte compMethod = chunk.getByte(pos++);
        InflaterInputStream infls = new InflaterInputStream(new ByteArrayInputStream(chunk.getData(), pos, chunk.getLength() - pos));
        try {
            this.iccProfile = ICC_Profile.getInstance(infls);
            this.iccProfileName = name;
        }
        catch (IOException e) {
            this.iccProfile = null;
            this.iccProfileName = null;
        }
    }

    private void parse_pHYs_chunk(PNGChunk chunk) {
        int xPixelsPerUnit = chunk.getInt4(0);
        int yPixelsPerUnit = chunk.getInt4(4);
        int unitSpecifier = chunk.getInt1(8);
        if (this.encodeParam != null) {
            this.encodeParam.setPhysicalDimension(xPixelsPerUnit, yPixelsPerUnit, unitSpecifier);
        }
        if (this.emitProperties) {
            this.properties.put("x_pixels_per_unit", new Integer(xPixelsPerUnit));
            this.properties.put("y_pixels_per_unit", new Integer(yPixelsPerUnit));
            this.properties.put("pixel_aspect_ratio", new Float((float)xPixelsPerUnit / (float)yPixelsPerUnit));
            if (unitSpecifier == 1) {
                this.properties.put("pixel_units", "Meters");
            } else if (unitSpecifier != 0) {
                throw new RuntimeException(JaiI18N.getString("PNGImageDecoder12"));
            }
        }
    }

    private void parse_sBIT_chunk(PNGChunk chunk) {
        this.significantBits = this.colorType == 3 ? new int[3] : new int[this.inputBands];
        for (int i = 0; i < this.significantBits.length; ++i) {
            int depth;
            int bits = chunk.getByte(i);
            int n = depth = this.colorType == 3 ? 8 : this.bitDepth;
            if (bits <= 0 || bits > depth) {
                throw new RuntimeException(JaiI18N.getString("PNGImageDecoder13"));
            }
            this.significantBits[i] = bits;
        }
        if (this.encodeParam != null) {
            this.encodeParam.setSignificantBits(this.significantBits);
        }
        if (this.emitProperties) {
            this.properties.put("significant_bits", this.significantBits);
        }
    }

    private void parse_sRGB_chunk(PNGChunk chunk) {
        this.sRGBRenderingIntent = chunk.getByte(0);
        this.fileGamma = 0.45455f;
        this.chromaticity = new float[8];
        this.chromaticity[0] = 3.127f;
        this.chromaticity[1] = 3.29f;
        this.chromaticity[2] = 6.4f;
        this.chromaticity[3] = 3.3f;
        this.chromaticity[4] = 3.0f;
        this.chromaticity[5] = 6.0f;
        this.chromaticity[6] = 1.5f;
        this.chromaticity[7] = 0.6f;
        if (this.performGammaCorrection) {
            float gamma = this.fileGamma * (this.displayExponent / this.userExponent);
            if (this.encodeParam != null) {
                this.encodeParam.setGamma(gamma);
                this.encodeParam.setChromaticity(this.chromaticity);
            }
            if (this.emitProperties) {
                this.properties.put("gamma", new Float(gamma));
                this.properties.put("white_point_x", new Float(this.chromaticity[0]));
                this.properties.put("white_point_y", new Float(this.chromaticity[1]));
                this.properties.put("red_x", new Float(this.chromaticity[2]));
                this.properties.put("red_y", new Float(this.chromaticity[3]));
                this.properties.put("green_x", new Float(this.chromaticity[4]));
                this.properties.put("green_y", new Float(this.chromaticity[5]));
                this.properties.put("blue_x", new Float(this.chromaticity[6]));
                this.properties.put("blue_y", new Float(this.chromaticity[7]));
            }
        }
    }

    private void parse_tEXt_chunk(PNGChunk chunk) {
        byte b;
        String key = new String();
        String value = new String();
        int textIndex = 0;
        while ((b = chunk.getByte(textIndex++)) != 0) {
            key = key + (char)b;
        }
        for (int i = textIndex; i < chunk.getLength(); ++i) {
            value = value + (char)chunk.getByte(i);
        }
        this.textKeys.add(key);
        this.textStrings.add(value);
    }

    private void parse_tIME_chunk(PNGChunk chunk) {
        int year = chunk.getInt2(0);
        int month = chunk.getInt1(2) - 1;
        int day = chunk.getInt1(3);
        int hour = chunk.getInt1(4);
        int minute = chunk.getInt1(5);
        int second = chunk.getInt1(6);
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        GregorianCalendar cal = new GregorianCalendar(gmt);
        cal.set(year, month, day, hour, minute, second);
        Date date = cal.getTime();
        if (this.encodeParam != null) {
            this.encodeParam.setModificationTime(date);
        }
        if (this.emitProperties) {
            this.properties.put("timestamp", date);
        }
    }

    private void parse_tRNS_chunk(PNGChunk chunk) {
        if (this.colorType == 3) {
            int i;
            int entries = chunk.getLength();
            if (entries > this.paletteEntries) {
                throw new RuntimeException(JaiI18N.getString("PNGImageDecoder14"));
            }
            this.alphaPalette = new byte[this.paletteEntries];
            for (i = 0; i < entries; ++i) {
                this.alphaPalette[i] = chunk.getByte(i);
            }
            for (i = entries; i < this.paletteEntries; ++i) {
                this.alphaPalette[i] = -1;
            }
            if (!this.suppressAlpha) {
                if (this.expandPalette) {
                    this.postProcess = 5;
                    this.outputBands = 4;
                } else {
                    this.outputHasAlphaPalette = true;
                }
            }
        } else if (this.colorType == 0) {
            this.grayTransparentAlpha = chunk.getInt2(0);
            if (!this.suppressAlpha) {
                if (this.bitDepth < 8) {
                    this.output8BitGray = true;
                    this.maxOpacity = 255;
                    this.postProcess = 3;
                } else {
                    this.postProcess = 6;
                }
                if (this.expandGrayAlpha) {
                    this.outputBands = 4;
                    this.postProcess |= 0x10;
                } else {
                    this.outputBands = 2;
                }
                if (this.encodeParam != null) {
                    ((PNGEncodeParam.Gray)this.encodeParam).setTransparentGray(this.grayTransparentAlpha);
                }
            }
        } else if (this.colorType == 2) {
            this.redTransparentAlpha = chunk.getInt2(0);
            this.greenTransparentAlpha = chunk.getInt2(2);
            this.blueTransparentAlpha = chunk.getInt2(4);
            if (!this.suppressAlpha) {
                this.outputBands = 4;
                this.postProcess = 7;
                if (this.encodeParam != null) {
                    int[] rgbTrans = new int[]{this.redTransparentAlpha, this.greenTransparentAlpha, this.blueTransparentAlpha};
                    ((PNGEncodeParam.RGB)this.encodeParam).setTransparentRGB(rgbTrans);
                }
            }
        } else if (this.colorType == 4 || this.colorType == 6) {
            throw new RuntimeException(JaiI18N.getString("PNGImageDecoder15"));
        }
    }

    private void parse_zTXt_chunk(PNGChunk chunk) {
        byte b;
        String key = new String();
        String value = new String();
        int textIndex = 0;
        while ((b = chunk.getByte(textIndex++)) != 0) {
            key = key + (char)b;
        }
        byte method = chunk.getByte(textIndex++);
        try {
            int c;
            int length = chunk.getLength() - textIndex;
            byte[] data = chunk.getData();
            ByteArrayInputStream cis = new ByteArrayInputStream(data, textIndex, length);
            InflaterInputStream iis = new InflaterInputStream(cis);
            while ((c = ((InputStream)iis).read()) != -1) {
                value = value + (char)c;
            }
            this.ztextKeys.add(key);
            this.ztextStrings.add(value);
        }
        catch (Exception e) {
            ImagingListenerProxy.errorOccurred(JaiI18N.getString("PNGImageDecoder21"), e, this, false);
        }
    }

    private WritableRaster createRaster(int width, int height, int bands, int scanlineStride, int bitDepth) {
        WritableRaster ras = null;
        Point origin = new Point(0, 0);
        if (bitDepth < 8 && bands == 1) {
            DataBufferByte dataBuffer = new DataBufferByte(height * scanlineStride);
            ras = Raster.createPackedRaster(dataBuffer, width, height, bitDepth, origin);
        } else if (bitDepth <= 8) {
            DataBufferByte dataBuffer = new DataBufferByte(height * scanlineStride);
            ras = Raster.createInterleavedRaster(dataBuffer, width, height, scanlineStride, bands, this.bandOffsets[bands], origin);
        } else {
            DataBufferUShort dataBuffer = new DataBufferUShort(height * scanlineStride);
            ras = Raster.createInterleavedRaster(dataBuffer, width, height, scanlineStride, bands, this.bandOffsets[bands], origin);
        }
        return ras;
    }

    private static void decodeSubFilter(byte[] curr, int count, int bpp) {
        for (int i = bpp; i < count; ++i) {
            int val = curr[i] & 0xFF;
            curr[i] = (byte)(val += curr[i - bpp] & 0xFF);
        }
    }

    private static void decodeUpFilter(byte[] curr, byte[] prev, int count) {
        for (int i = 0; i < count; ++i) {
            int raw = curr[i] & 0xFF;
            int prior = prev[i] & 0xFF;
            curr[i] = (byte)(raw + prior);
        }
    }

    private static void decodeAverageFilter(byte[] curr, byte[] prev, int count, int bpp) {
        int priorRow;
        int raw;
        int i;
        for (i = 0; i < bpp; ++i) {
            raw = curr[i] & 0xFF;
            priorRow = prev[i] & 0xFF;
            curr[i] = (byte)(raw + priorRow / 2);
        }
        for (i = bpp; i < count; ++i) {
            raw = curr[i] & 0xFF;
            int priorPixel = curr[i - bpp] & 0xFF;
            priorRow = prev[i] & 0xFF;
            curr[i] = (byte)(raw + (priorPixel + priorRow) / 2);
        }
    }

    private static int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc) {
            return a;
        }
        if (pb <= pc) {
            return b;
        }
        return c;
    }

    private static void decodePaethFilter(byte[] curr, byte[] prev, int count, int bpp) {
        int priorRow;
        int raw;
        int i;
        for (i = 0; i < bpp; ++i) {
            raw = curr[i] & 0xFF;
            priorRow = prev[i] & 0xFF;
            curr[i] = (byte)(raw + priorRow);
        }
        for (i = bpp; i < count; ++i) {
            raw = curr[i] & 0xFF;
            int priorPixel = curr[i - bpp] & 0xFF;
            priorRow = prev[i] & 0xFF;
            int priorRowPixel = prev[i - bpp] & 0xFF;
            curr[i] = (byte)(raw + PNGImage.paethPredictor(priorPixel, priorRow, priorRowPixel));
        }
    }

    private void processPixels(int process, Raster src, WritableRaster dst, int xOffset, int step, int y, int width) {
        int[] ps = src.getPixel(0, 0, (int[])null);
        int[] pd = dst.getPixel(0, 0, (int[])null);
        int dstX = xOffset;
        switch (process) {
            case 0: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    dst.setPixel(dstX, y, ps);
                    dstX += step;
                }
                break;
            }
            case 1: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    for (int i = 0; i < this.inputBands; ++i) {
                        int x = ps[i];
                        ps[i] = this.gammaLut[x];
                    }
                    dst.setPixel(dstX, y, ps);
                    dstX += step;
                }
                break;
            }
            case 2: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    pd[0] = this.grayLut[ps[0]];
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 3: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    pd[0] = this.grayLut[val];
                    pd[1] = val == this.grayTransparentAlpha ? 0 : this.maxOpacity;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 4: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    pd[0] = this.redPalette[val];
                    pd[1] = this.greenPalette[val];
                    pd[2] = this.bluePalette[val];
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 5: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    pd[0] = this.redPalette[val];
                    pd[1] = this.greenPalette[val];
                    pd[2] = this.bluePalette[val];
                    pd[3] = this.alphaPalette[val];
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 6: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    if (this.performGammaCorrection) {
                        val = this.gammaLut[val];
                    }
                    pd[0] = val;
                    pd[1] = val == this.grayTransparentAlpha ? 0 : this.maxOpacity;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 7: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int r = ps[0];
                    int g = ps[1];
                    int b = ps[2];
                    if (this.performGammaCorrection) {
                        pd[0] = this.gammaLut[r];
                        pd[1] = this.gammaLut[g];
                        pd[2] = this.gammaLut[b];
                    } else {
                        pd[0] = r;
                        pd[1] = g;
                        pd[2] = b;
                    }
                    pd[3] = r == this.redTransparentAlpha && g == this.greenTransparentAlpha && b == this.blueTransparentAlpha ? 0 : this.maxOpacity;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 8: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int g = ps[0];
                    pd[0] = this.performGammaCorrection ? this.gammaLut[g] : g;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 9: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int r = ps[0];
                    int g = ps[1];
                    int b = ps[2];
                    if (this.performGammaCorrection) {
                        pd[0] = this.gammaLut[r];
                        pd[1] = this.gammaLut[g];
                        pd[2] = this.gammaLut[b];
                    } else {
                        pd[0] = r;
                        pd[1] = g;
                        pd[2] = b;
                    }
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 17: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    int gamma;
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    int alpha = ps[1];
                    pd[0] = gamma = this.gammaLut[val];
                    pd[1] = gamma;
                    pd[2] = gamma;
                    pd[3] = alpha;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 16: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    int alpha = ps[1];
                    pd[0] = val;
                    pd[1] = val;
                    pd[2] = val;
                    pd[3] = alpha;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 22: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    if (this.performGammaCorrection) {
                        val = this.gammaLut[val];
                    }
                    pd[0] = val;
                    pd[1] = val;
                    pd[2] = val;
                    pd[3] = val == this.grayTransparentAlpha ? 0 : this.maxOpacity;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
            case 19: {
                for (int srcX = 0; srcX < width; ++srcX) {
                    int val2;
                    src.getPixel(srcX, 0, ps);
                    int val = ps[0];
                    pd[0] = val2 = this.grayLut[val];
                    pd[1] = val2;
                    pd[2] = val2;
                    pd[3] = val == this.grayTransparentAlpha ? 0 : this.maxOpacity;
                    dst.setPixel(dstX, y, pd);
                    dstX += step;
                }
                break;
            }
        }
    }

    private void decodePass(WritableRaster imRas, int xOffset, int yOffset, int xStep, int yStep, int passWidth, int passHeight) {
        if (passWidth == 0 || passHeight == 0) {
            return;
        }
        int bytesPerRow = (this.inputBands * passWidth * this.bitDepth + 7) / 8;
        int eltsPerRow = this.bitDepth == 16 ? bytesPerRow / 2 : bytesPerRow;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        WritableRaster passRow = this.createRaster(passWidth, 1, this.inputBands, eltsPerRow, this.bitDepth);
        DataBuffer dataBuffer = passRow.getDataBuffer();
        int type = dataBuffer.getDataType();
        byte[] byteData = null;
        short[] shortData = null;
        if (type == 0) {
            byteData = ((DataBufferByte)dataBuffer).getData();
        } else {
            shortData = ((DataBufferUShort)dataBuffer).getData();
        }
        int srcY = 0;
        int dstY = yOffset;
        while (srcY < passHeight) {
            int filter = 0;
            try {
                filter = this.dataStream.read();
                this.dataStream.readFully(curr, 0, bytesPerRow);
            }
            catch (Exception e) {
                ImagingListenerProxy.errorOccurred(JaiI18N.getString("PNGImageDecoder2"), e, this, false);
            }
            switch (filter) {
                case 0: {
                    break;
                }
                case 1: {
                    PNGImage.decodeSubFilter(curr, bytesPerRow, this.bytesPerPixel);
                    break;
                }
                case 2: {
                    PNGImage.decodeUpFilter(curr, prior, bytesPerRow);
                    break;
                }
                case 3: {
                    PNGImage.decodeAverageFilter(curr, prior, bytesPerRow, this.bytesPerPixel);
                    break;
                }
                case 4: {
                    PNGImage.decodePaethFilter(curr, prior, bytesPerRow, this.bytesPerPixel);
                    break;
                }
                default: {
                    throw new RuntimeException(JaiI18N.getString("PNGImageDecoder16"));
                }
            }
            if (this.bitDepth < 16) {
                System.arraycopy(curr, 0, byteData, 0, bytesPerRow);
            } else {
                int idx = 0;
                for (int j = 0; j < eltsPerRow; ++j) {
                    shortData[j] = (short)(curr[idx] << 8 | curr[idx + 1] & 0xFF);
                    idx += 2;
                }
            }
            this.processPixels(this.postProcess, passRow, imRas, xOffset, xStep, dstY, passWidth);
            byte[] tmp = prior;
            prior = curr;
            curr = tmp;
            ++srcY;
            dstY += yStep;
        }
    }

    private void decodeImage(boolean useInterlacing) {
        if (!useInterlacing) {
            this.decodePass(this.theTile, 0, 0, 1, 1, this.width, this.height);
        } else {
            this.decodePass(this.theTile, 0, 0, 8, 8, (this.width + 7) / 8, (this.height + 7) / 8);
            this.decodePass(this.theTile, 4, 0, 8, 8, (this.width + 3) / 8, (this.height + 7) / 8);
            this.decodePass(this.theTile, 0, 4, 4, 8, (this.width + 3) / 4, (this.height + 3) / 8);
            this.decodePass(this.theTile, 2, 0, 4, 4, (this.width + 1) / 4, (this.height + 3) / 4);
            this.decodePass(this.theTile, 0, 2, 2, 4, (this.width + 1) / 2, (this.height + 1) / 4);
            this.decodePass(this.theTile, 1, 0, 2, 2, this.width / 2, (this.height + 1) / 2);
            this.decodePass(this.theTile, 0, 1, 1, 2, this.width, this.height / 2);
        }
    }

    public Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("PNGImageDecoder17"));
        }
        return this.theTile;
    }

    public void dispose() {
        this.theTile = null;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

