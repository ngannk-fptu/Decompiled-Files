/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pcx;

import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.pcx.PcxWriter;
import org.apache.commons.imaging.formats.pcx.RleReader;

public class PcxImageParser
extends ImageParser {
    private static final String DEFAULT_EXTENSION = ".pcx";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".pcx", ".pcc"};

    public PcxImageParser() {
        super.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public String getName() {
        return "Pcx-Custom";
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
        return new ImageFormat[]{ImageFormats.PCX};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        PcxHeader pcxHeader = this.readPcxHeader(byteSource);
        Dimension size = this.getImageSize(byteSource, params);
        return new ImageInfo("PCX", pcxHeader.nPlanes * pcxHeader.bitsPerPixel, new ArrayList<String>(), ImageFormats.PCX, "ZSoft PCX Image", size.height, "image/x-pcx", 1, pcxHeader.vDpi, Math.round(size.getHeight() / (double)pcxHeader.vDpi), pcxHeader.hDpi, Math.round(size.getWidth() / (double)pcxHeader.hDpi), size.width, false, false, pcxHeader.nPlanes != 3 || pcxHeader.bitsPerPixel != 8, ImageInfo.ColorType.RGB, pcxHeader.encoding == 1 ? ImageInfo.CompressionAlgorithm.RLE : ImageInfo.CompressionAlgorithm.NONE);
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        PcxHeader pcxHeader = this.readPcxHeader(byteSource);
        int xSize = pcxHeader.xMax - pcxHeader.xMin + 1;
        if (xSize < 0) {
            throw new ImageReadException("Image width is negative");
        }
        int ySize = pcxHeader.yMax - pcxHeader.yMin + 1;
        if (ySize < 0) {
            throw new ImageReadException("Image height is negative");
        }
        return new Dimension(xSize, ySize);
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private PcxHeader readPcxHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            PcxHeader pcxHeader = this.readPcxHeader(is, false);
            return pcxHeader;
        }
    }

    private PcxHeader readPcxHeader(InputStream is, boolean isStrict) throws ImageReadException, IOException {
        byte[] pcxHeaderBytes = BinaryFunctions.readBytes("PcxHeader", is, 128, "Not a Valid PCX File");
        int manufacturer = 0xFF & pcxHeaderBytes[0];
        int version = 0xFF & pcxHeaderBytes[1];
        int encoding = 0xFF & pcxHeaderBytes[2];
        int bitsPerPixel = 0xFF & pcxHeaderBytes[3];
        int xMin = ByteConversions.toUInt16(pcxHeaderBytes, 4, this.getByteOrder());
        int yMin = ByteConversions.toUInt16(pcxHeaderBytes, 6, this.getByteOrder());
        int xMax = ByteConversions.toUInt16(pcxHeaderBytes, 8, this.getByteOrder());
        int yMax = ByteConversions.toUInt16(pcxHeaderBytes, 10, this.getByteOrder());
        int hDpi = ByteConversions.toUInt16(pcxHeaderBytes, 12, this.getByteOrder());
        int vDpi = ByteConversions.toUInt16(pcxHeaderBytes, 14, this.getByteOrder());
        int[] colormap = new int[16];
        for (int i = 0; i < 16; ++i) {
            colormap[i] = 0xFF000000 | (0xFF & pcxHeaderBytes[16 + 3 * i]) << 16 | (0xFF & pcxHeaderBytes[16 + 3 * i + 1]) << 8 | 0xFF & pcxHeaderBytes[16 + 3 * i + 2];
        }
        int reserved = 0xFF & pcxHeaderBytes[64];
        int nPlanes = 0xFF & pcxHeaderBytes[65];
        int bytesPerLine = ByteConversions.toUInt16(pcxHeaderBytes, 66, this.getByteOrder());
        int paletteInfo = ByteConversions.toUInt16(pcxHeaderBytes, 68, this.getByteOrder());
        int hScreenSize = ByteConversions.toUInt16(pcxHeaderBytes, 70, this.getByteOrder());
        int vScreenSize = ByteConversions.toUInt16(pcxHeaderBytes, 72, this.getByteOrder());
        if (manufacturer != 10) {
            throw new ImageReadException("Not a Valid PCX File: manufacturer is " + manufacturer);
        }
        if (isStrict && bytesPerLine % 2 != 0) {
            throw new ImageReadException("Not a Valid PCX File: bytesPerLine is odd");
        }
        return new PcxHeader(manufacturer, version, encoding, bitsPerPixel, xMin, yMin, xMax, yMax, hDpi, vDpi, colormap, reserved, nPlanes, bytesPerLine, paletteInfo, hScreenSize, vScreenSize);
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        this.readPcxHeader(byteSource).dump(pw);
        return true;
    }

    private int[] read256ColorPalette(InputStream stream) throws IOException {
        byte[] paletteBytes = BinaryFunctions.readBytes("Palette", stream, 769, "Error reading palette");
        if (paletteBytes[0] != 12) {
            return null;
        }
        int[] palette = new int[256];
        for (int i = 0; i < palette.length; ++i) {
            palette[i] = (0xFF & paletteBytes[1 + 3 * i]) << 16 | (0xFF & paletteBytes[1 + 3 * i + 1]) << 8 | 0xFF & paletteBytes[1 + 3 * i + 2];
        }
        return palette;
    }

    private int[] read256ColorPaletteFromEndOfFile(ByteSource byteSource) throws IOException {
        try (InputStream stream = byteSource.getInputStream();){
            int[] ret;
            long toSkip = byteSource.getLength() - 769L;
            BinaryFunctions.skipBytes(stream, (int)toSkip);
            int[] nArray = ret = this.read256ColorPalette(stream);
            return nArray;
        }
    }

    private BufferedImage readImage(PcxHeader pcxHeader, InputStream is, ByteSource byteSource) throws ImageReadException, IOException {
        RleReader rleReader;
        int xSize = pcxHeader.xMax - pcxHeader.xMin + 1;
        if (xSize < 0) {
            throw new ImageReadException("Image width is negative");
        }
        int ySize = pcxHeader.yMax - pcxHeader.yMin + 1;
        if (ySize < 0) {
            throw new ImageReadException("Image height is negative");
        }
        if (pcxHeader.nPlanes <= 0 || 4 < pcxHeader.nPlanes) {
            throw new ImageReadException("Unsupported/invalid image with " + pcxHeader.nPlanes + " planes");
        }
        if (pcxHeader.encoding == 0) {
            rleReader = new RleReader(false);
        } else if (pcxHeader.encoding == 1) {
            rleReader = new RleReader(true);
        } else {
            throw new ImageReadException("Unsupported/invalid image encoding " + pcxHeader.encoding);
        }
        int scanlineLength = pcxHeader.bytesPerLine * pcxHeader.nPlanes;
        byte[] scanline = new byte[scanlineLength];
        if ((pcxHeader.bitsPerPixel == 1 || pcxHeader.bitsPerPixel == 2 || pcxHeader.bitsPerPixel == 4 || pcxHeader.bitsPerPixel == 8) && pcxHeader.nPlanes == 1) {
            int[] palette;
            int bytesPerImageRow = (xSize * pcxHeader.bitsPerPixel + 7) / 8;
            byte[] image = new byte[ySize * bytesPerImageRow];
            for (int y = 0; y < ySize; ++y) {
                rleReader.read(is, scanline);
                System.arraycopy(scanline, 0, image, y * bytesPerImageRow, bytesPerImageRow);
            }
            DataBufferByte dataBuffer = new DataBufferByte(image, image.length);
            if (pcxHeader.bitsPerPixel == 1) {
                palette = new int[]{0, 0xFFFFFF};
            } else if (pcxHeader.bitsPerPixel == 8) {
                palette = this.read256ColorPalette(is);
                if (palette == null) {
                    palette = this.read256ColorPaletteFromEndOfFile(byteSource);
                }
                if (palette == null) {
                    throw new ImageReadException("No 256 color palette found in image that needs it");
                }
            } else {
                palette = pcxHeader.colormap;
            }
            WritableRaster raster = pcxHeader.bitsPerPixel == 8 ? Raster.createInterleavedRaster(dataBuffer, xSize, ySize, bytesPerImageRow, 1, new int[]{0}, null) : Raster.createPackedRaster(dataBuffer, xSize, ySize, pcxHeader.bitsPerPixel, null);
            IndexColorModel colorModel = new IndexColorModel(pcxHeader.bitsPerPixel, 1 << pcxHeader.bitsPerPixel, palette, 0, false, -1, 0);
            return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
        }
        if (pcxHeader.bitsPerPixel == 1 && 2 <= pcxHeader.nPlanes && pcxHeader.nPlanes <= 4) {
            IndexColorModel colorModel = new IndexColorModel(pcxHeader.nPlanes, 1 << pcxHeader.nPlanes, pcxHeader.colormap, 0, false, -1, 0);
            BufferedImage image = new BufferedImage(xSize, ySize, 12, colorModel);
            byte[] unpacked = new byte[xSize];
            for (int y = 0; y < ySize; ++y) {
                rleReader.read(is, scanline);
                int nextByte = 0;
                Arrays.fill(unpacked, (byte)0);
                for (int plane = 0; plane < pcxHeader.nPlanes; ++plane) {
                    for (int i = 0; i < pcxHeader.bytesPerLine; ++i) {
                        int b = 0xFF & scanline[nextByte++];
                        for (int j = 0; j < 8 && 8 * i + j < unpacked.length; ++j) {
                            int n = 8 * i + j;
                            unpacked[n] = (byte)(unpacked[n] | (byte)((b >> 7 - j & 1) << plane));
                        }
                    }
                }
                image.getRaster().setDataElements(0, y, xSize, 1, unpacked);
            }
            return image;
        }
        if (pcxHeader.bitsPerPixel == 8 && pcxHeader.nPlanes == 3) {
            byte[][] image = new byte[][]{new byte[xSize * ySize], new byte[xSize * ySize], new byte[xSize * ySize]};
            for (int y = 0; y < ySize; ++y) {
                rleReader.read(is, scanline);
                System.arraycopy(scanline, 0, image[0], y * xSize, xSize);
                System.arraycopy(scanline, pcxHeader.bytesPerLine, image[1], y * xSize, xSize);
                System.arraycopy(scanline, 2 * pcxHeader.bytesPerLine, image[2], y * xSize, xSize);
            }
            DataBufferByte dataBuffer = new DataBufferByte(image, image[0].length);
            WritableRaster raster = Raster.createBandedRaster(dataBuffer, xSize, ySize, xSize, new int[]{0, 1, 2}, new int[]{0, 0, 0}, null);
            ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(1000), false, false, 1, 0);
            return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
        }
        if (pcxHeader.bitsPerPixel == 24 && pcxHeader.nPlanes == 1 || pcxHeader.bitsPerPixel == 32 && pcxHeader.nPlanes == 1) {
            int rowLength = 3 * xSize;
            byte[] image = new byte[rowLength * ySize];
            for (int y = 0; y < ySize; ++y) {
                rleReader.read(is, scanline);
                if (pcxHeader.bitsPerPixel == 24) {
                    System.arraycopy(scanline, 0, image, y * rowLength, rowLength);
                    continue;
                }
                for (int x = 0; x < xSize; ++x) {
                    image[y * rowLength + 3 * x] = scanline[4 * x];
                    image[y * rowLength + 3 * x + 1] = scanline[4 * x + 1];
                    image[y * rowLength + 3 * x + 2] = scanline[4 * x + 2];
                }
            }
            DataBufferByte dataBuffer = new DataBufferByte(image, image.length);
            WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, xSize, ySize, rowLength, 3, new int[]{2, 1, 0}, null);
            ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(1000), false, false, 1, 0);
            return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
        }
        throw new ImageReadException("Invalid/unsupported image with bitsPerPixel " + pcxHeader.bitsPerPixel + " and planes " + pcxHeader.nPlanes);
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        boolean isStrict = false;
        Object strictness = params.get("STRICT");
        if (strictness != null) {
            isStrict = (Boolean)strictness;
        }
        try (InputStream is = byteSource.getInputStream();){
            BufferedImage ret;
            PcxHeader pcxHeader = this.readPcxHeader(is, isStrict);
            BufferedImage bufferedImage = ret = this.readImage(pcxHeader, is, byteSource);
            return bufferedImage;
        }
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        new PcxWriter(params).writeImage(src, os);
    }

    static class PcxHeader {
        public static final int ENCODING_UNCOMPRESSED = 0;
        public static final int ENCODING_RLE = 1;
        public static final int PALETTE_INFO_COLOR = 1;
        public static final int PALETTE_INFO_GRAYSCALE = 2;
        public final int manufacturer;
        public final int version;
        public final int encoding;
        public final int bitsPerPixel;
        public final int xMin;
        public final int yMin;
        public final int xMax;
        public final int yMax;
        public final int hDpi;
        public final int vDpi;
        public final int[] colormap;
        public final int reserved;
        public final int nPlanes;
        public final int bytesPerLine;
        public final int paletteInfo;
        public final int hScreenSize;
        public final int vScreenSize;

        PcxHeader(int manufacturer, int version, int encoding, int bitsPerPixel, int xMin, int yMin, int xMax, int yMax, int hDpi, int vDpi, int[] colormap, int reserved, int nPlanes, int bytesPerLine, int paletteInfo, int hScreenSize, int vScreenSize) {
            this.manufacturer = manufacturer;
            this.version = version;
            this.encoding = encoding;
            this.bitsPerPixel = bitsPerPixel;
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
            this.hDpi = hDpi;
            this.vDpi = vDpi;
            this.colormap = colormap;
            this.reserved = reserved;
            this.nPlanes = nPlanes;
            this.bytesPerLine = bytesPerLine;
            this.paletteInfo = paletteInfo;
            this.hScreenSize = hScreenSize;
            this.vScreenSize = vScreenSize;
        }

        public void dump(PrintWriter pw) {
            pw.println("PcxHeader");
            pw.println("Manufacturer: " + this.manufacturer);
            pw.println("Version: " + this.version);
            pw.println("Encoding: " + this.encoding);
            pw.println("BitsPerPixel: " + this.bitsPerPixel);
            pw.println("xMin: " + this.xMin);
            pw.println("yMin: " + this.yMin);
            pw.println("xMax: " + this.xMax);
            pw.println("yMax: " + this.yMax);
            pw.println("hDpi: " + this.hDpi);
            pw.println("vDpi: " + this.vDpi);
            pw.print("ColorMap: ");
            for (int i = 0; i < this.colormap.length; ++i) {
                if (i > 0) {
                    pw.print(",");
                }
                pw.print("(" + (0xFF & this.colormap[i] >> 16) + "," + (0xFF & this.colormap[i] >> 8) + "," + (0xFF & this.colormap[i]) + ")");
            }
            pw.println();
            pw.println("Reserved: " + this.reserved);
            pw.println("nPlanes: " + this.nPlanes);
            pw.println("BytesPerLine: " + this.bytesPerLine);
            pw.println("PaletteInfo: " + this.paletteInfo);
            pw.println("hScreenSize: " + this.hScreenSize);
            pw.println("vScreenSize: " + this.vScreenSize);
            pw.println();
        }
    }
}

