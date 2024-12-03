/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.wbmp;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;

public class WbmpImageParser
extends ImageParser {
    private static final String DEFAULT_EXTENSION = ".wbmp";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".wbmp"};

    @Override
    public String getName() {
        return "Wireless Application Protocol Bitmap Format";
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
        return new ImageFormat[]{ImageFormats.WBMP};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        WbmpHeader wbmpHeader = this.readWbmpHeader(byteSource);
        return new ImageInfo("WBMP", 1, new ArrayList<String>(), ImageFormats.WBMP, "Wireless Application Protocol Bitmap", wbmpHeader.height, "image/vnd.wap.wbmp", 1, 0, 0.0f, 0, 0.0f, wbmpHeader.width, false, false, false, ImageInfo.ColorType.BW, ImageInfo.CompressionAlgorithm.NONE);
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        WbmpHeader wbmpHeader = this.readWbmpHeader(byteSource);
        return new Dimension(wbmpHeader.width, wbmpHeader.height);
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private int readMultiByteInteger(InputStream is) throws ImageReadException, IOException {
        byte nextByte;
        int value = 0;
        int totalBits = 0;
        do {
            nextByte = BinaryFunctions.readByte("Header", is, "Error reading WBMP header");
            value <<= 7;
            value |= nextByte & 0x7F;
            if ((totalBits += 7) <= 31) continue;
            throw new ImageReadException("Overflow reading WBMP multi-byte field");
        } while ((nextByte & 0x80) != 0);
        return value;
    }

    private void writeMultiByteInteger(OutputStream os, int value) throws IOException {
        boolean wroteYet = false;
        for (int position = 28; position > 0; position -= 7) {
            int next7Bits = 0x7F & value >>> position;
            if (next7Bits == 0 && !wroteYet) continue;
            os.write(0x80 | next7Bits);
            wroteYet = true;
        }
        os.write(0x7F & value);
    }

    private WbmpHeader readWbmpHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            WbmpHeader wbmpHeader = this.readWbmpHeader(is);
            return wbmpHeader;
        }
    }

    private WbmpHeader readWbmpHeader(InputStream is) throws ImageReadException, IOException {
        int typeField = this.readMultiByteInteger(is);
        if (typeField != 0) {
            throw new ImageReadException("Invalid/unsupported WBMP type " + typeField);
        }
        byte fixHeaderField = BinaryFunctions.readByte("FixHeaderField", is, "Invalid WBMP File");
        if ((fixHeaderField & 0x9F) != 0) {
            throw new ImageReadException("Invalid/unsupported WBMP FixHeaderField 0x" + Integer.toHexString(0xFF & fixHeaderField));
        }
        int width = this.readMultiByteInteger(is);
        int height = this.readMultiByteInteger(is);
        return new WbmpHeader(typeField, fixHeaderField, width, height);
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        this.readWbmpHeader(byteSource).dump(pw);
        return true;
    }

    private BufferedImage readImage(WbmpHeader wbmpHeader, InputStream is) throws IOException {
        int rowLength = (wbmpHeader.width + 7) / 8;
        byte[] image = BinaryFunctions.readBytes("Pixels", is, rowLength * wbmpHeader.height, "Error reading image pixels");
        DataBufferByte dataBuffer = new DataBufferByte(image, image.length);
        WritableRaster raster = Raster.createPackedRaster(dataBuffer, wbmpHeader.width, wbmpHeader.height, 1, null);
        int[] palette = new int[]{0, 0xFFFFFF};
        IndexColorModel colorModel = new IndexColorModel(1, 2, palette, 0, false, -1, 0);
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            BufferedImage ret;
            WbmpHeader wbmpHeader = this.readWbmpHeader(is);
            BufferedImage bufferedImage = ret = this.readImage(wbmpHeader, is);
            return bufferedImage;
        }
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        Map<String, Object> map = params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        if (params.containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        this.writeMultiByteInteger(os, 0);
        os.write(0);
        this.writeMultiByteInteger(os, src.getWidth());
        this.writeMultiByteInteger(os, src.getHeight());
        for (int y = 0; y < src.getHeight(); ++y) {
            int pixel = 0;
            int nextBit = 128;
            for (int x = 0; x < src.getWidth(); ++x) {
                int blue;
                int green;
                int argb = src.getRGB(x, y);
                int red = 0xFF & argb >> 16;
                int sample = (red + (green = 0xFF & argb >> 8) + (blue = 0xFF & argb >> 0)) / 3;
                if (sample > 127) {
                    pixel |= nextBit;
                }
                if ((nextBit >>>= 1) != 0) continue;
                os.write(pixel);
                pixel = 0;
                nextBit = 128;
            }
            if (nextBit == 128) continue;
            os.write(pixel);
        }
    }

    static class WbmpHeader {
        int typeField;
        byte fixHeaderField;
        int width;
        int height;

        WbmpHeader(int typeField, byte fixHeaderField, int width, int height) {
            this.typeField = typeField;
            this.fixHeaderField = fixHeaderField;
            this.width = width;
            this.height = height;
        }

        public void dump(PrintWriter pw) {
            pw.println("WbmpHeader");
            pw.println("TypeField: " + this.typeField);
            pw.println("FixHeaderField: 0x" + Integer.toHexString(0xFF & this.fixHeaderField));
            pw.println("Width: " + this.width);
            pw.println("Height: " + this.height);
        }
    }
}

