/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.ico;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.PixelDensity;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.bmp.BmpImageParser;
import org.apache.commons.imaging.palette.PaletteFactory;
import org.apache.commons.imaging.palette.SimplePalette;

public class IcoImageParser
extends ImageParser {
    private static final String DEFAULT_EXTENSION = ".ico";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".ico", ".cur"};

    public IcoImageParser() {
        super.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public String getName() {
        return "ico-Custom";
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
        return new ImageFormat[]{ImageFormats.ICO};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private FileHeader readFileHeader(InputStream is) throws ImageReadException, IOException {
        int reserved = BinaryFunctions.read2Bytes("Reserved", is, "Not a Valid ICO File", this.getByteOrder());
        int iconType = BinaryFunctions.read2Bytes("IconType", is, "Not a Valid ICO File", this.getByteOrder());
        int iconCount = BinaryFunctions.read2Bytes("IconCount", is, "Not a Valid ICO File", this.getByteOrder());
        if (reserved != 0) {
            throw new ImageReadException("Not a Valid ICO File: reserved is " + reserved);
        }
        if (iconType != 1 && iconType != 2) {
            throw new ImageReadException("Not a Valid ICO File: icon type is " + iconType);
        }
        return new FileHeader(reserved, iconType, iconCount);
    }

    private IconInfo readIconInfo(InputStream is) throws IOException {
        byte width = BinaryFunctions.readByte("Width", is, "Not a Valid ICO File");
        byte height = BinaryFunctions.readByte("Height", is, "Not a Valid ICO File");
        byte colorCount = BinaryFunctions.readByte("ColorCount", is, "Not a Valid ICO File");
        byte reserved = BinaryFunctions.readByte("Reserved", is, "Not a Valid ICO File");
        int planes = BinaryFunctions.read2Bytes("Planes", is, "Not a Valid ICO File", this.getByteOrder());
        int bitCount = BinaryFunctions.read2Bytes("BitCount", is, "Not a Valid ICO File", this.getByteOrder());
        int imageSize = BinaryFunctions.read4Bytes("ImageSize", is, "Not a Valid ICO File", this.getByteOrder());
        int imageOffset = BinaryFunctions.read4Bytes("ImageOffset", is, "Not a Valid ICO File", this.getByteOrder());
        return new IconInfo(width, height, colorCount, reserved, planes, bitCount, imageSize, imageOffset);
    }

    private IconData readBitmapIconData(byte[] iconData, IconInfo fIconInfo) throws ImageReadException, IOException {
        BufferedImage resultImage;
        byte[] transparencyMap;
        int t_scanline_size;
        BufferedImage bmpImage;
        BitmapHeader header;
        int bitCount;
        block28: {
            ByteArrayInputStream is = new ByteArrayInputStream(iconData);
            int size = BinaryFunctions.read4Bytes("size", is, "Not a Valid ICO File", this.getByteOrder());
            int width = BinaryFunctions.read4Bytes("width", is, "Not a Valid ICO File", this.getByteOrder());
            int height = BinaryFunctions.read4Bytes("height", is, "Not a Valid ICO File", this.getByteOrder());
            int planes = BinaryFunctions.read2Bytes("planes", is, "Not a Valid ICO File", this.getByteOrder());
            bitCount = BinaryFunctions.read2Bytes("bitCount", is, "Not a Valid ICO File", this.getByteOrder());
            int compression = BinaryFunctions.read4Bytes("compression", is, "Not a Valid ICO File", this.getByteOrder());
            int sizeImage = BinaryFunctions.read4Bytes("sizeImage", is, "Not a Valid ICO File", this.getByteOrder());
            int xPelsPerMeter = BinaryFunctions.read4Bytes("xPelsPerMeter", is, "Not a Valid ICO File", this.getByteOrder());
            int yPelsPerMeter = BinaryFunctions.read4Bytes("yPelsPerMeter", is, "Not a Valid ICO File", this.getByteOrder());
            int colorsUsed = BinaryFunctions.read4Bytes("colorsUsed", is, "Not a Valid ICO File", this.getByteOrder());
            int colorsImportant = BinaryFunctions.read4Bytes("ColorsImportant", is, "Not a Valid ICO File", this.getByteOrder());
            int redMask = 0;
            int greenMask = 0;
            int blueMask = 0;
            int alphaMask = 0;
            if (compression == 3) {
                redMask = BinaryFunctions.read4Bytes("redMask", is, "Not a Valid ICO File", this.getByteOrder());
                greenMask = BinaryFunctions.read4Bytes("greenMask", is, "Not a Valid ICO File", this.getByteOrder());
                blueMask = BinaryFunctions.read4Bytes("blueMask", is, "Not a Valid ICO File", this.getByteOrder());
            }
            byte[] restOfFile = BinaryFunctions.readBytes("RestOfFile", is, is.available());
            if (size != 40) {
                throw new ImageReadException("Not a Valid ICO File: Wrong bitmap header size " + size);
            }
            if (planes != 1) {
                throw new ImageReadException("Not a Valid ICO File: Planes can't be " + planes);
            }
            if (compression == 0 && bitCount == 32) {
                compression = 3;
                redMask = 0xFF0000;
                greenMask = 65280;
                blueMask = 255;
                alphaMask = -16777216;
            }
            header = new BitmapHeader(size, width, height, planes, bitCount, compression, sizeImage, xPelsPerMeter, yPelsPerMeter, colorsUsed, colorsImportant);
            int bitmapPixelsOffset = 70 + 4 * (colorsUsed == 0 && bitCount <= 8 ? 1 << bitCount : colorsUsed);
            int bitmapSize = 70 + restOfFile.length;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bitmapSize);
            try (BinaryOutputStream bos = new BinaryOutputStream(baos, ByteOrder.LITTLE_ENDIAN);){
                bos.write(66);
                bos.write(77);
                bos.write4Bytes(bitmapSize);
                bos.write4Bytes(0);
                bos.write4Bytes(bitmapPixelsOffset);
                bos.write4Bytes(56);
                bos.write4Bytes(width);
                bos.write4Bytes(height / 2);
                bos.write2Bytes(planes);
                bos.write2Bytes(bitCount);
                bos.write4Bytes(compression);
                bos.write4Bytes(sizeImage);
                bos.write4Bytes(xPelsPerMeter);
                bos.write4Bytes(yPelsPerMeter);
                bos.write4Bytes(colorsUsed);
                bos.write4Bytes(colorsImportant);
                bos.write4Bytes(redMask);
                bos.write4Bytes(greenMask);
                bos.write4Bytes(blueMask);
                bos.write4Bytes(alphaMask);
                bos.write(restOfFile);
                bos.flush();
            }
            ByteArrayInputStream bmpInputStream = new ByteArrayInputStream(baos.toByteArray());
            bmpImage = new BmpImageParser().getBufferedImage(bmpInputStream, null);
            t_scanline_size = (width + 7) / 8;
            if (t_scanline_size % 4 != 0) {
                t_scanline_size += 4 - t_scanline_size % 4;
            }
            int colorMapSizeBytes = t_scanline_size * (height / 2);
            transparencyMap = null;
            try {
                transparencyMap = BinaryFunctions.readBytes("transparency_map", bmpInputStream, colorMapSizeBytes, "Not a Valid ICO File");
            }
            catch (IOException ioEx) {
                if (bitCount == 32) break block28;
                throw ioEx;
            }
        }
        boolean allAlphasZero = true;
        if (bitCount == 32) {
            block11: for (int y = 0; allAlphasZero && y < bmpImage.getHeight(); ++y) {
                for (int x = 0; x < bmpImage.getWidth(); ++x) {
                    if ((bmpImage.getRGB(x, y) & 0xFF000000) == 0) continue;
                    allAlphasZero = false;
                    continue block11;
                }
            }
        }
        if (allAlphasZero) {
            resultImage = new BufferedImage(bmpImage.getWidth(), bmpImage.getHeight(), 2);
            for (int y = 0; y < resultImage.getHeight(); ++y) {
                for (int x = 0; x < resultImage.getWidth(); ++x) {
                    int alpha = 255;
                    if (transparencyMap != null) {
                        int alphaByte = 0xFF & transparencyMap[t_scanline_size * (bmpImage.getHeight() - y - 1) + x / 8];
                        alpha = 1 & alphaByte >> 7 - x % 8;
                        alpha = alpha == 0 ? 255 : 0;
                    }
                    resultImage.setRGB(x, y, alpha << 24 | 0xFFFFFF & bmpImage.getRGB(x, y));
                }
            }
        } else {
            resultImage = bmpImage;
        }
        return new BitmapIconData(fIconInfo, header, resultImage);
    }

    private IconData readIconData(byte[] iconData, IconInfo fIconInfo) throws ImageReadException, IOException {
        ImageFormat imageFormat = Imaging.guessFormat(iconData);
        if (imageFormat.equals(ImageFormats.PNG)) {
            BufferedImage bufferedImage = Imaging.getBufferedImage(iconData);
            return new PNGIconData(fIconInfo, bufferedImage);
        }
        return this.readBitmapIconData(iconData, fIconInfo);
    }

    private ImageContents readImage(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            ImageContents ret;
            FileHeader fileHeader = this.readFileHeader(is);
            IconInfo[] fIconInfos = new IconInfo[fileHeader.iconCount];
            for (int i = 0; i < fileHeader.iconCount; ++i) {
                fIconInfos[i] = this.readIconInfo(is);
            }
            IconData[] fIconDatas = new IconData[fileHeader.iconCount];
            for (int i = 0; i < fileHeader.iconCount; ++i) {
                byte[] iconData = byteSource.getBlock(fIconInfos[i].imageOffset, fIconInfos[i].imageSize);
                fIconDatas[i] = this.readIconData(iconData, fIconInfos[i]);
            }
            ImageContents imageContents = ret = new ImageContents(fileHeader, fIconDatas);
            return imageContents;
        }
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        ImageContents contents = this.readImage(byteSource);
        contents.fileHeader.dump(pw);
        for (IconData iconData : contents.iconDatas) {
            iconData.dump(pw);
        }
        return true;
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageContents contents = this.readImage(byteSource);
        FileHeader fileHeader = contents.fileHeader;
        if (fileHeader.iconCount > 0) {
            return contents.iconDatas[0].readBufferedImage();
        }
        throw new ImageReadException("No icons in ICO file");
    }

    @Override
    public List<BufferedImage> getAllBufferedImages(ByteSource byteSource) throws ImageReadException, IOException {
        ImageContents contents = this.readImage(byteSource);
        FileHeader fileHeader = contents.fileHeader;
        ArrayList<BufferedImage> result = new ArrayList<BufferedImage>(fileHeader.iconCount);
        for (int i = 0; i < fileHeader.iconCount; ++i) {
            IconData iconData = contents.iconDatas[i];
            BufferedImage image = iconData.readBufferedImage();
            result.add(image);
        }
        return result;
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        int t_scanline_size;
        boolean hasTransparency;
        Map<String, Object> map = params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        if (params.containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        PixelDensity pixelDensity = (PixelDensity)params.remove("PIXEL_DENSITY");
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        PaletteFactory paletteFactory = new PaletteFactory();
        SimplePalette palette = paletteFactory.makeExactRgbPaletteSimple(src, 256);
        int bitCount = palette == null ? ((hasTransparency = paletteFactory.hasTransparency(src)) ? 32 : 24) : (palette.length() <= 2 ? 1 : (palette.length() <= 16 ? 4 : 8));
        BinaryOutputStream bos = new BinaryOutputStream(os, ByteOrder.LITTLE_ENDIAN);
        int scanline_size = (bitCount * src.getWidth() + 7) / 8;
        if (scanline_size % 4 != 0) {
            scanline_size += 4 - scanline_size % 4;
        }
        if ((t_scanline_size = (src.getWidth() + 7) / 8) % 4 != 0) {
            t_scanline_size += 4 - t_scanline_size % 4;
        }
        int imageSize = 40 + 4 * (bitCount <= 8 ? 1 << bitCount : 0) + src.getHeight() * scanline_size + src.getHeight() * t_scanline_size;
        bos.write2Bytes(0);
        bos.write2Bytes(1);
        bos.write2Bytes(1);
        int iconDirEntryWidth = src.getWidth();
        int iconDirEntryHeight = src.getHeight();
        if (iconDirEntryWidth > 255 || iconDirEntryHeight > 255) {
            iconDirEntryWidth = 0;
            iconDirEntryHeight = 0;
        }
        bos.write(iconDirEntryWidth);
        bos.write(iconDirEntryHeight);
        bos.write(bitCount >= 8 ? 0 : 1 << bitCount);
        bos.write(0);
        bos.write2Bytes(1);
        bos.write2Bytes(bitCount);
        bos.write4Bytes(imageSize);
        bos.write4Bytes(22);
        bos.write4Bytes(40);
        bos.write4Bytes(src.getWidth());
        bos.write4Bytes(2 * src.getHeight());
        bos.write2Bytes(1);
        bos.write2Bytes(bitCount);
        bos.write4Bytes(0);
        bos.write4Bytes(0);
        bos.write4Bytes(pixelDensity == null ? 0 : (int)Math.round(pixelDensity.horizontalDensityMetres()));
        bos.write4Bytes(pixelDensity == null ? 0 : (int)Math.round(pixelDensity.horizontalDensityMetres()));
        bos.write4Bytes(0);
        bos.write4Bytes(0);
        if (palette != null) {
            for (int i = 0; i < 1 << bitCount; ++i) {
                if (i < palette.length()) {
                    int argb = palette.getEntry(i);
                    bos.write3Bytes(argb);
                    bos.write(0);
                    continue;
                }
                bos.write4Bytes(0);
            }
        }
        int bitCache = 0;
        int bitsInCache = 0;
        int rowPadding = scanline_size - (bitCount * src.getWidth() + 7) / 8;
        for (int y = src.getHeight() - 1; y >= 0; --y) {
            int x;
            for (x = 0; x < src.getWidth(); ++x) {
                int index;
                int rgb;
                int argb = src.getRGB(x, y);
                if (palette == null) {
                    if (bitCount == 24) {
                        bos.write3Bytes(argb);
                        continue;
                    }
                    if (bitCount != 32) continue;
                    bos.write4Bytes(argb);
                    continue;
                }
                if (bitCount < 8) {
                    rgb = 0xFFFFFF & argb;
                    index = palette.getPaletteIndex(rgb);
                    bitCache <<= bitCount;
                    bitCache |= index;
                    if ((bitsInCache += bitCount) < 8) continue;
                    bos.write(0xFF & bitCache);
                    bitCache = 0;
                    bitsInCache = 0;
                    continue;
                }
                if (bitCount != 8) continue;
                rgb = 0xFFFFFF & argb;
                index = palette.getPaletteIndex(rgb);
                bos.write(0xFF & index);
            }
            if (bitsInCache > 0) {
                bos.write(0xFF & (bitCache <<= 8 - bitsInCache));
                bitCache = 0;
                bitsInCache = 0;
            }
            for (x = 0; x < rowPadding; ++x) {
                bos.write(0);
            }
        }
        int t_row_padding = t_scanline_size - (src.getWidth() + 7) / 8;
        for (int y = src.getHeight() - 1; y >= 0; --y) {
            int x;
            for (x = 0; x < src.getWidth(); ++x) {
                int argb = src.getRGB(x, y);
                int alpha = 0xFF & argb >> 24;
                bitCache <<= 1;
                if (alpha == 0) {
                    bitCache |= 1;
                }
                if (++bitsInCache < 8) continue;
                bos.write(0xFF & bitCache);
                bitCache = 0;
                bitsInCache = 0;
            }
            if (bitsInCache > 0) {
                bos.write(0xFF & (bitCache <<= 8 - bitsInCache));
                bitCache = 0;
                bitsInCache = 0;
            }
            for (x = 0; x < t_row_padding; ++x) {
                bos.write(0);
            }
        }
        bos.close();
    }

    private static class ImageContents {
        public final FileHeader fileHeader;
        public final IconData[] iconDatas;

        ImageContents(FileHeader fileHeader, IconData[] iconDatas) {
            this.fileHeader = fileHeader;
            this.iconDatas = iconDatas;
        }
    }

    private static class PNGIconData
    extends IconData {
        public final BufferedImage bufferedImage;

        PNGIconData(IconInfo iconInfo, BufferedImage bufferedImage) {
            super(iconInfo);
            this.bufferedImage = bufferedImage;
        }

        @Override
        public BufferedImage readBufferedImage() {
            return this.bufferedImage;
        }

        @Override
        protected void dumpSubclass(PrintWriter pw) {
            pw.println("PNGIconData");
            pw.println();
        }
    }

    private static class BitmapIconData
    extends IconData {
        public final BitmapHeader header;
        public final BufferedImage bufferedImage;

        BitmapIconData(IconInfo iconInfo, BitmapHeader header, BufferedImage bufferedImage) {
            super(iconInfo);
            this.header = header;
            this.bufferedImage = bufferedImage;
        }

        @Override
        public BufferedImage readBufferedImage() throws ImageReadException {
            return this.bufferedImage;
        }

        @Override
        protected void dumpSubclass(PrintWriter pw) {
            pw.println("BitmapIconData");
            this.header.dump(pw);
            pw.println();
        }
    }

    private static abstract class IconData {
        public final IconInfo iconInfo;

        IconData(IconInfo iconInfo) {
            this.iconInfo = iconInfo;
        }

        public void dump(PrintWriter pw) {
            this.iconInfo.dump(pw);
            pw.println();
            this.dumpSubclass(pw);
        }

        protected abstract void dumpSubclass(PrintWriter var1);

        public abstract BufferedImage readBufferedImage() throws ImageReadException;
    }

    private static class BitmapHeader {
        public final int size;
        public final int width;
        public final int height;
        public final int planes;
        public final int bitCount;
        public final int compression;
        public final int sizeImage;
        public final int xPelsPerMeter;
        public final int yPelsPerMeter;
        public final int colorsUsed;
        public final int colorsImportant;

        BitmapHeader(int size, int width, int height, int planes, int bitCount, int compression, int sizeImage, int pelsPerMeter, int pelsPerMeter2, int colorsUsed, int colorsImportant) {
            this.size = size;
            this.width = width;
            this.height = height;
            this.planes = planes;
            this.bitCount = bitCount;
            this.compression = compression;
            this.sizeImage = sizeImage;
            this.xPelsPerMeter = pelsPerMeter;
            this.yPelsPerMeter = pelsPerMeter2;
            this.colorsUsed = colorsUsed;
            this.colorsImportant = colorsImportant;
        }

        public void dump(PrintWriter pw) {
            pw.println("BitmapHeader");
            pw.println("Size: " + this.size);
            pw.println("Width: " + this.width);
            pw.println("Height: " + this.height);
            pw.println("Planes: " + this.planes);
            pw.println("BitCount: " + this.bitCount);
            pw.println("Compression: " + this.compression);
            pw.println("SizeImage: " + this.sizeImage);
            pw.println("XPelsPerMeter: " + this.xPelsPerMeter);
            pw.println("YPelsPerMeter: " + this.yPelsPerMeter);
            pw.println("ColorsUsed: " + this.colorsUsed);
            pw.println("ColorsImportant: " + this.colorsImportant);
        }
    }

    private static class IconInfo {
        public final byte width;
        public final byte height;
        public final byte colorCount;
        public final byte reserved;
        public final int planes;
        public final int bitCount;
        public final int imageSize;
        public final int imageOffset;

        IconInfo(byte width, byte height, byte colorCount, byte reserved, int planes, int bitCount, int imageSize, int imageOffset) {
            this.width = width;
            this.height = height;
            this.colorCount = colorCount;
            this.reserved = reserved;
            this.planes = planes;
            this.bitCount = bitCount;
            this.imageSize = imageSize;
            this.imageOffset = imageOffset;
        }

        public void dump(PrintWriter pw) {
            pw.println("IconInfo");
            pw.println("Width: " + this.width);
            pw.println("Height: " + this.height);
            pw.println("ColorCount: " + this.colorCount);
            pw.println("Reserved: " + this.reserved);
            pw.println("Planes: " + this.planes);
            pw.println("BitCount: " + this.bitCount);
            pw.println("ImageSize: " + this.imageSize);
            pw.println("ImageOffset: " + this.imageOffset);
        }
    }

    private static class FileHeader {
        public final int reserved;
        public final int iconType;
        public final int iconCount;

        FileHeader(int reserved, int iconType, int iconCount) {
            this.reserved = reserved;
            this.iconType = iconType;
            this.iconCount = iconCount;
        }

        public void dump(PrintWriter pw) {
            pw.println("FileHeader");
            pw.println("Reserved: " + this.reserved);
            pw.println("IconType: " + this.iconType);
            pw.println("IconCount: " + this.iconCount);
            pw.println();
        }
    }
}

