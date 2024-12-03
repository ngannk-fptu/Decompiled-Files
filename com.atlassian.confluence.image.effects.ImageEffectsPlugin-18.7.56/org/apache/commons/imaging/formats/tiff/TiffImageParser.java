/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.FormatCompliance;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.XmpEmbeddable;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.tiff.TiffContents;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffRasterData;
import org.apache.commons.imaging.formats.tiff.TiffReader;
import org.apache.commons.imaging.formats.tiff.constants.TiffEpTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.datareaders.ImageDataReader;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterBiLevel;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterCieLab;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterCmyk;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterLogLuv;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterPalette;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterRgb;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreterYCbCr;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterLossy;

public class TiffImageParser
extends ImageParser
implements XmpEmbeddable {
    private static final String DEFAULT_EXTENSION = ".tif";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".tif", ".tiff"};

    @Override
    public String getName() {
        return "Tiff-Custom";
    }

    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    protected String[] getAcceptedExtensions() {
        return ACCEPTED_EXTENSIONS;
    }

    @Override
    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{ImageFormats.TIFF};
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffContents contents = new TiffReader(TiffImageParser.isStrict(params)).readFirstDirectory(byteSource, params, false, formatCompliance);
        TiffDirectory directory = contents.directories.get(0);
        return directory.getFieldValue(TiffEpTagConstants.EXIF_TAG_INTER_COLOR_PROFILE, false);
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffContents contents = new TiffReader(TiffImageParser.isStrict(params)).readFirstDirectory(byteSource, params, false, formatCompliance);
        TiffDirectory directory = contents.directories.get(0);
        TiffField widthField = directory.findField(TiffTagConstants.TIFF_TAG_IMAGE_WIDTH, true);
        TiffField heightField = directory.findField(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH, true);
        if (widthField == null || heightField == null) {
            throw new ImageReadException("TIFF image missing size info.");
        }
        int height = heightField.getIntValue();
        int width = widthField.getIntValue();
        return new Dimension(width, height);
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffReader tiffReader = new TiffReader(TiffImageParser.isStrict(params));
        TiffContents contents = tiffReader.readContents(byteSource, params, formatCompliance);
        List<TiffDirectory> directories = contents.directories;
        TiffImageMetadata result = new TiffImageMetadata(contents);
        for (TiffDirectory dir : directories) {
            TiffImageMetadata.Directory metadataDirectory = new TiffImageMetadata.Directory(tiffReader.getByteOrder(), dir);
            List<TiffField> entries = dir.getDirectoryEntries();
            for (TiffField entry : entries) {
                metadataDirectory.add(entry);
            }
            result.add(metadataDirectory);
        }
        return result;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageInfo.CompressionAlgorithm compressionAlgorithm;
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffContents contents = new TiffReader(TiffImageParser.isStrict(params)).readDirectories(byteSource, false, formatCompliance);
        TiffDirectory directory = contents.directories.get(0);
        TiffField widthField = directory.findField(TiffTagConstants.TIFF_TAG_IMAGE_WIDTH, true);
        TiffField heightField = directory.findField(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH, true);
        if (widthField == null || heightField == null) {
            throw new ImageReadException("TIFF image missing size info.");
        }
        int height = heightField.getIntValue();
        int width = widthField.getIntValue();
        TiffField resolutionUnitField = directory.findField(TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT);
        int resolutionUnit = 2;
        if (resolutionUnitField != null && resolutionUnitField.getValue() != null) {
            resolutionUnit = resolutionUnitField.getIntValue();
        }
        double unitsPerInch = -1.0;
        switch (resolutionUnit) {
            case 1: {
                break;
            }
            case 2: {
                unitsPerInch = 1.0;
                break;
            }
            case 3: {
                unitsPerInch = 2.54;
                break;
            }
        }
        int physicalWidthDpi = -1;
        float physicalWidthInch = -1.0f;
        int physicalHeightDpi = -1;
        float physicalHeightInch = -1.0f;
        if (unitsPerInch > 0.0) {
            TiffField xResolutionField = directory.findField(TiffTagConstants.TIFF_TAG_XRESOLUTION);
            TiffField yResolutionField = directory.findField(TiffTagConstants.TIFF_TAG_YRESOLUTION);
            if (xResolutionField != null && xResolutionField.getValue() != null) {
                double xResolutionPixelsPerUnit = xResolutionField.getDoubleValue();
                physicalWidthDpi = (int)Math.round(xResolutionPixelsPerUnit * unitsPerInch);
                physicalWidthInch = (float)((double)width / (xResolutionPixelsPerUnit * unitsPerInch));
            }
            if (yResolutionField != null && yResolutionField.getValue() != null) {
                double yResolutionPixelsPerUnit = yResolutionField.getDoubleValue();
                physicalHeightDpi = (int)Math.round(yResolutionPixelsPerUnit * unitsPerInch);
                physicalHeightInch = (float)((double)height / (yResolutionPixelsPerUnit * unitsPerInch));
            }
        }
        TiffField bitsPerSampleField = directory.findField(TiffTagConstants.TIFF_TAG_BITS_PER_SAMPLE);
        int bitsPerSample = 1;
        if (bitsPerSampleField != null && bitsPerSampleField.getValue() != null) {
            bitsPerSample = bitsPerSampleField.getIntValueOrArraySum();
        }
        int bitsPerPixel = bitsPerSample;
        List<TiffField> entries = directory.entries;
        ArrayList<String> comments = new ArrayList<String>(entries.size());
        for (TiffField field : entries) {
            String comment = field.toString();
            comments.add(comment);
        }
        ImageFormats format = ImageFormats.TIFF;
        String formatName = "TIFF Tag-based Image File Format";
        String mimeType = "image/tiff";
        int numberOfImages = contents.directories.size();
        boolean progressive = false;
        String formatDetails = "Tiff v." + contents.header.tiffVersion;
        boolean transparent = false;
        boolean usesPalette = false;
        TiffField colorMapField = directory.findField(TiffTagConstants.TIFF_TAG_COLOR_MAP);
        if (colorMapField != null) {
            usesPalette = true;
        }
        ImageInfo.ColorType colorType = ImageInfo.ColorType.RGB;
        short compressionFieldValue = directory.findField(TiffTagConstants.TIFF_TAG_COMPRESSION) != null ? directory.getFieldValue(TiffTagConstants.TIFF_TAG_COMPRESSION) : (short)1;
        int compression = 0xFFFF & compressionFieldValue;
        switch (compression) {
            case 1: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.NONE;
                break;
            }
            case 2: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.CCITT_1D;
                break;
            }
            case 3: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.CCITT_GROUP_3;
                break;
            }
            case 4: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.CCITT_GROUP_4;
                break;
            }
            case 5: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.LZW;
                break;
            }
            case 6: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.JPEG;
                break;
            }
            case 32771: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.NONE;
                break;
            }
            case 32773: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.PACKBITS;
                break;
            }
            default: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.UNKNOWN;
            }
        }
        ImageInfo result = new ImageInfo(formatDetails, bitsPerPixel, comments, format, "TIFF Tag-based Image File Format", height, "image/tiff", numberOfImages, physicalHeightDpi, physicalHeightInch, physicalWidthDpi, physicalWidthInch, width, false, false, usesPalette, colorType, compressionAlgorithm);
        return result;
    }

    @Override
    public String getXmpXml(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffContents contents = new TiffReader(TiffImageParser.isStrict(params)).readDirectories(byteSource, false, formatCompliance);
        TiffDirectory directory = contents.directories.get(0);
        byte[] bytes = directory.getFieldValue(TiffTagConstants.TIFF_TAG_XMP, false);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        try {
            pw.println("tiff.dumpImageFile");
            ImageInfo imageData = this.getImageInfo(byteSource);
            if (imageData == null) {
                boolean bl = false;
                return bl;
            }
            imageData.toString(pw, "");
            pw.println("");
            FormatCompliance formatCompliance = FormatCompliance.getDefault();
            Map<String, Object> params = null;
            TiffContents contents = new TiffReader(true).readContents(byteSource, params, formatCompliance);
            List<TiffDirectory> directories = contents.directories;
            if (directories == null) {
                boolean bl = false;
                return bl;
            }
            for (int d = 0; d < directories.size(); ++d) {
                TiffDirectory directory = directories.get(d);
                List<TiffField> entries = directory.entries;
                if (entries == null) {
                    boolean bl = false;
                    return bl;
                }
                for (TiffField field : entries) {
                    field.dump(pw, Integer.toString(d));
                }
            }
            boolean bl = true;
            return bl;
        }
        finally {
            pw.println("");
        }
    }

    @Override
    public FormatCompliance getFormatCompliance(ByteSource byteSource) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        Map<String, Object> params = null;
        new TiffReader(TiffImageParser.isStrict(params)).readContents(byteSource, params, formatCompliance);
        return formatCompliance;
    }

    public List<byte[]> collectRawImageData(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffContents contents = new TiffReader(TiffImageParser.isStrict(params)).readDirectories(byteSource, true, formatCompliance);
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        for (int i = 0; i < contents.directories.size(); ++i) {
            TiffDirectory directory = contents.directories.get(i);
            List<TiffDirectory.ImageDataElement> dataElements = directory.getTiffRawImageDataElements();
            for (TiffDirectory.ImageDataElement element : dataElements) {
                byte[] bytes = byteSource.getBlock(element.offset, element.length);
                result.add(bytes);
            }
        }
        return result;
    }

    @Override
    public BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffReader reader = new TiffReader(TiffImageParser.isStrict(params));
        TiffContents contents = reader.readFirstDirectory(byteSource, params, true, formatCompliance);
        ByteOrder byteOrder = reader.getByteOrder();
        TiffDirectory directory = contents.directories.get(0);
        BufferedImage result = directory.getTiffImage(byteOrder, params);
        if (null == result) {
            throw new ImageReadException("TIFF does not contain an image.");
        }
        return result;
    }

    @Override
    public List<BufferedImage> getAllBufferedImages(ByteSource byteSource) throws ImageReadException, IOException {
        FormatCompliance formatCompliance = FormatCompliance.getDefault();
        TiffReader tiffReader = new TiffReader(true);
        TiffContents contents = tiffReader.readDirectories(byteSource, true, formatCompliance);
        ArrayList<BufferedImage> results = new ArrayList<BufferedImage>();
        for (int i = 0; i < contents.directories.size(); ++i) {
            TiffDirectory directory = contents.directories.get(i);
            BufferedImage result = directory.getTiffImage(tiffReader.getByteOrder(), null);
            if (result == null) continue;
            results.add(result);
        }
        return results;
    }

    private Integer getIntegerParameter(String key, Map<String, Object> params) throws ImageReadException {
        if (params == null) {
            return null;
        }
        if (!params.containsKey(key)) {
            return null;
        }
        Object obj = params.get(key);
        if (obj instanceof Integer) {
            return (Integer)obj;
        }
        throw new ImageReadException("Non-Integer parameter " + key);
    }

    private Rectangle checkForSubImage(Map<String, Object> params) throws ImageReadException {
        Integer ix0 = this.getIntegerParameter("SUBIMAGE_X", params);
        Integer iy0 = this.getIntegerParameter("SUBIMAGE_Y", params);
        Integer iwidth = this.getIntegerParameter("SUBIMAGE_WIDTH", params);
        Integer iheight = this.getIntegerParameter("SUBIMAGE_HEIGHT", params);
        if (ix0 == null && iy0 == null && iwidth == null && iheight == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(32);
        if (ix0 == null) {
            sb.append(" x0,");
        }
        if (iy0 == null) {
            sb.append(" y0,");
        }
        if (iwidth == null) {
            sb.append(" width,");
        }
        if (iheight == null) {
            sb.append(" height,");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            throw new ImageReadException("Incomplete subimage parameters, missing" + sb.toString());
        }
        return new Rectangle(ix0, iy0, iwidth, iheight);
    }

    protected BufferedImage getBufferedImage(TiffDirectory directory, ByteOrder byteOrder, Map<String, Object> params) throws ImageReadException, IOException {
        List<TiffField> entries = directory.entries;
        if (entries == null) {
            throw new ImageReadException("TIFF missing entries");
        }
        int photometricInterpretation = 0xFFFF & directory.getFieldValue(TiffTagConstants.TIFF_TAG_PHOTOMETRIC_INTERPRETATION);
        short compressionFieldValue = directory.findField(TiffTagConstants.TIFF_TAG_COMPRESSION) != null ? directory.getFieldValue(TiffTagConstants.TIFF_TAG_COMPRESSION) : (short)1;
        int compression = 0xFFFF & compressionFieldValue;
        int width = directory.getSingleFieldValue(TiffTagConstants.TIFF_TAG_IMAGE_WIDTH);
        int height = directory.getSingleFieldValue(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH);
        Rectangle subImage = this.checkForSubImage(params);
        if (subImage != null) {
            if (subImage.width <= 0) {
                throw new ImageReadException("negative or zero subimage width");
            }
            if (subImage.height <= 0) {
                throw new ImageReadException("negative or zero subimage height");
            }
            if (subImage.x < 0 || subImage.x >= width) {
                throw new ImageReadException("subimage x is outside raster");
            }
            if (subImage.x + subImage.width > width) {
                throw new ImageReadException("subimage (x+width) is outside raster");
            }
            if (subImage.y < 0 || subImage.y >= height) {
                throw new ImageReadException("subimage y is outside raster");
            }
            if (subImage.y + subImage.height > height) {
                throw new ImageReadException("subimage (y+height) is outside raster");
            }
            if (subImage.x == 0 && subImage.y == 0 && subImage.width == width && subImage.height == height) {
                subImage = null;
            }
        }
        int samplesPerPixel = 1;
        TiffField samplesPerPixelField = directory.findField(TiffTagConstants.TIFF_TAG_SAMPLES_PER_PIXEL);
        if (samplesPerPixelField != null) {
            samplesPerPixel = samplesPerPixelField.getIntValue();
        }
        int[] bitsPerSample = new int[]{1};
        int bitsPerPixel = samplesPerPixel;
        TiffField bitsPerSampleField = directory.findField(TiffTagConstants.TIFF_TAG_BITS_PER_SAMPLE);
        if (bitsPerSampleField != null) {
            bitsPerSample = bitsPerSampleField.getIntArrayValue();
            bitsPerPixel = bitsPerSampleField.getIntValueOrArraySum();
        }
        int predictor = -1;
        TiffField predictorField = directory.findField(TiffTagConstants.TIFF_TAG_PREDICTOR);
        if (null != predictorField) {
            predictor = predictorField.getIntValueOrArraySum();
        }
        if (samplesPerPixel != bitsPerSample.length) {
            throw new ImageReadException("Tiff: samplesPerPixel (" + samplesPerPixel + ")!=fBitsPerSample.length (" + bitsPerSample.length + ")");
        }
        Object test = params == null ? null : params.get("CUSTOM_PHOTOMETRIC_INTERPRETER");
        PhotometricInterpreter photometricInterpreter = test instanceof PhotometricInterpreter ? (PhotometricInterpreter)test : this.getPhotometricInterpreter(directory, photometricInterpretation, bitsPerPixel, bitsPerSample, predictor, samplesPerPixel, width, height);
        TiffImageData imageData = directory.getTiffImageData();
        ImageDataReader dataReader = imageData.getDataReader(directory, photometricInterpreter, bitsPerPixel, bitsPerSample, predictor, samplesPerPixel, width, height, compression, byteOrder);
        BufferedImage result = null;
        if (subImage != null) {
            result = dataReader.readImageData(subImage);
        } else {
            boolean hasAlpha = false;
            ImageBuilder imageBuilder = new ImageBuilder(width, height, false);
            dataReader.readImageData(imageBuilder);
            result = imageBuilder.getBufferedImage();
        }
        return result;
    }

    private PhotometricInterpreter getPhotometricInterpreter(TiffDirectory directory, int photometricInterpretation, int bitsPerPixel, int[] bitsPerSample, int predictor, int samplesPerPixel, int width, int height) throws ImageReadException {
        switch (photometricInterpretation) {
            case 0: 
            case 1: {
                boolean invert = photometricInterpretation == 0;
                return new PhotometricInterpreterBiLevel(samplesPerPixel, bitsPerSample, predictor, width, height, invert);
            }
            case 3: {
                int[] colorMap = directory.findField(TiffTagConstants.TIFF_TAG_COLOR_MAP, true).getIntArrayValue();
                int expectedColormapSize = 3 * (1 << bitsPerPixel);
                if (colorMap.length != expectedColormapSize) {
                    throw new ImageReadException("Tiff: fColorMap.length (" + colorMap.length + ")!=expectedColormapSize (" + expectedColormapSize + ")");
                }
                return new PhotometricInterpreterPalette(samplesPerPixel, bitsPerSample, predictor, width, height, colorMap);
            }
            case 2: {
                return new PhotometricInterpreterRgb(samplesPerPixel, bitsPerSample, predictor, width, height);
            }
            case 5: {
                return new PhotometricInterpreterCmyk(samplesPerPixel, bitsPerSample, predictor, width, height);
            }
            case 6: {
                return new PhotometricInterpreterYCbCr(samplesPerPixel, bitsPerSample, predictor, width, height);
            }
            case 8: {
                return new PhotometricInterpreterCieLab(samplesPerPixel, bitsPerSample, predictor, width, height);
            }
            case 32844: 
            case 32845: {
                return new PhotometricInterpreterLogLuv(samplesPerPixel, bitsPerSample, predictor, width, height);
            }
        }
        throw new ImageReadException("TIFF: Unknown fPhotometricInterpretation: " + photometricInterpretation);
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        new TiffImageWriterLossy().writeImage(src, os, params);
    }

    TiffRasterData getFloatingPointRasterData(TiffDirectory directory, ByteOrder byteOrder, Map<String, Object> params) throws ImageReadException, IOException {
        List<TiffField> entries = directory.entries;
        if (entries == null) {
            throw new ImageReadException("TIFF missing entries");
        }
        short[] sSampleFmt = directory.getFieldValue(TiffTagConstants.TIFF_TAG_SAMPLE_FORMAT, true);
        if (sSampleFmt[0] != 3) {
            throw new ImageReadException("TIFF does not provide floating-point data");
        }
        int samplesPerPixel = 1;
        TiffField samplesPerPixelField = directory.findField(TiffTagConstants.TIFF_TAG_SAMPLES_PER_PIXEL);
        if (samplesPerPixelField != null) {
            samplesPerPixel = samplesPerPixelField.getIntValue();
        }
        if (samplesPerPixel != 1) {
            throw new ImageReadException("TIFF floating-point data uses unsupported samples per pixel: " + samplesPerPixel);
        }
        int[] bitsPerSample = new int[]{1};
        int bitsPerPixel = samplesPerPixel;
        TiffField bitsPerSampleField = directory.findField(TiffTagConstants.TIFF_TAG_BITS_PER_SAMPLE);
        if (bitsPerSampleField != null) {
            bitsPerSample = bitsPerSampleField.getIntArrayValue();
            bitsPerPixel = bitsPerSampleField.getIntValueOrArraySum();
        }
        if (bitsPerPixel != 32 && bitsPerPixel != 64) {
            throw new ImageReadException("TIFF floating-point data uses unsupported bits-per-pixel: " + bitsPerPixel);
        }
        short compressionFieldValue = directory.findField(TiffTagConstants.TIFF_TAG_COMPRESSION) != null ? directory.getFieldValue(TiffTagConstants.TIFF_TAG_COMPRESSION) : (short)1;
        int compression = 0xFFFF & compressionFieldValue;
        int width = directory.getSingleFieldValue(TiffTagConstants.TIFF_TAG_IMAGE_WIDTH);
        int height = directory.getSingleFieldValue(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH);
        Rectangle subImage = this.checkForSubImage(params);
        if (subImage != null) {
            if (subImage.width <= 0) {
                throw new ImageReadException("negative or zero subimage width");
            }
            if (subImage.height <= 0) {
                throw new ImageReadException("negative or zero subimage height");
            }
            if (subImage.x < 0 || subImage.x >= width) {
                throw new ImageReadException("subimage x is outside raster");
            }
            if (subImage.x + subImage.width > width) {
                throw new ImageReadException("subimage (x+width) is outside raster");
            }
            if (subImage.y < 0 || subImage.y >= height) {
                throw new ImageReadException("subimage y is outside raster");
            }
            if (subImage.y + subImage.height > height) {
                throw new ImageReadException("subimage (y+height) is outside raster");
            }
            if (subImage.x == 0 && subImage.y == 0 && subImage.width == width && subImage.height == height) {
                subImage = null;
            }
        }
        int predictor = -1;
        TiffField predictorField = directory.findField(TiffTagConstants.TIFF_TAG_PREDICTOR);
        if (null != predictorField) {
            predictor = predictorField.getIntValueOrArraySum();
        }
        if (predictor == 2) {
            throw new ImageReadException("TIFF floating-point data uses unsupported horizontal-differencing predictor");
        }
        PhotometricInterpreterBiLevel photometricInterpreter = new PhotometricInterpreterBiLevel(samplesPerPixel, bitsPerSample, predictor, width, height, false);
        TiffImageData imageData = directory.getTiffImageData();
        ImageDataReader dataReader = imageData.getDataReader(directory, photometricInterpreter, bitsPerPixel, bitsPerSample, predictor, samplesPerPixel, width, height, compression, byteOrder);
        return dataReader.readRasterData(subImage);
    }
}

