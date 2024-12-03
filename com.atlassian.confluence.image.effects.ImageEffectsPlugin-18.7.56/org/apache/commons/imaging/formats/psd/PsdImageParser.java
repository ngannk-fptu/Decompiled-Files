/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.XmpEmbeddable;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.psd.ImageResourceBlock;
import org.apache.commons.imaging.formats.psd.ImageResourceType;
import org.apache.commons.imaging.formats.psd.PsdHeaderInfo;
import org.apache.commons.imaging.formats.psd.PsdImageContents;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParser;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParserBitmap;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParserCmyk;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParserGrayscale;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParserIndexed;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParserLab;
import org.apache.commons.imaging.formats.psd.dataparsers.DataParserRgb;
import org.apache.commons.imaging.formats.psd.datareaders.CompressedDataReader;
import org.apache.commons.imaging.formats.psd.datareaders.DataReader;
import org.apache.commons.imaging.formats.psd.datareaders.UncompressedDataReader;

public class PsdImageParser
extends ImageParser
implements XmpEmbeddable {
    private static final String DEFAULT_EXTENSION = ".psd";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".psd"};
    private static final int PSD_SECTION_HEADER = 0;
    private static final int PSD_SECTION_COLOR_MODE = 1;
    private static final int PSD_SECTION_IMAGE_RESOURCES = 2;
    private static final int PSD_SECTION_LAYER_AND_MASK_DATA = 3;
    private static final int PSD_SECTION_IMAGE_DATA = 4;
    private static final int PSD_HEADER_LENGTH = 26;
    private static final int COLOR_MODE_INDEXED = 2;
    public static final int IMAGE_RESOURCE_ID_ICC_PROFILE = 1039;
    public static final int IMAGE_RESOURCE_ID_XMP = 1060;
    public static final String BLOCK_NAME_XMP = "XMP";

    public PsdImageParser() {
        super.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    @Override
    public String getName() {
        return "PSD-Custom";
    }

    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    protected String[] getAcceptedExtensions() {
        return (String[])ACCEPTED_EXTENSIONS.clone();
    }

    @Override
    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{ImageFormats.PSD};
    }

    private PsdHeaderInfo readHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            PsdHeaderInfo ret;
            PsdHeaderInfo psdHeaderInfo = ret = this.readHeader(is);
            return psdHeaderInfo;
        }
    }

    private PsdHeaderInfo readHeader(InputStream is) throws ImageReadException, IOException {
        BinaryFunctions.readAndVerifyBytes(is, new byte[]{56, 66, 80, 83}, "Not a Valid PSD File");
        int version = BinaryFunctions.read2Bytes("Version", is, "Not a Valid PSD File", this.getByteOrder());
        byte[] reserved = BinaryFunctions.readBytes("Reserved", is, 6, "Not a Valid PSD File");
        int channels = BinaryFunctions.read2Bytes("Channels", is, "Not a Valid PSD File", this.getByteOrder());
        int rows = BinaryFunctions.read4Bytes("Rows", is, "Not a Valid PSD File", this.getByteOrder());
        int columns = BinaryFunctions.read4Bytes("Columns", is, "Not a Valid PSD File", this.getByteOrder());
        int depth = BinaryFunctions.read2Bytes("Depth", is, "Not a Valid PSD File", this.getByteOrder());
        int mode = BinaryFunctions.read2Bytes("Mode", is, "Not a Valid PSD File", this.getByteOrder());
        return new PsdHeaderInfo(version, reserved, channels, rows, columns, depth, mode);
    }

    private PsdImageContents readImageContents(InputStream is) throws ImageReadException, IOException {
        PsdHeaderInfo header = this.readHeader(is);
        int ColorModeDataLength = BinaryFunctions.read4Bytes("ColorModeDataLength", is, "Not a Valid PSD File", this.getByteOrder());
        BinaryFunctions.skipBytes(is, ColorModeDataLength);
        int ImageResourcesLength = BinaryFunctions.read4Bytes("ImageResourcesLength", is, "Not a Valid PSD File", this.getByteOrder());
        BinaryFunctions.skipBytes(is, ImageResourcesLength);
        int LayerAndMaskDataLength = BinaryFunctions.read4Bytes("LayerAndMaskDataLength", is, "Not a Valid PSD File", this.getByteOrder());
        BinaryFunctions.skipBytes(is, LayerAndMaskDataLength);
        int Compression = BinaryFunctions.read2Bytes("Compression", is, "Not a Valid PSD File", this.getByteOrder());
        return new PsdImageContents(header, ColorModeDataLength, ImageResourcesLength, LayerAndMaskDataLength, Compression);
    }

    private List<ImageResourceBlock> readImageResourceBlocks(byte[] bytes, int[] imageResourceIDs, int maxBlocksToRead) throws ImageReadException, IOException {
        return this.readImageResourceBlocks(new ByteArrayInputStream(bytes), imageResourceIDs, maxBlocksToRead, bytes.length);
    }

    private boolean keepImageResourceBlock(int ID, int[] imageResourceIDs) {
        if (imageResourceIDs == null) {
            return true;
        }
        for (int imageResourceID : imageResourceIDs) {
            if (ID != imageResourceID) continue;
            return true;
        }
        return false;
    }

    private List<ImageResourceBlock> readImageResourceBlocks(InputStream is, int[] imageResourceIDs, int maxBlocksToRead, int available) throws ImageReadException, IOException {
        ArrayList<ImageResourceBlock> result = new ArrayList<ImageResourceBlock>();
        while (available > 0) {
            BinaryFunctions.readAndVerifyBytes(is, new byte[]{56, 66, 73, 77}, "Not a Valid PSD File");
            available -= 4;
            int id = BinaryFunctions.read2Bytes("ID", is, "Not a Valid PSD File", this.getByteOrder());
            available -= 2;
            byte nameLength = BinaryFunctions.readByte("NameLength", is, "Not a Valid PSD File");
            --available;
            byte[] nameBytes = BinaryFunctions.readBytes("NameData", is, nameLength, "Not a Valid PSD File");
            available -= nameLength;
            if ((nameLength + 1) % 2 != 0) {
                BinaryFunctions.readByte("NameDiscard", is, "Not a Valid PSD File");
                --available;
            }
            int dataSize = BinaryFunctions.read4Bytes("Size", is, "Not a Valid PSD File", this.getByteOrder());
            available -= 4;
            byte[] data = BinaryFunctions.readBytes("Data", is, dataSize, "Not a Valid PSD File");
            available -= dataSize;
            if (dataSize % 2 != 0) {
                BinaryFunctions.readByte("DataDiscard", is, "Not a Valid PSD File");
                --available;
            }
            if (!this.keepImageResourceBlock(id, imageResourceIDs)) continue;
            result.add(new ImageResourceBlock(id, nameBytes, data));
            if (maxBlocksToRead < 0 || result.size() < maxBlocksToRead) continue;
            return result;
        }
        return result;
    }

    /*
     * Exception decompiling
     */
    private List<ImageResourceBlock> readImageResourceBlocks(ByteSource byteSource, int[] imageResourceIDs, int maxBlocksToRead) throws ImageReadException, IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private InputStream getInputStream(ByteSource byteSource, int section) throws ImageReadException, IOException {
        InputStream is = null;
        boolean notFound = false;
        try {
            is = byteSource.getInputStream();
            if (section == 0) {
                InputStream inputStream = is;
                return inputStream;
            }
            BinaryFunctions.skipBytes(is, 26L);
            int colorModeDataLength = BinaryFunctions.read4Bytes("ColorModeDataLength", is, "Not a Valid PSD File", this.getByteOrder());
            if (section == 1) {
                InputStream inputStream = is;
                return inputStream;
            }
            BinaryFunctions.skipBytes(is, colorModeDataLength);
            int imageResourcesLength = BinaryFunctions.read4Bytes("ImageResourcesLength", is, "Not a Valid PSD File", this.getByteOrder());
            if (section == 2) {
                InputStream inputStream = is;
                return inputStream;
            }
            BinaryFunctions.skipBytes(is, imageResourcesLength);
            int layerAndMaskDataLength = BinaryFunctions.read4Bytes("LayerAndMaskDataLength", is, "Not a Valid PSD File", this.getByteOrder());
            if (section == 3) {
                InputStream inputStream = is;
                return inputStream;
            }
            BinaryFunctions.skipBytes(is, layerAndMaskDataLength);
            BinaryFunctions.read2Bytes("Compression", is, "Not a Valid PSD File", this.getByteOrder());
            if (section == 4) {
                InputStream inputStream = is;
                return inputStream;
            }
            notFound = true;
        }
        finally {
            if (notFound && is != null) {
                is.close();
            }
        }
        throw new ImageReadException("getInputStream: Unknown Section: " + section);
    }

    private byte[] getData(ByteSource byteSource, int section) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            if (section == 0) {
                byte[] byArray = BinaryFunctions.readBytes("Header", is, 26, "Not a Valid PSD File");
                return byArray;
            }
            BinaryFunctions.skipBytes(is, 26L);
            int ColorModeDataLength = BinaryFunctions.read4Bytes("ColorModeDataLength", is, "Not a Valid PSD File", this.getByteOrder());
            if (section == 1) {
                byte[] byArray = BinaryFunctions.readBytes("ColorModeData", is, ColorModeDataLength, "Not a Valid PSD File");
                return byArray;
            }
            BinaryFunctions.skipBytes(is, ColorModeDataLength);
            int ImageResourcesLength = BinaryFunctions.read4Bytes("ImageResourcesLength", is, "Not a Valid PSD File", this.getByteOrder());
            if (section == 2) {
                byte[] byArray = BinaryFunctions.readBytes("ImageResources", is, ImageResourcesLength, "Not a Valid PSD File");
                return byArray;
            }
            BinaryFunctions.skipBytes(is, ImageResourcesLength);
            int LayerAndMaskDataLength = BinaryFunctions.read4Bytes("LayerAndMaskDataLength", is, "Not a Valid PSD File", this.getByteOrder());
            if (section == 3) {
                byte[] byArray = BinaryFunctions.readBytes("LayerAndMaskData", is, LayerAndMaskDataLength, "Not a Valid PSD File");
                return byArray;
            }
            BinaryFunctions.skipBytes(is, LayerAndMaskDataLength);
            BinaryFunctions.read2Bytes("Compression", is, "Not a Valid PSD File", this.getByteOrder());
        }
        throw new ImageReadException("getInputStream: Unknown Section: " + section);
    }

    private PsdImageContents readImageContents(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            PsdImageContents psdImageContents = this.readImageContents(is);
            return psdImageContents;
        }
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<ImageResourceBlock> blocks = this.readImageResourceBlocks(byteSource, new int[]{1039}, 1);
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }
        ImageResourceBlock irb = blocks.get(0);
        byte[] bytes = irb.data;
        if (bytes == null || bytes.length < 1) {
            return null;
        }
        return (byte[])bytes.clone();
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        PsdHeaderInfo bhi = this.readHeader(byteSource);
        if (bhi == null) {
            throw new ImageReadException("PSD: couldn't read header");
        }
        return new Dimension(bhi.columns, bhi.rows);
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private int getChannelsPerMode(int mode) {
        switch (mode) {
            case 0: {
                return 1;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return -1;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 4;
            }
            case 7: {
                return -1;
            }
            case 8: {
                return -1;
            }
            case 9: {
                return 4;
            }
        }
        return -1;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageInfo.CompressionAlgorithm compressionAlgorithm;
        PsdImageContents imageContents = this.readImageContents(byteSource);
        if (imageContents == null) {
            throw new ImageReadException("PSD: Couldn't read blocks");
        }
        PsdHeaderInfo header = imageContents.header;
        if (header == null) {
            throw new ImageReadException("PSD: Couldn't read Header");
        }
        int width = header.columns;
        int height = header.rows;
        ArrayList<String> comments = new ArrayList<String>();
        int BitsPerPixel = header.depth * this.getChannelsPerMode(header.mode);
        if (BitsPerPixel < 0) {
            BitsPerPixel = 0;
        }
        ImageFormats format = ImageFormats.PSD;
        String formatName = "Photoshop";
        String mimeType = "image/x-photoshop";
        int numberOfImages = -1;
        boolean progressive = false;
        int physicalWidthDpi = 72;
        float physicalWidthInch = (float)((double)width / 72.0);
        int physicalHeightDpi = 72;
        float physicalHeightInch = (float)((double)height / 72.0);
        String formatDetails = "Psd";
        boolean transparent = false;
        boolean usesPalette = header.mode == 2;
        ImageInfo.ColorType colorType = ImageInfo.ColorType.UNKNOWN;
        switch (imageContents.Compression) {
            case 0: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.NONE;
                break;
            }
            case 1: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.PSD;
                break;
            }
            default: {
                compressionAlgorithm = ImageInfo.CompressionAlgorithm.UNKNOWN;
            }
        }
        return new ImageInfo("Psd", BitsPerPixel, comments, format, "Photoshop", height, "image/x-photoshop", -1, 72, physicalHeightInch, 72, physicalWidthInch, width, false, false, usesPalette, colorType, compressionAlgorithm);
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        pw.println("gif.dumpImageFile");
        ImageInfo fImageData = this.getImageInfo(byteSource);
        if (fImageData == null) {
            return false;
        }
        fImageData.toString(pw, "");
        PsdImageContents imageContents = this.readImageContents(byteSource);
        imageContents.dump(pw);
        imageContents.header.dump(pw);
        List<ImageResourceBlock> blocks = this.readImageResourceBlocks(byteSource, null, -1);
        pw.println("blocks.size(): " + blocks.size());
        for (int i = 0; i < blocks.size(); ++i) {
            ImageResourceBlock block = blocks.get(i);
            pw.println("\t" + i + " (" + Integer.toHexString(block.id) + ", '" + new String(block.nameData, StandardCharsets.ISO_8859_1) + "' (" + block.nameData.length + "),  data: " + block.data.length + " type: '" + ImageResourceType.getDescription(block.id) + "' )");
        }
        pw.println("");
        return true;
    }

    @Override
    public BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        DataReader fDataReader;
        DataParser dataParser;
        PsdImageContents imageContents = this.readImageContents(byteSource);
        if (imageContents == null) {
            throw new ImageReadException("PSD: Couldn't read blocks");
        }
        PsdHeaderInfo header = imageContents.header;
        if (header == null) {
            throw new ImageReadException("PSD: Couldn't read Header");
        }
        this.readImageResourceBlocks(byteSource, null, -1);
        int width = header.columns;
        int height = header.rows;
        boolean hasAlpha = false;
        BufferedImage result = this.getBufferedImageFactory(params).getColorBufferedImage(width, height, false);
        switch (imageContents.header.mode) {
            case 0: {
                dataParser = new DataParserBitmap();
                break;
            }
            case 1: 
            case 8: {
                dataParser = new DataParserGrayscale();
                break;
            }
            case 3: {
                dataParser = new DataParserRgb();
                break;
            }
            case 4: {
                dataParser = new DataParserCmyk();
                break;
            }
            case 9: {
                dataParser = new DataParserLab();
                break;
            }
            case 2: {
                byte[] ColorModeData = this.getData(byteSource, 1);
                dataParser = new DataParserIndexed(ColorModeData);
                break;
            }
            default: {
                throw new ImageReadException("Unknown Mode: " + imageContents.header.mode);
            }
        }
        switch (imageContents.Compression) {
            case 0: {
                fDataReader = new UncompressedDataReader(dataParser);
                break;
            }
            case 1: {
                fDataReader = new CompressedDataReader(dataParser);
                break;
            }
            default: {
                throw new ImageReadException("Unknown Compression: " + imageContents.Compression);
            }
        }
        try (InputStream is = this.getInputStream(byteSource, 4);){
            fDataReader.readData(is, result, imageContents, this);
        }
        return result;
    }

    @Override
    public String getXmpXml(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        PsdImageContents imageContents = this.readImageContents(byteSource);
        if (imageContents == null) {
            throw new ImageReadException("PSD: Couldn't read blocks");
        }
        PsdHeaderInfo header = imageContents.header;
        if (header == null) {
            throw new ImageReadException("PSD: Couldn't read Header");
        }
        List<ImageResourceBlock> blocks = this.readImageResourceBlocks(byteSource, new int[]{1060}, -1);
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }
        ArrayList<ImageResourceBlock> xmpBlocks = new ArrayList<ImageResourceBlock>();
        xmpBlocks.addAll(blocks);
        if (xmpBlocks.isEmpty()) {
            return null;
        }
        if (xmpBlocks.size() > 1) {
            throw new ImageReadException("PSD contains more than one XMP block.");
        }
        ImageResourceBlock block = (ImageResourceBlock)xmpBlocks.get(0);
        return new String(block.data, 0, block.data.length, StandardCharsets.UTF_8);
    }
}

