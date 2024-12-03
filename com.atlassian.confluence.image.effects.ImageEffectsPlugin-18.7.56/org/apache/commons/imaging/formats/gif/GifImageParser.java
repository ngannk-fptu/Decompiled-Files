/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.FormatCompliance;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.XmpEmbeddable;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.mylzw.MyLzwCompressor;
import org.apache.commons.imaging.common.mylzw.MyLzwDecompressor;
import org.apache.commons.imaging.formats.gif.DisposalMethod;
import org.apache.commons.imaging.formats.gif.GenericGifBlock;
import org.apache.commons.imaging.formats.gif.GifBlock;
import org.apache.commons.imaging.formats.gif.GifHeaderInfo;
import org.apache.commons.imaging.formats.gif.GifImageContents;
import org.apache.commons.imaging.formats.gif.GifImageData;
import org.apache.commons.imaging.formats.gif.GifImageMetadata;
import org.apache.commons.imaging.formats.gif.GifImageMetadataItem;
import org.apache.commons.imaging.formats.gif.GraphicControlExtension;
import org.apache.commons.imaging.formats.gif.ImageDescriptor;
import org.apache.commons.imaging.palette.Palette;
import org.apache.commons.imaging.palette.PaletteFactory;

public class GifImageParser
extends ImageParser
implements XmpEmbeddable {
    private static final Logger LOGGER = Logger.getLogger(GifImageParser.class.getName());
    private static final String DEFAULT_EXTENSION = ".gif";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".gif"};
    private static final byte[] GIF_HEADER_SIGNATURE = new byte[]{71, 73, 70};
    private static final int EXTENSION_CODE = 33;
    private static final int IMAGE_SEPARATOR = 44;
    private static final int GRAPHIC_CONTROL_EXTENSION = 8697;
    private static final int COMMENT_EXTENSION = 254;
    private static final int PLAIN_TEXT_EXTENSION = 1;
    private static final int XMP_EXTENSION = 255;
    private static final int TERMINATOR_BYTE = 59;
    private static final int APPLICATION_EXTENSION_LABEL = 255;
    private static final int XMP_COMPLETE_CODE = 8703;
    private static final int LOCAL_COLOR_TABLE_FLAG_MASK = 128;
    private static final int INTERLACE_FLAG_MASK = 64;
    private static final int SORT_FLAG_MASK = 32;
    private static final byte[] XMP_APPLICATION_ID_AND_AUTH_CODE = new byte[]{88, 77, 80, 32, 68, 97, 116, 97, 88, 77, 80};

    public GifImageParser() {
        super.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public String getName() {
        return "Graphics Interchange Format";
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
        return new ImageFormat[]{ImageFormats.GIF};
    }

    private GifHeaderInfo readHeader(InputStream is, FormatCompliance formatCompliance) throws ImageReadException, IOException {
        boolean sortFlag;
        boolean globalColorTableFlag;
        byte identifier1 = BinaryFunctions.readByte("identifier1", is, "Not a Valid GIF File");
        byte identifier2 = BinaryFunctions.readByte("identifier2", is, "Not a Valid GIF File");
        byte identifier3 = BinaryFunctions.readByte("identifier3", is, "Not a Valid GIF File");
        byte version1 = BinaryFunctions.readByte("version1", is, "Not a Valid GIF File");
        byte version2 = BinaryFunctions.readByte("version2", is, "Not a Valid GIF File");
        byte version3 = BinaryFunctions.readByte("version3", is, "Not a Valid GIF File");
        if (formatCompliance != null) {
            formatCompliance.compareBytes("Signature", GIF_HEADER_SIGNATURE, new byte[]{identifier1, identifier2, identifier3});
            formatCompliance.compare("version", 56, (int)version1);
            formatCompliance.compare("version", new int[]{55, 57}, (int)version2);
            formatCompliance.compare("version", 97, (int)version3);
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            BinaryFunctions.printCharQuad("identifier: ", identifier1 << 16 | identifier2 << 8 | identifier3 << 0);
            BinaryFunctions.printCharQuad("version: ", version1 << 16 | version2 << 8 | version3 << 0);
        }
        int logicalScreenWidth = BinaryFunctions.read2Bytes("Logical Screen Width", is, "Not a Valid GIF File", this.getByteOrder());
        int logicalScreenHeight = BinaryFunctions.read2Bytes("Logical Screen Height", is, "Not a Valid GIF File", this.getByteOrder());
        if (formatCompliance != null) {
            formatCompliance.checkBounds("Width", 1, Integer.MAX_VALUE, logicalScreenWidth);
            formatCompliance.checkBounds("Height", 1, Integer.MAX_VALUE, logicalScreenHeight);
        }
        byte packedFields = BinaryFunctions.readByte("Packed Fields", is, "Not a Valid GIF File");
        byte backgroundColorIndex = BinaryFunctions.readByte("Background Color Index", is, "Not a Valid GIF File");
        byte pixelAspectRatio = BinaryFunctions.readByte("Pixel Aspect Ratio", is, "Not a Valid GIF File");
        if (LOGGER.isLoggable(Level.FINEST)) {
            BinaryFunctions.printByteBits("PackedFields bits", packedFields);
        }
        boolean bl = globalColorTableFlag = (packedFields & 0x80) > 0;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("GlobalColorTableFlag: " + globalColorTableFlag);
        }
        byte colorResolution = (byte)(packedFields >> 4 & 7);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("ColorResolution: " + colorResolution);
        }
        boolean bl2 = sortFlag = (packedFields & 8) > 0;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("SortFlag: " + sortFlag);
        }
        byte sizeofGlobalColorTable = (byte)(packedFields & 7);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("SizeofGlobalColorTable: " + sizeofGlobalColorTable);
        }
        if (formatCompliance != null && globalColorTableFlag && backgroundColorIndex != -1) {
            formatCompliance.checkBounds("Background Color Index", 0, this.convertColorTableSize(sizeofGlobalColorTable), backgroundColorIndex);
        }
        return new GifHeaderInfo(identifier1, identifier2, identifier3, version1, version2, version3, logicalScreenWidth, logicalScreenHeight, packedFields, backgroundColorIndex, pixelAspectRatio, globalColorTableFlag, colorResolution, sortFlag, sizeofGlobalColorTable);
    }

    private GraphicControlExtension readGraphicControlExtension(int code, InputStream is) throws IOException {
        BinaryFunctions.readByte("block_size", is, "GIF: corrupt GraphicControlExt");
        byte packed = BinaryFunctions.readByte("packed fields", is, "GIF: corrupt GraphicControlExt");
        int dispose = (packed & 0x1C) >> 2;
        boolean transparency = (packed & 1) != 0;
        int delay = BinaryFunctions.read2Bytes("delay in milliseconds", is, "GIF: corrupt GraphicControlExt", this.getByteOrder());
        int transparentColorIndex = 0xFF & BinaryFunctions.readByte("transparent color index", is, "GIF: corrupt GraphicControlExt");
        BinaryFunctions.readByte("block terminator", is, "GIF: corrupt GraphicControlExt");
        return new GraphicControlExtension(code, packed, dispose, transparency, delay, transparentColorIndex);
    }

    private byte[] readSubBlock(InputStream is) throws IOException {
        int blockSize = 0xFF & BinaryFunctions.readByte("block_size", is, "GIF: corrupt block");
        return BinaryFunctions.readBytes("block", is, blockSize, "GIF: corrupt block");
    }

    private GenericGifBlock readGenericGIFBlock(InputStream is, int code) throws IOException {
        return this.readGenericGIFBlock(is, code, null);
    }

    private GenericGifBlock readGenericGIFBlock(InputStream is, int code, byte[] first) throws IOException {
        byte[] bytes;
        ArrayList<byte[]> subblocks = new ArrayList<byte[]>();
        if (first != null) {
            subblocks.add(first);
        }
        while ((bytes = this.readSubBlock(is)).length >= 1) {
            subblocks.add(bytes);
        }
        return new GenericGifBlock(code, subblocks);
    }

    private List<GifBlock> readBlocks(GifHeaderInfo ghi, InputStream is, boolean stopBeforeImageData, FormatCompliance formatCompliance) throws ImageReadException, IOException {
        ArrayList<GifBlock> result = new ArrayList<GifBlock>();
        block12: while (true) {
            int code = is.read();
            switch (code) {
                case -1: {
                    throw new ImageReadException("GIF: unexpected end of data");
                }
                case 44: {
                    ImageDescriptor id = this.readImageDescriptor(ghi, code, is, stopBeforeImageData, formatCompliance);
                    result.add(id);
                    break;
                }
                case 33: {
                    int extensionCode = is.read();
                    int completeCode = (0xFF & code) << 8 | 0xFF & extensionCode;
                    switch (extensionCode) {
                        case 249: {
                            GraphicControlExtension gce = this.readGraphicControlExtension(completeCode, is);
                            result.add(gce);
                            break;
                        }
                        case 1: 
                        case 254: {
                            GenericGifBlock block = this.readGenericGIFBlock(is, completeCode);
                            result.add(block);
                            break;
                        }
                        case 255: {
                            byte[] label = this.readSubBlock(is);
                            if (formatCompliance != null) {
                                formatCompliance.addComment("Unknown Application Extension (" + new String(label, StandardCharsets.US_ASCII) + ")", completeCode);
                            }
                            if (label == null || label.length <= 0) continue block12;
                            GenericGifBlock block = this.readGenericGIFBlock(is, completeCode, label);
                            result.add(block);
                            break;
                        }
                        default: {
                            if (formatCompliance != null) {
                                formatCompliance.addComment("Unknown block", completeCode);
                            }
                            GenericGifBlock block = this.readGenericGIFBlock(is, completeCode);
                            result.add(block);
                            break;
                        }
                    }
                    break;
                }
                case 59: {
                    return result;
                }
                case 0: {
                    break;
                }
                default: {
                    throw new ImageReadException("GIF: unknown code: " + code);
                }
            }
        }
    }

    private ImageDescriptor readImageDescriptor(GifHeaderInfo ghi, int blockCode, InputStream is, boolean stopBeforeImageData, FormatCompliance formatCompliance) throws ImageReadException, IOException {
        boolean sortFlag;
        boolean interlaceFlag;
        boolean localColorTableFlag;
        int imageLeftPosition = BinaryFunctions.read2Bytes("Image Left Position", is, "Not a Valid GIF File", this.getByteOrder());
        int imageTopPosition = BinaryFunctions.read2Bytes("Image Top Position", is, "Not a Valid GIF File", this.getByteOrder());
        int imageWidth = BinaryFunctions.read2Bytes("Image Width", is, "Not a Valid GIF File", this.getByteOrder());
        int imageHeight = BinaryFunctions.read2Bytes("Image Height", is, "Not a Valid GIF File", this.getByteOrder());
        byte packedFields = BinaryFunctions.readByte("Packed Fields", is, "Not a Valid GIF File");
        if (formatCompliance != null) {
            formatCompliance.checkBounds("Width", 1, ghi.logicalScreenWidth, imageWidth);
            formatCompliance.checkBounds("Height", 1, ghi.logicalScreenHeight, imageHeight);
            formatCompliance.checkBounds("Left Position", 0, ghi.logicalScreenWidth - imageWidth, imageLeftPosition);
            formatCompliance.checkBounds("Top Position", 0, ghi.logicalScreenHeight - imageHeight, imageTopPosition);
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            BinaryFunctions.printByteBits("PackedFields bits", packedFields);
        }
        boolean bl = localColorTableFlag = (packedFields >> 7 & 1) > 0;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("LocalColorTableFlag: " + localColorTableFlag);
        }
        boolean bl2 = interlaceFlag = (packedFields >> 6 & 1) > 0;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Interlace Flag: " + interlaceFlag);
        }
        boolean bl3 = sortFlag = (packedFields >> 5 & 1) > 0;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Sort Flag: " + sortFlag);
        }
        byte sizeOfLocalColorTable = (byte)(packedFields & 7);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("SizeofLocalColorTable: " + sizeOfLocalColorTable);
        }
        byte[] localColorTable = null;
        if (localColorTableFlag) {
            localColorTable = this.readColorTable(is, sizeOfLocalColorTable);
        }
        byte[] imageData = null;
        if (!stopBeforeImageData) {
            int lzwMinimumCodeSize = is.read();
            GenericGifBlock block = this.readGenericGIFBlock(is, -1);
            byte[] bytes = block.appendSubBlocks();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            int size = imageWidth * imageHeight;
            MyLzwDecompressor myLzwDecompressor = new MyLzwDecompressor(lzwMinimumCodeSize, ByteOrder.LITTLE_ENDIAN);
            imageData = myLzwDecompressor.decompress(bais, size);
        } else {
            int LZWMinimumCodeSize = is.read();
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("LZWMinimumCodeSize: " + LZWMinimumCodeSize);
            }
            this.readGenericGIFBlock(is, -1);
        }
        return new ImageDescriptor(blockCode, imageLeftPosition, imageTopPosition, imageWidth, imageHeight, packedFields, localColorTableFlag, interlaceFlag, sortFlag, sizeOfLocalColorTable, localColorTable, imageData);
    }

    private int simplePow(int base, int power) {
        int result = 1;
        for (int i = 0; i < power; ++i) {
            result *= base;
        }
        return result;
    }

    private int convertColorTableSize(int tableSize) {
        return 3 * this.simplePow(2, tableSize + 1);
    }

    private byte[] readColorTable(InputStream is, int tableSize) throws IOException {
        int actualSize = this.convertColorTableSize(tableSize);
        return BinaryFunctions.readBytes("block", is, actualSize, "GIF: corrupt Color Table");
    }

    private GifBlock findBlock(List<GifBlock> blocks, int code) {
        for (GifBlock gifBlock : blocks) {
            if (gifBlock.blockCode != code) continue;
            return gifBlock;
        }
        return null;
    }

    private <T extends GifBlock> List<T> findAllBlocks(List<GifBlock> blocks, int code) {
        ArrayList<GifBlock> filteredBlocks = new ArrayList<GifBlock>();
        for (GifBlock gifBlock : blocks) {
            if (gifBlock.blockCode != code) continue;
            filteredBlocks.add(gifBlock);
        }
        return filteredBlocks;
    }

    private GifImageContents readFile(ByteSource byteSource, boolean stopBeforeImageData) throws ImageReadException, IOException {
        return this.readFile(byteSource, stopBeforeImageData, FormatCompliance.getDefault());
    }

    private GifImageContents readFile(ByteSource byteSource, boolean stopBeforeImageData, FormatCompliance formatCompliance) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            GifImageContents result;
            GifHeaderInfo ghi = this.readHeader(is, formatCompliance);
            byte[] globalColorTable = null;
            if (ghi.globalColorTableFlag) {
                globalColorTable = this.readColorTable(is, ghi.sizeOfGlobalColorTable);
            }
            List<GifBlock> blocks = this.readBlocks(ghi, is, stopBeforeImageData, formatCompliance);
            GifImageContents gifImageContents = result = new GifImageContents(ghi, globalColorTable, blocks);
            return gifImageContents;
        }
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        GifImageContents blocks = this.readFile(byteSource, false);
        if (blocks == null) {
            throw new ImageReadException("GIF: Couldn't read blocks");
        }
        GifHeaderInfo bhi = blocks.gifHeaderInfo;
        if (bhi == null) {
            throw new ImageReadException("GIF: Couldn't read Header");
        }
        return new Dimension(bhi.logicalScreenWidth, bhi.logicalScreenHeight);
    }

    static DisposalMethod createDisposalMethodFromIntValue(int value) throws ImageReadException {
        switch (value) {
            case 0: {
                return DisposalMethod.UNSPECIFIED;
            }
            case 1: {
                return DisposalMethod.DO_NOT_DISPOSE;
            }
            case 2: {
                return DisposalMethod.RESTORE_TO_BACKGROUND;
            }
            case 3: {
                return DisposalMethod.RESTORE_TO_PREVIOUS;
            }
            case 4: {
                return DisposalMethod.TO_BE_DEFINED_1;
            }
            case 5: {
                return DisposalMethod.TO_BE_DEFINED_2;
            }
            case 6: {
                return DisposalMethod.TO_BE_DEFINED_3;
            }
            case 7: {
                return DisposalMethod.TO_BE_DEFINED_4;
            }
        }
        throw new ImageReadException("GIF: Invalid parsing of disposal method");
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        GifImageContents imageContents = this.readFile(byteSource, false);
        if (imageContents == null) {
            throw new ImageReadException("GIF: Couldn't read blocks");
        }
        GifHeaderInfo bhi = imageContents.gifHeaderInfo;
        if (bhi == null) {
            throw new ImageReadException("GIF: Couldn't read Header");
        }
        List<GifImageData> imageData = this.findAllImageData(imageContents);
        ArrayList<GifImageMetadataItem> metadataItems = new ArrayList<GifImageMetadataItem>(imageData.size());
        for (GifImageData id : imageData) {
            DisposalMethod disposalMethod = GifImageParser.createDisposalMethodFromIntValue(id.gce.dispose);
            metadataItems.add(new GifImageMetadataItem(id.gce.delay, id.descriptor.imageLeftPosition, id.descriptor.imageTopPosition, disposalMethod));
        }
        return new GifImageMetadata(bhi.logicalScreenWidth, bhi.logicalScreenHeight, metadataItems);
    }

    private List<String> getComments(List<GifBlock> blocks) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        int code = 8702;
        for (GifBlock block : blocks) {
            if (block.blockCode != 8702) continue;
            byte[] bytes = ((GenericGifBlock)block).appendSubBlocks();
            result.add(new String(bytes, StandardCharsets.US_ASCII));
        }
        return result;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        GifImageContents blocks = this.readFile(byteSource, false);
        if (blocks == null) {
            throw new ImageReadException("GIF: Couldn't read blocks");
        }
        GifHeaderInfo bhi = blocks.gifHeaderInfo;
        if (bhi == null) {
            throw new ImageReadException("GIF: Couldn't read Header");
        }
        ImageDescriptor id = (ImageDescriptor)this.findBlock(blocks.blocks, 44);
        if (id == null) {
            throw new ImageReadException("GIF: Couldn't read ImageDescriptor");
        }
        GraphicControlExtension gce = (GraphicControlExtension)this.findBlock(blocks.blocks, 8697);
        int height = bhi.logicalScreenHeight;
        int width = bhi.logicalScreenWidth;
        List<String> comments = this.getComments(blocks.blocks);
        int bitsPerPixel = bhi.colorResolution + 1;
        ImageFormats format = ImageFormats.GIF;
        String formatName = "GIF Graphics Interchange Format";
        String mimeType = "image/gif";
        int numberOfImages = this.findAllBlocks(blocks.blocks, 44).size();
        boolean progressive = id.interlaceFlag;
        int physicalWidthDpi = 72;
        float physicalWidthInch = (float)((double)width / 72.0);
        int physicalHeightDpi = 72;
        float physicalHeightInch = (float)((double)height / 72.0);
        String formatDetails = "Gif " + (char)blocks.gifHeaderInfo.version1 + (char)blocks.gifHeaderInfo.version2 + (char)blocks.gifHeaderInfo.version3;
        boolean transparent = false;
        if (gce != null && gce.transparency) {
            transparent = true;
        }
        boolean usesPalette = true;
        ImageInfo.ColorType colorType = ImageInfo.ColorType.RGB;
        ImageInfo.CompressionAlgorithm compressionAlgorithm = ImageInfo.CompressionAlgorithm.LZW;
        return new ImageInfo(formatDetails, bitsPerPixel, comments, format, "GIF Graphics Interchange Format", height, "image/gif", numberOfImages, 72, physicalHeightInch, 72, physicalWidthInch, width, progressive, transparent, true, colorType, compressionAlgorithm);
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        pw.println("gif.dumpImageFile");
        ImageInfo imageData = this.getImageInfo(byteSource);
        if (imageData == null) {
            return false;
        }
        imageData.toString(pw, "");
        GifImageContents blocks = this.readFile(byteSource, false);
        pw.println("gif.blocks: " + blocks.blocks.size());
        for (int i = 0; i < blocks.blocks.size(); ++i) {
            GifBlock gifBlock = blocks.blocks.get(i);
            this.debugNumber(pw, "\t" + i + " (" + gifBlock.getClass().getName() + ")", gifBlock.blockCode, 4);
        }
        pw.println("");
        return true;
    }

    private int[] getColorTable(byte[] bytes) throws ImageReadException {
        if (bytes.length % 3 != 0) {
            throw new ImageReadException("Bad Color Table Length: " + bytes.length);
        }
        int length = bytes.length / 3;
        int[] result = new int[length];
        for (int i = 0; i < length; ++i) {
            int rgb;
            int red = 0xFF & bytes[i * 3 + 0];
            int green = 0xFF & bytes[i * 3 + 1];
            int blue = 0xFF & bytes[i * 3 + 2];
            int alpha = 255;
            result[i] = rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
        }
        return result;
    }

    @Override
    public FormatCompliance getFormatCompliance(ByteSource byteSource) throws ImageReadException, IOException {
        FormatCompliance result = new FormatCompliance(byteSource.getDescription());
        this.readFile(byteSource, false, result);
        return result;
    }

    private List<GifImageData> findAllImageData(GifImageContents imageContents) throws ImageReadException {
        List descriptors = this.findAllBlocks(imageContents.blocks, 44);
        if (descriptors.isEmpty()) {
            throw new ImageReadException("GIF: Couldn't read Image Descriptor");
        }
        List gcExtensions = this.findAllBlocks(imageContents.blocks, 8697);
        if (!gcExtensions.isEmpty() && gcExtensions.size() != descriptors.size()) {
            throw new ImageReadException("GIF: Invalid amount of Graphic Control Extensions");
        }
        ArrayList<GifImageData> imageData = new ArrayList<GifImageData>(descriptors.size());
        for (int i = 0; i < descriptors.size(); ++i) {
            ImageDescriptor descriptor = (ImageDescriptor)descriptors.get(i);
            if (descriptor == null) {
                throw new ImageReadException(String.format("GIF: Couldn't read Image Descriptor of image number %d", i));
            }
            GraphicControlExtension gce = gcExtensions.isEmpty() ? null : (GraphicControlExtension)gcExtensions.get(i);
            imageData.add(new GifImageData(descriptor, gce));
        }
        return imageData;
    }

    private GifImageData findFirstImageData(GifImageContents imageContents) throws ImageReadException {
        ImageDescriptor descriptor = (ImageDescriptor)this.findBlock(imageContents.blocks, 44);
        if (descriptor == null) {
            throw new ImageReadException("GIF: Couldn't read Image Descriptor");
        }
        GraphicControlExtension gce = (GraphicControlExtension)this.findBlock(imageContents.blocks, 8697);
        return new GifImageData(descriptor, gce);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private BufferedImage getBufferedImage(GifHeaderInfo headerInfo, GifImageData imageData, byte[] globalColorTable) throws ImageReadException {
        int[] colorTable;
        ImageDescriptor id = imageData.descriptor;
        GraphicControlExtension gce = imageData.gce;
        int width = id.imageWidth;
        int height = id.imageHeight;
        boolean hasAlpha = false;
        if (gce != null && gce.transparency) {
            hasAlpha = true;
        }
        ImageBuilder imageBuilder = new ImageBuilder(width, height, hasAlpha);
        if (id.localColorTable != null) {
            colorTable = this.getColorTable(id.localColorTable);
        } else {
            if (globalColorTable == null) throw new ImageReadException("Gif: No Color Table");
            colorTable = this.getColorTable(globalColorTable);
        }
        int transparentIndex = -1;
        if (gce != null && hasAlpha) {
            transparentIndex = gce.transparentColorIndex;
        }
        int counter = 0;
        int rowsInPass1 = (height + 7) / 8;
        int rowsInPass2 = (height + 3) / 8;
        int rowsInPass3 = (height + 1) / 4;
        int rowsInPass4 = height / 2;
        for (int row = 0; row < height; ++row) {
            int y;
            if (id.interlaceFlag) {
                int theRow = row;
                if (theRow < rowsInPass1) {
                    y = theRow * 8;
                } else if ((theRow -= rowsInPass1) < rowsInPass2) {
                    y = 4 + theRow * 8;
                } else if ((theRow -= rowsInPass2) < rowsInPass3) {
                    y = 2 + theRow * 4;
                } else {
                    if ((theRow -= rowsInPass3) >= rowsInPass4) throw new ImageReadException("Gif: Strange Row");
                    y = 1 + theRow * 2;
                }
            } else {
                y = row;
            }
            for (int x = 0; x < width; ++x) {
                int index = 0xFF & id.imageData[counter++];
                int rgb = colorTable[index];
                if (transparentIndex == index) {
                    rgb = 0;
                }
                imageBuilder.setRGB(x, y, rgb);
            }
        }
        return imageBuilder.getBufferedImage();
    }

    @Override
    public List<BufferedImage> getAllBufferedImages(ByteSource byteSource) throws ImageReadException, IOException {
        GifImageContents imageContents = this.readFile(byteSource, false);
        if (imageContents == null) {
            throw new ImageReadException("GIF: Couldn't read blocks");
        }
        GifHeaderInfo ghi = imageContents.gifHeaderInfo;
        if (ghi == null) {
            throw new ImageReadException("GIF: Couldn't read Header");
        }
        List<GifImageData> imageData = this.findAllImageData(imageContents);
        ArrayList<BufferedImage> result = new ArrayList<BufferedImage>(imageData.size());
        for (GifImageData id : imageData) {
            result.add(this.getBufferedImage(ghi, id, imageContents.globalColorTable));
        }
        return result;
    }

    @Override
    public BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        GifImageContents imageContents = this.readFile(byteSource, false);
        if (imageContents == null) {
            throw new ImageReadException("GIF: Couldn't read blocks");
        }
        GifHeaderInfo ghi = imageContents.gifHeaderInfo;
        if (ghi == null) {
            throw new ImageReadException("GIF: Couldn't read Header");
        }
        GifImageData imageData = this.findFirstImageData(imageContents);
        return this.getBufferedImage(ghi, imageData, imageContents.globalColorTable);
    }

    private void writeAsSubBlocks(OutputStream os, byte[] bytes) throws IOException {
        int blockSize;
        for (int index = 0; index < bytes.length; index += blockSize) {
            blockSize = Math.min(bytes.length - index, 255);
            os.write(blockSize);
            os.write(bytes, index, blockSize);
        }
        os.write(0);
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        if ((params = new HashMap<String, Object>(params)).containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        String xmpXml = null;
        if (params.containsKey("XMP_XML")) {
            xmpXml = (String)params.get("XMP_XML");
            params.remove("XMP_XML");
        }
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        int width = src.getWidth();
        int height = src.getHeight();
        boolean hasAlpha = new PaletteFactory().hasTransparency(src);
        int maxColors = hasAlpha ? 255 : 256;
        Palette palette2 = new PaletteFactory().makeExactRgbPaletteSimple(src, maxColors);
        if (palette2 == null) {
            palette2 = new PaletteFactory().makeQuantizedRgbPalette(src, maxColors);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("quantizing");
            }
        } else if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("exact palette");
        }
        if (palette2 == null) {
            throw new ImageWriteException("Gif: can't write images with more than 256 colors");
        }
        int paletteSize = palette2.length() + (hasAlpha ? 1 : 0);
        BinaryOutputStream bos = new BinaryOutputStream(os, ByteOrder.LITTLE_ENDIAN);
        os.write(71);
        os.write(73);
        os.write(70);
        os.write(56);
        os.write(57);
        os.write(97);
        bos.write2Bytes(width);
        bos.write2Bytes(height);
        int colorTableScaleLessOne = paletteSize > 128 ? 7 : (paletteSize > 64 ? 6 : (paletteSize > 32 ? 5 : (paletteSize > 16 ? 4 : (paletteSize > 8 ? 3 : (paletteSize > 4 ? 2 : (paletteSize > 2 ? 1 : 0))))));
        int colorTableSizeInFormat = 1 << colorTableScaleLessOne + 1;
        byte colorResolution = (byte)colorTableScaleLessOne;
        int packedFields = (7 & colorResolution) * 16;
        bos.write(packedFields);
        boolean backgroundColorIndex = false;
        bos.write(0);
        boolean pixelAspectRatio = false;
        bos.write(0);
        bos.write(33);
        bos.write(-7);
        bos.write(4);
        boolean packedFields2 = hasAlpha;
        bos.write((byte)(packedFields2 ? 1 : 0));
        bos.write(0);
        bos.write(0);
        bos.write((byte)(hasAlpha ? palette2.length() : 0));
        bos.write(0);
        if (null != xmpXml) {
            bos.write(33);
            bos.write(255);
            bos.write(XMP_APPLICATION_ID_AND_AUTH_CODE.length);
            bos.write(XMP_APPLICATION_ID_AND_AUTH_CODE);
            byte[] xmpXmlBytes = xmpXml.getBytes(StandardCharsets.UTF_8);
            bos.write(xmpXmlBytes);
            for (int magic = 0; magic <= 255; ++magic) {
                bos.write(255 - magic);
            }
            bos.write(0);
        }
        bos.write(44);
        bos.write2Bytes(0);
        bos.write2Bytes(0);
        bos.write2Bytes(width);
        bos.write2Bytes(height);
        boolean localColorTableFlag = true;
        boolean interlaceFlag = false;
        boolean sortFlag = false;
        int sizeOfLocalColorTable = colorTableScaleLessOne;
        int packedFields3 = 0x80 | 7 & sizeOfLocalColorTable;
        bos.write(packedFields3);
        for (int i = 0; i < colorTableSizeInFormat; ++i) {
            if (i < palette2.length()) {
                int rgb = palette2.getEntry(i);
                int red = 0xFF & rgb >> 16;
                int green = 0xFF & rgb >> 8;
                int blue = 0xFF & rgb >> 0;
                bos.write(red);
                bos.write(green);
                bos.write(blue);
                continue;
            }
            bos.write(0);
            bos.write(0);
            bos.write(0);
        }
        int lzwMinimumCodeSize = colorTableScaleLessOne + 1;
        if (lzwMinimumCodeSize < 2) {
            lzwMinimumCodeSize = 2;
        }
        bos.write(lzwMinimumCodeSize);
        MyLzwCompressor compressor = new MyLzwCompressor(lzwMinimumCodeSize, ByteOrder.LITTLE_ENDIAN, false);
        byte[] imagedata = new byte[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int index;
                int argb = src.getRGB(x, y);
                int rgb = 0xFFFFFF & argb;
                if (hasAlpha) {
                    int alpha = 0xFF & argb >> 24;
                    int alphaThreshold = 255;
                    index = alpha < 255 ? palette2.length() : palette2.getPaletteIndex(rgb);
                } else {
                    index = palette2.getPaletteIndex(rgb);
                }
                imagedata[y * width + x] = (byte)index;
            }
        }
        byte[] compressed = compressor.compress(imagedata);
        this.writeAsSubBlocks(bos, compressed);
        bos.write(59);
        bos.close();
        os.close();
    }

    @Override
    public String getXmpXml(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            Object object;
            FormatCompliance formatCompliance = null;
            GifHeaderInfo ghi = this.readHeader(is, formatCompliance);
            if (ghi.globalColorTableFlag) {
                this.readColorTable(is, ghi.sizeOfGlobalColorTable);
            }
            List<GifBlock> blocks = this.readBlocks(ghi, is, true, formatCompliance);
            ArrayList<String> result = new ArrayList<String>();
            for (GifBlock block : blocks) {
                GenericGifBlock genericBlock;
                byte[] blockBytes;
                if (block.blockCode != 8703 || (blockBytes = (genericBlock = (GenericGifBlock)block).appendSubBlocks(true)).length < XMP_APPLICATION_ID_AND_AUTH_CODE.length || !BinaryFunctions.compareBytes(blockBytes, 0, XMP_APPLICATION_ID_AND_AUTH_CODE, 0, XMP_APPLICATION_ID_AND_AUTH_CODE.length)) continue;
                byte[] GIF_MAGIC_TRAILER = new byte[256];
                for (int magic = 0; magic <= 255; ++magic) {
                    GIF_MAGIC_TRAILER[magic] = (byte)(255 - magic);
                }
                if (blockBytes.length < XMP_APPLICATION_ID_AND_AUTH_CODE.length + GIF_MAGIC_TRAILER.length) continue;
                if (!BinaryFunctions.compareBytes(blockBytes, blockBytes.length - GIF_MAGIC_TRAILER.length, GIF_MAGIC_TRAILER, 0, GIF_MAGIC_TRAILER.length)) {
                    throw new ImageReadException("XMP block in GIF missing magic trailer.");
                }
                String xml = new String(blockBytes, XMP_APPLICATION_ID_AND_AUTH_CODE.length, blockBytes.length - (XMP_APPLICATION_ID_AND_AUTH_CODE.length + GIF_MAGIC_TRAILER.length), StandardCharsets.UTF_8);
                result.add(xml);
            }
            if (result.isEmpty()) {
                object = null;
                return object;
            }
            if (result.size() > 1) {
                throw new ImageReadException("More than one XMP Block in GIF.");
            }
            object = (String)result.get(0);
            return object;
        }
    }
}

