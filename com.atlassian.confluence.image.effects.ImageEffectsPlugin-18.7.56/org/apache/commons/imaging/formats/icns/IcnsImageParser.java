/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.icns;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.icns.IcnsDecoder;
import org.apache.commons.imaging.formats.icns.IcnsType;

public class IcnsImageParser
extends ImageParser {
    static final int ICNS_MAGIC = IcnsType.typeAsInt("icns");
    private static final String DEFAULT_EXTENSION = ".icns";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".icns"};

    public IcnsImageParser() {
        super.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    @Override
    public String getName() {
        return "Apple Icon Image";
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
        return new ImageFormat[]{ImageFormats.ICNS};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        Map<String, Object> map = params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageReadException("Unknown parameter: " + firstKey);
        }
        IcnsContents contents = this.readImage(byteSource);
        List<BufferedImage> images = IcnsDecoder.decodeAllImages(contents.icnsElements);
        if (images.isEmpty()) {
            throw new ImageReadException("No icons in ICNS file");
        }
        BufferedImage image0 = images.get(0);
        return new ImageInfo("Icns", 32, new ArrayList<String>(), ImageFormats.ICNS, "ICNS Apple Icon Image", image0.getHeight(), "image/x-icns", images.size(), 0, 0.0f, 0, 0.0f, image0.getWidth(), false, true, false, ImageInfo.ColorType.RGB, ImageInfo.CompressionAlgorithm.UNKNOWN);
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        Map<String, Object> map = params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageReadException("Unknown parameter: " + firstKey);
        }
        IcnsContents contents = this.readImage(byteSource);
        List<BufferedImage> images = IcnsDecoder.decodeAllImages(contents.icnsElements);
        if (images.isEmpty()) {
            throw new ImageReadException("No icons in ICNS file");
        }
        BufferedImage image0 = images.get(0);
        return new Dimension(image0.getWidth(), image0.getHeight());
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private IcnsHeader readIcnsHeader(InputStream is) throws ImageReadException, IOException {
        int magic = BinaryFunctions.read4Bytes("Magic", is, "Not a Valid ICNS File", this.getByteOrder());
        int fileSize = BinaryFunctions.read4Bytes("FileSize", is, "Not a Valid ICNS File", this.getByteOrder());
        if (magic != ICNS_MAGIC) {
            throw new ImageReadException("Not a Valid ICNS File: magic is 0x" + Integer.toHexString(magic));
        }
        return new IcnsHeader(magic, fileSize);
    }

    private IcnsElement readIcnsElement(InputStream is, int remainingSize) throws IOException {
        int type = BinaryFunctions.read4Bytes("Type", is, "Not a valid ICNS file", this.getByteOrder());
        int elementSize = BinaryFunctions.read4Bytes("ElementSize", is, "Not a valid ICNS file", this.getByteOrder());
        if (elementSize > remainingSize) {
            throw new IOException(String.format("Corrupted ICNS file: element size %d is greater than remaining size %d", elementSize, remainingSize));
        }
        byte[] data = BinaryFunctions.readBytes("Data", is, elementSize - 8, "Not a valid ICNS file");
        return new IcnsElement(type, elementSize, data);
    }

    private IcnsContents readImage(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            IcnsContents ret;
            IcnsElement icnsElement;
            IcnsHeader icnsHeader = this.readIcnsHeader(is);
            ArrayList<IcnsElement> icnsElementList = new ArrayList<IcnsElement>();
            for (int remainingSize = icnsHeader.fileSize - 8; remainingSize > 0; remainingSize -= icnsElement.elementSize) {
                icnsElement = this.readIcnsElement(is, remainingSize);
                icnsElementList.add(icnsElement);
            }
            IcnsElement[] icnsElements = new IcnsElement[icnsElementList.size()];
            for (int i = 0; i < icnsElements.length; ++i) {
                icnsElements[i] = (IcnsElement)icnsElementList.get(i);
            }
            IcnsContents icnsContents = ret = new IcnsContents(icnsHeader, icnsElements);
            return icnsContents;
        }
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        IcnsContents icnsContents = this.readImage(byteSource);
        icnsContents.icnsHeader.dump(pw);
        for (IcnsElement icnsElement : icnsContents.icnsElements) {
            icnsElement.dump(pw);
        }
        return true;
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        IcnsContents icnsContents = this.readImage(byteSource);
        List<BufferedImage> result = IcnsDecoder.decodeAllImages(icnsContents.icnsElements);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        throw new ImageReadException("No icons in ICNS file");
    }

    @Override
    public List<BufferedImage> getAllBufferedImages(ByteSource byteSource) throws ImageReadException, IOException {
        IcnsContents icnsContents = this.readImage(byteSource);
        return IcnsDecoder.decodeAllImages(icnsContents.icnsElements);
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        IcnsType imageType;
        Map<String, Object> map = params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        if (params.containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        if (src.getWidth() == 16 && src.getHeight() == 16) {
            imageType = IcnsType.ICNS_16x16_32BIT_IMAGE;
        } else if (src.getWidth() == 32 && src.getHeight() == 32) {
            imageType = IcnsType.ICNS_32x32_32BIT_IMAGE;
        } else if (src.getWidth() == 48 && src.getHeight() == 48) {
            imageType = IcnsType.ICNS_48x48_32BIT_IMAGE;
        } else if (src.getWidth() == 128 && src.getHeight() == 128) {
            imageType = IcnsType.ICNS_128x128_32BIT_IMAGE;
        } else {
            throw new ImageWriteException("Invalid/unsupported source width " + src.getWidth() + " and height " + src.getHeight());
        }
        try (BinaryOutputStream bos = new BinaryOutputStream(os, ByteOrder.BIG_ENDIAN);){
            bos.write4Bytes(ICNS_MAGIC);
            bos.write4Bytes(16 + 4 * imageType.getWidth() * imageType.getHeight() + 4 + 4 + imageType.getWidth() * imageType.getHeight());
            bos.write4Bytes(imageType.getType());
            bos.write4Bytes(8 + 4 * imageType.getWidth() * imageType.getHeight());
            for (int y = 0; y < src.getHeight(); ++y) {
                for (int x = 0; x < src.getWidth(); ++x) {
                    int argb = src.getRGB(x, y);
                    bos.write(0);
                    bos.write(argb >> 16);
                    bos.write(argb >> 8);
                    bos.write(argb);
                }
            }
            IcnsType maskType = IcnsType.find8BPPMaskType(imageType);
            bos.write4Bytes(maskType.getType());
            bos.write4Bytes(8 + imageType.getWidth() * imageType.getWidth());
            for (int y = 0; y < src.getHeight(); ++y) {
                for (int x = 0; x < src.getWidth(); ++x) {
                    int argb = src.getRGB(x, y);
                    bos.write(argb >> 24);
                }
            }
        }
    }

    private static class IcnsContents {
        public final IcnsHeader icnsHeader;
        public final IcnsElement[] icnsElements;

        IcnsContents(IcnsHeader icnsHeader, IcnsElement[] icnsElements) {
            this.icnsHeader = icnsHeader;
            this.icnsElements = icnsElements;
        }
    }

    static class IcnsElement {
        public final int type;
        public final int elementSize;
        public final byte[] data;

        IcnsElement(int type, int elementSize, byte[] data) {
            this.type = type;
            this.elementSize = elementSize;
            this.data = data;
        }

        public void dump(PrintWriter pw) {
            pw.println("IcnsElement");
            IcnsType icnsType = IcnsType.findAnyType(this.type);
            String typeDescription = icnsType == null ? "" : " " + icnsType.toString();
            pw.println("Type: 0x" + Integer.toHexString(this.type) + " (" + IcnsType.describeType(this.type) + ")" + typeDescription);
            pw.println("ElementSize: " + this.elementSize);
            pw.println("");
        }
    }

    private static class IcnsHeader {
        public final int magic;
        public final int fileSize;

        IcnsHeader(int magic, int fileSize) {
            this.magic = magic;
            this.fileSize = fileSize;
        }

        public void dump(PrintWriter pw) {
            pw.println("IcnsHeader");
            pw.println("Magic: 0x" + Integer.toHexString(this.magic) + " (" + IcnsType.describeType(this.magic) + ")");
            pw.println("FileSize: " + this.fileSize);
            pw.println("");
        }
    }
}

