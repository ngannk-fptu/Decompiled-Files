/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pnm;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.pnm.FileInfo;
import org.apache.commons.imaging.formats.pnm.PamFileInfo;
import org.apache.commons.imaging.formats.pnm.PamWriter;
import org.apache.commons.imaging.formats.pnm.PbmFileInfo;
import org.apache.commons.imaging.formats.pnm.PbmWriter;
import org.apache.commons.imaging.formats.pnm.PgmFileInfo;
import org.apache.commons.imaging.formats.pnm.PgmWriter;
import org.apache.commons.imaging.formats.pnm.PnmWriter;
import org.apache.commons.imaging.formats.pnm.PpmFileInfo;
import org.apache.commons.imaging.formats.pnm.PpmWriter;
import org.apache.commons.imaging.formats.pnm.WhiteSpaceReader;
import org.apache.commons.imaging.palette.PaletteFactory;

public class PnmImageParser
extends ImageParser {
    private static final String DEFAULT_EXTENSION = ".pnm";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".pbm", ".pgm", ".ppm", ".pnm", ".pam"};
    public static final String PARAM_KEY_PNM_RAWBITS = "PNM_RAWBITS";
    public static final String PARAM_VALUE_PNM_RAWBITS_YES = "YES";
    public static final String PARAM_VALUE_PNM_RAWBITS_NO = "NO";

    public PnmImageParser() {
        super.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public String getName() {
        return "Pbm-Custom";
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
        return new ImageFormat[]{ImageFormats.PBM, ImageFormats.PGM, ImageFormats.PPM, ImageFormats.PNM, ImageFormats.PAM};
    }

    private FileInfo readHeader(InputStream is) throws ImageReadException, IOException {
        byte identifier1 = BinaryFunctions.readByte("Identifier1", is, "Not a Valid PNM File");
        byte identifier2 = BinaryFunctions.readByte("Identifier2", is, "Not a Valid PNM File");
        if (identifier1 != 80) {
            throw new ImageReadException("PNM file has invalid prefix byte 1");
        }
        WhiteSpaceReader wsr = new WhiteSpaceReader(is);
        if (identifier2 == 49 || identifier2 == 52 || identifier2 == 50 || identifier2 == 53 || identifier2 == 51 || identifier2 == 54) {
            int height;
            int width;
            try {
                width = Integer.parseInt(wsr.readtoWhiteSpace());
            }
            catch (NumberFormatException e) {
                throw new ImageReadException("Invalid width specified.", e);
            }
            try {
                height = Integer.parseInt(wsr.readtoWhiteSpace());
            }
            catch (NumberFormatException e) {
                throw new ImageReadException("Invalid height specified.", e);
            }
            if (identifier2 == 49) {
                return new PbmFileInfo(width, height, false);
            }
            if (identifier2 == 52) {
                return new PbmFileInfo(width, height, true);
            }
            if (identifier2 == 50) {
                int maxgray = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PgmFileInfo(width, height, false, maxgray);
            }
            if (identifier2 == 53) {
                int maxgray = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PgmFileInfo(width, height, true, maxgray);
            }
            if (identifier2 == 51) {
                int max = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PpmFileInfo(width, height, false, max);
            }
            if (identifier2 == 54) {
                int max = Integer.parseInt(wsr.readtoWhiteSpace());
                return new PpmFileInfo(width, height, true, max);
            }
        } else if (identifier2 == 55) {
            String line;
            int width = -1;
            boolean seenWidth = false;
            int height = -1;
            boolean seenHeight = false;
            int depth = -1;
            boolean seenDepth = false;
            int maxVal = -1;
            boolean seenMaxVal = false;
            StringBuilder tupleType = new StringBuilder();
            boolean seenTupleType = false;
            wsr.readLine();
            while ((line = wsr.readLine()) != null) {
                if ((line = line.trim()).charAt(0) == '#') continue;
                StringTokenizer tokenizer = new StringTokenizer(line, " ", false);
                String type = tokenizer.nextToken();
                if ("WIDTH".equals(type)) {
                    seenWidth = true;
                    if (!tokenizer.hasMoreTokens()) {
                        throw new ImageReadException("PAM header has no WIDTH value");
                    }
                    width = Integer.parseInt(tokenizer.nextToken());
                    continue;
                }
                if ("HEIGHT".equals(type)) {
                    seenHeight = true;
                    if (!tokenizer.hasMoreTokens()) {
                        throw new ImageReadException("PAM header has no HEIGHT value");
                    }
                    height = Integer.parseInt(tokenizer.nextToken());
                    continue;
                }
                if ("DEPTH".equals(type)) {
                    seenDepth = true;
                    if (!tokenizer.hasMoreTokens()) {
                        throw new ImageReadException("PAM header has no DEPTH value");
                    }
                    depth = Integer.parseInt(tokenizer.nextToken());
                    continue;
                }
                if ("MAXVAL".equals(type)) {
                    seenMaxVal = true;
                    if (!tokenizer.hasMoreTokens()) {
                        throw new ImageReadException("PAM header has no MAXVAL value");
                    }
                    maxVal = Integer.parseInt(tokenizer.nextToken());
                    continue;
                }
                if ("TUPLTYPE".equals(type)) {
                    seenTupleType = true;
                    if (!tokenizer.hasMoreTokens()) {
                        throw new ImageReadException("PAM header has no TUPLTYPE value");
                    }
                    tupleType.append(tokenizer.nextToken());
                    continue;
                }
                if ("ENDHDR".equals(type)) break;
                throw new ImageReadException("Invalid PAM file header type " + type);
            }
            if (!seenWidth) {
                throw new ImageReadException("PAM header has no WIDTH");
            }
            if (!seenHeight) {
                throw new ImageReadException("PAM header has no HEIGHT");
            }
            if (!seenDepth) {
                throw new ImageReadException("PAM header has no DEPTH");
            }
            if (!seenMaxVal) {
                throw new ImageReadException("PAM header has no MAXVAL");
            }
            if (!seenTupleType) {
                throw new ImageReadException("PAM header has no TUPLTYPE");
            }
            return new PamFileInfo(width, height, depth, maxVal, tupleType.toString());
        }
        throw new ImageReadException("PNM file has invalid prefix byte 2");
    }

    private FileInfo readHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            FileInfo fileInfo = this.readHeader(is);
            return fileInfo;
        }
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FileInfo info = this.readHeader(byteSource);
        if (info == null) {
            throw new ImageReadException("PNM: Couldn't read Header");
        }
        return new Dimension(info.width, info.height);
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        FileInfo info = this.readHeader(byteSource);
        if (info == null) {
            throw new ImageReadException("PNM: Couldn't read Header");
        }
        ArrayList<String> comments = new ArrayList<String>();
        int bitsPerPixel = info.getBitDepth() * info.getNumComponents();
        ImageFormat format = info.getImageType();
        String formatName = info.getImageTypeDescription();
        String mimeType = info.getMIMEType();
        boolean numberOfImages = true;
        boolean progressive = false;
        int physicalWidthDpi = 72;
        float physicalWidthInch = (float)((double)info.width / 72.0);
        int physicalHeightDpi = 72;
        float physicalHeightInch = (float)((double)info.height / 72.0);
        String formatDetails = info.getImageTypeDescription();
        boolean transparent = info.hasAlpha();
        boolean usesPalette = false;
        ImageInfo.ColorType colorType = info.getColorType();
        ImageInfo.CompressionAlgorithm compressionAlgorithm = ImageInfo.CompressionAlgorithm.NONE;
        return new ImageInfo(formatDetails, bitsPerPixel, comments, format, formatName, info.height, mimeType, 1, 72, physicalHeightInch, 72, physicalWidthInch, info.width, false, transparent, false, colorType, compressionAlgorithm);
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        pw.println("pnm.dumpImageFile");
        ImageInfo imageData = this.getImageInfo(byteSource);
        if (imageData == null) {
            return false;
        }
        imageData.toString(pw, "");
        pw.println("");
        return true;
    }

    @Override
    public BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            BufferedImage ret;
            FileInfo info = this.readHeader(is);
            int width = info.width;
            int height = info.height;
            boolean hasAlpha = info.hasAlpha();
            ImageBuilder imageBuilder = new ImageBuilder(width, height, hasAlpha);
            info.readImage(imageBuilder, is);
            BufferedImage bufferedImage = ret = imageBuilder.getBufferedImage();
            return bufferedImage;
        }
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        PnmWriter writer = null;
        boolean useRawbits = true;
        if (params != null) {
            Object subtype;
            Object useRawbitsParam = params.get(PARAM_KEY_PNM_RAWBITS);
            if (useRawbitsParam != null && useRawbitsParam.equals(PARAM_VALUE_PNM_RAWBITS_NO)) {
                useRawbits = false;
            }
            if ((subtype = params.get("FORMAT")) != null) {
                if (subtype.equals(ImageFormats.PBM)) {
                    writer = new PbmWriter(useRawbits);
                } else if (subtype.equals(ImageFormats.PGM)) {
                    writer = new PgmWriter(useRawbits);
                } else if (subtype.equals(ImageFormats.PPM)) {
                    writer = new PpmWriter(useRawbits);
                } else if (subtype.equals(ImageFormats.PAM)) {
                    writer = new PamWriter();
                }
            }
        }
        if (writer == null) {
            boolean hasAlpha = new PaletteFactory().hasTransparency(src);
            writer = hasAlpha ? new PamWriter() : new PpmWriter(useRawbits);
        }
        if ((params = params != null ? new HashMap<String, Object>(params) : new HashMap<String, Object>()).containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        if (params.containsKey(PARAM_KEY_PNM_RAWBITS)) {
            params.remove(PARAM_KEY_PNM_RAWBITS);
        }
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        writer.writeImage(src, os, params);
    }
}

