/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.xmlgraphics.image.codec.tiff.TIFFDecodeParam;
import org.apache.xmlgraphics.image.codec.tiff.TIFFDirectory;
import org.apache.xmlgraphics.image.codec.tiff.TIFFFaxDecoder;
import org.apache.xmlgraphics.image.codec.tiff.TIFFField;
import org.apache.xmlgraphics.image.codec.tiff.TIFFLZWDecoder;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;
import org.apache.xmlgraphics.image.codec.util.SeekableStream;
import org.apache.xmlgraphics.image.rendered.AbstractRed;
import org.apache.xmlgraphics.image.rendered.CachableRed;

public class TIFFImage
extends AbstractRed {
    public static final int COMP_NONE = 1;
    public static final int COMP_FAX_G3_1D = 2;
    public static final int COMP_FAX_G3_2D = 3;
    public static final int COMP_FAX_G4_2D = 4;
    public static final int COMP_LZW = 5;
    public static final int COMP_JPEG_OLD = 6;
    public static final int COMP_JPEG_TTN2 = 7;
    public static final int COMP_PACKBITS = 32773;
    public static final int COMP_DEFLATE = 32946;
    private static final int TYPE_UNSUPPORTED = -1;
    private static final int TYPE_BILEVEL = 0;
    private static final int TYPE_GRAY_4BIT = 1;
    private static final int TYPE_GRAY = 2;
    private static final int TYPE_GRAY_ALPHA = 3;
    private static final int TYPE_PALETTE = 4;
    private static final int TYPE_RGB = 5;
    private static final int TYPE_RGB_ALPHA = 6;
    private static final int TYPE_YCBCR_SUB = 7;
    private static final int TYPE_GENERIC = 8;
    private static final int TIFF_JPEG_TABLES = 347;
    private static final int TIFF_YCBCR_SUBSAMPLING = 530;
    SeekableStream stream;
    int tileSize;
    int tilesX;
    int tilesY;
    long[] tileOffsets;
    long[] tileByteCounts;
    char[] colormap;
    int sampleSize;
    int compression;
    byte[] palette;
    int numBands;
    int chromaSubH;
    int chromaSubV;
    long tiffT4Options;
    long tiffT6Options;
    int fillOrder;
    int predictor;
    Inflater inflater;
    boolean isBigEndian;
    int imageType;
    boolean isWhiteZero;
    int dataType;
    boolean decodePaletteAsShorts;
    boolean tiled;
    private TIFFFaxDecoder decoder;
    private TIFFLZWDecoder lzwDecoder;

    private void inflate(byte[] deflated, byte[] inflated) {
        this.inflater.setInput(deflated);
        try {
            this.inflater.inflate(inflated);
        }
        catch (DataFormatException dfe) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage17") + ": " + dfe.getMessage());
        }
        this.inflater.reset();
    }

    private static SampleModel createPixelInterleavedSampleModel(int dataType, int tileWidth, int tileHeight, int bands) {
        int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; ++i) {
            bandOffsets[i] = i;
        }
        return new PixelInterleavedSampleModel(dataType, tileWidth, tileHeight, bands, tileWidth * bands, bandOffsets);
    }

    private long[] getFieldAsLongs(TIFFField field) {
        long[] value = null;
        if (field.getType() == 3) {
            char[] charValue = field.getAsChars();
            value = new long[charValue.length];
            for (int i = 0; i < charValue.length; ++i) {
                value[i] = charValue[i] & 0xFFFF;
            }
        } else if (field.getType() == 4) {
            value = field.getAsLongs();
        } else {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage18") + ": " + field.getType());
        }
        return value;
    }

    public TIFFImage(SeekableStream stream, TIFFDecodeParam param, int directory) throws IOException {
        int tileHeight;
        int tileWidth;
        int extraSamples;
        char[] planarConfiguration;
        char[] cArray;
        this.stream = stream;
        if (param == null) {
            param = new TIFFDecodeParam();
        }
        this.decodePaletteAsShorts = param.getDecodePaletteAsShorts();
        TIFFDirectory dir = param.getIFDOffset() == null ? new TIFFDirectory(stream, directory) : new TIFFDirectory(stream, param.getIFDOffset(), directory);
        TIFFField sfield = dir.getField(277);
        int samplesPerPixel = sfield == null ? 1 : (int)sfield.getAsLong(0);
        TIFFField planarConfigurationField = dir.getField(284);
        if (planarConfigurationField == null) {
            char[] cArray2 = new char[1];
            cArray = cArray2;
            cArray2[0] = '\u0001';
        } else {
            cArray = planarConfiguration = planarConfigurationField.getAsChars();
        }
        if (planarConfiguration[0] != '\u0001' && samplesPerPixel != 1) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage0"));
        }
        TIFFField bitsField = dir.getField(258);
        char[] bitsPerSample = null;
        if (bitsField != null) {
            bitsPerSample = bitsField.getAsChars();
        } else {
            bitsPerSample = new char[]{'\u0001'};
            for (int i = 1; i < bitsPerSample.length; ++i) {
                if (bitsPerSample[i] == bitsPerSample[0]) continue;
                throw new RuntimeException(PropertyUtil.getString("TIFFImage1"));
            }
        }
        this.sampleSize = bitsPerSample[0];
        TIFFField sampleFormatField = dir.getField(339);
        char[] sampleFormat = null;
        if (sampleFormatField != null) {
            sampleFormat = sampleFormatField.getAsChars();
            for (int l = 1; l < sampleFormat.length; ++l) {
                if (sampleFormat[l] == sampleFormat[0]) continue;
                throw new RuntimeException(PropertyUtil.getString("TIFFImage2"));
            }
        } else {
            sampleFormat = new char[]{'\u0001'};
        }
        boolean isValidDataFormat = false;
        switch (this.sampleSize) {
            case 1: 
            case 4: 
            case 8: {
                if (sampleFormat[0] == '\u0003') break;
                this.dataType = 0;
                isValidDataFormat = true;
                break;
            }
            case 16: {
                if (sampleFormat[0] == '\u0003') break;
                this.dataType = sampleFormat[0] == '\u0002' ? 2 : 1;
                isValidDataFormat = true;
                break;
            }
            case 32: {
                if (sampleFormat[0] == '\u0003') {
                    isValidDataFormat = false;
                    break;
                }
                this.dataType = 3;
                isValidDataFormat = true;
            }
        }
        if (!isValidDataFormat) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage3"));
        }
        TIFFField compField = dir.getField(259);
        this.compression = compField == null ? 1 : compField.getAsInt(0);
        TIFFField photometricTypeField = dir.getField(262);
        int photometricType = photometricTypeField == null ? 0 : photometricTypeField.getAsInt(0);
        this.imageType = -1;
        switch (photometricType) {
            case 0: {
                this.isWhiteZero = true;
            }
            case 1: {
                if (this.sampleSize == 1 && samplesPerPixel == 1) {
                    this.imageType = 0;
                    break;
                }
                if (this.sampleSize == 4 && samplesPerPixel == 1) {
                    this.imageType = 1;
                    break;
                }
                if (this.sampleSize % 8 != 0) break;
                if (samplesPerPixel == 1) {
                    this.imageType = 2;
                    break;
                }
                if (samplesPerPixel == 2) {
                    this.imageType = 3;
                    break;
                }
                this.imageType = 8;
                break;
            }
            case 2: {
                if (this.sampleSize % 8 != 0) break;
                if (samplesPerPixel == 3) {
                    this.imageType = 5;
                    break;
                }
                if (samplesPerPixel == 4) {
                    this.imageType = 6;
                    break;
                }
                this.imageType = 8;
                break;
            }
            case 3: {
                if (samplesPerPixel != true || this.sampleSize != 4 && this.sampleSize != 8 && this.sampleSize != 16) break;
                this.imageType = 4;
                break;
            }
            case 4: {
                if (this.sampleSize != 1 || samplesPerPixel != true) break;
                this.imageType = 0;
                break;
            }
            default: {
                if (this.sampleSize % 8 != 0) break;
                this.imageType = 8;
            }
        }
        if (this.imageType == -1) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage4") + ": " + this.imageType);
        }
        Rectangle bounds = new Rectangle(0, 0, (int)dir.getFieldAsLong(256), (int)dir.getFieldAsLong(257));
        this.numBands = samplesPerPixel;
        TIFFField efield = dir.getField(338);
        int n = extraSamples = efield == null ? 0 : (int)efield.getAsLong(0);
        if (dir.getField(324) != null) {
            this.tiled = true;
            tileWidth = (int)dir.getFieldAsLong(322);
            tileHeight = (int)dir.getFieldAsLong(323);
            this.tileOffsets = dir.getField(324).getAsLongs();
            this.tileByteCounts = this.getFieldAsLongs(dir.getField(325));
        } else {
            this.tiled = false;
            tileWidth = dir.getField(322) != null ? (int)dir.getFieldAsLong(322) : bounds.width;
            TIFFField field = dir.getField(278);
            if (field == null) {
                tileHeight = dir.getField(323) != null ? (int)dir.getFieldAsLong(323) : bounds.height;
            } else {
                long l = field.getAsLong(0);
                long infinity = 1L;
                tileHeight = l == (infinity = (infinity << 32) - 1L) ? bounds.height : (int)l;
            }
            TIFFField tileOffsetsField = dir.getField(273);
            if (tileOffsetsField == null) {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage5"));
            }
            this.tileOffsets = this.getFieldAsLongs(tileOffsetsField);
            TIFFField tileByteCountsField = dir.getField(279);
            if (tileByteCountsField == null) {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage6"));
            }
            this.tileByteCounts = this.getFieldAsLongs(tileByteCountsField);
        }
        this.tilesX = (bounds.width + tileWidth - 1) / tileWidth;
        this.tilesY = (bounds.height + tileHeight - 1) / tileHeight;
        this.tileSize = tileWidth * tileHeight * this.numBands;
        this.isBigEndian = dir.isBigEndian();
        TIFFField fillOrderField = dir.getField(266);
        this.fillOrder = fillOrderField != null ? fillOrderField.getAsInt(0) : 1;
        switch (this.compression) {
            case 1: 
            case 32773: {
                break;
            }
            case 32946: {
                this.inflater = new Inflater();
                break;
            }
            case 2: 
            case 3: 
            case 4: {
                if (this.sampleSize != 1) {
                    throw new RuntimeException(PropertyUtil.getString("TIFFImage7"));
                }
                if (this.compression == 3) {
                    TIFFField t4OptionsField = dir.getField(292);
                    this.tiffT4Options = t4OptionsField != null ? t4OptionsField.getAsLong(0) : 0L;
                }
                if (this.compression == 4) {
                    TIFFField t6OptionsField = dir.getField(293);
                    this.tiffT6Options = t6OptionsField != null ? t6OptionsField.getAsLong(0) : 0L;
                }
                this.decoder = new TIFFFaxDecoder(this.fillOrder, tileWidth, tileHeight);
                break;
            }
            case 5: {
                TIFFField predictorField = dir.getField(317);
                if (predictorField == null) {
                    this.predictor = 1;
                } else {
                    this.predictor = predictorField.getAsInt(0);
                    if (this.predictor != 1 && this.predictor != 2) {
                        throw new RuntimeException(PropertyUtil.getString("TIFFImage8"));
                    }
                    if (this.predictor == 2 && this.sampleSize != 8) {
                        throw new RuntimeException(PropertyUtil.getString("TIFFImage9"));
                    }
                }
                this.lzwDecoder = new TIFFLZWDecoder(tileWidth, this.predictor, samplesPerPixel);
                break;
            }
            case 6: {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage15"));
            }
            default: {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage10") + ": " + this.compression);
            }
        }
        ColorModel colorModel = null;
        SampleModel sampleModel = null;
        switch (this.imageType) {
            case 0: 
            case 1: {
                sampleModel = new MultiPixelPackedSampleModel(this.dataType, tileWidth, tileHeight, this.sampleSize);
                if (this.imageType == 0) {
                    byte[] map = new byte[]{(byte)(this.isWhiteZero ? 255 : 0), (byte)(this.isWhiteZero ? 0 : 255)};
                    colorModel = new IndexColorModel(1, 2, map, map, map);
                    break;
                }
                byte[] map = new byte[16];
                if (this.isWhiteZero) {
                    for (int i = 0; i < map.length; ++i) {
                        map[i] = (byte)(255 - 16 * i);
                    }
                } else {
                    for (int i = 0; i < map.length; ++i) {
                        map[i] = (byte)(16 * i);
                    }
                }
                colorModel = new IndexColorModel(4, 16, map, map, map);
                break;
            }
            case 2: 
            case 3: 
            case 5: 
            case 6: {
                int[] reverseOffsets = new int[this.numBands];
                for (int i = 0; i < this.numBands; ++i) {
                    reverseOffsets[i] = this.numBands - 1 - i;
                }
                sampleModel = new PixelInterleavedSampleModel(this.dataType, tileWidth, tileHeight, this.numBands, this.numBands * tileWidth, reverseOffsets);
                if (this.imageType == 2) {
                    colorModel = new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{this.sampleSize}, false, false, 1, this.dataType);
                    break;
                }
                if (this.imageType == 5) {
                    colorModel = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{this.sampleSize, this.sampleSize, this.sampleSize}, false, false, 1, this.dataType);
                    break;
                }
                int transparency = 1;
                if (extraSamples == 1) {
                    transparency = 3;
                } else if (extraSamples == 2) {
                    transparency = 2;
                }
                colorModel = this.createAlphaComponentColorModel(this.dataType, this.numBands, extraSamples == 1, transparency);
                break;
            }
            case 7: 
            case 8: {
                int[] bandOffsets = new int[this.numBands];
                for (int i = 0; i < this.numBands; ++i) {
                    bandOffsets[i] = i;
                }
                sampleModel = new PixelInterleavedSampleModel(this.dataType, tileWidth, tileHeight, this.numBands, this.numBands * tileWidth, bandOffsets);
                colorModel = null;
                break;
            }
            case 4: {
                TIFFField cfield = dir.getField(320);
                if (cfield == null) {
                    throw new RuntimeException(PropertyUtil.getString("TIFFImage11"));
                }
                this.colormap = cfield.getAsChars();
                if (this.decodePaletteAsShorts) {
                    this.numBands = 3;
                    if (this.dataType == 0) {
                        this.dataType = 1;
                    }
                    sampleModel = TIFFImage.createPixelInterleavedSampleModel(this.dataType, tileWidth, tileHeight, this.numBands);
                    colorModel = new ComponentColorModel(ColorSpace.getInstance(1000), new int[]{16, 16, 16}, false, false, 1, this.dataType);
                    break;
                }
                this.numBands = 1;
                if (this.sampleSize == 4) {
                    sampleModel = new MultiPixelPackedSampleModel(0, tileWidth, tileHeight, this.sampleSize);
                } else if (this.sampleSize == 8) {
                    sampleModel = TIFFImage.createPixelInterleavedSampleModel(0, tileWidth, tileHeight, this.numBands);
                } else if (this.sampleSize == 16) {
                    this.dataType = 1;
                    sampleModel = TIFFImage.createPixelInterleavedSampleModel(1, tileWidth, tileHeight, this.numBands);
                }
                int bandLength = this.colormap.length / 3;
                byte[] r = new byte[bandLength];
                byte[] g = new byte[bandLength];
                byte[] b = new byte[bandLength];
                int gIndex = bandLength;
                int bIndex = bandLength * 2;
                if (this.dataType == 2) {
                    for (int i = 0; i < bandLength; ++i) {
                        r[i] = param.decodeSigned16BitsTo8Bits((short)this.colormap[i]);
                        g[i] = param.decodeSigned16BitsTo8Bits((short)this.colormap[gIndex + i]);
                        b[i] = param.decodeSigned16BitsTo8Bits((short)this.colormap[bIndex + i]);
                    }
                } else {
                    for (int i = 0; i < bandLength; ++i) {
                        r[i] = param.decode16BitsTo8Bits(this.colormap[i] & 0xFFFF);
                        g[i] = param.decode16BitsTo8Bits(this.colormap[gIndex + i] & 0xFFFF);
                        b[i] = param.decode16BitsTo8Bits(this.colormap[bIndex + i] & 0xFFFF);
                    }
                }
                colorModel = new IndexColorModel(this.sampleSize, bandLength, r, g, b);
                break;
            }
            default: {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage4") + ": " + this.imageType);
            }
        }
        HashMap<String, TIFFDirectory> properties = new HashMap<String, TIFFDirectory>();
        properties.put("tiff_directory", dir);
        this.init((CachableRed)null, bounds, colorModel, sampleModel, 0, 0, properties);
    }

    public TIFFDirectory getPrivateIFD(long offset) throws IOException {
        return new TIFFDirectory(this.stream, offset, 0);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        this.copyToRaster(wr);
        return wr;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public synchronized Raster getTile(int tileX, int tileY) {
        byte[] data;
        if (tileX < 0 || tileX >= this.tilesX || tileY < 0 || tileY >= this.tilesY) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFImage12"));
        }
        byte[] bdata = null;
        short[] sdata = null;
        int[] idata = null;
        SampleModel sampleModel = this.getSampleModel();
        WritableRaster tile = this.makeTile(tileX, tileY);
        DataBuffer buffer = tile.getDataBuffer();
        int dataType = sampleModel.getDataType();
        if (dataType == 0) {
            bdata = ((DataBufferByte)buffer).getData();
        } else if (dataType == 1) {
            sdata = ((DataBufferUShort)buffer).getData();
        } else if (dataType == 2) {
            sdata = ((DataBufferShort)buffer).getData();
        } else if (dataType == 3) {
            idata = ((DataBufferInt)buffer).getData();
        }
        long saveOffset = 0L;
        try {
            saveOffset = this.stream.getFilePointer();
            this.stream.seek(this.tileOffsets[tileY * this.tilesX + tileX]);
        }
        catch (IOException ioe) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
        }
        int byteCount = (int)this.tileByteCounts[tileY * this.tilesX + tileX];
        Rectangle newRect = !this.tiled ? tile.getBounds() : new Rectangle(tile.getMinX(), tile.getMinY(), this.tileWidth, this.tileHeight);
        int unitsInThisTile = newRect.width * newRect.height * this.numBands;
        Object object = data = (Object)(this.compression != 1 || this.imageType == 4 ? new byte[byteCount] : null);
        if (this.imageType == 0) {
            try {
                if (this.compression == 32773) {
                    this.stream.readFully(data, 0, byteCount);
                    int bytesInThisTile = newRect.width % 8 == 0 ? newRect.width / 8 * newRect.height : (newRect.width / 8 + 1) * newRect.height;
                    this.decodePackbits(data, bytesInThisTile, bdata);
                } else if (this.compression == 5) {
                    this.stream.readFully(data, 0, byteCount);
                    this.lzwDecoder.decode(data, bdata, newRect.height);
                } else if (this.compression == 2) {
                    this.stream.readFully(data, 0, byteCount);
                    this.decoder.decode1D(bdata, data, 0, newRect.height);
                } else if (this.compression == 3) {
                    this.stream.readFully(data, 0, byteCount);
                    this.decoder.decode2D(bdata, data, 0, newRect.height, this.tiffT4Options);
                } else if (this.compression == 4) {
                    this.stream.readFully(data, 0, byteCount);
                    this.decoder.decodeT6(bdata, data, 0, newRect.height, this.tiffT6Options);
                } else if (this.compression == 32946) {
                    this.stream.readFully(data, 0, byteCount);
                    this.inflate(data, bdata);
                } else if (this.compression == 1) {
                    this.stream.readFully(bdata, 0, byteCount);
                }
                this.stream.seek(saveOffset);
                return tile;
            }
            catch (IOException ioe) {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
            }
        }
        if (this.imageType == 4) {
            if (this.sampleSize == 16) {
                if (this.decodePaletteAsShorts) {
                    short[] tempData = null;
                    int unitsBeforeLookup = unitsInThisTile / 3;
                    int entries = unitsBeforeLookup * 2;
                    try {
                        byte[] byteArray;
                        if (this.compression == 32773) {
                            this.stream.readFully(data, 0, byteCount);
                            byteArray = new byte[entries];
                            this.decodePackbits(data, entries, byteArray);
                            tempData = new short[unitsBeforeLookup];
                            this.interpretBytesAsShorts(byteArray, tempData, unitsBeforeLookup);
                        } else if (this.compression == 5) {
                            this.stream.readFully(data, 0, byteCount);
                            byteArray = new byte[entries];
                            this.lzwDecoder.decode(data, byteArray, newRect.height);
                            tempData = new short[unitsBeforeLookup];
                            this.interpretBytesAsShorts(byteArray, tempData, unitsBeforeLookup);
                        } else if (this.compression == 32946) {
                            this.stream.readFully(data, 0, byteCount);
                            byteArray = new byte[entries];
                            this.inflate(data, byteArray);
                            tempData = new short[unitsBeforeLookup];
                            this.interpretBytesAsShorts(byteArray, tempData, unitsBeforeLookup);
                        } else if (this.compression == 1) {
                            tempData = new short[byteCount / 2];
                            this.readShorts(byteCount / 2, tempData);
                        }
                        this.stream.seek(saveOffset);
                    }
                    catch (IOException ioe) {
                        throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
                    }
                    if (dataType == 1) {
                        int count = 0;
                        int len = this.colormap.length / 3;
                        int len2 = len * 2;
                        for (int i = 0; i < unitsBeforeLookup; ++i) {
                            int lookup = tempData[i] & 0xFFFF;
                            char cmapValue = this.colormap[lookup + len2];
                            sdata[count++] = (short)(cmapValue & 0xFFFF);
                            cmapValue = this.colormap[lookup + len];
                            sdata[count++] = (short)(cmapValue & 0xFFFF);
                            cmapValue = this.colormap[lookup];
                            sdata[count++] = (short)(cmapValue & 0xFFFF);
                        }
                        return tile;
                    } else {
                        if (dataType != 2) return tile;
                        int count = 0;
                        int len = this.colormap.length / 3;
                        int len2 = len * 2;
                        for (int i = 0; i < unitsBeforeLookup; ++i) {
                            int lookup = tempData[i] & 0xFFFF;
                            char cmapValue = this.colormap[lookup + len2];
                            sdata[count++] = (short)cmapValue;
                            cmapValue = this.colormap[lookup + len];
                            sdata[count++] = (short)cmapValue;
                            cmapValue = this.colormap[lookup];
                            sdata[count++] = (short)cmapValue;
                        }
                    }
                    return tile;
                }
                try {
                    if (this.compression == 32773) {
                        this.stream.readFully(data, 0, byteCount);
                        int bytesInThisTile = unitsInThisTile * 2;
                        byte[] byteArray = new byte[bytesInThisTile];
                        this.decodePackbits(data, bytesInThisTile, byteArray);
                        this.interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);
                    } else if (this.compression == 5) {
                        this.stream.readFully(data, 0, byteCount);
                        byte[] byteArray = new byte[unitsInThisTile * 2];
                        this.lzwDecoder.decode(data, byteArray, newRect.height);
                        this.interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);
                    } else if (this.compression == 32946) {
                        this.stream.readFully(data, 0, byteCount);
                        byte[] byteArray = new byte[unitsInThisTile * 2];
                        this.inflate(data, byteArray);
                        this.interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);
                    } else if (this.compression == 1) {
                        this.readShorts(byteCount / 2, sdata);
                    }
                    this.stream.seek(saveOffset);
                    return tile;
                }
                catch (IOException ioe) {
                    throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
                }
            }
            if (this.sampleSize == 8) {
                if (this.decodePaletteAsShorts) {
                    byte[] tempData = null;
                    int unitsBeforeLookup = unitsInThisTile / 3;
                    try {
                        if (this.compression == 32773) {
                            this.stream.readFully(data, 0, byteCount);
                            tempData = new byte[unitsBeforeLookup];
                            this.decodePackbits(data, unitsBeforeLookup, tempData);
                        } else if (this.compression == 5) {
                            this.stream.readFully(data, 0, byteCount);
                            tempData = new byte[unitsBeforeLookup];
                            this.lzwDecoder.decode(data, tempData, newRect.height);
                        } else if (this.compression == 32946) {
                            this.stream.readFully(data, 0, byteCount);
                            tempData = new byte[unitsBeforeLookup];
                            this.inflate(data, tempData);
                        } else {
                            if (this.compression != 1) throw new RuntimeException(PropertyUtil.getString("IFFImage10") + ": " + this.compression);
                            tempData = new byte[byteCount];
                            this.stream.readFully(tempData, 0, byteCount);
                        }
                        this.stream.seek(saveOffset);
                    }
                    catch (IOException ioe) {
                        throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
                    }
                    int count = 0;
                    int len = this.colormap.length / 3;
                    int len2 = len * 2;
                    for (int i = 0; i < unitsBeforeLookup; ++i) {
                        int lookup = tempData[i] & 0xFF;
                        char cmapValue = this.colormap[lookup + len2];
                        sdata[count++] = (short)(cmapValue & 0xFFFF);
                        cmapValue = this.colormap[lookup + len];
                        sdata[count++] = (short)(cmapValue & 0xFFFF);
                        cmapValue = this.colormap[lookup];
                        sdata[count++] = (short)(cmapValue & 0xFFFF);
                    }
                    return tile;
                }
                try {
                    if (this.compression == 32773) {
                        this.stream.readFully(data, 0, byteCount);
                        this.decodePackbits(data, unitsInThisTile, bdata);
                    } else if (this.compression == 5) {
                        this.stream.readFully(data, 0, byteCount);
                        this.lzwDecoder.decode(data, bdata, newRect.height);
                    } else if (this.compression == 32946) {
                        this.stream.readFully(data, 0, byteCount);
                        this.inflate(data, bdata);
                    } else {
                        if (this.compression != 1) throw new RuntimeException(PropertyUtil.getString("TIFFImage10") + ": " + this.compression);
                        this.stream.readFully(bdata, 0, byteCount);
                    }
                    this.stream.seek(saveOffset);
                    return tile;
                }
                catch (IOException ioe) {
                    throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
                }
            }
            if (this.sampleSize != 4) return tile;
            int padding = newRect.width % 2 == 0 ? 0 : 1;
            int bytesPostDecoding = (newRect.width / 2 + padding) * newRect.height;
            if (this.decodePaletteAsShorts) {
                byte[] tempData = null;
                try {
                    this.stream.readFully(data, 0, byteCount);
                    this.stream.seek(saveOffset);
                }
                catch (IOException ioe) {
                    throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
                }
                if (this.compression == 32773) {
                    tempData = new byte[bytesPostDecoding];
                    this.decodePackbits(data, bytesPostDecoding, tempData);
                } else if (this.compression == 5) {
                    tempData = new byte[bytesPostDecoding];
                    this.lzwDecoder.decode(data, tempData, newRect.height);
                } else if (this.compression == 32946) {
                    tempData = new byte[bytesPostDecoding];
                    this.inflate(data, tempData);
                } else if (this.compression == 1) {
                    tempData = data;
                }
                int bytes = unitsInThisTile / 3;
                data = new byte[bytes];
                int srcCount = 0;
                int dstCount = 0;
                for (int j = 0; j < newRect.height; ++j) {
                    for (int i = 0; i < newRect.width / 2; ++i) {
                        data[dstCount++] = (byte)((tempData[srcCount] & 0xF0) >> 4);
                        data[dstCount++] = (byte)(tempData[srcCount++] & 0xF);
                    }
                    if (padding != 1) continue;
                    data[dstCount++] = (byte)((tempData[srcCount++] & 0xF0) >> 4);
                }
                int len = this.colormap.length / 3;
                int len2 = len * 2;
                int count = 0;
                for (int i = 0; i < bytes; ++i) {
                    int lookup = data[i] & 0xFF;
                    char cmapValue = this.colormap[lookup + len2];
                    sdata[count++] = (short)(cmapValue & 0xFFFF);
                    cmapValue = this.colormap[lookup + len];
                    sdata[count++] = (short)(cmapValue & 0xFFFF);
                    cmapValue = this.colormap[lookup];
                    sdata[count++] = (short)(cmapValue & 0xFFFF);
                }
                return tile;
            }
            try {
                if (this.compression == 32773) {
                    this.stream.readFully(data, 0, byteCount);
                    this.decodePackbits(data, bytesPostDecoding, bdata);
                } else if (this.compression == 5) {
                    this.stream.readFully(data, 0, byteCount);
                    this.lzwDecoder.decode(data, bdata, newRect.height);
                } else if (this.compression == 32946) {
                    this.stream.readFully(data, 0, byteCount);
                    this.inflate(data, bdata);
                } else if (this.compression == 1) {
                    this.stream.readFully(bdata, 0, byteCount);
                }
                this.stream.seek(saveOffset);
                return tile;
            }
            catch (IOException ioe) {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
            }
        }
        if (this.imageType == 1) {
            try {
                if (this.compression == 32773) {
                    this.stream.readFully(data, 0, byteCount);
                    int bytesInThisTile = newRect.width % 8 == 0 ? newRect.width / 2 * newRect.height : (newRect.width / 2 + 1) * newRect.height;
                    this.decodePackbits(data, bytesInThisTile, bdata);
                } else if (this.compression == 5) {
                    this.stream.readFully(data, 0, byteCount);
                    this.lzwDecoder.decode(data, bdata, newRect.height);
                } else if (this.compression == 32946) {
                    this.stream.readFully(data, 0, byteCount);
                    this.inflate(data, bdata);
                } else {
                    this.stream.readFully(bdata, 0, byteCount);
                }
                this.stream.seek(saveOffset);
                return tile;
            }
            catch (IOException ioe) {
                throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
            }
        }
        try {
            byte[] byteArray;
            if (this.sampleSize == 8) {
                if (this.compression == 1) {
                    this.stream.readFully(bdata, 0, byteCount);
                } else if (this.compression == 5) {
                    this.stream.readFully(data, 0, byteCount);
                    this.lzwDecoder.decode(data, bdata, newRect.height);
                } else if (this.compression == 32773) {
                    this.stream.readFully(data, 0, byteCount);
                    this.decodePackbits(data, unitsInThisTile, bdata);
                } else {
                    if (this.compression != 32946) throw new RuntimeException(PropertyUtil.getString("TIFFImage10") + ": " + this.compression);
                    this.stream.readFully(data, 0, byteCount);
                    this.inflate(data, bdata);
                }
            } else if (this.sampleSize == 16) {
                if (this.compression == 1) {
                    this.readShorts(byteCount / 2, sdata);
                } else if (this.compression == 5) {
                    this.stream.readFully(data, 0, byteCount);
                    byte[] byteArray2 = new byte[unitsInThisTile * 2];
                    this.lzwDecoder.decode(data, byteArray2, newRect.height);
                    this.interpretBytesAsShorts(byteArray2, sdata, unitsInThisTile);
                } else if (this.compression == 32773) {
                    this.stream.readFully(data, 0, byteCount);
                    int bytesInThisTile = unitsInThisTile * 2;
                    byteArray = new byte[bytesInThisTile];
                    this.decodePackbits(data, bytesInThisTile, byteArray);
                    this.interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);
                } else if (this.compression == 32946) {
                    this.stream.readFully(data, 0, byteCount);
                    byte[] byteArray3 = new byte[unitsInThisTile * 2];
                    this.inflate(data, byteArray3);
                    this.interpretBytesAsShorts(byteArray3, sdata, unitsInThisTile);
                }
            } else if (this.sampleSize == 32 && dataType == 3) {
                if (this.compression == 1) {
                    this.readInts(byteCount / 4, idata);
                } else if (this.compression == 5) {
                    this.stream.readFully(data, 0, byteCount);
                    byte[] byteArray4 = new byte[unitsInThisTile * 4];
                    this.lzwDecoder.decode(data, byteArray4, newRect.height);
                    this.interpretBytesAsInts(byteArray4, idata, unitsInThisTile);
                } else if (this.compression == 32773) {
                    this.stream.readFully(data, 0, byteCount);
                    int bytesInThisTile = unitsInThisTile * 4;
                    byteArray = new byte[bytesInThisTile];
                    this.decodePackbits(data, bytesInThisTile, byteArray);
                    this.interpretBytesAsInts(byteArray, idata, unitsInThisTile);
                } else if (this.compression == 32946) {
                    this.stream.readFully(data, 0, byteCount);
                    byte[] byteArray5 = new byte[unitsInThisTile * 4];
                    this.inflate(data, byteArray5);
                    this.interpretBytesAsInts(byteArray5, idata, unitsInThisTile);
                }
            }
            this.stream.seek(saveOffset);
        }
        catch (IOException ioe) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
        }
        switch (this.imageType) {
            case 2: 
            case 3: {
                if (!this.isWhiteZero) return tile;
                if (dataType == 0 && !(this.getColorModel() instanceof IndexColorModel)) {
                    for (int l = 0; l < bdata.length; l += this.numBands) {
                        bdata[l] = (byte)(255 - bdata[l]);
                    }
                    return tile;
                } else if (dataType == 1) {
                    int ushortMax = 65535;
                    for (int l = 0; l < sdata.length; l += this.numBands) {
                        sdata[l] = (short)(ushortMax - sdata[l]);
                    }
                    return tile;
                } else if (dataType == 2) {
                    for (int l = 0; l < sdata.length; l += this.numBands) {
                        sdata[l] = ~sdata[l];
                    }
                    return tile;
                } else {
                    if (dataType != 3) return tile;
                    long uintMax = 0xFFFFFFFFL;
                    for (int l = 0; l < idata.length; l += this.numBands) {
                        idata[l] = (int)(uintMax - (long)idata[l]);
                    }
                }
                return tile;
            }
            case 5: {
                if (this.sampleSize == 8 && this.compression != 7) {
                    for (int i = 0; i < unitsInThisTile; i += 3) {
                        byte bswap = bdata[i];
                        bdata[i] = bdata[i + 2];
                        bdata[i + 2] = bswap;
                    }
                    return tile;
                } else if (this.sampleSize == 16) {
                    for (int i = 0; i < unitsInThisTile; i += 3) {
                        short sswap = sdata[i];
                        sdata[i] = sdata[i + 2];
                        sdata[i + 2] = sswap;
                    }
                    return tile;
                } else {
                    if (this.sampleSize != 32 || dataType != 3) return tile;
                    for (int i = 0; i < unitsInThisTile; i += 3) {
                        int iswap = idata[i];
                        idata[i] = idata[i + 2];
                        idata[i + 2] = iswap;
                    }
                }
                return tile;
            }
            case 6: {
                if (this.sampleSize == 8) {
                    for (int i = 0; i < unitsInThisTile; i += 4) {
                        byte bswap = bdata[i];
                        bdata[i] = bdata[i + 3];
                        bdata[i + 3] = bswap;
                        bswap = bdata[i + 1];
                        bdata[i + 1] = bdata[i + 2];
                        bdata[i + 2] = bswap;
                    }
                    return tile;
                } else if (this.sampleSize == 16) {
                    for (int i = 0; i < unitsInThisTile; i += 4) {
                        short sswap = sdata[i];
                        sdata[i] = sdata[i + 3];
                        sdata[i + 3] = sswap;
                        sswap = sdata[i + 1];
                        sdata[i + 1] = sdata[i + 2];
                        sdata[i + 2] = sswap;
                    }
                    return tile;
                } else {
                    if (this.sampleSize != 32 || dataType != 3) return tile;
                    for (int i = 0; i < unitsInThisTile; i += 4) {
                        int iswap = idata[i];
                        idata[i] = idata[i + 3];
                        idata[i + 3] = iswap;
                        iswap = idata[i + 1];
                        idata[i + 1] = idata[i + 2];
                        idata[i + 2] = iswap;
                    }
                }
                return tile;
            }
            case 7: {
                int pixelsPerDataUnit = this.chromaSubH * this.chromaSubV;
                int numH = newRect.width / this.chromaSubH;
                int numV = newRect.height / this.chromaSubV;
                byte[] tempData = new byte[numH * numV * (pixelsPerDataUnit + 2)];
                System.arraycopy(bdata, 0, tempData, 0, tempData.length);
                int samplesPerDataUnit = pixelsPerDataUnit * 3;
                int[] pixels = new int[samplesPerDataUnit];
                int bOffset = 0;
                int offsetCb = pixelsPerDataUnit;
                int offsetCr = offsetCb + 1;
                int y = newRect.y;
                for (int j = 0; j < numV; ++j) {
                    int x = newRect.x;
                    for (int i = 0; i < numH; ++i) {
                        int cb = tempData[bOffset + offsetCb];
                        int cr = tempData[bOffset + offsetCr];
                        int k = 0;
                        while (k < samplesPerDataUnit) {
                            pixels[k++] = tempData[bOffset++];
                            pixels[k++] = cb;
                            pixels[k++] = cr;
                        }
                        bOffset += 2;
                        tile.setPixels(x, y, this.chromaSubH, this.chromaSubV, pixels);
                        x += this.chromaSubH;
                    }
                    y += this.chromaSubV;
                }
                return tile;
            }
        }
        return tile;
    }

    private void readShorts(int shortCount, short[] shortArray) {
        int byteCount = 2 * shortCount;
        byte[] byteArray = new byte[byteCount];
        try {
            this.stream.readFully(byteArray, 0, byteCount);
        }
        catch (IOException ioe) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
        }
        this.interpretBytesAsShorts(byteArray, shortArray, shortCount);
    }

    private void readInts(int intCount, int[] intArray) {
        int byteCount = 4 * intCount;
        byte[] byteArray = new byte[byteCount];
        try {
            this.stream.readFully(byteArray, 0, byteCount);
        }
        catch (IOException ioe) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage13") + ": " + ioe.getMessage());
        }
        this.interpretBytesAsInts(byteArray, intArray, intCount);
    }

    private void interpretBytesAsShorts(byte[] byteArray, short[] shortArray, int shortCount) {
        int j = 0;
        if (this.isBigEndian) {
            for (int i = 0; i < shortCount; ++i) {
                int firstByte = byteArray[j++] & 0xFF;
                int secondByte = byteArray[j++] & 0xFF;
                shortArray[i] = (short)((firstByte << 8) + secondByte);
            }
        } else {
            for (int i = 0; i < shortCount; ++i) {
                int firstByte = byteArray[j++] & 0xFF;
                int secondByte = byteArray[j++] & 0xFF;
                shortArray[i] = (short)((secondByte << 8) + firstByte);
            }
        }
    }

    private void interpretBytesAsInts(byte[] byteArray, int[] intArray, int intCount) {
        int j = 0;
        if (this.isBigEndian) {
            for (int i = 0; i < intCount; ++i) {
                intArray[i] = (byteArray[j++] & 0xFF) << 24 | (byteArray[j++] & 0xFF) << 16 | (byteArray[j++] & 0xFF) << 8 | byteArray[j++] & 0xFF;
            }
        } else {
            for (int i = 0; i < intCount; ++i) {
                intArray[i] = byteArray[j++] & 0xFF | (byteArray[j++] & 0xFF) << 8 | (byteArray[j++] & 0xFF) << 16 | (byteArray[j++] & 0xFF) << 24;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private byte[] decodePackbits(byte[] data, int arraySize, byte[] dst) {
        if (dst == null) {
            dst = new byte[arraySize];
        }
        int srcCount = 0;
        int dstCount = 0;
        try {
            block2: while (dstCount < arraySize) {
                int i;
                byte b;
                if ((b = data[srcCount++]) >= 0 && b <= 127) {
                    i = 0;
                    while (true) {
                        if (i >= b + 1) continue block2;
                        dst[dstCount++] = data[srcCount++];
                        ++i;
                    }
                }
                if (b <= -1 && b >= -127) {
                    byte repeat = data[srcCount++];
                    i = 0;
                    while (true) {
                        if (i >= -b + 1) continue block2;
                        dst[dstCount++] = repeat;
                        ++i;
                    }
                }
                ++srcCount;
            }
            return dst;
        }
        catch (ArrayIndexOutOfBoundsException ae) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImage14") + ": " + ae.getMessage());
        }
    }

    private ComponentColorModel createAlphaComponentColorModel(int dataType, int numBands, boolean isAlphaPremultiplied, int transparency) {
        ComponentColorModel ccm = null;
        int[] rgbBits = null;
        ColorSpace cs = null;
        switch (numBands) {
            case 2: {
                cs = ColorSpace.getInstance(1003);
                break;
            }
            case 4: {
                cs = ColorSpace.getInstance(1000);
                break;
            }
            default: {
                throw new IllegalArgumentException(PropertyUtil.getString("TIFFImage19") + ": " + numBands);
            }
        }
        int componentSize = 0;
        switch (dataType) {
            case 0: {
                componentSize = 8;
                break;
            }
            case 1: 
            case 2: {
                componentSize = 16;
                break;
            }
            case 3: {
                componentSize = 32;
                break;
            }
            default: {
                throw new IllegalArgumentException(PropertyUtil.getString("TIFFImage20") + ": " + dataType);
            }
        }
        rgbBits = new int[numBands];
        for (int i = 0; i < numBands; ++i) {
            rgbBits[i] = componentSize;
        }
        ccm = new ComponentColorModel(cs, rgbBits, true, isAlphaPremultiplied, transparency, dataType);
        return ccm;
    }
}

