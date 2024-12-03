/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.PixelDensity;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.PackBits;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.common.ZlibDeflate;
import org.apache.commons.imaging.common.itu_t4.T4AndT6Compression;
import org.apache.commons.imaging.common.mylzw.MyLzwCompressor;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSummary;

public abstract class TiffImageWriterBase {
    protected final ByteOrder byteOrder;

    public TiffImageWriterBase() {
        this.byteOrder = TiffConstants.DEFAULT_TIFF_BYTE_ORDER;
    }

    public TiffImageWriterBase(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    protected static int imageDataPaddingLength(int dataLength) {
        return (4 - dataLength % 4) % 4;
    }

    public abstract void write(OutputStream var1, TiffOutputSet var2) throws IOException, ImageWriteException;

    protected TiffOutputSummary validateDirectories(TiffOutputSet outputSet) throws ImageWriteException {
        List<TiffOutputDirectory> directories = outputSet.getDirectories();
        if (directories.isEmpty()) {
            throw new ImageWriteException("No directories.");
        }
        TiffOutputDirectory exifDirectory = null;
        TiffOutputDirectory gpsDirectory = null;
        TiffOutputDirectory interoperabilityDirectory = null;
        TiffOutputField exifDirectoryOffsetField = null;
        TiffOutputField gpsDirectoryOffsetField = null;
        TiffOutputField interoperabilityDirectoryOffsetField = null;
        ArrayList<Integer> directoryIndices = new ArrayList<Integer>();
        HashMap<Integer, TiffOutputDirectory> directoryTypeMap = new HashMap<Integer, TiffOutputDirectory>();
        for (TiffOutputDirectory directory : directories) {
            block33: {
                int dirType;
                block32: {
                    dirType = directory.type;
                    directoryTypeMap.put(dirType, directory);
                    if (dirType >= 0) break block32;
                    switch (dirType) {
                        case -2: {
                            if (exifDirectory != null) {
                                throw new ImageWriteException("More than one EXIF directory.");
                            }
                            exifDirectory = directory;
                            break block33;
                        }
                        case -3: {
                            if (gpsDirectory != null) {
                                throw new ImageWriteException("More than one GPS directory.");
                            }
                            gpsDirectory = directory;
                            break block33;
                        }
                        case -4: {
                            if (interoperabilityDirectory != null) {
                                throw new ImageWriteException("More than one Interoperability directory.");
                            }
                            interoperabilityDirectory = directory;
                            break block33;
                        }
                        default: {
                            throw new ImageWriteException("Unknown directory: " + dirType);
                        }
                    }
                }
                if (directoryIndices.contains(dirType)) {
                    throw new ImageWriteException("More than one directory with index: " + dirType + ".");
                }
                directoryIndices.add(dirType);
            }
            HashSet<Integer> fieldTags = new HashSet<Integer>();
            List<TiffOutputField> fields = directory.getFields();
            for (TiffOutputField field : fields) {
                if (fieldTags.contains(field.tag)) {
                    throw new ImageWriteException("Tag (" + field.tagInfo.getDescription() + ") appears twice in directory.");
                }
                fieldTags.add(field.tag);
                if (field.tag == ExifTagConstants.EXIF_TAG_EXIF_OFFSET.tag) {
                    if (exifDirectoryOffsetField != null) {
                        throw new ImageWriteException("More than one Exif directory offset field.");
                    }
                    exifDirectoryOffsetField = field;
                    continue;
                }
                if (field.tag == ExifTagConstants.EXIF_TAG_INTEROP_OFFSET.tag) {
                    if (interoperabilityDirectoryOffsetField != null) {
                        throw new ImageWriteException("More than one Interoperability directory offset field.");
                    }
                    interoperabilityDirectoryOffsetField = field;
                    continue;
                }
                if (field.tag != ExifTagConstants.EXIF_TAG_GPSINFO.tag) continue;
                if (gpsDirectoryOffsetField != null) {
                    throw new ImageWriteException("More than one GPS directory offset field.");
                }
                gpsDirectoryOffsetField = field;
            }
        }
        if (directoryIndices.isEmpty()) {
            throw new ImageWriteException("Missing root directory.");
        }
        Collections.sort(directoryIndices);
        TiffOutputDirectory previousDirectory = null;
        for (int i = 0; i < directoryIndices.size(); ++i) {
            Integer index = (Integer)directoryIndices.get(i);
            if (index != i) {
                throw new ImageWriteException("Missing directory: " + i + ".");
            }
            TiffOutputDirectory directory = (TiffOutputDirectory)directoryTypeMap.get(index);
            if (null != previousDirectory) {
                previousDirectory.setNextDirectory(directory);
            }
            previousDirectory = directory;
        }
        TiffOutputDirectory rootDirectory = (TiffOutputDirectory)directoryTypeMap.get(0);
        TiffOutputSummary result = new TiffOutputSummary(this.byteOrder, rootDirectory, directoryTypeMap);
        if (interoperabilityDirectory == null && interoperabilityDirectoryOffsetField != null) {
            throw new ImageWriteException("Output set has Interoperability Directory Offset field, but no Interoperability Directory");
        }
        if (interoperabilityDirectory != null) {
            if (exifDirectory == null) {
                exifDirectory = outputSet.addExifDirectory();
            }
            if (interoperabilityDirectoryOffsetField == null) {
                interoperabilityDirectoryOffsetField = TiffOutputField.createOffsetField(ExifTagConstants.EXIF_TAG_INTEROP_OFFSET, this.byteOrder);
                exifDirectory.add(interoperabilityDirectoryOffsetField);
            }
            result.add(interoperabilityDirectory, interoperabilityDirectoryOffsetField);
        }
        if (exifDirectory == null && exifDirectoryOffsetField != null) {
            throw new ImageWriteException("Output set has Exif Directory Offset field, but no Exif Directory");
        }
        if (exifDirectory != null) {
            if (exifDirectoryOffsetField == null) {
                exifDirectoryOffsetField = TiffOutputField.createOffsetField(ExifTagConstants.EXIF_TAG_EXIF_OFFSET, this.byteOrder);
                rootDirectory.add(exifDirectoryOffsetField);
            }
            result.add(exifDirectory, exifDirectoryOffsetField);
        }
        if (gpsDirectory == null && gpsDirectoryOffsetField != null) {
            throw new ImageWriteException("Output set has GPS Directory Offset field, but no GPS Directory");
        }
        if (gpsDirectory != null) {
            if (gpsDirectoryOffsetField == null) {
                gpsDirectoryOffsetField = TiffOutputField.createOffsetField(ExifTagConstants.EXIF_TAG_GPSINFO, this.byteOrder);
                rootDirectory.add(gpsDirectoryOffsetField);
            }
            result.add(gpsDirectory, gpsDirectoryOffsetField);
        }
        return result;
    }

    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        int photometricInterpretation;
        int bitsPerSample;
        int samplesPerPixel;
        PixelDensity pixelDensity;
        if ((params = new HashMap<String, Object>(params)).containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        TiffOutputSet userExif = null;
        if (params.containsKey("EXIF")) {
            userExif = (TiffOutputSet)params.remove("EXIF");
        }
        String xmpXml = null;
        if (params.containsKey("XMP_XML")) {
            xmpXml = (String)params.get("XMP_XML");
            params.remove("XMP_XML");
        }
        if ((pixelDensity = (PixelDensity)params.remove("PIXEL_DENSITY")) == null) {
            pixelDensity = PixelDensity.createFromPixelsPerInch(72.0, 72.0);
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int compression = 5;
        int stripSizeInBits = 64000;
        if (params.containsKey("COMPRESSION")) {
            Object value = params.get("COMPRESSION");
            if (value != null) {
                if (!(value instanceof Number)) {
                    throw new ImageWriteException("Invalid compression parameter, must be numeric: " + value);
                }
                compression = ((Number)value).intValue();
            }
            params.remove("COMPRESSION");
            if (params.containsKey("PARAM_KEY_LZW_COMPRESSION_BLOCK_SIZE")) {
                Object bValue = params.get("PARAM_KEY_LZW_COMPRESSION_BLOCK_SIZE");
                if (!(bValue instanceof Number)) {
                    throw new ImageWriteException("Invalid compression block-size parameter: " + value);
                }
                int stripSizeInBytes = ((Number)bValue).intValue();
                if (stripSizeInBytes < 8000) {
                    throw new ImageWriteException("Block size parameter " + stripSizeInBytes + " is less than 8000 minimum");
                }
                stripSizeInBits = stripSizeInBytes * 8;
                params.remove("PARAM_KEY_LZW_COMPRESSION_BLOCK_SIZE");
            }
        }
        HashMap<String, Object> rawParams = new HashMap<String, Object>(params);
        params.remove("T4_OPTIONS");
        params.remove("T6_OPTIONS");
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        if (compression == 2 || compression == 3 || compression == 4) {
            samplesPerPixel = 1;
            bitsPerSample = 1;
            photometricInterpretation = 0;
        } else {
            samplesPerPixel = 3;
            bitsPerSample = 8;
            photometricInterpretation = 2;
        }
        int rowsPerStrip = stripSizeInBits / (width * bitsPerSample * samplesPerPixel);
        rowsPerStrip = Math.max(1, rowsPerStrip);
        byte[][] strips = this.getStrips(src, samplesPerPixel, bitsPerSample, rowsPerStrip);
        int t4Options = 0;
        int t6Options = 0;
        if (compression == 2) {
            for (int i = 0; i < strips.length; ++i) {
                strips[i] = T4AndT6Compression.compressModifiedHuffman(strips[i], width, strips[i].length / ((width + 7) / 8));
            }
        } else if (compression == 3) {
            boolean usesUncompressedMode;
            Integer t4Parameter = (Integer)rawParams.get("T4_OPTIONS");
            if (t4Parameter != null) {
                t4Options = t4Parameter;
            }
            boolean is2D = ((t4Options &= 7) & 1) != 0;
            boolean bl = usesUncompressedMode = (t4Options & 2) != 0;
            if (usesUncompressedMode) {
                throw new ImageWriteException("T.4 compression with the uncompressed mode extension is not yet supported");
            }
            boolean hasFillBitsBeforeEOL = (t4Options & 4) != 0;
            for (int i = 0; i < strips.length; ++i) {
                strips[i] = is2D ? T4AndT6Compression.compressT4_2D(strips[i], width, strips[i].length / ((width + 7) / 8), hasFillBitsBeforeEOL, rowsPerStrip) : T4AndT6Compression.compressT4_1D(strips[i], width, strips[i].length / ((width + 7) / 8), hasFillBitsBeforeEOL);
            }
        } else if (compression == 4) {
            boolean usesUncompressedMode;
            Integer t6Parameter = (Integer)rawParams.get("T6_OPTIONS");
            if (t6Parameter != null) {
                t6Options = t6Parameter;
            }
            boolean bl = usesUncompressedMode = ((t6Options &= 4) & 2) != 0;
            if (usesUncompressedMode) {
                throw new ImageWriteException("T.6 compression with the uncompressed mode extension is not yet supported");
            }
            for (int i = 0; i < strips.length; ++i) {
                strips[i] = T4AndT6Compression.compressT6(strips[i], width, strips[i].length / ((width + 7) / 8));
            }
        } else if (compression == 32773) {
            for (int i = 0; i < strips.length; ++i) {
                strips[i] = new PackBits().compress(strips[i]);
            }
        } else if (compression == 5) {
            for (int i = 0; i < strips.length; ++i) {
                byte[] uncompressed = strips[i];
                int LZW_MINIMUM_CODE_SIZE = 8;
                MyLzwCompressor compressor = new MyLzwCompressor(8, ByteOrder.BIG_ENDIAN, true);
                byte[] compressed = compressor.compress(uncompressed);
                strips[i] = compressed;
            }
        } else if (compression == 8) {
            for (int i = 0; i < strips.length; ++i) {
                strips[i] = ZlibDeflate.compress(strips[i]);
            }
        } else if (compression != 1) {
            throw new ImageWriteException("Invalid compression parameter (Only CCITT 1D/Group 3/Group 4, LZW, Packbits, Zlib Deflate and uncompressed supported).");
        }
        TiffElement.DataElement[] imageData = new TiffElement.DataElement[strips.length];
        for (int i = 0; i < strips.length; ++i) {
            imageData[i] = new TiffImageData.Data(0L, strips[i].length, strips[i]);
        }
        TiffOutputSet outputSet = new TiffOutputSet(this.byteOrder);
        TiffOutputDirectory directory = outputSet.addRootDirectory();
        directory.add(TiffTagConstants.TIFF_TAG_IMAGE_WIDTH, width);
        directory.add(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH, height);
        directory.add(TiffTagConstants.TIFF_TAG_PHOTOMETRIC_INTERPRETATION, (short)photometricInterpretation);
        directory.add(TiffTagConstants.TIFF_TAG_COMPRESSION, (short)compression);
        directory.add(TiffTagConstants.TIFF_TAG_SAMPLES_PER_PIXEL, (short)samplesPerPixel);
        if (samplesPerPixel == 3) {
            directory.add(TiffTagConstants.TIFF_TAG_BITS_PER_SAMPLE, (short)bitsPerSample, (short)bitsPerSample, (short)bitsPerSample);
        } else if (samplesPerPixel == 1) {
            directory.add(TiffTagConstants.TIFF_TAG_BITS_PER_SAMPLE, (short)bitsPerSample);
        }
        directory.add(TiffTagConstants.TIFF_TAG_ROWS_PER_STRIP, rowsPerStrip);
        if (pixelDensity.isUnitless()) {
            directory.add(TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT, (short)0);
            directory.add(TiffTagConstants.TIFF_TAG_XRESOLUTION, RationalNumber.valueOf(pixelDensity.getRawHorizontalDensity()));
            directory.add(TiffTagConstants.TIFF_TAG_YRESOLUTION, RationalNumber.valueOf(pixelDensity.getRawVerticalDensity()));
        } else if (pixelDensity.isInInches()) {
            directory.add(TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT, (short)2);
            directory.add(TiffTagConstants.TIFF_TAG_XRESOLUTION, RationalNumber.valueOf(pixelDensity.horizontalDensityInches()));
            directory.add(TiffTagConstants.TIFF_TAG_YRESOLUTION, RationalNumber.valueOf(pixelDensity.verticalDensityInches()));
        } else {
            directory.add(TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT, (short)1);
            directory.add(TiffTagConstants.TIFF_TAG_XRESOLUTION, RationalNumber.valueOf(pixelDensity.horizontalDensityCentimetres()));
            directory.add(TiffTagConstants.TIFF_TAG_YRESOLUTION, RationalNumber.valueOf(pixelDensity.verticalDensityCentimetres()));
        }
        if (t4Options != 0) {
            directory.add(TiffTagConstants.TIFF_TAG_T4_OPTIONS, t4Options);
        }
        if (t6Options != 0) {
            directory.add(TiffTagConstants.TIFF_TAG_T6_OPTIONS, t6Options);
        }
        if (null != xmpXml) {
            byte[] xmpXmlBytes = xmpXml.getBytes(StandardCharsets.UTF_8);
            directory.add(TiffTagConstants.TIFF_TAG_XMP, xmpXmlBytes);
        }
        TiffImageData.Strips tiffImageData = new TiffImageData.Strips(imageData, rowsPerStrip);
        directory.setTiffImageData(tiffImageData);
        if (userExif != null) {
            this.combineUserExifIntoFinalExif(userExif, outputSet);
        }
        this.write(os, outputSet);
    }

    private void combineUserExifIntoFinalExif(TiffOutputSet userExif, TiffOutputSet outputSet) throws ImageWriteException {
        List<TiffOutputDirectory> outputDirectories = outputSet.getDirectories();
        Collections.sort(outputDirectories, TiffOutputDirectory.COMPARATOR);
        for (TiffOutputDirectory userDirectory : userExif.getDirectories()) {
            int location = Collections.binarySearch(outputDirectories, userDirectory, TiffOutputDirectory.COMPARATOR);
            if (location < 0) {
                outputSet.addDirectory(userDirectory);
                continue;
            }
            TiffOutputDirectory outputDirectory = outputDirectories.get(location);
            for (TiffOutputField userField : userDirectory.getFields()) {
                if (outputDirectory.findField(userField.tagInfo) != null) continue;
                outputDirectory.add(userField);
            }
        }
    }

    private byte[][] getStrips(BufferedImage src, int samplesPerPixel, int bitsPerSample, int rowsPerStrip) {
        int width = src.getWidth();
        int height = src.getHeight();
        int stripCount = (height + rowsPerStrip - 1) / rowsPerStrip;
        byte[][] result = new byte[stripCount][];
        int remainingRows = height;
        for (int i = 0; i < stripCount; ++i) {
            int rowsInStrip = Math.min(rowsPerStrip, remainingRows);
            remainingRows -= rowsInStrip;
            int bitsInRow = bitsPerSample * samplesPerPixel * width;
            int bytesPerRow = (bitsInRow + 7) / 8;
            int bytesInStrip = rowsInStrip * bytesPerRow;
            byte[] uncompressed = new byte[bytesInStrip];
            int counter = 0;
            int stop = i * rowsPerStrip + rowsPerStrip;
            for (int y = i * rowsPerStrip; y < height && y < stop; ++y) {
                int bitCache = 0;
                int bitsInCache = 0;
                for (int x = 0; x < width; ++x) {
                    int rgb = src.getRGB(x, y);
                    int red = 0xFF & rgb >> 16;
                    int green = 0xFF & rgb >> 8;
                    int blue = 0xFF & rgb >> 0;
                    if (bitsPerSample == 1) {
                        int sample = (red + green + blue) / 3;
                        sample = sample > 127 ? 0 : 1;
                        bitCache <<= 1;
                        bitCache |= sample;
                        if (++bitsInCache != 8) continue;
                        uncompressed[counter++] = (byte)bitCache;
                        bitCache = 0;
                        bitsInCache = 0;
                        continue;
                    }
                    uncompressed[counter++] = (byte)red;
                    uncompressed[counter++] = (byte)green;
                    uncompressed[counter++] = (byte)blue;
                }
                if (bitsInCache <= 0) continue;
                uncompressed[counter++] = (byte)(bitCache <<= 8 - bitsInCache);
            }
            result[i] = uncompressed;
        }
        return result;
    }

    protected void writeImageFileHeader(BinaryOutputStream bos) throws IOException {
        int offsetToFirstIFD = 8;
        this.writeImageFileHeader(bos, 8L);
    }

    protected void writeImageFileHeader(BinaryOutputStream bos, long offsetToFirstIFD) throws IOException {
        if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            bos.write(73);
            bos.write(73);
        } else {
            bos.write(77);
            bos.write(77);
        }
        bos.write2Bytes(42);
        bos.write4Bytes((int)offsetToFirstIFD);
    }
}

