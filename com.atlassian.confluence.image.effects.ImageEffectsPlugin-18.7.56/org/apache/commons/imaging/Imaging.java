/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import java.awt.Dimension;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.imaging.FormatCompliance;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.XmpEmbeddable;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.commons.imaging.icc.IccProfileInfo;
import org.apache.commons.imaging.icc.IccProfileParser;

public final class Imaging {
    private static final int[] MAGIC_NUMBERS_GIF = new int[]{71, 73};
    private static final int[] MAGIC_NUMBERS_PNG = new int[]{137, 80};
    private static final int[] MAGIC_NUMBERS_JPEG = new int[]{255, 216};
    private static final int[] MAGIC_NUMBERS_BMP = new int[]{66, 77};
    private static final int[] MAGIC_NUMBERS_TIFF_MOTOROLA = new int[]{77, 77};
    private static final int[] MAGIC_NUMBERS_TIFF_INTEL = new int[]{73, 73};
    private static final int[] MAGIC_NUMBERS_PAM = new int[]{80, 55};
    private static final int[] MAGIC_NUMBERS_PSD = new int[]{56, 66};
    private static final int[] MAGIC_NUMBERS_PBM_A = new int[]{80, 49};
    private static final int[] MAGIC_NUMBERS_PBM_B = new int[]{80, 52};
    private static final int[] MAGIC_NUMBERS_PGM_A = new int[]{80, 50};
    private static final int[] MAGIC_NUMBERS_PGM_B = new int[]{80, 53};
    private static final int[] MAGIC_NUMBERS_PPM_A = new int[]{80, 51};
    private static final int[] MAGIC_NUMBERS_PPM_B = new int[]{80, 54};
    private static final int[] MAGIC_NUMBERS_JBIG2_1 = new int[]{151, 74};
    private static final int[] MAGIC_NUMBERS_JBIG2_2 = new int[]{66, 50};
    private static final int[] MAGIC_NUMBERS_ICNS = new int[]{105, 99};
    private static final int[] MAGIC_NUMBERS_DCX = new int[]{177, 104};
    private static final int[] MAGIC_NUMBERS_RGBE = new int[]{35, 63};

    private Imaging() {
    }

    public static boolean hasImageFileExtension(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        return Imaging.hasImageFileExtension(file.getName());
    }

    public static boolean hasImageFileExtension(String fileName) {
        if (fileName == null) {
            return false;
        }
        String normalizedFilename = fileName.toLowerCase(Locale.ENGLISH);
        for (ImageParser imageParser : ImageParser.getAllImageParsers()) {
            for (String extension : imageParser.getAcceptedExtensions()) {
                if (!normalizedFilename.endsWith(extension.toLowerCase(Locale.ENGLISH))) continue;
                return true;
            }
        }
        return false;
    }

    public static ImageFormat guessFormat(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.guessFormat(new ByteSourceArray(bytes));
    }

    public static ImageFormat guessFormat(File file) throws ImageReadException, IOException {
        return Imaging.guessFormat(new ByteSourceFile(file));
    }

    private static boolean compareBytePair(int[] a, int[] b) {
        if (a.length != 2 && b.length != 2) {
            throw new RuntimeException("Invalid Byte Pair.");
        }
        return a[0] == b[0] && a[1] == b[1];
    }

    public static ImageFormat guessFormat(ByteSource byteSource) throws ImageReadException, IOException {
        if (byteSource == null) {
            return ImageFormats.UNKNOWN;
        }
        try (InputStream is = byteSource.getInputStream();){
            int i1 = is.read();
            int i2 = is.read();
            if (i1 < 0 || i2 < 0) {
                throw new ImageReadException("Couldn't read magic numbers to guess format.");
            }
            int b1 = i1 & 0xFF;
            int b2 = i2 & 0xFF;
            int[] bytePair = new int[]{b1, b2};
            if (Imaging.compareBytePair(MAGIC_NUMBERS_GIF, bytePair)) {
                ImageFormats imageFormats = ImageFormats.GIF;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PNG, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PNG;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_JPEG, bytePair)) {
                ImageFormats imageFormats = ImageFormats.JPEG;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_BMP, bytePair)) {
                ImageFormats imageFormats = ImageFormats.BMP;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_TIFF_MOTOROLA, bytePair)) {
                ImageFormats imageFormats = ImageFormats.TIFF;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_TIFF_INTEL, bytePair)) {
                ImageFormats imageFormats = ImageFormats.TIFF;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PSD, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PSD;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PAM, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PAM;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PBM_A, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PBM;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PBM_B, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PBM;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PGM_A, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PGM;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PGM_B, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PGM;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PPM_A, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PPM;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_PPM_B, bytePair)) {
                ImageFormats imageFormats = ImageFormats.PPM;
                return imageFormats;
            }
            if (Imaging.compareBytePair(MAGIC_NUMBERS_JBIG2_1, bytePair)) {
                int i3 = is.read();
                int i4 = is.read();
                if (i3 < 0 || i4 < 0) {
                    throw new ImageReadException("Couldn't read magic numbers to guess format.");
                }
                int b3 = i3 & 0xFF;
                int b4 = i4 & 0xFF;
                int[] bytePair2 = new int[]{b3, b4};
                if (Imaging.compareBytePair(MAGIC_NUMBERS_JBIG2_2, bytePair2)) {
                    ImageFormats imageFormats = ImageFormats.JBIG2;
                    return imageFormats;
                }
            } else {
                if (Imaging.compareBytePair(MAGIC_NUMBERS_ICNS, bytePair)) {
                    ImageFormats imageFormats = ImageFormats.ICNS;
                    return imageFormats;
                }
                if (Imaging.compareBytePair(MAGIC_NUMBERS_DCX, bytePair)) {
                    ImageFormats imageFormats = ImageFormats.DCX;
                    return imageFormats;
                }
                if (Imaging.compareBytePair(MAGIC_NUMBERS_RGBE, bytePair)) {
                    ImageFormats imageFormats = ImageFormats.RGBE;
                    return imageFormats;
                }
            }
            ImageFormats imageFormats = ImageFormats.UNKNOWN;
            return imageFormats;
        }
    }

    public static ICC_Profile getICCProfile(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getICCProfile(bytes, null);
    }

    public static ICC_Profile getICCProfile(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getICCProfile(new ByteSourceArray(bytes), params);
    }

    public static ICC_Profile getICCProfile(InputStream is, String fileName) throws ImageReadException, IOException {
        return Imaging.getICCProfile(is, fileName, null);
    }

    public static ICC_Profile getICCProfile(InputStream is, String fileName, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getICCProfile(new ByteSourceInputStream(is, fileName), params);
    }

    public static ICC_Profile getICCProfile(File file) throws ImageReadException, IOException {
        return Imaging.getICCProfile(file, null);
    }

    public static ICC_Profile getICCProfile(File file, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getICCProfile(new ByteSourceFile(file), params);
    }

    protected static ICC_Profile getICCProfile(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        byte[] bytes = Imaging.getICCProfileBytes(byteSource, params);
        if (bytes == null) {
            return null;
        }
        IccProfileParser parser = new IccProfileParser();
        IccProfileInfo info = parser.getICCProfileInfo(bytes);
        if (info == null) {
            return null;
        }
        if (info.issRGB()) {
            return null;
        }
        return ICC_Profile.getInstance(bytes);
    }

    public static byte[] getICCProfileBytes(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getICCProfileBytes(bytes, null);
    }

    public static byte[] getICCProfileBytes(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getICCProfileBytes(new ByteSourceArray(bytes), params);
    }

    public static byte[] getICCProfileBytes(File file) throws ImageReadException, IOException {
        return Imaging.getICCProfileBytes(file, null);
    }

    public static byte[] getICCProfileBytes(File file, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getICCProfileBytes(new ByteSourceFile(file), params);
    }

    private static byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        return imageParser.getICCProfileBytes(byteSource, params);
    }

    public static ImageInfo getImageInfo(String fileName, byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageInfo(new ByteSourceArray(fileName, bytes), params);
    }

    public static ImageInfo getImageInfo(String fileName, byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getImageInfo(new ByteSourceArray(fileName, bytes), null);
    }

    public static ImageInfo getImageInfo(InputStream is, String fileName) throws ImageReadException, IOException {
        return Imaging.getImageInfo(new ByteSourceInputStream(is, fileName), null);
    }

    public static ImageInfo getImageInfo(InputStream is, String fileName, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageInfo(new ByteSourceInputStream(is, fileName), params);
    }

    public static ImageInfo getImageInfo(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getImageInfo(new ByteSourceArray(bytes), null);
    }

    public static ImageInfo getImageInfo(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageInfo(new ByteSourceArray(bytes), params);
    }

    public static ImageInfo getImageInfo(File file, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageInfo(new ByteSourceFile(file), params);
    }

    public static ImageInfo getImageInfo(File file) throws ImageReadException, IOException {
        return Imaging.getImageInfo(file, null);
    }

    private static ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageParser(byteSource).getImageInfo(byteSource, params);
    }

    private static ImageParser getImageParser(ByteSource byteSource) throws ImageReadException, IOException {
        String fileName;
        ImageFormat format = Imaging.guessFormat(byteSource);
        if (!format.equals(ImageFormats.UNKNOWN)) {
            ImageParser[] imageParsers;
            for (ImageParser imageParser : imageParsers = ImageParser.getAllImageParsers()) {
                if (!imageParser.canAcceptType(format)) continue;
                return imageParser;
            }
        }
        if ((fileName = byteSource.getFileName()) != null) {
            ImageParser[] imageParsers;
            for (ImageParser imageParser : imageParsers = ImageParser.getAllImageParsers()) {
                if (!imageParser.canAcceptExtension(fileName)) continue;
                return imageParser;
            }
        }
        throw new ImageReadException("Can't parse this format.");
    }

    public static Dimension getImageSize(InputStream is, String fileName) throws ImageReadException, IOException {
        return Imaging.getImageSize(is, fileName, null);
    }

    public static Dimension getImageSize(InputStream is, String fileName, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageSize(new ByteSourceInputStream(is, fileName), params);
    }

    public static Dimension getImageSize(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getImageSize(bytes, null);
    }

    public static Dimension getImageSize(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageSize(new ByteSourceArray(bytes), params);
    }

    public static Dimension getImageSize(File file) throws ImageReadException, IOException {
        return Imaging.getImageSize(file, null);
    }

    public static Dimension getImageSize(File file, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getImageSize(new ByteSourceFile(file), params);
    }

    public static Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        return imageParser.getImageSize(byteSource, params);
    }

    public static String getXmpXml(InputStream is, String fileName) throws ImageReadException, IOException {
        return Imaging.getXmpXml(is, fileName, null);
    }

    public static String getXmpXml(InputStream is, String fileName, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getXmpXml(new ByteSourceInputStream(is, fileName), params);
    }

    public static String getXmpXml(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getXmpXml(bytes, null);
    }

    public static String getXmpXml(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getXmpXml(new ByteSourceArray(bytes), params);
    }

    public static String getXmpXml(File file) throws ImageReadException, IOException {
        return Imaging.getXmpXml(file, null);
    }

    public static String getXmpXml(File file, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getXmpXml(new ByteSourceFile(file), params);
    }

    public static String getXmpXml(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        if (imageParser instanceof XmpEmbeddable) {
            return ((XmpEmbeddable)((Object)imageParser)).getXmpXml(byteSource, params);
        }
        return null;
    }

    public static ImageMetadata getMetadata(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getMetadata(bytes, null);
    }

    public static ImageMetadata getMetadata(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getMetadata(new ByteSourceArray(bytes), params);
    }

    public static ImageMetadata getMetadata(InputStream is, String fileName) throws ImageReadException, IOException {
        return Imaging.getMetadata(is, fileName, null);
    }

    public static ImageMetadata getMetadata(InputStream is, String fileName, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getMetadata(new ByteSourceInputStream(is, fileName), params);
    }

    public static ImageMetadata getMetadata(File file) throws ImageReadException, IOException {
        return Imaging.getMetadata(file, null);
    }

    public static ImageMetadata getMetadata(File file, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getMetadata(new ByteSourceFile(file), params);
    }

    private static ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        return imageParser.getMetadata(byteSource, params);
    }

    public static String dumpImageFile(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.dumpImageFile(new ByteSourceArray(bytes));
    }

    public static String dumpImageFile(File file) throws ImageReadException, IOException {
        return Imaging.dumpImageFile(new ByteSourceFile(file));
    }

    private static String dumpImageFile(ByteSource byteSource) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        return imageParser.dumpImageFile(byteSource);
    }

    public static FormatCompliance getFormatCompliance(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getFormatCompliance(new ByteSourceArray(bytes));
    }

    public static FormatCompliance getFormatCompliance(File file) throws ImageReadException, IOException {
        return Imaging.getFormatCompliance(new ByteSourceFile(file));
    }

    private static FormatCompliance getFormatCompliance(ByteSource byteSource) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        return imageParser.getFormatCompliance(byteSource);
    }

    public static List<BufferedImage> getAllBufferedImages(InputStream is, String fileName) throws ImageReadException, IOException {
        return Imaging.getAllBufferedImages(new ByteSourceInputStream(is, fileName));
    }

    public static List<BufferedImage> getAllBufferedImages(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getAllBufferedImages(new ByteSourceArray(bytes));
    }

    public static List<BufferedImage> getAllBufferedImages(File file) throws ImageReadException, IOException {
        return Imaging.getAllBufferedImages(new ByteSourceFile(file));
    }

    private static List<BufferedImage> getAllBufferedImages(ByteSource byteSource) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        return imageParser.getAllBufferedImages(byteSource);
    }

    public static BufferedImage getBufferedImage(InputStream is) throws ImageReadException, IOException {
        return Imaging.getBufferedImage(is, null);
    }

    public static BufferedImage getBufferedImage(InputStream is, Map<String, Object> params) throws ImageReadException, IOException {
        String fileName = null;
        if (params != null && params.containsKey("FILENAME")) {
            fileName = (String)params.get("FILENAME");
        }
        return Imaging.getBufferedImage(new ByteSourceInputStream(is, fileName), params);
    }

    public static BufferedImage getBufferedImage(byte[] bytes) throws ImageReadException, IOException {
        return Imaging.getBufferedImage(new ByteSourceArray(bytes), null);
    }

    public static BufferedImage getBufferedImage(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getBufferedImage(new ByteSourceArray(bytes), params);
    }

    public static BufferedImage getBufferedImage(File file) throws ImageReadException, IOException {
        return Imaging.getBufferedImage(new ByteSourceFile(file), null);
    }

    public static BufferedImage getBufferedImage(File file, Map<String, Object> params) throws ImageReadException, IOException {
        return Imaging.getBufferedImage(new ByteSourceFile(file), params);
    }

    private static BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageParser imageParser = Imaging.getImageParser(byteSource);
        if (null == params) {
            params = new HashMap<String, Object>();
        }
        return imageParser.getBufferedImage(byteSource, params);
    }

    public static void writeImage(BufferedImage src, File file, ImageFormat format, Map<String, Object> params) throws ImageWriteException, IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream os = new BufferedOutputStream(fos);){
            Imaging.writeImage(src, os, format, params);
        }
    }

    public static byte[] writeImageToBytes(BufferedImage src, ImageFormat format, Map<String, Object> params) throws ImageWriteException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Imaging.writeImage(src, os, format, params);
        return os.toByteArray();
    }

    public static void writeImage(BufferedImage src, OutputStream os, ImageFormat format, Map<String, Object> params) throws ImageWriteException, IOException {
        ImageParser[] imageParsers = ImageParser.getAllImageParsers();
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put("FORMAT", format);
        ImageParser imageParser = null;
        for (ImageParser imageParser2 : imageParsers) {
            if (!imageParser2.canAcceptType(format)) continue;
            imageParser = imageParser2;
            break;
        }
        if (imageParser == null) {
            throw new ImageWriteException("Unknown Format: " + format);
        }
        imageParser.writeImage(src, os, params);
    }
}

