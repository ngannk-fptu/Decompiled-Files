/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.image.codec.jpeg.JPEGCodec
 *  com.sun.image.codec.jpeg.JPEGDecodeParam
 *  com.sun.image.codec.jpeg.JPEGImageDecoder
 *  com.sun.media.jai.util.SimpleCMYKColorSpace
 */
package com.sun.media.jai.codecimpl;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.NoEOFStream;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import com.sun.media.jai.codecimpl.TIFFFaxDecoder;
import com.sun.media.jai.codecimpl.TIFFLZWDecoder;
import com.sun.media.jai.codecimpl.util.DataBufferFloat;
import com.sun.media.jai.codecimpl.util.FloatDoubleColorModel;
import com.sun.media.jai.codecimpl.util.ImagingException;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import com.sun.media.jai.util.SimpleCMYKColorSpace;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class TIFFImage
extends SimpleRenderedImage {
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
    private static final int TYPE_CMYK = 9;
    private static final int TIFF_JPEG_TABLES = 347;
    private static final int TIFF_YCBCR_SUBSAMPLING = 530;
    SeekableStream stream;
    private boolean isTiled;
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
    JPEGDecodeParam decodeParam = null;
    boolean colorConvertJPEG = false;
    Inflater inflater = null;
    boolean isBigEndian;
    int imageType;
    boolean isWhiteZero = false;
    int dataType;
    boolean decodePaletteAsShorts;
    private TIFFFaxDecoder decoder = null;
    private TIFFLZWDecoder lzwDecoder = null;
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$TIFFImage;
    static /* synthetic */ Class array$I;

    private static final Raster decodeJPEG(byte[] data, JPEGDecodeParam decodeParam, boolean colorConvert, int minX, int minY) {
        ByteArrayInputStream jpegStream = new ByteArrayInputStream(data);
        JPEGImageDecoder decoder = decodeParam == null ? JPEGCodec.createJPEGDecoder((InputStream)jpegStream) : JPEGCodec.createJPEGDecoder((InputStream)jpegStream, (JPEGDecodeParam)decodeParam);
        Raster jpegRaster = null;
        try {
            jpegRaster = colorConvert ? decoder.decodeAsBufferedImage().getWritableTile(0, 0) : decoder.decodeAsRaster();
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("TIFFImage13");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), class$com$sun$media$jai$codecimpl$TIFFImage == null ? (class$com$sun$media$jai$codecimpl$TIFFImage = TIFFImage.class$("com.sun.media.jai.codecimpl.TIFFImage")) : class$com$sun$media$jai$codecimpl$TIFFImage, false);
        }
        return jpegRaster.createTranslatedChild(minX, minY);
    }

    private final void inflate(byte[] deflated, byte[] inflated) {
        this.inflater.setInput(deflated);
        try {
            this.inflater.inflate(inflated);
        }
        catch (DataFormatException dfe) {
            String message = JaiI18N.getString("TIFFImage17");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, dfe), this, false);
        }
        this.inflater.reset();
    }

    private static final SampleModel createPixelInterleavedSampleModel(int dataType, int tileWidth, int tileHeight, int pixelStride, int scanlineStride, int[] bandOffsets) {
        SampleModel sampleModel = null;
        if (dataType == 4) {
            try {
                Class<?> rfClass = Class.forName("javax.media.jai.RasterFactory");
                Class[] paramTypes = new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, array$I == null ? (array$I = TIFFImage.class$("[I")) : array$I};
                Method rfMthd = rfClass.getMethod("createPixelInterleavedSampleModel", paramTypes);
                Object[] params = new Object[]{new Integer(dataType), new Integer(tileWidth), new Integer(tileHeight), new Integer(pixelStride), new Integer(scanlineStride), bandOffsets};
                sampleModel = (SampleModel)rfMthd.invoke(null, params);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (dataType != 4 || sampleModel == null) {
            sampleModel = RasterFactory.createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, pixelStride, scanlineStride, bandOffsets);
        }
        return sampleModel;
    }

    private final long[] getFieldAsLongs(TIFFField field) {
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
            throw new RuntimeException();
        }
        return value;
    }

    private TIFFField getField(TIFFDirectory dir, int tagID, String tagName) {
        TIFFField field = dir.getField(tagID);
        if (field == null) {
            MessageFormat mf = new MessageFormat(JaiI18N.getString("TIFFImage5"));
            mf.setLocale(Locale.getDefault());
            throw new RuntimeException(mf.format(new Object[]{tagName}));
        }
        return field;
    }

    public TIFFImage(SeekableStream stream, TIFFDecodeParam param, int directory) throws IOException {
        int extraSamples;
        char[] planarConfiguration;
        char[] cArray;
        this.stream = stream;
        if (param == null) {
            param = new TIFFDecodeParam();
        }
        this.decodePaletteAsShorts = param.getDecodePaletteAsShorts();
        TIFFDirectory dir = param.getIFDOffset() == null ? new TIFFDirectory(stream, directory) : new TIFFDirectory(stream, param.getIFDOffset(), directory);
        this.properties.put("tiff_directory", dir);
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
            throw new RuntimeException(JaiI18N.getString("TIFFImage0"));
        }
        TIFFField bitsField = dir.getField(258);
        char[] bitsPerSample = null;
        if (bitsField != null) {
            bitsPerSample = bitsField.getAsChars();
        } else {
            bitsPerSample = new char[]{'\u0001'};
            for (int i = 1; i < bitsPerSample.length; ++i) {
                if (bitsPerSample[i] == bitsPerSample[0]) continue;
                throw new RuntimeException(JaiI18N.getString("TIFFImage1"));
            }
        }
        this.sampleSize = bitsPerSample[0];
        TIFFField sampleFormatField = dir.getField(339);
        char[] sampleFormat = null;
        if (sampleFormatField != null) {
            sampleFormat = sampleFormatField.getAsChars();
            for (int l = 1; l < sampleFormat.length; ++l) {
                if (sampleFormat[l] == sampleFormat[0]) continue;
                throw new RuntimeException(JaiI18N.getString("TIFFImage2"));
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
                this.dataType = sampleFormat[0] == '\u0003' ? 4 : 3;
                isValidDataFormat = true;
            }
        }
        if (!isValidDataFormat) {
            throw new RuntimeException(JaiI18N.getString("TIFFImage3"));
        }
        TIFFField compField = dir.getField(259);
        this.compression = compField == null ? 1 : compField.getAsInt(0);
        TIFFField photoInterpField = dir.getField(262);
        int photometricType = photoInterpField != null ? (int)photoInterpField.getAsLong(0) : (dir.getField(320) != null ? 3 : (this.sampleSize == 1 ? (this.compression == 2 || this.compression == 3 || this.compression == 4 ? 0 : 1) : (samplesPerPixel == 3 || samplesPerPixel == 4 ? 2 : 1)));
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
                if (samplesPerPixel != 1 || this.sampleSize != 4 && this.sampleSize != 8 && this.sampleSize != 16) break;
                this.imageType = 4;
                break;
            }
            case 4: {
                if (this.sampleSize != 1 || samplesPerPixel != 1) break;
                this.imageType = 0;
                break;
            }
            case 5: {
                if (this.sampleSize == 8 && samplesPerPixel == 4) {
                    this.imageType = 9;
                }
            }
            case 6: {
                if (this.compression == 7 && this.sampleSize == 8 && samplesPerPixel == 3) {
                    this.colorConvertJPEG = param.getJPEGDecompressYCbCrToRGB();
                    this.imageType = this.colorConvertJPEG ? 5 : 8;
                    break;
                }
                TIFFField chromaField = dir.getField(530);
                if (chromaField != null) {
                    this.chromaSubH = chromaField.getAsInt(0);
                    this.chromaSubV = chromaField.getAsInt(1);
                } else {
                    this.chromaSubV = 2;
                    this.chromaSubH = 2;
                }
                if (this.chromaSubH * this.chromaSubV == 1) {
                    this.imageType = 8;
                    break;
                }
                if (this.sampleSize != 8 || samplesPerPixel != 3) break;
                this.imageType = 7;
                break;
            }
            default: {
                if (this.sampleSize % 8 != 0) break;
                this.imageType = 8;
            }
        }
        if (this.imageType == -1) {
            throw new RuntimeException(JaiI18N.getString("TIFFImage4"));
        }
        this.minY = 0;
        this.minX = 0;
        this.width = (int)this.getField(dir, 256, "Image Width").getAsLong(0);
        this.height = (int)this.getField(dir, 257, "Image Length").getAsLong(0);
        this.numBands = samplesPerPixel;
        TIFFField efield = dir.getField(338);
        int n = extraSamples = efield == null ? 0 : (int)efield.getAsLong(0);
        if (dir.getField(324) != null) {
            this.isTiled = true;
            this.tileWidth = (int)this.getField(dir, 322, "Tile Width").getAsLong(0);
            this.tileHeight = (int)this.getField(dir, 323, "Tile Length").getAsLong(0);
            this.tileOffsets = this.getField(dir, 324, "Tile Offsets").getAsLongs();
            this.tileByteCounts = this.getFieldAsLongs(this.getField(dir, 325, "Tile Byte Counts"));
        } else {
            this.isTiled = false;
            this.tileWidth = dir.getField(322) != null ? (int)dir.getFieldAsLong(322) : this.width;
            TIFFField field = dir.getField(278);
            if (field == null) {
                this.tileHeight = dir.getField(323) != null ? (int)dir.getFieldAsLong(323) : this.height;
            } else {
                long l = field.getAsLong(0);
                long infinity = 1L;
                this.tileHeight = l == (infinity = (infinity << 32) - 1L) || l > (long)this.height ? this.height : (int)l;
            }
            TIFFField tileOffsetsField = this.getField(dir, 273, "Strip Offsets");
            this.tileOffsets = this.getFieldAsLongs(tileOffsetsField);
            TIFFField tileByteCountsField = dir.getField(279);
            if (tileByteCountsField == null) {
                int totalBytes = (this.sampleSize + 7) / 8 * this.numBands * this.width * this.height;
                int bytesPerStrip = (this.sampleSize + 7) / 8 * this.numBands * this.width * this.tileHeight;
                int cumulativeBytes = 0;
                this.tileByteCounts = new long[this.tileOffsets.length];
                for (int i = 0; i < this.tileOffsets.length; ++i) {
                    this.tileByteCounts[i] = Math.min(totalBytes - cumulativeBytes, bytesPerStrip);
                    cumulativeBytes += bytesPerStrip;
                }
                if (this.compression != 1) {
                    this.stream = new NoEOFStream(stream);
                }
            } else {
                this.tileByteCounts = this.getFieldAsLongs(tileByteCountsField);
            }
            int maxBytes = this.width * this.height * this.numBands * ((this.sampleSize + 7) / 8);
            if (this.tileByteCounts.length == 1 && this.compression == 1 && this.tileByteCounts[0] > (long)maxBytes) {
                this.tileByteCounts[0] = maxBytes;
            }
        }
        this.tilesX = (this.width + this.tileWidth - 1) / this.tileWidth;
        this.tilesY = (this.height + this.tileHeight - 1) / this.tileHeight;
        this.tileSize = this.tileWidth * this.tileHeight * this.numBands;
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
                    throw new RuntimeException(JaiI18N.getString("TIFFImage7"));
                }
                if (this.compression == 3) {
                    TIFFField t4OptionsField = dir.getField(292);
                    this.tiffT4Options = t4OptionsField != null ? t4OptionsField.getAsLong(0) : 0L;
                }
                if (this.compression == 4) {
                    TIFFField t6OptionsField = dir.getField(293);
                    this.tiffT6Options = t6OptionsField != null ? t6OptionsField.getAsLong(0) : 0L;
                }
                this.decoder = new TIFFFaxDecoder(this.fillOrder, this.tileWidth, this.tileHeight);
                break;
            }
            case 5: {
                TIFFField predictorField = dir.getField(317);
                if (predictorField == null) {
                    this.predictor = 1;
                } else {
                    this.predictor = predictorField.getAsInt(0);
                    if (this.predictor != 1 && this.predictor != 2) {
                        throw new RuntimeException(JaiI18N.getString("TIFFImage8"));
                    }
                    if (this.predictor == 2 && this.sampleSize != 8) {
                        throw new RuntimeException(this.sampleSize + JaiI18N.getString("TIFFImage9"));
                    }
                }
                this.lzwDecoder = new TIFFLZWDecoder(this.tileWidth, this.predictor, samplesPerPixel);
                break;
            }
            case 6: {
                throw new RuntimeException(JaiI18N.getString("TIFFImage15"));
            }
            case 7: {
                if (!(this.sampleSize == 8 && (this.imageType == 2 && samplesPerPixel == 1 || this.imageType == 4 && samplesPerPixel == 1 || this.imageType == 5 && samplesPerPixel == 3))) {
                    throw new RuntimeException(JaiI18N.getString("TIFFImage16"));
                }
                if (!dir.isTagPresent(347)) break;
                TIFFField jpegTableField = dir.getField(347);
                byte[] jpegTable = jpegTableField.getAsBytes();
                ByteArrayInputStream tableStream = new ByteArrayInputStream(jpegTable);
                JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder((InputStream)tableStream);
                decoder.decodeAsRaster();
                this.decodeParam = decoder.getJPEGDecodeParam();
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("TIFFImage10"));
            }
        }
        switch (this.imageType) {
            case 0: 
            case 1: {
                this.sampleModel = new MultiPixelPackedSampleModel(this.dataType, this.tileWidth, this.tileHeight, this.sampleSize);
                if (this.imageType == 0) {
                    byte[] map = new byte[]{(byte)(this.isWhiteZero ? 255 : 0), (byte)(this.isWhiteZero ? 0 : 255)};
                    this.colorModel = new IndexColorModel(1, 2, map, map, map);
                    break;
                }
                this.colorModel = ImageCodec.createGrayIndexColorModel(this.sampleModel, !this.isWhiteZero);
                break;
            }
            case 2: 
            case 3: 
            case 5: 
            case 6: 
            case 9: {
                int i;
                int[] RGBOffsets = new int[this.numBands];
                if (this.compression == 7) {
                    for (i = 0; i < this.numBands; ++i) {
                        RGBOffsets[i] = this.numBands - 1 - i;
                    }
                } else {
                    for (i = 0; i < this.numBands; ++i) {
                        RGBOffsets[i] = i;
                    }
                }
                this.sampleModel = TIFFImage.createPixelInterleavedSampleModel(this.dataType, this.tileWidth, this.tileHeight, this.numBands, this.numBands * this.tileWidth, RGBOffsets);
                if (this.imageType == 2 || this.imageType == 5) {
                    this.colorModel = ImageCodec.createComponentColorModel(this.sampleModel);
                    break;
                }
                if (this.imageType == 9) {
                    this.colorModel = ImageCodec.createComponentColorModel(this.sampleModel, SimpleCMYKColorSpace.getInstance());
                    break;
                }
                int transparency = 1;
                if (extraSamples == 1 || extraSamples == 2) {
                    transparency = 3;
                }
                this.colorModel = this.createAlphaComponentColorModel(this.dataType, this.numBands, extraSamples == 1, transparency);
                break;
            }
            case 7: 
            case 8: {
                int[] bandOffsets = new int[this.numBands];
                for (int i = 0; i < this.numBands; ++i) {
                    bandOffsets[i] = i;
                }
                this.sampleModel = TIFFImage.createPixelInterleavedSampleModel(this.dataType, this.tileWidth, this.tileHeight, this.numBands, this.numBands * this.tileWidth, bandOffsets);
                this.colorModel = null;
                break;
            }
            case 4: {
                TIFFField cfield = this.getField(dir, 320, "Colormap");
                this.colormap = cfield.getAsChars();
                if (this.decodePaletteAsShorts) {
                    this.numBands = 3;
                    if (this.dataType == 0) {
                        this.dataType = 1;
                    }
                    this.sampleModel = RasterFactory.createPixelInterleavedSampleModel(this.dataType, this.tileWidth, this.tileHeight, this.numBands);
                    this.colorModel = ImageCodec.createComponentColorModel(this.sampleModel);
                    break;
                }
                this.numBands = 1;
                if (this.sampleSize == 4) {
                    this.sampleModel = new MultiPixelPackedSampleModel(0, this.tileWidth, this.tileHeight, this.sampleSize);
                } else if (this.sampleSize == 8) {
                    this.sampleModel = RasterFactory.createPixelInterleavedSampleModel(0, this.tileWidth, this.tileHeight, this.numBands);
                } else if (this.sampleSize == 16) {
                    this.dataType = 1;
                    this.sampleModel = RasterFactory.createPixelInterleavedSampleModel(1, this.tileWidth, this.tileHeight, this.numBands);
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
                this.colorModel = new IndexColorModel(this.sampleSize, bandLength, r, g, b);
                break;
            }
            default: {
                throw new RuntimeException("TIFFImage4");
            }
        }
    }

    public TIFFDirectory getPrivateIFD(long offset) throws IOException {
        return new TIFFDirectory(this.stream, offset, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Raster getTile(int tileX, int tileY) {
        if (tileX < 0 || tileX >= this.tilesX || tileY < 0 || tileY >= this.tilesY) {
            throw new IllegalArgumentException(JaiI18N.getString("TIFFImage12"));
        }
        WritableRaster tile = null;
        SeekableStream seekableStream = this.stream;
        synchronized (seekableStream) {
            byte[] data;
            byte[] bdata = null;
            short[] sdata = null;
            int[] idata = null;
            float[] fdata = null;
            DataBuffer buffer = this.sampleModel.createDataBuffer();
            int dataType = this.sampleModel.getDataType();
            if (dataType == 0) {
                bdata = ((DataBufferByte)buffer).getData();
            } else if (dataType == 1) {
                sdata = ((DataBufferUShort)buffer).getData();
            } else if (dataType == 2) {
                sdata = ((DataBufferShort)buffer).getData();
            } else if (dataType == 3) {
                idata = ((DataBufferInt)buffer).getData();
            } else if (dataType == 4) {
                if (buffer instanceof DataBufferFloat) {
                    fdata = ((DataBufferFloat)buffer).getData();
                } else {
                    try {
                        Method getDataMethod = buffer.getClass().getMethod("getData", null);
                        fdata = (float[])getDataMethod.invoke((Object)buffer, null);
                    }
                    catch (Exception e) {
                        String message = JaiI18N.getString("TIFFImage18");
                        ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
                    }
                }
            }
            tile = RasterFactory.createWritableRaster(this.sampleModel, buffer, new Point(this.tileXToX(tileX), this.tileYToY(tileY)));
            long save_offset = 0L;
            try {
                save_offset = this.stream.getFilePointer();
                this.stream.seek(this.tileOffsets[tileY * this.tilesX + tileX]);
            }
            catch (IOException ioe) {
                String message = JaiI18N.getString("TIFFImage13");
                ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
            }
            int byteCount = (int)this.tileByteCounts[tileY * this.tilesX + tileX];
            Rectangle tileRect = new Rectangle(this.tileXToX(tileX), this.tileYToY(tileY), this.tileWidth, this.tileHeight);
            Rectangle newRect = this.isTiled ? tileRect : tileRect.intersection(this.getBounds());
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
                    this.stream.seek(save_offset);
                }
                catch (IOException ioe) {
                    String message = JaiI18N.getString("TIFFImage13");
                    ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
                }
            } else if (this.imageType == 4) {
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
                            this.stream.seek(save_offset);
                        }
                        catch (IOException ioe) {
                            String message = JaiI18N.getString("TIFFImage13");
                            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
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
                        } else if (dataType == 2) {
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
                    } else {
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
                            this.stream.seek(save_offset);
                        }
                        catch (IOException ioe) {
                            String message = JaiI18N.getString("TIFFImage13");
                            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
                        }
                    }
                } else if (this.sampleSize == 8) {
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
                            } else if (this.compression == 7) {
                                this.stream.readFully(data, 0, byteCount);
                                Raster tempTile = TIFFImage.decodeJPEG(data, this.decodeParam, this.colorConvertJPEG, tile.getMinX(), tile.getMinY());
                                int[] tempPixels = new int[unitsBeforeLookup];
                                tempTile.getPixels(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight(), tempPixels);
                                tempData = new byte[unitsBeforeLookup];
                                for (int i = 0; i < unitsBeforeLookup; ++i) {
                                    tempData[i] = (byte)tempPixels[i];
                                }
                            } else if (this.compression == 32946) {
                                this.stream.readFully(data, 0, byteCount);
                                tempData = new byte[unitsBeforeLookup];
                                this.inflate(data, tempData);
                            } else if (this.compression == 1) {
                                tempData = new byte[byteCount];
                                this.stream.readFully(tempData, 0, byteCount);
                            }
                            this.stream.seek(save_offset);
                        }
                        catch (IOException ioe) {
                            String message = JaiI18N.getString("TIFFImage13");
                            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
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
                    } else {
                        try {
                            if (this.compression == 32773) {
                                this.stream.readFully(data, 0, byteCount);
                                this.decodePackbits(data, unitsInThisTile, bdata);
                            } else if (this.compression == 5) {
                                this.stream.readFully(data, 0, byteCount);
                                this.lzwDecoder.decode(data, bdata, newRect.height);
                            } else if (this.compression == 7) {
                                this.stream.readFully(data, 0, byteCount);
                                tile.setRect(TIFFImage.decodeJPEG(data, this.decodeParam, this.colorConvertJPEG, tile.getMinX(), tile.getMinY()));
                            } else if (this.compression == 32946) {
                                this.stream.readFully(data, 0, byteCount);
                                this.inflate(data, bdata);
                            } else if (this.compression == 1) {
                                this.stream.readFully(bdata, 0, byteCount);
                            }
                            this.stream.seek(save_offset);
                        }
                        catch (IOException ioe) {
                            String message = JaiI18N.getString("TIFFImage13");
                            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
                        }
                    }
                } else if (this.sampleSize == 4) {
                    int padding = newRect.width % 2 == 0 ? 0 : 1;
                    int bytesPostDecoding = (newRect.width / 2 + padding) * newRect.height;
                    if (this.decodePaletteAsShorts) {
                        byte[] tempData = null;
                        try {
                            this.stream.readFully(data, 0, byteCount);
                            this.stream.seek(save_offset);
                        }
                        catch (IOException ioe) {
                            String message = JaiI18N.getString("TIFFImage13");
                            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
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
                    } else {
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
                            this.stream.seek(save_offset);
                        }
                        catch (IOException ioe) {
                            String message = JaiI18N.getString("TIFFImage13");
                            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
                        }
                    }
                }
            } else if (this.imageType == 1) {
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
                    this.stream.seek(save_offset);
                }
                catch (IOException ioe) {
                    String message = JaiI18N.getString("TIFFImage13");
                    ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
                }
            } else {
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
                        } else if (this.compression == 7) {
                            this.stream.readFully(data, 0, byteCount);
                            tile.setRect(TIFFImage.decodeJPEG(data, this.decodeParam, this.colorConvertJPEG, tile.getMinX(), tile.getMinY()));
                        } else if (this.compression == 32946) {
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
                    } else if (this.sampleSize == 32 && dataType == 4) {
                        if (this.compression == 1) {
                            this.readFloats(byteCount / 4, fdata);
                        } else if (this.compression == 5) {
                            this.stream.readFully(data, 0, byteCount);
                            byte[] byteArray6 = new byte[unitsInThisTile * 4];
                            this.lzwDecoder.decode(data, byteArray6, newRect.height);
                            this.interpretBytesAsFloats(byteArray6, fdata, unitsInThisTile);
                        } else if (this.compression == 32773) {
                            this.stream.readFully(data, 0, byteCount);
                            int bytesInThisTile = unitsInThisTile * 4;
                            byteArray = new byte[bytesInThisTile];
                            this.decodePackbits(data, bytesInThisTile, byteArray);
                            this.interpretBytesAsFloats(byteArray, fdata, unitsInThisTile);
                        } else if (this.compression == 32946) {
                            this.stream.readFully(data, 0, byteCount);
                            byte[] byteArray7 = new byte[unitsInThisTile * 4];
                            this.inflate(data, byteArray7);
                            this.interpretBytesAsFloats(byteArray7, fdata, unitsInThisTile);
                        }
                    }
                    this.stream.seek(save_offset);
                }
                catch (IOException ioe) {
                    String message = JaiI18N.getString("TIFFImage13");
                    ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
                }
                switch (this.imageType) {
                    case 2: 
                    case 3: {
                        if (!this.isWhiteZero) break;
                        if (dataType == 0 && !(this.colorModel instanceof IndexColorModel)) {
                            for (int l = 0; l < bdata.length; l += this.numBands) {
                                bdata[l] = (byte)(255 - bdata[l]);
                            }
                        } else if (dataType == 1) {
                            int ushortMax = 65535;
                            for (int l = 0; l < sdata.length; l += this.numBands) {
                                sdata[l] = (short)(ushortMax - sdata[l]);
                            }
                        } else if (dataType == 2) {
                            for (int l = 0; l < sdata.length; l += this.numBands) {
                                sdata[l] = ~sdata[l];
                            }
                        } else {
                            if (dataType != 3) break;
                            long uintMax = -1L;
                            for (int l = 0; l < idata.length; l += this.numBands) {
                                idata[l] = (int)(uintMax - (long)idata[l]);
                            }
                        }
                        break;
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
                                int Cb = tempData[bOffset + offsetCb];
                                int Cr = tempData[bOffset + offsetCr];
                                int k = 0;
                                while (k < samplesPerDataUnit) {
                                    pixels[k++] = tempData[bOffset++];
                                    pixels[k++] = Cb;
                                    pixels[k++] = Cr;
                                }
                                bOffset += 2;
                                tile.setPixels(x, y, this.chromaSubH, this.chromaSubV, pixels);
                                x += this.chromaSubH;
                            }
                            y += this.chromaSubV;
                        }
                        break;
                    }
                }
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
            String message = JaiI18N.getString("TIFFImage13");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
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
            String message = JaiI18N.getString("TIFFImage13");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        this.interpretBytesAsInts(byteArray, intArray, intCount);
    }

    private void readFloats(int floatCount, float[] floatArray) {
        int byteCount = 4 * floatCount;
        byte[] byteArray = new byte[byteCount];
        try {
            this.stream.readFully(byteArray, 0, byteCount);
        }
        catch (IOException ioe) {
            String message = JaiI18N.getString("TIFFImage13");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        this.interpretBytesAsFloats(byteArray, floatArray, floatCount);
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

    private void interpretBytesAsFloats(byte[] byteArray, float[] floatArray, int floatCount) {
        int j = 0;
        if (this.isBigEndian) {
            for (int i = 0; i < floatCount; ++i) {
                int value = (byteArray[j++] & 0xFF) << 24 | (byteArray[j++] & 0xFF) << 16 | (byteArray[j++] & 0xFF) << 8 | byteArray[j++] & 0xFF;
                floatArray[i] = Float.intBitsToFloat(value);
            }
        } else {
            for (int i = 0; i < floatCount; ++i) {
                int value = byteArray[j++] & 0xFF | (byteArray[j++] & 0xFF) << 8 | (byteArray[j++] & 0xFF) << 16 | (byteArray[j++] & 0xFF) << 24;
                floatArray[i] = Float.intBitsToFloat(value);
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
        int srcArraySize = data.length;
        try {
            block2: while (dstCount < arraySize) {
                int i;
                byte b;
                if (srcCount >= srcArraySize) return dst;
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
            String message = JaiI18N.getString("TIFFImage14");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ae), this, false);
        }
        return dst;
    }

    private ComponentColorModel createAlphaComponentColorModel(int dataType, int numBands, boolean isAlphaPremultiplied, int transparency) {
        ComponentColorModel ccm = null;
        int[] RGBBits = null;
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
                throw new IllegalArgumentException();
            }
        }
        if (dataType == 4) {
            ccm = new FloatDoubleColorModel(cs, true, isAlphaPremultiplied, transparency, dataType);
        } else {
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
                    throw new IllegalArgumentException();
                }
            }
            RGBBits = new int[numBands];
            for (int i = 0; i < numBands; ++i) {
                RGBBits[i] = componentSize;
            }
            ccm = new ComponentColorModel(cs, RGBBits, true, isAlphaPremultiplied, transparency, dataType);
        }
        return ccm;
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

