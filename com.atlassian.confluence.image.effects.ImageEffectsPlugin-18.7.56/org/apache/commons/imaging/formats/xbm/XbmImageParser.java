/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.xbm;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BasicCParser;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;

public class XbmImageParser
extends ImageParser {
    private static final String DEFAULT_EXTENSION = ".xbm";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".xbm"};

    @Override
    public String getName() {
        return "X BitMap";
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
        return new ImageFormat[]{ImageFormats.XBM};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        XbmHeader xbmHeader = this.readXbmHeader(byteSource);
        return new ImageInfo("XBM", 1, new ArrayList<String>(), ImageFormats.XBM, "X BitMap", xbmHeader.height, "image/x-xbitmap", 1, 0, 0.0f, 0, 0.0f, xbmHeader.width, false, false, false, ImageInfo.ColorType.BW, ImageInfo.CompressionAlgorithm.NONE);
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        XbmHeader xbmHeader = this.readXbmHeader(byteSource);
        return new Dimension(xbmHeader.width, xbmHeader.height);
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private XbmHeader readXbmHeader(ByteSource byteSource) throws ImageReadException, IOException {
        return this.parseXbmHeader((ByteSource)byteSource).xbmHeader;
    }

    private XbmParseResult parseXbmHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            HashMap<String, String> defines = new HashMap<String, String>();
            ByteArrayOutputStream preprocessedFile = BasicCParser.preprocess(is, null, defines);
            int width = -1;
            int height = -1;
            int xHot = -1;
            int yHot = -1;
            for (Map.Entry entry : defines.entrySet()) {
                String name = (String)entry.getKey();
                if (name.endsWith("_width")) {
                    width = XbmImageParser.parseCIntegerLiteral((String)entry.getValue());
                    continue;
                }
                if (name.endsWith("_height")) {
                    height = XbmImageParser.parseCIntegerLiteral((String)entry.getValue());
                    continue;
                }
                if (name.endsWith("_x_hot")) {
                    xHot = XbmImageParser.parseCIntegerLiteral((String)entry.getValue());
                    continue;
                }
                if (!name.endsWith("_y_hot")) continue;
                yHot = XbmImageParser.parseCIntegerLiteral((String)entry.getValue());
            }
            if (width == -1) {
                throw new ImageReadException("width not found");
            }
            if (height == -1) {
                throw new ImageReadException("height not found");
            }
            XbmParseResult xbmParseResult = new XbmParseResult();
            xbmParseResult.cParser = new BasicCParser(new ByteArrayInputStream(preprocessedFile.toByteArray()));
            xbmParseResult.xbmHeader = new XbmHeader(width, height, xHot, yHot);
            XbmParseResult xbmParseResult2 = xbmParseResult;
            return xbmParseResult2;
        }
    }

    private static int parseCIntegerLiteral(String value) {
        if (value.startsWith("0")) {
            if (value.length() >= 2) {
                if (value.charAt(1) == 'x' || value.charAt(1) == 'X') {
                    return Integer.parseInt(value.substring(2), 16);
                }
                return Integer.parseInt(value.substring(1), 8);
            }
            return 0;
        }
        return Integer.parseInt(value);
    }

    private BufferedImage readXbmImage(XbmHeader xbmHeader, BasicCParser cParser) throws ImageReadException, IOException {
        int hexWidth;
        int inputWidth;
        String token = cParser.nextToken();
        if (!"static".equals(token)) {
            throw new ImageReadException("Parsing XBM file failed, no 'static' token");
        }
        token = cParser.nextToken();
        if (token == null) {
            throw new ImageReadException("Parsing XBM file failed, no 'unsigned' or 'char' or 'short' token");
        }
        if ("unsigned".equals(token)) {
            token = cParser.nextToken();
        }
        if ("char".equals(token)) {
            inputWidth = 8;
            hexWidth = 4;
        } else if ("short".equals(token)) {
            inputWidth = 16;
            hexWidth = 6;
        } else {
            throw new ImageReadException("Parsing XBM file failed, no 'char' or 'short' token");
        }
        String name = cParser.nextToken();
        if (name == null) {
            throw new ImageReadException("Parsing XBM file failed, no variable name");
        }
        if (name.charAt(0) != '_' && !Character.isLetter(name.charAt(0))) {
            throw new ImageReadException("Parsing XBM file failed, variable name doesn't start with letter or underscore");
        }
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') continue;
            throw new ImageReadException("Parsing XBM file failed, variable name contains non-letter non-digit non-underscore");
        }
        token = cParser.nextToken();
        if (!"[".equals(token)) {
            throw new ImageReadException("Parsing XBM file failed, no '[' token");
        }
        token = cParser.nextToken();
        if (!"]".equals(token)) {
            throw new ImageReadException("Parsing XBM file failed, no ']' token");
        }
        token = cParser.nextToken();
        if (!"=".equals(token)) {
            throw new ImageReadException("Parsing XBM file failed, no '=' token");
        }
        token = cParser.nextToken();
        if (!"{".equals(token)) {
            throw new ImageReadException("Parsing XBM file failed, no '{' token");
        }
        int rowLength = (xbmHeader.width + 7) / 8;
        byte[] imageData = new byte[rowLength * xbmHeader.height];
        int i = 0;
        for (int y = 0; y < xbmHeader.height; ++y) {
            for (int x = 0; x < xbmHeader.width; x += inputWidth) {
                token = cParser.nextToken();
                if (token == null || !token.startsWith("0x")) {
                    throw new ImageReadException("Parsing XBM file failed, hex value missing");
                }
                if (token.length() > hexWidth) {
                    throw new ImageReadException("Parsing XBM file failed, hex value too long");
                }
                int value = Integer.parseInt(token.substring(2), 16);
                int flipped = Integer.reverse(value) >>> 32 - inputWidth;
                if (inputWidth == 16) {
                    imageData[i++] = (byte)(flipped >>> 8);
                    if (x + 8 < xbmHeader.width) {
                        imageData[i++] = (byte)flipped;
                    }
                } else {
                    imageData[i++] = (byte)flipped;
                }
                if ((token = cParser.nextToken()) == null) {
                    throw new ImageReadException("Parsing XBM file failed, premature end of file");
                }
                if (",".equals(token) || i >= imageData.length && "}".equals(token)) continue;
                throw new ImageReadException("Parsing XBM file failed, punctuation error");
            }
        }
        int[] palette = new int[]{0xFFFFFF, 0};
        IndexColorModel colorModel = new IndexColorModel(1, 2, palette, 0, false, -1, 0);
        DataBufferByte dataBuffer = new DataBufferByte(imageData, imageData.length);
        WritableRaster raster = Raster.createPackedRaster(dataBuffer, xbmHeader.width, xbmHeader.height, 1, null);
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        this.readXbmHeader(byteSource).dump(pw);
        return true;
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        XbmParseResult result = this.parseXbmHeader(byteSource);
        return this.readXbmImage(result.xbmHeader, result.cParser);
    }

    private static String randomName() {
        int i;
        UUID uuid = UUID.randomUUID();
        StringBuilder stringBuilder = new StringBuilder("a");
        long bits = uuid.getMostSignificantBits();
        for (i = 56; i >= 0; i -= 8) {
            stringBuilder.append(Integer.toHexString((int)(bits >> i & 0xFFL)));
        }
        bits = uuid.getLeastSignificantBits();
        for (i = 56; i >= 0; i -= 8) {
            stringBuilder.append(Integer.toHexString((int)(bits >> i & 0xFFL)));
        }
        return stringBuilder.toString();
    }

    private static String toPrettyHex(int value) {
        String s = Integer.toHexString(0xFF & value);
        if (s.length() == 2) {
            return "0x" + s;
        }
        return "0x0" + s;
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
        String name = XbmImageParser.randomName();
        os.write(("#define " + name + "_width " + src.getWidth() + "\n").getBytes(StandardCharsets.US_ASCII));
        os.write(("#define " + name + "_height " + src.getHeight() + "\n").getBytes(StandardCharsets.US_ASCII));
        os.write(("static unsigned char " + name + "_bits[] = {").getBytes(StandardCharsets.US_ASCII));
        int bitcache = 0;
        int bitsInCache = 0;
        String separator = "\n  ";
        int written = 0;
        for (int y = 0; y < src.getHeight(); ++y) {
            for (int x = 0; x < src.getWidth(); ++x) {
                int blue;
                int green;
                int argb = src.getRGB(x, y);
                int red = 0xFF & argb >> 16;
                int sample = (red + (green = 0xFF & argb >> 8) + (blue = 0xFF & argb >> 0)) / 3;
                sample = sample > 127 ? 0 : 1;
                bitcache |= sample << bitsInCache;
                if (++bitsInCache != 8) continue;
                os.write(separator.getBytes(StandardCharsets.US_ASCII));
                separator = ",";
                if (written == 12) {
                    os.write("\n  ".getBytes(StandardCharsets.US_ASCII));
                    written = 0;
                }
                os.write(XbmImageParser.toPrettyHex(bitcache).getBytes(StandardCharsets.US_ASCII));
                bitcache = 0;
                bitsInCache = 0;
                ++written;
            }
            if (bitsInCache == 0) continue;
            os.write(separator.getBytes(StandardCharsets.US_ASCII));
            separator = ",";
            if (written == 12) {
                os.write("\n  ".getBytes(StandardCharsets.US_ASCII));
                written = 0;
            }
            os.write(XbmImageParser.toPrettyHex(bitcache).getBytes(StandardCharsets.US_ASCII));
            bitcache = 0;
            bitsInCache = 0;
            ++written;
        }
        os.write("\n};\n".getBytes(StandardCharsets.US_ASCII));
    }

    private static class XbmParseResult {
        XbmHeader xbmHeader;
        BasicCParser cParser;

        private XbmParseResult() {
        }
    }

    private static class XbmHeader {
        int width;
        int height;
        int xHot = -1;
        int yHot = -1;

        XbmHeader(int width, int height, int xHot, int yHot) {
            this.width = width;
            this.height = height;
            this.xHot = xHot;
            this.yHot = yHot;
        }

        public void dump(PrintWriter pw) {
            pw.println("XbmHeader");
            pw.println("Width: " + this.width);
            pw.println("Height: " + this.height);
            if (this.xHot != -1 && this.yHot != -1) {
                pw.println("X hot: " + this.xHot);
                pw.println("Y hot: " + this.yHot);
            }
        }
    }
}

