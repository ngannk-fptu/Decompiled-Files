/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.image.codec.jpeg.JPEGCodec
 *  com.sun.image.codec.jpeg.JPEGEncodeParam
 *  com.sun.image.codec.jpeg.JPEGImageEncoder
 */
package com.sun.media.jai.codecimpl;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.SeekableOutputStream;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.jai.codecimpl.CodecUtils;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.SingleTileRenderedImage;
import com.sun.media.jai.codecimpl.TIFFFaxEncoder;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.Deflater;

public class TIFFImageEncoder
extends ImageEncoderImpl {
    private static final int TIFF_UNSUPPORTED = -1;
    private static final int TIFF_BILEVEL_WHITE_IS_ZERO = 0;
    private static final int TIFF_BILEVEL_BLACK_IS_ZERO = 1;
    private static final int TIFF_GRAY = 2;
    private static final int TIFF_PALETTE = 3;
    private static final int TIFF_RGB = 4;
    private static final int TIFF_CMYK = 5;
    private static final int TIFF_YCBCR = 6;
    private static final int TIFF_CIELAB = 7;
    private static final int TIFF_GENERIC = 8;
    private static final int COMP_NONE = 1;
    private static final int COMP_GROUP3_1D = 2;
    private static final int COMP_GROUP3_2D = 3;
    private static final int COMP_GROUP4 = 4;
    private static final int COMP_JPEG_TTN2 = 7;
    private static final int COMP_PACKBITS = 32773;
    private static final int COMP_DEFLATE = 32946;
    private static final int TIFF_JPEG_TABLES = 347;
    private static final int TIFF_YCBCR_SUBSAMPLING = 530;
    private static final int TIFF_YCBCR_POSITIONING = 531;
    private static final int TIFF_REF_BLACK_WHITE = 532;
    private static final int EXTRA_SAMPLE_UNSPECIFIED = 0;
    private static final int EXTRA_SAMPLE_ASSOCIATED_ALPHA = 1;
    private static final int EXTRA_SAMPLE_UNASSOCIATED_ALPHA = 2;
    private static final int DEFAULT_ROWS_PER_STRIP = 8;
    private boolean isLittleEndian = false;
    private static final int[] sizeOfType = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    private static final char[] intsToChars(int[] intArray) {
        int arrayLength = intArray.length;
        char[] charArray = new char[arrayLength];
        for (int i = 0; i < arrayLength; ++i) {
            charArray[i] = (char)(intArray[i] & 0xFFFF);
        }
        return charArray;
    }

    public TIFFImageEncoder(OutputStream output, ImageEncodeParam param) {
        super(output, param);
        if (this.param == null) {
            this.param = new TIFFEncodeParam();
        }
    }

    public void encode(RenderedImage im) throws IOException {
        TIFFEncodeParam encodeParam = (TIFFEncodeParam)this.param;
        this.isLittleEndian = encodeParam.getLittleEndian();
        this.writeFileHeader();
        Iterator iter = encodeParam.getExtraImages();
        if (iter != null) {
            boolean hasNext;
            int ifdOffset = 8;
            RenderedImage nextImage = im;
            TIFFEncodeParam nextParam = encodeParam;
            do {
                ifdOffset = this.encode(nextImage, nextParam, ifdOffset, !(hasNext = iter.hasNext()));
                if (!hasNext) continue;
                Object obj = iter.next();
                if (obj instanceof RenderedImage) {
                    nextImage = (RenderedImage)obj;
                    nextParam = encodeParam;
                    continue;
                }
                if (!(obj instanceof Object[])) continue;
                Object[] o = (Object[])obj;
                nextImage = (RenderedImage)o[0];
                nextParam = (TIFFEncodeParam)o[1];
            } while (hasNext);
        } else {
            this.encode(im, encodeParam, 8, true);
        }
    }

    private int encode(RenderedImage im, TIFFEncodeParam encodeParam, int ifdOffset, boolean isLast) throws IOException {
        TIFFField[] extraFields;
        int tileHeight;
        int tileWidth;
        byte[] b;
        byte[] g;
        byte[] r;
        if (CodecUtils.isPackedByteImage(im)) {
            ColorModel sourceCM = im.getColorModel();
            ComponentColorModel destCM = RasterFactory.createComponentColorModel(0, sourceCM.getColorSpace(), sourceCM.hasAlpha(), sourceCM.isAlphaPremultiplied(), sourceCM.getTransparency());
            Point origin = new Point(im.getMinX(), im.getMinY());
            WritableRaster raster = Raster.createWritableRaster(((ColorModel)destCM).createCompatibleSampleModel(im.getWidth(), im.getHeight()), origin);
            raster.setRect(im.getData());
            im = new SingleTileRenderedImage(raster, destCM);
        }
        int compression = encodeParam.getCompression();
        boolean isTiled = encodeParam.getWriteTiled();
        int minX = im.getMinX();
        int minY = im.getMinY();
        int width = im.getWidth();
        int height = im.getHeight();
        SampleModel sampleModel = im.getSampleModel();
        int[] sampleSize = sampleModel.getSampleSize();
        for (int i = 1; i < sampleSize.length; ++i) {
            if (sampleSize[i] == sampleSize[0]) continue;
            throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder0"));
        }
        int numBands = sampleModel.getNumBands();
        if ((sampleSize[0] == 1 || sampleSize[0] == 4) && numBands != 1) {
            throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder1"));
        }
        int dataType = sampleModel.getDataType();
        switch (dataType) {
            case 0: {
                if (sampleSize[0] == 1 || sampleSize[0] == 4 || sampleSize[0] == 8) break;
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder2"));
            }
            case 1: 
            case 2: {
                if (sampleSize[0] == 16) break;
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder3"));
            }
            case 3: 
            case 4: {
                if (sampleSize[0] == 32) break;
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder4"));
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder5"));
            }
        }
        boolean dataTypeIsShort = dataType == 2 || dataType == 1;
        ColorModel colorModel = im.getColorModel();
        if (colorModel != null && colorModel instanceof IndexColorModel && dataType != 0) {
            throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder6"));
        }
        IndexColorModel icm = null;
        int sizeOfColormap = 0;
        int[] colormap = null;
        int imageType = -1;
        int numExtraSamples = 0;
        int extraSampleType = 0;
        if (colorModel instanceof IndexColorModel) {
            icm = (IndexColorModel)colorModel;
            int mapSize = icm.getMapSize();
            if (sampleSize[0] == 1 && numBands == 1) {
                if (mapSize != 2) {
                    throw new IllegalArgumentException(JaiI18N.getString("TIFFImageEncoder7"));
                }
                r = new byte[mapSize];
                icm.getReds(r);
                g = new byte[mapSize];
                icm.getGreens(g);
                b = new byte[mapSize];
                icm.getBlues(b);
                imageType = (r[0] & 0xFF) == 0 && (r[1] & 0xFF) == 255 && (g[0] & 0xFF) == 0 && (g[1] & 0xFF) == 255 && (b[0] & 0xFF) == 0 && (b[1] & 0xFF) == 255 ? 1 : ((r[0] & 0xFF) == 255 && (r[1] & 0xFF) == 0 && (g[0] & 0xFF) == 255 && (g[1] & 0xFF) == 0 && (b[0] & 0xFF) == 255 && (b[1] & 0xFF) == 0 ? 0 : 3);
            } else if (numBands == 1) {
                imageType = 3;
            }
        } else if (colorModel == null) {
            if (sampleSize[0] == 1 && numBands == 1) {
                imageType = 1;
            } else {
                imageType = 8;
                if (numBands > 1) {
                    numExtraSamples = numBands - 1;
                }
            }
        } else {
            ColorSpace colorSpace = colorModel.getColorSpace();
            switch (colorSpace.getType()) {
                case 9: {
                    imageType = 5;
                    break;
                }
                case 6: {
                    imageType = 2;
                    break;
                }
                case 1: {
                    imageType = 7;
                    break;
                }
                case 5: {
                    if (compression == 7 && encodeParam.getJPEGCompressRGBToYCbCr()) {
                        imageType = 6;
                        break;
                    }
                    imageType = 4;
                    break;
                }
                case 3: {
                    imageType = 6;
                    break;
                }
                default: {
                    imageType = 8;
                }
            }
            if (imageType == 8) {
                numExtraSamples = numBands - 1;
            } else if (numBands > 1) {
                numExtraSamples = numBands - colorSpace.getNumComponents();
            }
            if (numExtraSamples == 1 && colorModel.hasAlpha()) {
                int n = extraSampleType = colorModel.isAlphaPremultiplied() ? 1 : 2;
            }
        }
        if (imageType == -1) {
            throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder8"));
        }
        if (compression == 7) {
            if (imageType == 3) {
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder11"));
            }
            if (sampleSize[0] != 8 || imageType != 2 && imageType != 4 && imageType != 6) {
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder9"));
            }
        }
        if (imageType != 0 && imageType != 1 && (compression == 2 || compression == 3 || compression == 4)) {
            throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder12"));
        }
        int photometricInterpretation = -1;
        switch (imageType) {
            case 0: {
                photometricInterpretation = 0;
                break;
            }
            case 1: {
                photometricInterpretation = 1;
                break;
            }
            case 2: 
            case 8: {
                photometricInterpretation = 1;
                break;
            }
            case 3: {
                photometricInterpretation = 3;
                icm = (IndexColorModel)colorModel;
                sizeOfColormap = icm.getMapSize();
                r = new byte[sizeOfColormap];
                icm.getReds(r);
                g = new byte[sizeOfColormap];
                icm.getGreens(g);
                b = new byte[sizeOfColormap];
                icm.getBlues(b);
                int redIndex = 0;
                int greenIndex = sizeOfColormap;
                int blueIndex = 2 * sizeOfColormap;
                colormap = new int[sizeOfColormap * 3];
                for (int i = 0; i < sizeOfColormap; ++i) {
                    colormap[redIndex++] = r[i] << 8 & 0xFFFF;
                    colormap[greenIndex++] = g[i] << 8 & 0xFFFF;
                    colormap[blueIndex++] = b[i] << 8 & 0xFFFF;
                }
                sizeOfColormap *= 3;
                break;
            }
            case 4: {
                photometricInterpretation = 2;
                break;
            }
            case 5: {
                photometricInterpretation = 5;
                break;
            }
            case 6: {
                photometricInterpretation = 6;
                break;
            }
            case 7: {
                photometricInterpretation = 8;
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder8"));
            }
        }
        if (isTiled) {
            tileWidth = encodeParam.getTileWidth() > 0 ? encodeParam.getTileWidth() : im.getTileWidth();
            tileHeight = encodeParam.getTileHeight() > 0 ? encodeParam.getTileHeight() : im.getTileHeight();
        } else {
            tileWidth = width;
            tileHeight = encodeParam.getTileHeight() > 0 ? encodeParam.getTileHeight() : 8;
        }
        JPEGEncodeParam jep = null;
        if (compression == 7) {
            int factorH;
            jep = encodeParam.getJPEGEncodeParam();
            int maxSubH = jep.getHorizontalSubsampling(0);
            int maxSubV = jep.getVerticalSubsampling(0);
            for (int i = 1; i < numBands; ++i) {
                int subV;
                int subH = jep.getHorizontalSubsampling(i);
                if (subH > maxSubH) {
                    maxSubH = subH;
                }
                if ((subV = jep.getVerticalSubsampling(i)) <= maxSubV) continue;
                maxSubV = subV;
            }
            int factorV = 8 * maxSubV;
            if ((tileHeight = (int)((float)tileHeight / (float)factorV + 0.5f) * factorV) < factorV) {
                tileHeight = factorV;
            }
            if (isTiled && (tileWidth = (int)((float)tileWidth / (float)(factorH = 8 * maxSubH) + 0.5f) * factorH) < factorH) {
                tileWidth = factorH;
            }
        }
        int numTiles = isTiled ? (width + tileWidth - 1) / tileWidth * ((height + tileHeight - 1) / tileHeight) : (int)Math.ceil((double)height / (double)tileHeight);
        long[] tileByteCounts = new long[numTiles];
        long bytesPerRow = (long)Math.ceil((double)sampleSize[0] / 8.0 * (double)tileWidth * (double)numBands);
        long bytesPerTile = bytesPerRow * (long)tileHeight;
        for (int i = 0; i < numTiles; ++i) {
            tileByteCounts[i] = bytesPerTile;
        }
        if (!isTiled) {
            long lastStripRows = height - tileHeight * (numTiles - 1);
            tileByteCounts[numTiles - 1] = lastStripRows * bytesPerRow;
        }
        long totalBytesOfData = bytesPerTile * (long)(numTiles - 1) + tileByteCounts[numTiles - 1];
        long[] tileOffsets = new long[numTiles];
        TreeSet<TIFFField> fields = new TreeSet<TIFFField>();
        fields.add(new TIFFField(256, 4, 1, new long[]{width}));
        fields.add(new TIFFField(257, 4, 1, new long[]{height}));
        fields.add(new TIFFField(258, 3, numBands, TIFFImageEncoder.intsToChars(sampleSize)));
        fields.add(new TIFFField(259, 3, 1, new char[]{(char)compression}));
        fields.add(new TIFFField(262, 3, 1, new char[]{(char)photometricInterpretation}));
        if (!isTiled) {
            fields.add(new TIFFField(273, 4, numTiles, tileOffsets));
        }
        fields.add(new TIFFField(277, 3, 1, new char[]{(char)numBands}));
        if (!isTiled) {
            fields.add(new TIFFField(278, 4, 1, new long[]{tileHeight}));
            fields.add(new TIFFField(279, 4, numTiles, tileByteCounts));
        }
        if (colormap != null) {
            fields.add(new TIFFField(320, 3, sizeOfColormap, TIFFImageEncoder.intsToChars(colormap)));
        }
        if (isTiled) {
            fields.add(new TIFFField(322, 4, 1, new long[]{tileWidth}));
            fields.add(new TIFFField(323, 4, 1, new long[]{tileHeight}));
            fields.add(new TIFFField(324, 4, numTiles, tileOffsets));
            fields.add(new TIFFField(325, 4, numTiles, tileByteCounts));
        }
        if (numExtraSamples > 0) {
            int[] extraSamples = new int[numExtraSamples];
            for (int i = 0; i < numExtraSamples; ++i) {
                extraSamples[i] = extraSampleType;
            }
            fields.add(new TIFFField(338, 3, numExtraSamples, TIFFImageEncoder.intsToChars(extraSamples)));
        }
        if (dataType != 0) {
            int[] sampleFormat = new int[numBands];
            sampleFormat[0] = dataType == 4 ? 3 : (dataType == 1 ? 1 : 2);
            for (int b2 = 1; b2 < numBands; ++b2) {
                sampleFormat[b2] = sampleFormat[0];
            }
            fields.add(new TIFFField(339, 3, numBands, TIFFImageEncoder.intsToChars(sampleFormat)));
        }
        boolean inverseFill = encodeParam.getReverseFillOrder();
        boolean T4encode2D = encodeParam.getT4Encode2D();
        boolean T4PadEOLs = encodeParam.getT4PadEOLs();
        TIFFFaxEncoder faxEncoder = null;
        if (!(imageType != 1 && imageType != 0 || compression != 2 && compression != 3 && compression != 4)) {
            faxEncoder = new TIFFFaxEncoder(inverseFill);
            fields.add(new TIFFField(266, 3, 1, new char[]{inverseFill ? (char)'\u0002' : '\u0001'}));
            if (compression == 3) {
                long T4Options = 0L;
                if (T4encode2D) {
                    T4Options |= 1L;
                }
                if (T4PadEOLs) {
                    T4Options |= 4L;
                }
                fields.add(new TIFFField(292, 4, 1, new long[]{T4Options}));
            } else if (compression == 4) {
                fields.add(new TIFFField(293, 4, 1, new long[]{0L}));
            }
        }
        com.sun.image.codec.jpeg.JPEGEncodeParam jpegEncodeParam = null;
        com.sun.image.codec.jpeg.JPEGImageEncoder jpegEncoder = null;
        int jpegColorID = 0;
        if (compression == 7) {
            jpegColorID = 0;
            switch (imageType) {
                case 2: 
                case 3: {
                    jpegColorID = 1;
                    break;
                }
                case 4: {
                    jpegColorID = 2;
                    break;
                }
                case 6: {
                    jpegColorID = 3;
                }
            }
            Raster tile00 = im.getTile(im.getMinTileX(), im.getMinTileY());
            jpegEncodeParam = JPEGCodec.getDefaultJPEGEncodeParam((Raster)tile00, (int)jpegColorID);
            JPEGImageEncoder.modifyEncodeParam(jep, jpegEncodeParam, numBands);
            if (jep.getWriteImageOnly()) {
                jpegEncodeParam.setImageInfoValid(false);
                jpegEncodeParam.setTableInfoValid(true);
                ByteArrayOutputStream tableStream = new ByteArrayOutputStream();
                jpegEncoder = JPEGCodec.createJPEGEncoder((OutputStream)tableStream, (com.sun.image.codec.jpeg.JPEGEncodeParam)jpegEncodeParam);
                jpegEncoder.encode(tile00);
                byte[] tableData = tableStream.toByteArray();
                fields.add(new TIFFField(347, 7, tableData.length, tableData));
                jpegEncoder = null;
            }
        }
        if (imageType == 6) {
            int subsampleH = 1;
            int subsampleV = 1;
            if (compression == 7) {
                subsampleH = jep.getHorizontalSubsampling(0);
                subsampleV = jep.getVerticalSubsampling(0);
                for (int i = 1; i < numBands; ++i) {
                    int subV;
                    int subH = jep.getHorizontalSubsampling(i);
                    if (subH > subsampleH) {
                        subsampleH = subH;
                    }
                    if ((subV = jep.getVerticalSubsampling(i)) <= subsampleV) continue;
                    subsampleV = subV;
                }
            }
            fields.add(new TIFFField(530, 3, 2, new char[]{(char)subsampleH, (char)subsampleV}));
            fields.add(new TIFFField(531, 3, 1, new char[]{compression == 7 ? (char)'\u0001' : '\u0002'}));
            long[][] refbw = compression == 7 ? new long[][]{{0L, 1L}, {255L, 1L}, {128L, 1L}, {255L, 1L}, {128L, 1L}, {255L, 1L}} : new long[][]{{15L, 1L}, {235L, 1L}, {128L, 1L}, {240L, 1L}, {128L, 1L}, {240L, 1L}};
            fields.add(new TIFFField(532, 5, 6, refbw));
        }
        if ((extraFields = encodeParam.getExtraFields()) != null) {
            ArrayList<Integer> extantTags = new ArrayList<Integer>(fields.size());
            Iterator fieldIter = fields.iterator();
            while (fieldIter.hasNext()) {
                TIFFField fld = (TIFFField)fieldIter.next();
                extantTags.add(new Integer(fld.getTag()));
            }
            int numExtraFields = extraFields.length;
            for (int i = 0; i < numExtraFields; ++i) {
                TIFFField fld = extraFields[i];
                Integer tagValue = new Integer(fld.getTag());
                if (extantTags.contains(tagValue)) continue;
                fields.add(fld);
                extantTags.add(tagValue);
            }
        }
        int dirSize = this.getDirectorySize(fields);
        tileOffsets[0] = ifdOffset + dirSize;
        OutputStream outCache = null;
        byte[] compressBuf = null;
        File tempFile = null;
        int nextIFDOffset = 0;
        boolean skipByte = false;
        Deflater deflater = null;
        int deflateLevel = -1;
        boolean jpegRGBToYCbCr = false;
        if (compression == 1) {
            int numBytesPadding = 0;
            if (sampleSize[0] == 16 && tileOffsets[0] % 2L != 0L) {
                numBytesPadding = 1;
                tileOffsets[0] = tileOffsets[0] + 1L;
            } else if (sampleSize[0] == 32 && tileOffsets[0] % 4L != 0L) {
                numBytesPadding = (int)(4L - tileOffsets[0] % 4L);
                tileOffsets[0] = tileOffsets[0] + (long)numBytesPadding;
            }
            for (int i = 1; i < numTiles; ++i) {
                tileOffsets[i] = tileOffsets[i - 1] + tileByteCounts[i - 1];
            }
            if (!isLast && (nextIFDOffset = (int)(tileOffsets[0] + totalBytesOfData)) % 2 != 0) {
                ++nextIFDOffset;
                skipByte = true;
            }
            this.writeDirectory(ifdOffset, fields, nextIFDOffset);
            if (numBytesPadding != 0) {
                for (int padding = 0; padding < numBytesPadding; ++padding) {
                    this.output.write(0);
                }
            }
        } else {
            if (this.output instanceof SeekableOutputStream) {
                ((SeekableOutputStream)this.output).seek(tileOffsets[0]);
            } else {
                outCache = this.output;
                try {
                    tempFile = File.createTempFile("jai-SOS-", ".tmp");
                    tempFile.deleteOnExit();
                    RandomAccessFile raFile = new RandomAccessFile(tempFile, "rw");
                    this.output = new SeekableOutputStream(raFile);
                }
                catch (Exception e) {
                    tempFile = null;
                    this.output = new ByteArrayOutputStream((int)totalBytesOfData);
                }
            }
            int bufSize = 0;
            switch (compression) {
                case 2: {
                    bufSize = (int)Math.ceil((double)((tileWidth + 1) / 2 * 9 + 2) / 8.0);
                    break;
                }
                case 3: 
                case 4: {
                    bufSize = (int)Math.ceil((double)((tileWidth + 1) / 2 * 9 + 2) / 8.0);
                    bufSize = tileHeight * (bufSize + 2) + 12;
                    break;
                }
                case 32773: {
                    bufSize = (int)(bytesPerTile + (bytesPerRow + 127L) / 128L * (long)tileHeight);
                    break;
                }
                case 7: {
                    bufSize = 0;
                    if (imageType != 6 || colorModel == null || colorModel.getColorSpace().getType() != 5) break;
                    jpegRGBToYCbCr = true;
                    break;
                }
                case 32946: {
                    bufSize = (int)bytesPerTile;
                    deflater = new Deflater(encodeParam.getDeflateLevel());
                    break;
                }
                default: {
                    bufSize = 0;
                }
            }
            if (bufSize != 0) {
                compressBuf = new byte[bufSize];
            }
        }
        int[] pixels = null;
        float[] fpixels = null;
        boolean checkContiguous = sampleSize[0] == 1 && sampleModel instanceof MultiPixelPackedSampleModel && dataType == 0 || sampleSize[0] == 8 && sampleModel instanceof ComponentSampleModel;
        byte[] bpixels = null;
        if (compression != 7) {
            if (dataType == 0) {
                bpixels = new byte[tileHeight * tileWidth * numBands];
            } else if (dataTypeIsShort) {
                bpixels = new byte[2 * tileHeight * tileWidth * numBands];
            } else if (dataType == 3 || dataType == 4) {
                bpixels = new byte[4 * tileHeight * tileWidth * numBands];
            }
        }
        int lastRow = minY + height;
        int lastCol = minX + width;
        int tileNum = 0;
        for (int row = minY; row < lastRow; row += tileHeight) {
            int rows = isTiled ? tileHeight : Math.min(tileHeight, lastRow - row);
            int size = rows * tileWidth * numBands;
            block55: for (int col = minX; col < lastCol; col += tileWidth) {
                int i;
                Raster src = im.getData(new Rectangle(col, row, tileWidth, rows));
                boolean useDataBuffer = false;
                if (compression != 7) {
                    if (checkContiguous) {
                        if (sampleSize[0] == 8) {
                            ComponentSampleModel csm = (ComponentSampleModel)src.getSampleModel();
                            int[] bankIndices = csm.getBankIndices();
                            int[] bandOffsets = csm.getBandOffsets();
                            int pixelStride = csm.getPixelStride();
                            int lineStride = csm.getScanlineStride();
                            if (pixelStride != numBands || (long)lineStride != bytesPerRow) {
                                useDataBuffer = false;
                            } else {
                                useDataBuffer = true;
                                for (i = 0; useDataBuffer && i < numBands; ++i) {
                                    if (bankIndices[i] == 0 && bandOffsets[i] == i) continue;
                                    useDataBuffer = false;
                                }
                            }
                        } else {
                            MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)src.getSampleModel();
                            if (mpp.getNumBands() == 1 && mpp.getDataBitOffset() == 0 && mpp.getPixelBitStride() == 1) {
                                useDataBuffer = true;
                            }
                        }
                    }
                    if (!useDataBuffer) {
                        if (dataType == 4) {
                            fpixels = src.getPixels(col, row, tileWidth, rows, fpixels);
                        } else {
                            pixels = src.getPixels(col, row, tileWidth, rows, pixels);
                        }
                    }
                }
                int pixel = 0;
                int k = 0;
                switch (sampleSize[0]) {
                    case 1: {
                        int j;
                        int i2;
                        int j2;
                        int outOffset;
                        if (useDataBuffer) {
                            byte[] btmp = ((DataBufferByte)src.getDataBuffer()).getData();
                            MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)src.getSampleModel();
                            int lineStride = mpp.getScanlineStride();
                            int inOffset = mpp.getOffset(col - src.getSampleModelTranslateX(), row - src.getSampleModelTranslateY());
                            if (lineStride == (int)bytesPerRow) {
                                System.arraycopy(btmp, inOffset, bpixels, 0, (int)bytesPerRow * rows);
                            } else {
                                outOffset = 0;
                                for (j2 = 0; j2 < rows; ++j2) {
                                    System.arraycopy(btmp, inOffset, bpixels, outOffset, (int)bytesPerRow);
                                    inOffset += lineStride;
                                    outOffset += (int)bytesPerRow;
                                }
                            }
                        } else {
                            int index = 0;
                            for (i2 = 0; i2 < rows; ++i2) {
                                for (j = 0; j < tileWidth / 8; ++j) {
                                    pixel = pixels[index++] << 7 | pixels[index++] << 6 | pixels[index++] << 5 | pixels[index++] << 4 | pixels[index++] << 3 | pixels[index++] << 2 | pixels[index++] << 1 | pixels[index++];
                                    bpixels[k++] = (byte)pixel;
                                }
                                if (tileWidth % 8 <= 0) continue;
                                pixel = 0;
                                for (j = 0; j < tileWidth % 8; ++j) {
                                    pixel |= pixels[index++] << 7 - j;
                                }
                                bpixels[k++] = (byte)pixel;
                            }
                        }
                        if (compression == 1) {
                            this.output.write(bpixels, 0, rows * ((tileWidth + 7) / 8));
                            continue block55;
                        }
                        if (compression == 2) {
                            int rowStride = (tileWidth + 7) / 8;
                            int rowOffset = 0;
                            int numCompressedBytes = 0;
                            for (int tileRow = 0; tileRow < rows; ++tileRow) {
                                int numCompressedBytesInRow = faxEncoder.encodeRLE(bpixels, rowOffset, 0, tileWidth, compressBuf);
                                this.output.write(compressBuf, 0, numCompressedBytesInRow);
                                rowOffset += rowStride;
                                numCompressedBytes += numCompressedBytesInRow;
                            }
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            continue block55;
                        }
                        if (compression == 3) {
                            int numCompressedBytes = faxEncoder.encodeT4(!T4encode2D, T4PadEOLs, bpixels, (tileWidth + 7) / 8, 0, tileWidth, rows, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block55;
                        }
                        if (compression == 4) {
                            int numCompressedBytes = faxEncoder.encodeT6(bpixels, (tileWidth + 7) / 8, 0, tileWidth, rows, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block55;
                        }
                        if (compression == 32773) {
                            int numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block55;
                        }
                        if (compression != 32946) continue block55;
                        int numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block55;
                    }
                    case 4: {
                        int numCompressedBytes;
                        int j;
                        int i2;
                        int index = 0;
                        for (i2 = 0; i2 < rows; ++i2) {
                            for (j = 0; j < tileWidth / 2; ++j) {
                                pixel = pixels[index++] << 4 | pixels[index++];
                                bpixels[k++] = (byte)pixel;
                            }
                            if (tileWidth % 2 != 1) continue;
                            pixel = pixels[index++] << 4;
                            bpixels[k++] = (byte)pixel;
                        }
                        if (compression == 1) {
                            this.output.write(bpixels, 0, rows * ((tileWidth + 1) / 2));
                            continue block55;
                        }
                        if (compression == 32773) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block55;
                        }
                        if (compression != 32946) continue block55;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block55;
                    }
                    case 8: {
                        int numCompressedBytes;
                        int i2;
                        int j2;
                        int outOffset;
                        if (compression != 7) {
                            if (useDataBuffer) {
                                byte[] btmp = ((DataBufferByte)src.getDataBuffer()).getData();
                                ComponentSampleModel csm = (ComponentSampleModel)src.getSampleModel();
                                int inOffset = csm.getOffset(col - src.getSampleModelTranslateX(), row - src.getSampleModelTranslateY());
                                int lineStride = csm.getScanlineStride();
                                if (lineStride == (int)bytesPerRow) {
                                    System.arraycopy(btmp, inOffset, bpixels, 0, (int)bytesPerRow * rows);
                                } else {
                                    outOffset = 0;
                                    for (j2 = 0; j2 < rows; ++j2) {
                                        System.arraycopy(btmp, inOffset, bpixels, outOffset, (int)bytesPerRow);
                                        inOffset += lineStride;
                                        outOffset += (int)bytesPerRow;
                                    }
                                }
                            } else {
                                for (i2 = 0; i2 < size; ++i2) {
                                    bpixels[i2] = (byte)pixels[i2];
                                }
                            }
                        }
                        if (compression == 1) {
                            this.output.write(bpixels, 0, size);
                            continue block55;
                        }
                        if (compression == 32773) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block55;
                        }
                        if (compression == 7) {
                            long startPos = this.getOffset(this.output);
                            if (jpegEncoder == null || jpegEncodeParam.getWidth() != src.getWidth() || jpegEncodeParam.getHeight() != src.getHeight()) {
                                jpegEncodeParam = JPEGCodec.getDefaultJPEGEncodeParam((Raster)src, (int)jpegColorID);
                                JPEGImageEncoder.modifyEncodeParam(jep, jpegEncodeParam, numBands);
                                jpegEncoder = JPEGCodec.createJPEGEncoder((OutputStream)this.output, (com.sun.image.codec.jpeg.JPEGEncodeParam)jpegEncodeParam);
                            }
                            if (jpegRGBToYCbCr) {
                                WritableRaster wRas = null;
                                if (src instanceof WritableRaster) {
                                    wRas = (WritableRaster)src;
                                } else {
                                    wRas = src.createCompatibleWritableRaster();
                                    wRas.setRect(src);
                                }
                                if (wRas.getMinX() != 0 || wRas.getMinY() != 0) {
                                    wRas = wRas.createWritableTranslatedChild(0, 0);
                                }
                                BufferedImage bi = new BufferedImage(colorModel, wRas, false, null);
                                jpegEncoder.encode(bi);
                            } else {
                                jpegEncoder.encode(src.createTranslatedChild(0, 0));
                            }
                            long endPos = this.getOffset(this.output);
                            tileByteCounts[tileNum++] = (int)(endPos - startPos);
                            continue block55;
                        }
                        if (compression != 32946) continue block55;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block55;
                    }
                    case 16: {
                        int numCompressedBytes;
                        int ls = 0;
                        for (int i3 = 0; i3 < size; ++i3) {
                            short value = (short)pixels[i3];
                            bpixels[ls++] = (byte)((value & 0xFF00) >> 8);
                            bpixels[ls++] = (byte)(value & 0xFF);
                        }
                        if (compression == 1) {
                            this.output.write(bpixels, 0, size * 2);
                            continue block55;
                        }
                        if (compression == 32773) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block55;
                        }
                        if (compression != 32946) continue block55;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block55;
                    }
                    case 32: {
                        int numCompressedBytes;
                        if (dataType == 3) {
                            int li = 0;
                            for (i = 0; i < size; ++i) {
                                int value = pixels[i];
                                bpixels[li++] = (byte)((value & 0xFF000000) >> 24);
                                bpixels[li++] = (byte)((value & 0xFF0000) >> 16);
                                bpixels[li++] = (byte)((value & 0xFF00) >> 8);
                                bpixels[li++] = (byte)(value & 0xFF);
                            }
                        } else {
                            int lf = 0;
                            for (i = 0; i < size; ++i) {
                                int value = Float.floatToIntBits(fpixels[i]);
                                bpixels[lf++] = (byte)((value & 0xFF000000) >> 24);
                                bpixels[lf++] = (byte)((value & 0xFF0000) >> 16);
                                bpixels[lf++] = (byte)((value & 0xFF00) >> 8);
                                bpixels[lf++] = (byte)(value & 0xFF);
                            }
                        }
                        if (compression == 1) {
                            this.output.write(bpixels, 0, size * 4);
                            continue block55;
                        }
                        if (compression == 32773) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block55;
                        }
                        if (compression != 32946) continue block55;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                    }
                }
            }
        }
        if (compression == 1) {
            if (skipByte) {
                this.output.write(0);
            }
        } else {
            int totalBytes = 0;
            for (int i = 1; i < numTiles; ++i) {
                int numBytes = (int)tileByteCounts[i - 1];
                totalBytes += numBytes;
                tileOffsets[i] = tileOffsets[i - 1] + (long)numBytes;
            }
            int n = nextIFDOffset = isLast ? 0 : ifdOffset + dirSize + (totalBytes += (int)tileByteCounts[numTiles - 1]);
            if (nextIFDOffset % 2 != 0) {
                ++nextIFDOffset;
                skipByte = true;
            }
            if (outCache == null) {
                if (skipByte) {
                    this.output.write(0);
                }
                SeekableOutputStream sos = (SeekableOutputStream)this.output;
                long savePos = sos.getFilePointer();
                sos.seek(ifdOffset);
                this.writeDirectory(ifdOffset, fields, nextIFDOffset);
                sos.seek(savePos);
            } else if (tempFile != null) {
                int bytesRead;
                this.output.close();
                FileInputStream fileStream = new FileInputStream(tempFile);
                this.output = outCache;
                this.writeDirectory(ifdOffset, fields, nextIFDOffset);
                byte[] copyBuffer = new byte[8192];
                for (int bytesCopied = 0; bytesCopied < totalBytes && (bytesRead = fileStream.read(copyBuffer)) != -1; bytesCopied += bytesRead) {
                    this.output.write(copyBuffer, 0, bytesRead);
                }
                fileStream.close();
                tempFile.delete();
                if (skipByte) {
                    this.output.write(0);
                }
            } else if (this.output instanceof ByteArrayOutputStream) {
                ByteArrayOutputStream memoryStream = (ByteArrayOutputStream)this.output;
                this.output = outCache;
                this.writeDirectory(ifdOffset, fields, nextIFDOffset);
                memoryStream.writeTo(this.output);
                if (skipByte) {
                    this.output.write(0);
                }
            } else {
                throw new IllegalStateException();
            }
        }
        return nextIFDOffset;
    }

    private int getDirectorySize(SortedSet fields) {
        int numEntries = fields.size();
        int dirSize = 2 + numEntries * 12 + 4;
        Iterator iter = fields.iterator();
        while (iter.hasNext()) {
            TIFFField field = (TIFFField)iter.next();
            int valueSize = TIFFImageEncoder.getValueSize(field);
            if (valueSize <= 4) continue;
            dirSize += valueSize;
        }
        return dirSize;
    }

    private void writeFileHeader() throws IOException {
        if (this.isLittleEndian) {
            this.output.write(73);
            this.output.write(73);
        } else {
            this.output.write(77);
            this.output.write(77);
        }
        this.writeUnsignedShort(42);
        this.writeLong(8L);
    }

    private void writeDirectory(int thisIFDOffset, SortedSet fields, int nextIFDOffset) throws IOException {
        int numEntries = fields.size();
        long offsetBeyondIFD = thisIFDOffset + 12 * numEntries + 4 + 2;
        ArrayList<TIFFField> tooBig = new ArrayList<TIFFField>();
        this.writeUnsignedShort(numEntries);
        Iterator iter = fields.iterator();
        while (iter.hasNext()) {
            TIFFField field = (TIFFField)iter.next();
            int tag = field.getTag();
            this.writeUnsignedShort(tag);
            int type = field.getType();
            this.writeUnsignedShort(type);
            int count = field.getCount();
            int valueSize = TIFFImageEncoder.getValueSize(field);
            this.writeLong(type == 2 ? (long)valueSize : (long)count);
            if (valueSize > 4) {
                this.writeLong(offsetBeyondIFD);
                offsetBeyondIFD += (long)valueSize;
                tooBig.add(field);
                continue;
            }
            this.writeValuesAsFourBytes(field);
        }
        this.writeLong(nextIFDOffset);
        for (int i = 0; i < tooBig.size(); ++i) {
            this.writeValues((TIFFField)tooBig.get(i));
        }
    }

    private static final int getValueSize(TIFFField field) {
        int type = field.getType();
        int count = field.getCount();
        int valueSize = 0;
        if (type == 2) {
            for (int i = 0; i < count; ++i) {
                byte[] stringBytes = field.getAsString(i).getBytes();
                valueSize += stringBytes.length;
                if (stringBytes[stringBytes.length - 1] == 0) continue;
                ++valueSize;
            }
        } else {
            valueSize = count * sizeOfType[type];
        }
        return valueSize;
    }

    private void writeValuesAsFourBytes(TIFFField field) throws IOException {
        int dataType = field.getType();
        int count = field.getCount();
        switch (dataType) {
            case 1: 
            case 6: 
            case 7: {
                int i;
                byte[] bytes = field.getAsBytes();
                for (i = 0; i < count; ++i) {
                    this.output.write(bytes[i]);
                }
                for (i = 0; i < 4 - count; ++i) {
                    this.output.write(0);
                }
                break;
            }
            case 3: {
                int i;
                char[] shorts = field.getAsChars();
                for (i = 0; i < count; ++i) {
                    this.writeUnsignedShort(shorts[i]);
                }
                for (i = 0; i < 2 - count; ++i) {
                    this.writeUnsignedShort(0);
                }
                break;
            }
            case 8: {
                int i;
                short[] sshorts = field.getAsShorts();
                for (i = 0; i < count; ++i) {
                    this.writeUnsignedShort(sshorts[i]);
                }
                for (i = 0; i < 2 - count; ++i) {
                    this.writeUnsignedShort(0);
                }
                break;
            }
            case 4: {
                this.writeLong(field.getAsLong(0));
                break;
            }
            case 9: {
                this.writeLong(field.getAsInt(0));
                break;
            }
            case 11: {
                this.writeLong(Float.floatToIntBits(field.getAsFloat(0)));
                break;
            }
            case 2: {
                int i;
                int asciiByteCount = 0;
                for (i = 0; i < count; ++i) {
                    byte[] stringBytes = field.getAsString(i).getBytes();
                    this.output.write(stringBytes);
                    asciiByteCount += stringBytes.length;
                    if (stringBytes[stringBytes.length - 1] == 0) continue;
                    this.output.write(0);
                    ++asciiByteCount;
                }
                for (i = 0; i < 4 - asciiByteCount; ++i) {
                    this.output.write(0);
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder10"));
            }
        }
    }

    private void writeValues(TIFFField field) throws IOException {
        int dataType = field.getType();
        int count = field.getCount();
        switch (dataType) {
            case 1: 
            case 6: 
            case 7: {
                byte[] bytes = field.getAsBytes();
                for (int i = 0; i < count; ++i) {
                    this.output.write(bytes[i]);
                }
                break;
            }
            case 3: {
                char[] shorts = field.getAsChars();
                for (int i = 0; i < count; ++i) {
                    this.writeUnsignedShort(shorts[i]);
                }
                break;
            }
            case 8: {
                short[] sshorts = field.getAsShorts();
                for (int i = 0; i < count; ++i) {
                    this.writeUnsignedShort(sshorts[i]);
                }
                break;
            }
            case 4: {
                long[] longs = field.getAsLongs();
                for (int i = 0; i < count; ++i) {
                    this.writeLong(longs[i]);
                }
                break;
            }
            case 9: {
                int[] slongs = field.getAsInts();
                for (int i = 0; i < count; ++i) {
                    this.writeLong(slongs[i]);
                }
                break;
            }
            case 11: {
                float[] floats = field.getAsFloats();
                for (int i = 0; i < count; ++i) {
                    int intBits = Float.floatToIntBits(floats[i]);
                    this.writeLong(intBits);
                }
                break;
            }
            case 12: {
                double[] doubles = field.getAsDoubles();
                for (int i = 0; i < count; ++i) {
                    long longBits = Double.doubleToLongBits(doubles[i]);
                    this.writeLong((int)(longBits >> 32));
                    this.writeLong((int)(longBits & 0xFFFFFFFFFFFFFFFFL));
                }
                break;
            }
            case 5: {
                long[][] rationals = field.getAsRationals();
                for (int i = 0; i < count; ++i) {
                    this.writeLong(rationals[i][0]);
                    this.writeLong(rationals[i][1]);
                }
                break;
            }
            case 10: {
                int[][] srationals = field.getAsSRationals();
                for (int i = 0; i < count; ++i) {
                    this.writeLong(srationals[i][0]);
                    this.writeLong(srationals[i][1]);
                }
                break;
            }
            case 2: {
                for (int i = 0; i < count; ++i) {
                    byte[] stringBytes = field.getAsString(i).getBytes();
                    this.output.write(stringBytes);
                    if (stringBytes[stringBytes.length - 1] == 0) continue;
                    this.output.write(0);
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder10"));
            }
        }
    }

    private void writeUnsignedShort(int s) throws IOException {
        if (this.isLittleEndian) {
            this.output.write(s & 0xFF);
            this.output.write((s & 0xFF00) >>> 8);
        } else {
            this.output.write((s & 0xFF00) >>> 8);
            this.output.write(s & 0xFF);
        }
    }

    private void writeLong(long l) throws IOException {
        if (this.isLittleEndian) {
            this.output.write((int)l & 0xFF);
            this.output.write((int)((l & 0xFF00L) >>> 8));
            this.output.write((int)((l & 0xFF0000L) >>> 16));
            this.output.write((int)((l & 0xFFFFFFFFFF000000L) >>> 24));
        } else {
            this.output.write((int)((l & 0xFFFFFFFFFF000000L) >>> 24));
            this.output.write((int)((l & 0xFF0000L) >>> 16));
            this.output.write((int)((l & 0xFF00L) >>> 8));
            this.output.write((int)l & 0xFF);
        }
    }

    private long getOffset(OutputStream out) throws IOException {
        if (out instanceof ByteArrayOutputStream) {
            return ((ByteArrayOutputStream)out).size();
        }
        if (out instanceof SeekableOutputStream) {
            return ((SeekableOutputStream)out).getFilePointer();
        }
        throw new IllegalStateException();
    }

    private static int compressPackBits(byte[] data, int numRows, int bytesPerRow, byte[] compData) {
        int inOffset = 0;
        int outOffset = 0;
        for (int i = 0; i < numRows; ++i) {
            outOffset = TIFFImageEncoder.packBits(data, inOffset, bytesPerRow, compData, outOffset);
            inOffset += bytesPerRow;
        }
        return outOffset;
    }

    private static int packBits(byte[] input, int inOffset, int inCount, byte[] output, int outOffset) {
        int inMax = inOffset + inCount - 1;
        int inMaxMinus1 = inMax - 1;
        while (inOffset <= inMax) {
            int run;
            byte replicate = input[inOffset];
            for (run = 1; run < 127 && inOffset < inMax && input[inOffset] == input[inOffset + 1]; ++run, ++inOffset) {
            }
            if (run > 1) {
                ++inOffset;
                output[outOffset++] = (byte)(-(run - 1));
                output[outOffset++] = replicate;
            }
            int saveOffset = outOffset;
            for (run = 0; run < 128 && (inOffset < inMax && input[inOffset] != input[inOffset + 1] || inOffset < inMaxMinus1 && input[inOffset] != input[inOffset + 2]); ++run) {
                output[++outOffset] = input[inOffset++];
            }
            if (run > 0) {
                output[saveOffset] = (byte)(run - 1);
                ++outOffset;
            }
            if (inOffset != inMax) continue;
            if (run > 0 && run < 128) {
                int n = saveOffset;
                output[n] = (byte)(output[n] + 1);
                output[outOffset++] = input[inOffset++];
                continue;
            }
            output[outOffset++] = 0;
            output[outOffset++] = input[inOffset++];
        }
        return outOffset;
    }

    private static int deflate(Deflater deflater, byte[] inflated, byte[] deflated) {
        deflater.setInput(inflated);
        deflater.finish();
        int numCompressedBytes = deflater.deflate(deflated);
        deflater.reset();
        return numCompressedBytes;
    }
}

