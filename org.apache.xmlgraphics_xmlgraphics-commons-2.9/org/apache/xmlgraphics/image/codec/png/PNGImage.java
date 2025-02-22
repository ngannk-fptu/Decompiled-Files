/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.png;

import java.awt.Color;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.xmlgraphics.image.codec.png.PNGChunk;
import org.apache.xmlgraphics.image.codec.png.PNGDecodeParam;
import org.apache.xmlgraphics.image.codec.png.PNGEncodeParam;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;
import org.apache.xmlgraphics.image.codec.util.SimpleRenderedImage;
import org.apache.xmlgraphics.image.loader.impl.PNGConstants;

class PNGImage
extends SimpleRenderedImage
implements PNGConstants {
    private static final String[] colorTypeNames = new String[]{"Grayscale", "Error", "Truecolor", "Index", "Grayscale with alpha", "Error", "Truecolor with alpha"};
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
    private int[] significantBits;
    private boolean suppressAlpha;
    private boolean expandPalette;
    private boolean output8BitGray;
    private boolean outputHasAlphaPalette;
    private boolean performGammaCorrection;
    private boolean expandGrayAlpha;
    private boolean generateEncodeParam;
    private PNGDecodeParam decodeParam;
    private PNGEncodeParam encodeParam;
    private boolean emitProperties = true;
    private float fileGamma = 0.45455f;
    private float userExponent = 1.0f;
    private float displayExponent = 2.2f;
    private float[] chromaticity;
    private int sRGBRenderingIntent = -1;
    private int postProcess = 0;
    protected int xPixelsPerUnit;
    protected int yPixelsPerUnit;
    protected int unitSpecifier;
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
    private List<InputStream> streamVec = new ArrayList<InputStream>();
    private DataInputStream dataStream;
    private int bytesPerPixel;
    private int inputBands;
    private int outputBands;
    private int chunkIndex;
    private List textKeys = new ArrayList();
    private List textStrings = new ArrayList();
    private List ztextKeys = new ArrayList();
    private List ztextStrings = new ArrayList();
    private WritableRaster theTile;
    private int[] gammaLut;
    private final byte[][] expandBits = new byte[][]{null, {0, -1}, {0, 85, -86, -1}, null, {0, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1}};
    private int[] grayLut;
    private static final int[] GrayBits8 = new int[]{8};
    private static final ComponentColorModel colorModelGray8 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits8, false, false, 1, 0);
    private static final int[] GrayAlphaBits8 = new int[]{8, 8};
    private static final ComponentColorModel colorModelGrayAlpha8 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits8, true, false, 3, 0);
    private static final int[] GrayBits16 = new int[]{16};
    private static final ComponentColorModel colorModelGray16 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits16, false, false, 1, 1);
    private static final int[] GrayAlphaBits16 = new int[]{16, 16};
    private static final ComponentColorModel colorModelGrayAlpha16 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits16, true, false, 3, 1);
    private static final int[] GrayBits32 = new int[]{32};
    private static final ComponentColorModel colorModelGray32 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits32, false, false, 1, 3);
    private static final int[] GrayAlphaBits32 = new int[]{32, 32};
    private static final ComponentColorModel colorModelGrayAlpha32 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits32, true, false, 3, 3);
    private static final int[] RGBBits8 = new int[]{8, 8, 8};
    private static final ComponentColorModel colorModelRGB8 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits8, false, false, 1, 0);
    private static final int[] RGBABits8 = new int[]{8, 8, 8, 8};
    private static final ComponentColorModel colorModelRGBA8 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits8, true, false, 3, 0);
    private static final int[] RGBBits16 = new int[]{16, 16, 16};
    private static final ComponentColorModel colorModelRGB16 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits16, false, false, 1, 1);
    private static final int[] RGBABits16 = new int[]{16, 16, 16, 16};
    private static final ComponentColorModel colorModelRGBA16 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits16, true, false, 3, 1);
    private static final int[] RGBBits32 = new int[]{32, 32, 32};
    private static final ComponentColorModel colorModelRGB32 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits32, false, false, 1, 3);
    private static final int[] RGBABits32 = new int[]{32, 32, 32, 32};
    private static final ComponentColorModel colorModelRGBA32 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits32, true, false, 3, 3);

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
            System.arraycopy(this.gammaLut, 0, this.grayLut, 0, len);
        } else {
            for (int i = 0; i < len; ++i) {
                this.grayLut[i] = this.expandBits[bits][i];
            }
        }
    }

    public PNGImage(InputStream stream) throws IOException {
        DataInputStream distream = new DataInputStream(stream);
        long magic = distream.readLong();
        if (magic != -8552249625308161526L) {
            throw new IOException("Not a png file");
        }
        while (true) {
            PNGChunk chunk;
            String chunkType;
            if ((chunkType = PNGChunk.getChunkType(distream)).equals(PNGChunk.ChunkType.IHDR.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_IHDR_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.pHYs.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_pHYs_chunk(chunk);
                return;
            }
            if (chunkType.equals(PNGChunk.ChunkType.IEND.name())) {
                return;
            }
            PNGChunk.readChunk(distream);
        }
    }

    public PNGImage(InputStream stream, PNGDecodeParam decodeParam) throws IOException {
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
                String msg = PropertyUtil.getString("PNGImageDecoder0");
                throw new RuntimeException(msg);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            String msg = PropertyUtil.getString("PNGImageDecoder1");
            throw new RuntimeException(msg);
        }
        while (true) {
            PNGChunk chunk;
            String chunkType;
            if ((chunkType = PNGChunk.getChunkType(distream)).equals(PNGChunk.ChunkType.IHDR.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_IHDR_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.PLTE.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_PLTE_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.IDAT.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.streamVec.add(new ByteArrayInputStream(chunk.getData()));
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.IEND.name())) {
                PNGChunk chunk2 = PNGChunk.readChunk(distream);
                try {
                    this.parse_IEND_chunk(chunk2);
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    String msg = PropertyUtil.getString("PNGImageDecoder2");
                    throw new RuntimeException(msg);
                }
            }
            if (chunkType.equals(PNGChunk.ChunkType.bKGD.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_bKGD_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.cHRM.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_cHRM_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.gAMA.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_gAMA_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.hIST.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_hIST_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.iCCP.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_iCCP_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.pHYs.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_pHYs_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.sBIT.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_sBIT_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.sRGB.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_sRGB_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.tEXt.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_tEXt_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.tIME.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_tIME_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.tRNS.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_tRNS_chunk(chunk);
                continue;
            }
            if (chunkType.equals(PNGChunk.ChunkType.zTXt.name())) {
                chunk = PNGChunk.readChunk(distream);
                this.parse_zTXt_chunk(chunk);
                continue;
            }
            chunk = PNGChunk.readChunk(distream);
            String type = chunk.getTypeString();
            byte[] data = chunk.getData();
            if (this.encodeParam != null) {
                this.encodeParam.addPrivateChunk(type, data);
            }
            if (!this.emitProperties) continue;
            String key = "chunk_" + this.chunkIndex++ + ':' + type;
            this.properties.put(key.toLowerCase(Locale.getDefault()), data);
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

    private void parse_IHDR_chunk(PNGChunk chunk) {
        this.tileWidth = this.width = chunk.getInt4(0);
        this.tileHeight = this.height = chunk.getInt4(4);
        this.bitDepth = chunk.getInt1(8);
        if (this.bitDepth != 1 && this.bitDepth != 2 && this.bitDepth != 4 && this.bitDepth != 8 && this.bitDepth != 16) {
            String msg = PropertyUtil.getString("PNGImageDecoder3");
            throw new RuntimeException(msg);
        }
        this.maxOpacity = (1 << this.bitDepth) - 1;
        this.colorType = chunk.getInt1(9);
        if (this.colorType != 0 && this.colorType != 2 && this.colorType != 3 && this.colorType != 4 && this.colorType != 6) {
            System.out.println(PropertyUtil.getString("PNGImageDecoder4"));
        }
        if (this.colorType == 2 && this.bitDepth < 8) {
            String msg = PropertyUtil.getString("PNGImageDecoder5");
            throw new RuntimeException(msg);
        }
        if (this.colorType == 3 && this.bitDepth == 16) {
            String msg = PropertyUtil.getString("PNGImageDecoder6");
            throw new RuntimeException(msg);
        }
        if (this.colorType == 4 && this.bitDepth < 8) {
            String msg = PropertyUtil.getString("PNGImageDecoder7");
            throw new RuntimeException(msg);
        }
        if (this.colorType == 6 && this.bitDepth < 8) {
            String msg = PropertyUtil.getString("PNGImageDecoder8");
            throw new RuntimeException(msg);
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
            this.properties.put("bit_depth", this.bitDepth);
        }
        if (this.performGammaCorrection) {
            float gamma = 0.45454544f * (this.displayExponent / this.userExponent);
            if (this.encodeParam != null) {
                this.encodeParam.setGamma(gamma);
            }
            if (this.emitProperties) {
                this.properties.put("gamma", Float.valueOf(gamma));
            }
        }
        this.compressionMethod = chunk.getInt1(10);
        if (this.compressionMethod != 0) {
            String msg = PropertyUtil.getString("PNGImageDecoder9");
            throw new RuntimeException(msg);
        }
        this.filterMethod = chunk.getInt1(11);
        if (this.filterMethod != 0) {
            String msg = PropertyUtil.getString("PNGImageDecoder10");
            throw new RuntimeException(msg);
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
            String msg = PropertyUtil.getString("PNGImageDecoder11");
            throw new RuntimeException(msg);
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
            String key = (String)this.textKeys.get(i);
            String val = (String)this.textStrings.get(i);
            textArray[2 * i] = key;
            textArray[2 * i + 1] = val;
            if (!this.emitProperties) continue;
            String uniqueKey = "text_" + i + ':' + key;
            this.properties.put(uniqueKey.toLowerCase(Locale.getDefault()), val);
        }
        if (this.encodeParam != null) {
            this.encodeParam.setText(textArray);
        }
        int ztextLen = this.ztextKeys.size();
        String[] ztextArray = new String[2 * ztextLen];
        for (int i = 0; i < ztextLen; ++i) {
            String key = (String)this.ztextKeys.get(i);
            String val = (String)this.ztextStrings.get(i);
            ztextArray[2 * i] = key;
            ztextArray[2 * i + 1] = val;
            if (!this.emitProperties) continue;
            String uniqueKey = "ztext_" + i + ':' + key;
            this.properties.put(uniqueKey.toLowerCase(Locale.getDefault()), val);
        }
        if (this.encodeParam != null) {
            this.encodeParam.setCompressedText(ztextArray);
        }
        SequenceInputStream seqStream = new SequenceInputStream(Collections.enumeration(this.streamVec));
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
            this.colorModel = PNGImage.createComponentColorModel(this.sampleModel);
        }
    }

    public static ColorModel createComponentColorModel(SampleModel sm) {
        int type = sm.getDataType();
        int bands = sm.getNumBands();
        ComponentColorModel cm = null;
        if (type == 0) {
            switch (bands) {
                case 1: {
                    cm = colorModelGray8;
                    break;
                }
                case 2: {
                    cm = colorModelGrayAlpha8;
                    break;
                }
                case 3: {
                    cm = colorModelRGB8;
                    break;
                }
                case 4: {
                    cm = colorModelRGBA8;
                }
            }
        } else if (type == 1) {
            switch (bands) {
                case 1: {
                    cm = colorModelGray16;
                    break;
                }
                case 2: {
                    cm = colorModelGrayAlpha16;
                    break;
                }
                case 3: {
                    cm = colorModelRGB16;
                    break;
                }
                case 4: {
                    cm = colorModelRGBA16;
                }
            }
        } else if (type == 3) {
            switch (bands) {
                case 1: {
                    cm = colorModelGray32;
                    break;
                }
                case 2: {
                    cm = colorModelGrayAlpha32;
                    break;
                }
                case 3: {
                    cm = colorModelRGB32;
                    break;
                }
                case 4: {
                    cm = colorModelRGBA32;
                }
            }
        }
        return cm;
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
                this.bkgdRed = chunk.getInt2(0);
                this.bkgdGreen = chunk.getInt2(2);
                this.bkgdBlue = chunk.getInt2(4);
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
            this.properties.put("white_point_x", Float.valueOf(this.chromaticity[0]));
            this.properties.put("white_point_y", Float.valueOf(this.chromaticity[1]));
            this.properties.put("red_x", Float.valueOf(this.chromaticity[2]));
            this.properties.put("red_y", Float.valueOf(this.chromaticity[3]));
            this.properties.put("green_x", Float.valueOf(this.chromaticity[4]));
            this.properties.put("green_y", Float.valueOf(this.chromaticity[5]));
            this.properties.put("blue_x", Float.valueOf(this.chromaticity[6]));
            this.properties.put("blue_y", Float.valueOf(this.chromaticity[7]));
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
            this.properties.put("gamma", Float.valueOf(this.fileGamma * exp));
        }
    }

    private void parse_hIST_chunk(PNGChunk chunk) {
        if (this.redPalette == null) {
            String msg = PropertyUtil.getString("PNGImageDecoder18");
            throw new RuntimeException(msg);
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
    }

    private void parse_pHYs_chunk(PNGChunk chunk) {
        this.xPixelsPerUnit = chunk.getInt4(0);
        this.yPixelsPerUnit = chunk.getInt4(4);
        this.unitSpecifier = chunk.getInt1(8);
        if (this.encodeParam != null) {
            this.encodeParam.setPhysicalDimension(this.xPixelsPerUnit, this.yPixelsPerUnit, this.unitSpecifier);
        }
        if (this.emitProperties) {
            this.properties.put("x_pixels_per_unit", this.xPixelsPerUnit);
            this.properties.put("y_pixels_per_unit", this.yPixelsPerUnit);
            this.properties.put("pixel_aspect_ratio", Float.valueOf((float)this.xPixelsPerUnit / (float)this.yPixelsPerUnit));
            if (this.unitSpecifier == 1) {
                this.properties.put("pixel_units", "Meters");
            } else if (this.unitSpecifier != 0) {
                String msg = PropertyUtil.getString("PNGImageDecoder12");
                throw new RuntimeException(msg);
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
                String msg = PropertyUtil.getString("PNGImageDecoder13");
                throw new RuntimeException(msg);
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
                this.properties.put("gamma", Float.valueOf(gamma));
                this.properties.put("white_point_x", Float.valueOf(this.chromaticity[0]));
                this.properties.put("white_point_y", Float.valueOf(this.chromaticity[1]));
                this.properties.put("red_x", Float.valueOf(this.chromaticity[2]));
                this.properties.put("red_y", Float.valueOf(this.chromaticity[3]));
                this.properties.put("green_x", Float.valueOf(this.chromaticity[4]));
                this.properties.put("green_y", Float.valueOf(this.chromaticity[5]));
                this.properties.put("blue_x", Float.valueOf(this.chromaticity[6]));
                this.properties.put("blue_y", Float.valueOf(this.chromaticity[7]));
            }
        }
    }

    private void parse_tEXt_chunk(PNGChunk chunk) {
        byte b;
        StringBuffer key = new StringBuffer();
        int textIndex = 0;
        while ((b = chunk.getByte(textIndex++)) != 0) {
            key.append((char)b);
        }
        StringBuilder value = new StringBuilder();
        for (int i = textIndex; i < chunk.getLength(); ++i) {
            value.append((char)chunk.getByte(i));
        }
        this.textKeys.add(key.toString());
        this.textStrings.add(value.toString());
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
                String msg = PropertyUtil.getString("PNGImageDecoder14");
                throw new RuntimeException(msg);
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
            String msg = PropertyUtil.getString("PNGImageDecoder15");
            throw new RuntimeException(msg);
        }
    }

    private void parse_zTXt_chunk(PNGChunk chunk) {
        byte b;
        int textIndex = 0;
        StringBuffer key = new StringBuffer();
        while ((b = chunk.getByte(textIndex++)) != 0) {
            key.append((char)b);
        }
        ++textIndex;
        StringBuffer value = new StringBuffer();
        try {
            int c;
            int length = chunk.getLength() - textIndex;
            byte[] data = chunk.getData();
            ByteArrayInputStream cis = new ByteArrayInputStream(data, textIndex, length);
            InflaterInputStream iis = new InflaterInputStream(cis);
            while ((c = ((InputStream)iis).read()) != -1) {
                value.append((char)c);
            }
            this.ztextKeys.add(key.toString());
            this.ztextStrings.add(value.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
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
            curr[i] = (byte)(raw + PNGEncodeParam.paethPredictor(priorPixel, priorRow, priorRowPixel));
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
                e.printStackTrace();
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
                    String msg = PropertyUtil.getString("PNGImageDecoder16");
                    throw new RuntimeException(msg);
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

    @Override
    public Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            String msg = PropertyUtil.getString("PNGImageDecoder17");
            throw new IllegalArgumentException(msg);
        }
        return this.theTile;
    }
}

