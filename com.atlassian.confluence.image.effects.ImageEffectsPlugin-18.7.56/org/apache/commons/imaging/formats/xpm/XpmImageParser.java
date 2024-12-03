/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.xpm;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
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
import org.apache.commons.imaging.palette.PaletteFactory;
import org.apache.commons.imaging.palette.SimplePalette;

public class XpmImageParser
extends ImageParser {
    private static final String DEFAULT_EXTENSION = ".xpm";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".xpm"};
    private static Map<String, Integer> colorNames;
    private static final char[] WRITE_PALETTE;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void loadColorNames() throws ImageReadException {
        var0 = XpmImageParser.class;
        synchronized (XpmImageParser.class) {
            block34: {
                block33: {
                    if (XpmImageParser.colorNames != null) {
                        // ** MonitorExit[var0] (shouldn't be in output)
                        return;
                    }
                    try {
                        rgbTxtStream = XpmImageParser.class.getResourceAsStream("rgb.txt");
                        if (rgbTxtStream == null) {
                            throw new ImageReadException("Couldn't find rgb.txt in our resources");
                        }
                        colors = new HashMap<String, Integer>();
                        isReader = new InputStreamReader(rgbTxtStream, StandardCharsets.US_ASCII);
                        var4_5 = null;
                        try {
                            reader = new BufferedReader(isReader);
                            var6_9 = null;
                            ** try [egrp 3[TRYBLOCK] [2, 3 : 72->209)] { 
lbl17:
                            // 1 sources

                            break block33;
lbl18:
                            // 1 sources

                            catch (Throwable var7_12) {
                                var6_9 = var7_12;
                                throw var7_12;
                            }
                        }
                        catch (Throwable var5_8) {
                            var4_5 = var5_8;
                            throw var5_8;
                        }
                    }
                    catch (IOException ioException) {
                        throw new ImageReadException("Could not parse rgb.txt", ioException);
                    }
                }
                while ((line = reader.readLine()) != null) {
                    if (line.charAt(0) == '!') continue;
                    try {
                        red = Integer.parseInt(line.substring(0, 3).trim());
                        green = Integer.parseInt(line.substring(4, 7).trim());
                        blue = Integer.parseInt(line.substring(8, 11).trim());
                        colorName = line.substring(11).trim();
                        colors.put(colorName.toLowerCase(Locale.ENGLISH), -16777216 | red << 16 | green << 8 | blue);
                    }
                    catch (NumberFormatException nfe) {
                        throw new ImageReadException("Couldn't parse color in rgb.txt", nfe);
                    }
                }
                break block34;
lbl40:
                // 1 sources

                finally {
                    if (reader != null) {
                        if (var6_9 != null) {
                            try {
                                reader.close();
                            }
                            catch (Throwable var7_11) {
                                var6_9.addSuppressed(var7_11);
                            }
                        } else {
                            reader.close();
                        }
                    }
                }
                finally {
                    if (isReader != null) {
                        if (var4_5 != null) {
                            try {
                                isReader.close();
                            }
                            catch (Throwable var5_7) {
                                var4_5.addSuppressed(var5_7);
                            }
                        } else {
                            isReader.close();
                        }
                    }
                }
            }
            XpmImageParser.colorNames = colors;
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    @Override
    public String getName() {
        return "X PixMap";
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
        return new ImageFormat[]{ImageFormats.XPM};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        XpmHeader xpmHeader = this.readXpmHeader(byteSource);
        boolean transparent = false;
        ImageInfo.ColorType colorType = ImageInfo.ColorType.BW;
        for (Map.Entry<Object, PaletteEntry> entry : xpmHeader.palette.entrySet()) {
            PaletteEntry paletteEntry = entry.getValue();
            if ((paletteEntry.getBestARGB() & 0xFF000000) != -16777216) {
                transparent = true;
            }
            if (paletteEntry.haveColor) {
                colorType = ImageInfo.ColorType.RGB;
                continue;
            }
            if (colorType == ImageInfo.ColorType.RGB || !paletteEntry.haveGray && !paletteEntry.haveGray4Level) continue;
            colorType = ImageInfo.ColorType.GRAYSCALE;
        }
        return new ImageInfo("XPM version 3", xpmHeader.numCharsPerPixel * 8, new ArrayList<String>(), ImageFormats.XPM, "X PixMap", xpmHeader.height, "image/x-xpixmap", 1, 0, 0.0f, 0, 0.0f, xpmHeader.width, false, transparent, true, colorType, ImageInfo.CompressionAlgorithm.NONE);
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        XpmHeader xpmHeader = this.readXpmHeader(byteSource);
        return new Dimension(xpmHeader.width, xpmHeader.height);
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private XpmHeader readXpmHeader(ByteSource byteSource) throws ImageReadException, IOException {
        return this.parseXpmHeader((ByteSource)byteSource).xpmHeader;
    }

    private XpmParseResult parseXpmHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            StringBuilder firstComment = new StringBuilder();
            ByteArrayOutputStream preprocessedFile = BasicCParser.preprocess(is, firstComment, null);
            if (!"XPM".equals(firstComment.toString().trim())) {
                throw new ImageReadException("Parsing XPM file failed, signature isn't '/* XPM */'");
            }
            XpmParseResult xpmParseResult = new XpmParseResult();
            xpmParseResult.cParser = new BasicCParser(new ByteArrayInputStream(preprocessedFile.toByteArray()));
            xpmParseResult.xpmHeader = this.parseXpmHeader(xpmParseResult.cParser);
            XpmParseResult xpmParseResult2 = xpmParseResult;
            return xpmParseResult2;
        }
    }

    private boolean parseNextString(BasicCParser cParser, StringBuilder stringBuilder) throws IOException, ImageReadException {
        stringBuilder.setLength(0);
        String token = cParser.nextToken();
        if (token.charAt(0) != '\"') {
            throw new ImageReadException("Parsing XPM file failed, no string found where expected");
        }
        BasicCParser.unescapeString(stringBuilder, token);
        token = cParser.nextToken();
        while (token.charAt(0) == '\"') {
            BasicCParser.unescapeString(stringBuilder, token);
            token = cParser.nextToken();
        }
        if (",".equals(token)) {
            return true;
        }
        if ("}".equals(token)) {
            return false;
        }
        throw new ImageReadException("Parsing XPM file failed, no ',' or '}' found where expected");
    }

    private XpmHeader parseXpmValuesSection(String row) throws ImageReadException {
        String[] tokens = BasicCParser.tokenizeRow(row);
        if (tokens.length < 4 || tokens.length > 7) {
            throw new ImageReadException("Parsing XPM file failed, <Values> section has incorrect tokens");
        }
        try {
            int width = Integer.parseInt(tokens[0]);
            int height = Integer.parseInt(tokens[1]);
            int numColors = Integer.parseInt(tokens[2]);
            int numCharsPerPixel = Integer.parseInt(tokens[3]);
            int xHotSpot = -1;
            int yHotSpot = -1;
            boolean xpmExt = false;
            if (tokens.length >= 6) {
                xHotSpot = Integer.parseInt(tokens[4]);
                yHotSpot = Integer.parseInt(tokens[5]);
            }
            if (tokens.length == 5 || tokens.length == 7) {
                if ("XPMEXT".equals(tokens[tokens.length - 1])) {
                    xpmExt = true;
                } else {
                    throw new ImageReadException("Parsing XPM file failed, can't parse <Values> section XPMEXT");
                }
            }
            return new XpmHeader(width, height, numColors, numCharsPerPixel, xHotSpot, yHotSpot, xpmExt);
        }
        catch (NumberFormatException nfe) {
            throw new ImageReadException("Parsing XPM file failed, error parsing <Values> section", nfe);
        }
    }

    private int parseColor(String color) throws ImageReadException {
        if (color.charAt(0) == '#') {
            if ((color = color.substring(1)).length() == 3) {
                int red = Integer.parseInt(color.substring(0, 1), 16);
                int green = Integer.parseInt(color.substring(1, 2), 16);
                int blue = Integer.parseInt(color.substring(2, 3), 16);
                return 0xFF000000 | red << 20 | green << 12 | blue << 4;
            }
            if (color.length() == 6) {
                return 0xFF000000 | Integer.parseInt(color, 16);
            }
            if (color.length() == 9) {
                int red = Integer.parseInt(color.substring(0, 1), 16);
                int green = Integer.parseInt(color.substring(3, 4), 16);
                int blue = Integer.parseInt(color.substring(6, 7), 16);
                return 0xFF000000 | red << 16 | green << 8 | blue;
            }
            if (color.length() == 12) {
                int red = Integer.parseInt(color.substring(0, 1), 16);
                int green = Integer.parseInt(color.substring(4, 5), 16);
                int blue = Integer.parseInt(color.substring(8, 9), 16);
                return 0xFF000000 | red << 16 | green << 8 | blue;
            }
            if (color.length() == 24) {
                int red = Integer.parseInt(color.substring(0, 1), 16);
                int green = Integer.parseInt(color.substring(8, 9), 16);
                int blue = Integer.parseInt(color.substring(16, 17), 16);
                return 0xFF000000 | red << 16 | green << 8 | blue;
            }
            return 0;
        }
        if (color.charAt(0) == '%') {
            throw new ImageReadException("HSV colors are not implemented even in the XPM specification!");
        }
        if ("None".equals(color)) {
            return 0;
        }
        XpmImageParser.loadColorNames();
        String colorLowercase = color.toLowerCase(Locale.ENGLISH);
        if (colorNames.containsKey(colorLowercase)) {
            return colorNames.get(colorLowercase);
        }
        return 0;
    }

    private void populatePaletteEntry(PaletteEntry paletteEntry, String key, String color) throws ImageReadException {
        if ("m".equals(key)) {
            paletteEntry.monoArgb = this.parseColor(color);
            paletteEntry.haveMono = true;
        } else if ("g4".equals(key)) {
            paletteEntry.gray4LevelArgb = this.parseColor(color);
            paletteEntry.haveGray4Level = true;
        } else if ("g".equals(key)) {
            paletteEntry.grayArgb = this.parseColor(color);
            paletteEntry.haveGray = true;
        } else if ("s".equals(key)) {
            paletteEntry.colorArgb = this.parseColor(color);
            paletteEntry.haveColor = true;
        } else if ("c".equals(key)) {
            paletteEntry.colorArgb = this.parseColor(color);
            paletteEntry.haveColor = true;
        }
    }

    private void parsePaletteEntries(XpmHeader xpmHeader, BasicCParser cParser) throws IOException, ImageReadException {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < xpmHeader.numColors; ++i) {
            row.setLength(0);
            boolean hasMore = this.parseNextString(cParser, row);
            if (!hasMore) {
                throw new ImageReadException("Parsing XPM file failed, file ended while reading palette");
            }
            String name = row.substring(0, xpmHeader.numCharsPerPixel);
            String[] tokens = BasicCParser.tokenizeRow(row.substring(xpmHeader.numCharsPerPixel));
            PaletteEntry paletteEntry = new PaletteEntry();
            paletteEntry.index = i;
            int previousKeyIndex = Integer.MIN_VALUE;
            StringBuilder colorBuffer = new StringBuilder();
            for (int j = 0; j < tokens.length; ++j) {
                String token = tokens[j];
                boolean isKey = false;
                if (previousKeyIndex < j - 1 && "m".equals(token) || "g4".equals(token) || "g".equals(token) || "c".equals(token) || "s".equals(token)) {
                    isKey = true;
                }
                if (isKey) {
                    if (previousKeyIndex >= 0) {
                        String key = tokens[previousKeyIndex];
                        String color = colorBuffer.toString();
                        colorBuffer.setLength(0);
                        this.populatePaletteEntry(paletteEntry, key, color);
                    }
                    previousKeyIndex = j;
                    continue;
                }
                if (previousKeyIndex < 0) break;
                if (colorBuffer.length() > 0) {
                    colorBuffer.append(' ');
                }
                colorBuffer.append(token);
            }
            if (previousKeyIndex >= 0 && colorBuffer.length() > 0) {
                String key = tokens[previousKeyIndex];
                String color = colorBuffer.toString();
                colorBuffer.setLength(0);
                this.populatePaletteEntry(paletteEntry, key, color);
            }
            xpmHeader.palette.put(name, paletteEntry);
        }
    }

    private XpmHeader parseXpmHeader(BasicCParser cParser) throws ImageReadException, IOException {
        String token = cParser.nextToken();
        if (!"static".equals(token)) {
            throw new ImageReadException("Parsing XPM file failed, no 'static' token");
        }
        token = cParser.nextToken();
        if (!"char".equals(token)) {
            throw new ImageReadException("Parsing XPM file failed, no 'char' token");
        }
        token = cParser.nextToken();
        if (!"*".equals(token)) {
            throw new ImageReadException("Parsing XPM file failed, no '*' token");
        }
        String name = cParser.nextToken();
        if (name == null) {
            throw new ImageReadException("Parsing XPM file failed, no variable name");
        }
        if (name.charAt(0) != '_' && !Character.isLetter(name.charAt(0))) {
            throw new ImageReadException("Parsing XPM file failed, variable name doesn't start with letter or underscore");
        }
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') continue;
            throw new ImageReadException("Parsing XPM file failed, variable name contains non-letter non-digit non-underscore");
        }
        token = cParser.nextToken();
        if (!"[".equals(token)) {
            throw new ImageReadException("Parsing XPM file failed, no '[' token");
        }
        token = cParser.nextToken();
        if (!"]".equals(token)) {
            throw new ImageReadException("Parsing XPM file failed, no ']' token");
        }
        token = cParser.nextToken();
        if (!"=".equals(token)) {
            throw new ImageReadException("Parsing XPM file failed, no '=' token");
        }
        token = cParser.nextToken();
        if (!"{".equals(token)) {
            throw new ImageReadException("Parsing XPM file failed, no '{' token");
        }
        StringBuilder row = new StringBuilder();
        boolean hasMore = this.parseNextString(cParser, row);
        if (!hasMore) {
            throw new ImageReadException("Parsing XPM file failed, file too short");
        }
        XpmHeader xpmHeader = this.parseXpmValuesSection(row.toString());
        this.parsePaletteEntries(xpmHeader, cParser);
        return xpmHeader;
    }

    private BufferedImage readXpmImage(XpmHeader xpmHeader, BasicCParser cParser) throws ImageReadException, IOException {
        int bpp;
        WritableRaster raster;
        ColorModel colorModel;
        PaletteEntry paletteEntry;
        int[] palette;
        if (xpmHeader.palette.size() <= 256) {
            palette = new int[xpmHeader.palette.size()];
            for (Map.Entry<Object, PaletteEntry> entry : xpmHeader.palette.entrySet()) {
                paletteEntry = entry.getValue();
                palette[paletteEntry.index] = paletteEntry.getBestARGB();
            }
            colorModel = new IndexColorModel(8, xpmHeader.palette.size(), palette, 0, true, -1, 0);
            raster = Raster.createInterleavedRaster(0, xpmHeader.width, xpmHeader.height, 1, null);
            bpp = 8;
        } else if (xpmHeader.palette.size() <= 65536) {
            palette = new int[xpmHeader.palette.size()];
            for (Map.Entry<Object, PaletteEntry> entry : xpmHeader.palette.entrySet()) {
                paletteEntry = entry.getValue();
                palette[paletteEntry.index] = paletteEntry.getBestARGB();
            }
            colorModel = new IndexColorModel(16, xpmHeader.palette.size(), palette, 0, true, -1, 1);
            raster = Raster.createInterleavedRaster(1, xpmHeader.width, xpmHeader.height, 1, null);
            bpp = 16;
        } else {
            colorModel = new DirectColorModel(32, 0xFF0000, 65280, 255, -16777216);
            raster = Raster.createPackedRaster(3, xpmHeader.width, xpmHeader.height, new int[]{0xFF0000, 65280, 255, -16777216}, null);
            bpp = 32;
        }
        BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
        DataBuffer dataBuffer = raster.getDataBuffer();
        StringBuilder row = new StringBuilder();
        boolean hasMore = true;
        for (int y = 0; y < xpmHeader.height; ++y) {
            row.setLength(0);
            hasMore = this.parseNextString(cParser, row);
            if (y < xpmHeader.height - 1 && !hasMore) {
                throw new ImageReadException("Parsing XPM file failed, insufficient image rows in file");
            }
            int rowOffset = y * xpmHeader.width;
            for (int x = 0; x < xpmHeader.width; ++x) {
                String index = row.substring(x * xpmHeader.numCharsPerPixel, (x + 1) * xpmHeader.numCharsPerPixel);
                PaletteEntry paletteEntry2 = xpmHeader.palette.get(index);
                if (paletteEntry2 == null) {
                    throw new ImageReadException("No palette entry was defined for " + index);
                }
                if (bpp <= 16) {
                    dataBuffer.setElem(rowOffset + x, paletteEntry2.index);
                    continue;
                }
                dataBuffer.setElem(rowOffset + x, paletteEntry2.getBestARGB());
            }
        }
        while (hasMore) {
            row.setLength(0);
            hasMore = this.parseNextString(cParser, row);
        }
        String token = cParser.nextToken();
        if (!";".equals(token)) {
            throw new ImageReadException("Last token wasn't ';'");
        }
        return image;
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        this.readXpmHeader(byteSource).dump(pw);
        return true;
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        XpmParseResult result = this.parseXpmHeader(byteSource);
        return this.readXpmImage(result.xpmHeader, result.cParser);
    }

    private String randomName() {
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

    private String pixelsForIndex(int index, int charsPerPixel) {
        int i;
        StringBuilder stringBuilder = new StringBuilder();
        int highestPower = 1;
        for (i = 1; i < charsPerPixel; ++i) {
            highestPower *= WRITE_PALETTE.length;
        }
        for (i = 0; i < charsPerPixel; ++i) {
            int multiple = index / highestPower;
            index -= multiple * highestPower;
            highestPower /= WRITE_PALETTE.length;
            stringBuilder.append(WRITE_PALETTE[multiple]);
        }
        return stringBuilder.toString();
    }

    private String toColor(int color) {
        String hex = Integer.toHexString(color);
        if (hex.length() < 6) {
            char[] zeroes = new char[6 - hex.length()];
            Arrays.fill(zeroes, '0');
            return "#" + new String(zeroes) + hex;
        }
        return "#" + hex;
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
        PaletteFactory paletteFactory = new PaletteFactory();
        boolean hasTransparency = false;
        if (paletteFactory.hasTransparency(src, 1)) {
            hasTransparency = true;
        }
        SimplePalette palette = null;
        int maxColors = WRITE_PALETTE.length;
        int charsPerPixel = 1;
        while (palette == null) {
            palette = paletteFactory.makeExactRgbPaletteSimple(src, hasTransparency ? maxColors - 1 : maxColors);
            long nextMaxColors = maxColors * WRITE_PALETTE.length;
            long nextCharsPerPixel = charsPerPixel + 1;
            if (nextMaxColors > Integer.MAX_VALUE) {
                throw new ImageWriteException("Xpm: Can't write images with more than Integer.MAX_VALUE colors.");
            }
            if (nextCharsPerPixel > Integer.MAX_VALUE) {
                throw new ImageWriteException("Xpm: Can't write images with more than Integer.MAX_VALUE chars per pixel.");
            }
            if (palette != null) continue;
            maxColors *= WRITE_PALETTE.length;
            ++charsPerPixel;
        }
        int colors = palette.length();
        if (hasTransparency) {
            ++colors;
        }
        String line = "/* XPM */\n";
        os.write(line.getBytes(StandardCharsets.US_ASCII));
        line = "static char *" + this.randomName() + "[] = {\n";
        os.write(line.getBytes(StandardCharsets.US_ASCII));
        line = "\"" + src.getWidth() + " " + src.getHeight() + " " + colors + " " + charsPerPixel + "\",\n";
        os.write(line.getBytes(StandardCharsets.US_ASCII));
        for (int i = 0; i < colors; ++i) {
            String color = i < palette.length() ? this.toColor(palette.getEntry(i)) : "None";
            line = "\"" + this.pixelsForIndex(i, charsPerPixel) + " c " + color + "\",\n";
            os.write(line.getBytes(StandardCharsets.US_ASCII));
        }
        String separator = "";
        for (int y = 0; y < src.getHeight(); ++y) {
            os.write(separator.getBytes(StandardCharsets.US_ASCII));
            separator = ",\n";
            line = "\"";
            os.write(line.getBytes(StandardCharsets.US_ASCII));
            for (int x = 0; x < src.getWidth(); ++x) {
                int argb = src.getRGB(x, y);
                line = (argb & 0xFF000000) == 0 ? this.pixelsForIndex(palette.length(), charsPerPixel) : this.pixelsForIndex(palette.getPaletteIndex(0xFFFFFF & argb), charsPerPixel);
                os.write(line.getBytes(StandardCharsets.US_ASCII));
            }
            line = "\"";
            os.write(line.getBytes(StandardCharsets.US_ASCII));
        }
        line = "\n};\n";
        os.write(line.getBytes(StandardCharsets.US_ASCII));
    }

    static {
        WRITE_PALETTE = new char[]{' ', '.', 'X', 'o', 'O', '+', '@', '#', '$', '%', '&', '*', '=', '-', ';', ':', '>', ',', '<', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'M', 'N', 'B', 'V', 'C', 'Z', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'P', 'I', 'U', 'Y', 'T', 'R', 'E', 'W', 'Q', '!', '~', '^', '/', '(', ')', '_', '`', '\'', ']', '[', '{', '}', '|'};
    }

    private static class XpmParseResult {
        XpmHeader xpmHeader;
        BasicCParser cParser;

        private XpmParseResult() {
        }
    }

    private static class PaletteEntry {
        int index;
        boolean haveColor = false;
        int colorArgb;
        boolean haveGray = false;
        int grayArgb;
        boolean haveGray4Level = false;
        int gray4LevelArgb;
        boolean haveMono = false;
        int monoArgb;

        private PaletteEntry() {
        }

        int getBestARGB() {
            if (this.haveColor) {
                return this.colorArgb;
            }
            if (this.haveGray) {
                return this.grayArgb;
            }
            if (this.haveGray4Level) {
                return this.gray4LevelArgb;
            }
            if (this.haveMono) {
                return this.monoArgb;
            }
            return 0;
        }
    }

    private static class XpmHeader {
        int width;
        int height;
        int numColors;
        int numCharsPerPixel;
        int xHotSpot = -1;
        int yHotSpot = -1;
        boolean xpmExt;
        Map<Object, PaletteEntry> palette = new HashMap<Object, PaletteEntry>();

        XpmHeader(int width, int height, int numColors, int numCharsPerPixel, int xHotSpot, int yHotSpot, boolean xpmExt) {
            this.width = width;
            this.height = height;
            this.numColors = numColors;
            this.numCharsPerPixel = numCharsPerPixel;
            this.xHotSpot = xHotSpot;
            this.yHotSpot = yHotSpot;
            this.xpmExt = xpmExt;
        }

        public void dump(PrintWriter pw) {
            pw.println("XpmHeader");
            pw.println("Width: " + this.width);
            pw.println("Height: " + this.height);
            pw.println("NumColors: " + this.numColors);
            pw.println("NumCharsPerPixel: " + this.numCharsPerPixel);
            if (this.xHotSpot != -1 && this.yHotSpot != -1) {
                pw.println("X hotspot: " + this.xHotSpot);
                pw.println("Y hotspot: " + this.yHotSpot);
            }
            pw.println("XpmExt: " + this.xpmExt);
        }
    }
}

