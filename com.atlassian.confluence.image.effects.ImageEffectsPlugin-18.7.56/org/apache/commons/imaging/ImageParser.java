/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.FormatCompliance;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BufferedImageFactory;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.SimpleBufferedImageFactory;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.bmp.BmpImageParser;
import org.apache.commons.imaging.formats.dcx.DcxImageParser;
import org.apache.commons.imaging.formats.gif.GifImageParser;
import org.apache.commons.imaging.formats.icns.IcnsImageParser;
import org.apache.commons.imaging.formats.ico.IcoImageParser;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.pcx.PcxImageParser;
import org.apache.commons.imaging.formats.png.PngImageParser;
import org.apache.commons.imaging.formats.pnm.PnmImageParser;
import org.apache.commons.imaging.formats.psd.PsdImageParser;
import org.apache.commons.imaging.formats.rgbe.RgbeImageParser;
import org.apache.commons.imaging.formats.tiff.TiffImageParser;
import org.apache.commons.imaging.formats.wbmp.WbmpImageParser;
import org.apache.commons.imaging.formats.xbm.XbmImageParser;
import org.apache.commons.imaging.formats.xpm.XpmImageParser;

public abstract class ImageParser
extends BinaryFileParser {
    private static final Logger LOGGER = Logger.getLogger(ImageParser.class.getName());

    public static ImageParser[] getAllImageParsers() {
        return new ImageParser[]{new BmpImageParser(), new DcxImageParser(), new GifImageParser(), new IcnsImageParser(), new IcoImageParser(), new JpegImageParser(), new PcxImageParser(), new PngImageParser(), new PnmImageParser(), new PsdImageParser(), new RgbeImageParser(), new TiffImageParser(), new WbmpImageParser(), new XbmImageParser(), new XpmImageParser()};
    }

    public final ImageMetadata getMetadata(ByteSource byteSource) throws ImageReadException, IOException {
        return this.getMetadata(byteSource, null);
    }

    public abstract ImageMetadata getMetadata(ByteSource var1, Map<String, Object> var2) throws ImageReadException, IOException;

    public final ImageMetadata getMetadata(byte[] bytes) throws ImageReadException, IOException {
        return this.getMetadata(bytes, null);
    }

    public final ImageMetadata getMetadata(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return this.getMetadata(new ByteSourceArray(bytes), params);
    }

    public final ImageMetadata getMetadata(File file) throws ImageReadException, IOException {
        return this.getMetadata(file, null);
    }

    public final ImageMetadata getMetadata(File file, Map<String, Object> params) throws ImageReadException, IOException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(this.getName() + ".getMetadata: " + file.getName());
        }
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        return this.getMetadata(new ByteSourceFile(file), params);
    }

    public abstract ImageInfo getImageInfo(ByteSource var1, Map<String, Object> var2) throws ImageReadException, IOException;

    public final ImageInfo getImageInfo(ByteSource byteSource) throws ImageReadException, IOException {
        return this.getImageInfo(byteSource, null);
    }

    public final ImageInfo getImageInfo(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return this.getImageInfo(new ByteSourceArray(bytes), params);
    }

    public final ImageInfo getImageInfo(File file, Map<String, Object> params) throws ImageReadException, IOException {
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        return this.getImageInfo(new ByteSourceFile(file), params);
    }

    public FormatCompliance getFormatCompliance(ByteSource byteSource) throws ImageReadException, IOException {
        return null;
    }

    public final FormatCompliance getFormatCompliance(byte[] bytes) throws ImageReadException, IOException {
        return this.getFormatCompliance(new ByteSourceArray(bytes));
    }

    public final FormatCompliance getFormatCompliance(File file) throws ImageReadException, IOException {
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        return this.getFormatCompliance(new ByteSourceFile(file));
    }

    public List<BufferedImage> getAllBufferedImages(ByteSource byteSource) throws ImageReadException, IOException {
        BufferedImage bi = this.getBufferedImage(byteSource, null);
        ArrayList<BufferedImage> result = new ArrayList<BufferedImage>();
        result.add(bi);
        return result;
    }

    public final List<BufferedImage> getAllBufferedImages(byte[] bytes) throws ImageReadException, IOException {
        return this.getAllBufferedImages(new ByteSourceArray(bytes));
    }

    public final List<BufferedImage> getAllBufferedImages(File file) throws ImageReadException, IOException {
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        return this.getAllBufferedImages(new ByteSourceFile(file));
    }

    public abstract BufferedImage getBufferedImage(ByteSource var1, Map<String, Object> var2) throws ImageReadException, IOException;

    public final BufferedImage getBufferedImage(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return this.getBufferedImage(new ByteSourceArray(bytes), params);
    }

    public final BufferedImage getBufferedImage(File file, Map<String, Object> params) throws ImageReadException, IOException {
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        return this.getBufferedImage(new ByteSourceFile(file), params);
    }

    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        os.close();
        throw new ImageWriteException("This image format (" + this.getName() + ") cannot be written.");
    }

    public final Dimension getImageSize(byte[] bytes) throws ImageReadException, IOException {
        return this.getImageSize(bytes, null);
    }

    public final Dimension getImageSize(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return this.getImageSize(new ByteSourceArray(bytes), params);
    }

    public final Dimension getImageSize(File file) throws ImageReadException, IOException {
        return this.getImageSize(file, null);
    }

    public final Dimension getImageSize(File file, Map<String, Object> params) throws ImageReadException, IOException {
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        return this.getImageSize(new ByteSourceFile(file), params);
    }

    public abstract Dimension getImageSize(ByteSource var1, Map<String, Object> var2) throws ImageReadException, IOException;

    public final byte[] getICCProfileBytes(byte[] bytes) throws ImageReadException, IOException {
        return this.getICCProfileBytes(bytes, null);
    }

    public final byte[] getICCProfileBytes(byte[] bytes, Map<String, Object> params) throws ImageReadException, IOException {
        return this.getICCProfileBytes(new ByteSourceArray(bytes), params);
    }

    public final byte[] getICCProfileBytes(File file) throws ImageReadException, IOException {
        return this.getICCProfileBytes(file, null);
    }

    public final byte[] getICCProfileBytes(File file, Map<String, Object> params) throws ImageReadException, IOException {
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(this.getName() + ": " + file.getName());
        }
        return this.getICCProfileBytes(new ByteSourceFile(file), params);
    }

    public abstract byte[] getICCProfileBytes(ByteSource var1, Map<String, Object> var2) throws ImageReadException, IOException;

    public final String dumpImageFile(byte[] bytes) throws ImageReadException, IOException {
        return this.dumpImageFile(new ByteSourceArray(bytes));
    }

    public final String dumpImageFile(File file) throws ImageReadException, IOException {
        if (!this.canAcceptExtension(file)) {
            return null;
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(this.getName() + ": " + file.getName());
        }
        return this.dumpImageFile(new ByteSourceFile(file));
    }

    public final String dumpImageFile(ByteSource byteSource) throws ImageReadException, IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.dumpImageFile(pw, byteSource);
        pw.flush();
        return sw.toString();
    }

    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        return false;
    }

    public abstract String getName();

    public abstract String getDefaultExtension();

    protected abstract String[] getAcceptedExtensions();

    protected abstract ImageFormat[] getAcceptedTypes();

    public boolean canAcceptType(ImageFormat type) {
        ImageFormat[] types;
        for (ImageFormat type2 : types = this.getAcceptedTypes()) {
            if (!type2.equals(type)) continue;
            return true;
        }
        return false;
    }

    protected final boolean canAcceptExtension(File file) {
        return this.canAcceptExtension(file.getName());
    }

    protected final boolean canAcceptExtension(String fileName) {
        String[] exts = this.getAcceptedExtensions();
        if (exts == null) {
            return true;
        }
        int index = fileName.lastIndexOf(46);
        if (index >= 0) {
            String ext = fileName.substring(index);
            ext = ext.toLowerCase(Locale.ENGLISH);
            for (String ext2 : exts) {
                String ext2Lower = ext2.toLowerCase(Locale.ENGLISH);
                if (!ext2Lower.equals(ext)) continue;
                return true;
            }
        }
        return false;
    }

    protected BufferedImageFactory getBufferedImageFactory(Map<String, Object> params) {
        if (params == null) {
            return new SimpleBufferedImageFactory();
        }
        BufferedImageFactory result = (BufferedImageFactory)params.get("BUFFERED_IMAGE_FACTORY");
        if (null != result) {
            return result;
        }
        return new SimpleBufferedImageFactory();
    }

    public static boolean isStrict(Map<String, Object> params) {
        if (params == null || !params.containsKey("STRICT")) {
            return false;
        }
        return (Boolean)params.get("STRICT");
    }
}

