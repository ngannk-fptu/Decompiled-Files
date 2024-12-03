/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.Deflater;
import org.apache.xmlgraphics.image.codec.tiff.CompressionValue;
import org.apache.xmlgraphics.image.codec.tiff.ImageInfo;
import org.apache.xmlgraphics.image.codec.tiff.ImageType;
import org.apache.xmlgraphics.image.codec.tiff.TIFFEncodeParam;
import org.apache.xmlgraphics.image.codec.tiff.TIFFField;
import org.apache.xmlgraphics.image.codec.util.ImageEncodeParam;
import org.apache.xmlgraphics.image.codec.util.ImageEncoderImpl;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;
import org.apache.xmlgraphics.image.codec.util.SeekableOutputStream;

public class TIFFImageEncoder
extends ImageEncoderImpl {
    private static final int TIFF_JPEG_TABLES = 347;
    private static final int TIFF_YCBCR_SUBSAMPLING = 530;
    private static final int TIFF_YCBCR_POSITIONING = 531;
    private static final int TIFF_REF_BLACK_WHITE = 532;
    private static final int[] SIZE_OF_TYPE = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    public TIFFImageEncoder(OutputStream output, ImageEncodeParam param) {
        super(output, param);
        if (this.param == null) {
            this.param = new TIFFEncodeParam();
        }
    }

    @Override
    public void encode(RenderedImage im) throws IOException {
        this.writeFileHeader();
        TIFFEncodeParam encodeParam = (TIFFEncodeParam)this.param;
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

    public Object encodeMultiple(Object context, RenderedImage img) throws IOException {
        TIFFEncodeParam encodeParam = (TIFFEncodeParam)this.param;
        if (encodeParam.getExtraImages() != null) {
            throw new IllegalStateException(PropertyUtil.getString("TIFFImageEncoder11"));
        }
        Context c = (Context)context;
        if (c == null) {
            c = new Context();
            this.writeFileHeader();
        } else {
            c.ifdOffset = this.encode(c.nextImage, encodeParam, c.ifdOffset, false);
        }
        c.nextImage = img;
        return c;
    }

    public void finishMultiple(Object context) throws IOException {
        if (context == null) {
            throw new NullPointerException();
        }
        Context c = (Context)context;
        TIFFEncodeParam encodeParam = (TIFFEncodeParam)this.param;
        c.ifdOffset = this.encode(c.nextImage, encodeParam, c.ifdOffset, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int encode(RenderedImage im, TIFFEncodeParam encodeParam, int ifdOffset, boolean isLast) throws IOException {
        CompressionValue compression = encodeParam.getCompression();
        if (compression == CompressionValue.JPEG_TTN2) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFImageEncoder12"));
        }
        boolean isTiled = encodeParam.getWriteTiled();
        int minX = im.getMinX();
        int minY = im.getMinY();
        int width = im.getWidth();
        int height = im.getHeight();
        SampleModel sampleModel = im.getSampleModel();
        ColorModel colorModel = im.getColorModel();
        int[] sampleSize = sampleModel.getSampleSize();
        int dataTypeSize = sampleSize[0];
        int numBands = sampleModel.getNumBands();
        int dataType = sampleModel.getDataType();
        this.validateImage(dataTypeSize, sampleSize, numBands, dataType, colorModel);
        boolean dataTypeIsShort = dataType == 2 || dataType == 1;
        ImageInfo imageInfo = ImageInfo.newInstance(im, dataTypeSize, numBands, colorModel, encodeParam);
        if (imageInfo.getType() == ImageType.UNSUPPORTED) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder8"));
        }
        int numTiles = imageInfo.getNumTiles();
        long bytesPerTile = imageInfo.getBytesPerTile();
        long bytesPerRow = imageInfo.getBytesPerRow();
        int tileHeight = imageInfo.getTileHeight();
        int tileWidth = imageInfo.getTileWidth();
        long[] tileByteCounts = new long[numTiles];
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
        char[] shortSampleSize = new char[numBands];
        for (int i = 0; i < numBands; ++i) {
            shortSampleSize[i] = (char)dataTypeSize;
        }
        fields.add(new TIFFField(258, 3, numBands, shortSampleSize));
        fields.add(new TIFFField(259, 3, 1, new char[]{(char)compression.getValue()}));
        fields.add(new TIFFField(262, 3, 1, new char[]{(char)imageInfo.getType().getPhotometricInterpretation()}));
        if (!isTiled) {
            fields.add(new TIFFField(273, 4, numTiles, tileOffsets));
        }
        fields.add(new TIFFField(277, 3, 1, new char[]{(char)numBands}));
        if (!isTiled) {
            fields.add(new TIFFField(278, 4, 1, new long[]{tileHeight}));
            fields.add(new TIFFField(279, 4, numTiles, tileByteCounts));
        }
        if (imageInfo.getColormap() != null) {
            fields.add(new TIFFField(320, 3, imageInfo.getColormapSize(), imageInfo.getColormap()));
        }
        if (isTiled) {
            fields.add(new TIFFField(322, 4, 1, new long[]{tileWidth}));
            fields.add(new TIFFField(323, 4, 1, new long[]{tileHeight}));
            fields.add(new TIFFField(324, 4, numTiles, tileOffsets));
            fields.add(new TIFFField(325, 4, numTiles, tileByteCounts));
        }
        if (imageInfo.getNumberOfExtraSamples() > 0) {
            char[] extraSamples = new char[imageInfo.getNumberOfExtraSamples()];
            for (int i = 0; i < imageInfo.getNumberOfExtraSamples(); ++i) {
                extraSamples[i] = (char)imageInfo.getExtraSamplesType().getValue();
            }
            fields.add(new TIFFField(338, 3, imageInfo.getNumberOfExtraSamples(), extraSamples));
        }
        if (dataType != 0) {
            char[] sampleFormat = new char[numBands];
            sampleFormat[0] = dataType == 4 ? 3 : (dataType == 1 ? 1 : 2);
            for (int b = 1; b < numBands; ++b) {
                sampleFormat[b] = sampleFormat[0];
            }
            fields.add(new TIFFField(339, 3, numBands, sampleFormat));
        }
        if (imageInfo.getType() == ImageType.YCBCR) {
            char subsampleH = '\u0001';
            char subsampleV = '\u0001';
            fields.add(new TIFFField(530, 3, 2, new char[]{subsampleH, subsampleV}));
            fields.add(new TIFFField(531, 3, 1, new char[]{(char)(compression == CompressionValue.JPEG_TTN2 ? 1 : 2)}));
            long[][] lArrayArray = new long[][]{{15L, 1L}, {235L, 1L}, {128L, 1L}, {240L, 1L}, {128L, 1L}, {240L, 1L}};
            fields.add(new TIFFField(532, 5, 6, lArrayArray));
        }
        TIFFField[] extraFields = encodeParam.getExtraFields();
        ArrayList<Integer> extantTags = new ArrayList<Integer>(fields.size());
        for (TIFFField fld : fields) {
            extantTags.add(fld.getTag());
        }
        for (TIFFField fld : extraFields) {
            Integer tagValue = fld.getTag();
            if (extantTags.contains(tagValue)) continue;
            fields.add(fld);
            extantTags.add(tagValue);
        }
        int n = this.getDirectorySize(fields);
        tileOffsets[0] = ifdOffset + n;
        OutputStream outCache = null;
        byte[] compressBuf = null;
        File tempFile = null;
        int nextIFDOffset = 0;
        boolean skipByte = false;
        Deflater deflater = null;
        boolean jpegRGBToYCbCr = false;
        if (compression == CompressionValue.NONE) {
            int numBytesPadding = 0;
            if (dataTypeSize == 16 && tileOffsets[0] % 2L != 0L) {
                numBytesPadding = 1;
                tileOffsets[0] = tileOffsets[0] + 1L;
            } else if (dataTypeSize == 32 && tileOffsets[0] % 4L != 0L) {
                numBytesPadding = (int)(4L - tileOffsets[0] % 4L);
                tileOffsets[0] = tileOffsets[0] + (long)numBytesPadding;
            }
            for (int i = 1; i < numTiles; ++i) {
                tileOffsets[i] = tileOffsets[i - 1] + tileByteCounts[i - 1];
            }
            if (!isLast && ((nextIFDOffset = (int)(tileOffsets[0] + totalBytesOfData)) & 1) != 0) {
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
                catch (IOException e) {
                    this.output = new ByteArrayOutputStream((int)totalBytesOfData);
                }
            }
            int bufSize = 0;
            switch (compression) {
                case PACKBITS: {
                    bufSize = (int)(bytesPerTile + (bytesPerRow + 127L) / 128L * (long)tileHeight);
                    break;
                }
                case DEFLATE: {
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
        boolean checkContiguous = dataTypeSize == 1 && sampleModel instanceof MultiPixelPackedSampleModel && dataType == 0 || dataTypeSize == 8 && sampleModel instanceof ComponentSampleModel;
        byte[] bpixels = null;
        if (compression != CompressionValue.JPEG_TTN2) {
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
            block25: for (int col = minX; col < lastCol; col += tileWidth) {
                int i;
                Raster src = im.getData(new Rectangle(col, row, tileWidth, rows));
                boolean useDataBuffer = false;
                if (compression != CompressionValue.JPEG_TTN2) {
                    if (checkContiguous) {
                        if (dataTypeSize == 8) {
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
                switch (dataTypeSize) {
                    case 1: {
                        int j;
                        int i2;
                        int outOffset;
                        if (useDataBuffer) {
                            byte[] btmp = ((DataBufferByte)src.getDataBuffer()).getData();
                            MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)src.getSampleModel();
                            int lineStride = mpp.getScanlineStride();
                            int inOffset = mpp.getOffset(col - src.getSampleModelTranslateX(), row - src.getSampleModelTranslateY());
                            if ((long)lineStride == bytesPerRow) {
                                System.arraycopy(btmp, inOffset, bpixels, 0, (int)bytesPerRow * rows);
                            } else {
                                outOffset = 0;
                                for (int j2 = 0; j2 < rows; ++j2) {
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
                        if (compression == CompressionValue.NONE) {
                            this.output.write(bpixels, 0, rows * ((tileWidth + 7) / 8));
                            continue block25;
                        }
                        if (compression == CompressionValue.PACKBITS) {
                            int numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block25;
                        }
                        if (compression != CompressionValue.DEFLATE) continue block25;
                        int numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block25;
                    }
                    case 4: {
                        int numCompressedBytes;
                        int j;
                        int index = 0;
                        for (int i2 = 0; i2 < rows; ++i2) {
                            for (j = 0; j < tileWidth / 2; ++j) {
                                pixel = pixels[index++] << 4 | pixels[index++];
                                bpixels[k++] = (byte)pixel;
                            }
                            if ((tileWidth & 1) != 1) continue;
                            pixel = pixels[index++] << 4;
                            bpixels[k++] = (byte)pixel;
                        }
                        if (compression == CompressionValue.NONE) {
                            this.output.write(bpixels, 0, rows * ((tileWidth + 1) / 2));
                            continue block25;
                        }
                        if (compression == CompressionValue.PACKBITS) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block25;
                        }
                        if (compression != CompressionValue.DEFLATE) continue block25;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block25;
                    }
                    case 8: {
                        int numCompressedBytes;
                        int i2;
                        int outOffset;
                        if (compression != CompressionValue.JPEG_TTN2) {
                            if (useDataBuffer) {
                                byte[] btmp = ((DataBufferByte)src.getDataBuffer()).getData();
                                ComponentSampleModel csm = (ComponentSampleModel)src.getSampleModel();
                                int inOffset = csm.getOffset(col - src.getSampleModelTranslateX(), row - src.getSampleModelTranslateY());
                                int lineStride = csm.getScanlineStride();
                                if ((long)lineStride == bytesPerRow) {
                                    System.arraycopy(btmp, inOffset, bpixels, 0, (int)bytesPerRow * rows);
                                } else {
                                    outOffset = 0;
                                    for (int j = 0; j < rows; ++j) {
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
                        if (compression == CompressionValue.NONE) {
                            this.output.write(bpixels, 0, size);
                            continue block25;
                        }
                        if (compression == CompressionValue.PACKBITS) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block25;
                        }
                        if (compression != CompressionValue.DEFLATE) continue block25;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block25;
                    }
                    case 16: {
                        int numCompressedBytes;
                        int ls = 0;
                        for (int i3 = 0; i3 < size; ++i3) {
                            int value = pixels[i3];
                            bpixels[ls++] = (byte)((value & 0xFF00) >> 8);
                            bpixels[ls++] = (byte)(value & 0xFF);
                        }
                        if (compression == CompressionValue.NONE) {
                            this.output.write(bpixels, 0, size * 2);
                            continue block25;
                        }
                        if (compression == CompressionValue.PACKBITS) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block25;
                        }
                        if (compression != CompressionValue.DEFLATE) continue block25;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block25;
                    }
                    case 32: {
                        int value;
                        int numCompressedBytes;
                        if (dataType == 3) {
                            int li = 0;
                            for (i = 0; i < size; ++i) {
                                value = pixels[i];
                                bpixels[li++] = (byte)((value & 0xFF000000) >>> 24);
                                bpixels[li++] = (byte)((value & 0xFF0000) >>> 16);
                                bpixels[li++] = (byte)((value & 0xFF00) >>> 8);
                                bpixels[li++] = (byte)(value & 0xFF);
                            }
                        } else {
                            int lf = 0;
                            for (i = 0; i < size; ++i) {
                                value = Float.floatToIntBits(fpixels[i]);
                                bpixels[lf++] = (byte)((value & 0xFF000000) >>> 24);
                                bpixels[lf++] = (byte)((value & 0xFF0000) >>> 16);
                                bpixels[lf++] = (byte)((value & 0xFF00) >>> 8);
                                bpixels[lf++] = (byte)(value & 0xFF);
                            }
                        }
                        if (compression == CompressionValue.NONE) {
                            this.output.write(bpixels, 0, size * 4);
                            continue block25;
                        }
                        if (compression == CompressionValue.PACKBITS) {
                            numCompressedBytes = TIFFImageEncoder.compressPackBits(bpixels, rows, bytesPerRow, compressBuf);
                            tileByteCounts[tileNum++] = numCompressedBytes;
                            this.output.write(compressBuf, 0, numCompressedBytes);
                            continue block25;
                        }
                        if (compression != CompressionValue.DEFLATE) continue block25;
                        numCompressedBytes = TIFFImageEncoder.deflate(deflater, bpixels, compressBuf);
                        tileByteCounts[tileNum++] = numCompressedBytes;
                        this.output.write(compressBuf, 0, numCompressedBytes);
                        continue block25;
                    }
                }
            }
        }
        if (compression == CompressionValue.NONE) {
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
            int n2 = nextIFDOffset = isLast ? 0 : ifdOffset + n + (totalBytes += (int)tileByteCounts[numTiles - 1]);
            if ((nextIFDOffset & 1) != 0) {
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
                try (FileInputStream fileStream = new FileInputStream(tempFile);){
                    int bytesRead;
                    this.output.close();
                    this.output = outCache;
                    this.writeDirectory(ifdOffset, fields, nextIFDOffset);
                    byte[] copyBuffer = new byte[8192];
                    for (int bytesCopied = 0; bytesCopied < totalBytes; bytesCopied += bytesRead) {
                        bytesRead = fileStream.read(copyBuffer);
                        if (bytesRead == -1) {
                            break;
                        }
                        this.output.write(copyBuffer, 0, bytesRead);
                    }
                }
                boolean isDeleted = tempFile.delete();
                assert (isDeleted);
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
                throw new IllegalStateException(PropertyUtil.getString("TIFFImageEncoder13"));
            }
        }
        return nextIFDOffset;
    }

    private void validateImage(int dataTypeSize, int[] sampleSize, int numBands, int dataType, ColorModel colorModel) {
        for (int i = 1; i < sampleSize.length; ++i) {
            if (sampleSize[i] == dataTypeSize) continue;
            throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder0"));
        }
        if ((dataTypeSize == 1 || dataTypeSize == 4) && numBands != 1) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder1"));
        }
        switch (dataType) {
            case 0: {
                if (dataTypeSize != 4) break;
                throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder2"));
            }
            case 1: 
            case 2: {
                if (dataTypeSize == 16) break;
                throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder3"));
            }
            case 3: 
            case 4: {
                if (dataTypeSize == 32) break;
                throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder4"));
            }
            default: {
                throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder5"));
            }
        }
        if (colorModel instanceof IndexColorModel && dataType != 0) {
            throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder6"));
        }
    }

    private int getDirectorySize(SortedSet fields) {
        int numEntries = fields.size();
        int dirSize = 2 + numEntries * 12 + 4;
        for (Object field1 : fields) {
            TIFFField field = (TIFFField)field1;
            int valueSize = field.getCount() * SIZE_OF_TYPE[field.getType()];
            if (valueSize <= 4) continue;
            dirSize += valueSize;
        }
        return dirSize;
    }

    private void writeFileHeader() throws IOException {
        this.output.write(77);
        this.output.write(77);
        this.output.write(0);
        this.output.write(42);
        this.writeLong(8L);
    }

    private void writeDirectory(int thisIFDOffset, SortedSet fields, int nextIFDOffset) throws IOException {
        int numEntries = fields.size();
        long offsetBeyondIFD = thisIFDOffset + 12 * numEntries + 4 + 2;
        ArrayList<TIFFField> tooBig = new ArrayList<TIFFField>();
        this.writeUnsignedShort(numEntries);
        for (Object e : fields) {
            TIFFField field = (TIFFField)e;
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
        for (Object e : tooBig) {
            this.writeValues((TIFFField)e);
        }
    }

    private static int getValueSize(TIFFField field) throws UnsupportedEncodingException {
        int type = field.getType();
        int count = field.getCount();
        int valueSize = 0;
        if (type == 2) {
            for (int i = 0; i < count; ++i) {
                byte[] stringBytes = field.getAsString(i).getBytes("UTF-8");
                valueSize += stringBytes.length;
                if (stringBytes[stringBytes.length - 1] == 0) continue;
                ++valueSize;
            }
        } else {
            valueSize = count * SIZE_OF_TYPE[type];
        }
        return valueSize;
    }

    private void writeValuesAsFourBytes(TIFFField field) throws IOException {
        int dataType = field.getType();
        int count = field.getCount();
        switch (dataType) {
            case 1: {
                int i;
                byte[] bytes = field.getAsBytes();
                if (count > 4) {
                    count = 4;
                }
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
                char[] chars = field.getAsChars();
                if (count > 2) {
                    count = 2;
                }
                for (i = 0; i < count; ++i) {
                    this.writeUnsignedShort(chars[i]);
                }
                for (i = 0; i < 2 - count; ++i) {
                    this.writeUnsignedShort(0);
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
                char[] chars = field.getAsChars();
                for (int i = 0; i < count; ++i) {
                    this.writeUnsignedShort(chars[i]);
                }
                break;
            }
            case 8: {
                short[] shorts = field.getAsShorts();
                for (int i = 0; i < count; ++i) {
                    this.writeUnsignedShort(shorts[i]);
                }
                break;
            }
            case 4: 
            case 9: {
                long[] longs = field.getAsLongs();
                for (int i = 0; i < count; ++i) {
                    this.writeLong(longs[i]);
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
                    this.writeLong(longBits >>> 32);
                    this.writeLong(longBits & 0xFFFFFFFFL);
                }
                break;
            }
            case 5: 
            case 10: {
                long[][] rationals = field.getAsRationals();
                for (int i = 0; i < count; ++i) {
                    this.writeLong(rationals[i][0]);
                    this.writeLong(rationals[i][1]);
                }
                break;
            }
            case 2: {
                for (int i = 0; i < count; ++i) {
                    byte[] stringBytes = field.getAsString(i).getBytes("UTF-8");
                    this.output.write(stringBytes);
                    if (stringBytes[stringBytes.length - 1] == 0) continue;
                    this.output.write(0);
                }
                break;
            }
            default: {
                throw new RuntimeException(PropertyUtil.getString("TIFFImageEncoder10"));
            }
        }
    }

    private void writeUnsignedShort(int s) throws IOException {
        this.output.write((s & 0xFF00) >>> 8);
        this.output.write(s & 0xFF);
    }

    private void writeLong(long l) throws IOException {
        this.output.write((int)((l & 0xFFFFFFFFFF000000L) >>> 24));
        this.output.write((int)((l & 0xFF0000L) >>> 16));
        this.output.write((int)((l & 0xFF00L) >>> 8));
        this.output.write((int)(l & 0xFFL));
    }

    private static int compressPackBits(byte[] data, int numRows, long bytesPerRow, byte[] compData) {
        int inOffset = 0;
        int outOffset = 0;
        for (int i = 0; i < numRows; ++i) {
            outOffset = TIFFImageEncoder.packBits(data, inOffset, (int)bytesPerRow, compData, outOffset);
            inOffset += (int)bytesPerRow;
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

    private static class Context {
        private RenderedImage nextImage;
        private int ifdOffset = 8;

        private Context() {
        }
    }
}

