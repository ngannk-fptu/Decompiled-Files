/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pcx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.PixelDensity;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.formats.pcx.RleWriter;
import org.apache.commons.imaging.palette.PaletteFactory;
import org.apache.commons.imaging.palette.SimplePalette;

class PcxWriter {
    private int encoding;
    private int bitDepthWanted = -1;
    private int planesWanted = -1;
    private PixelDensity pixelDensity;
    private final RleWriter rleWriter;

    PcxWriter(Map<String, Object> params) throws ImageWriteException {
        Object value;
        Map<String, Object> map = params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        if (params.containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        this.encoding = 1;
        if (params.containsKey("PCX_COMPRESSION") && (value = params.remove("PCX_COMPRESSION")) != null) {
            if (!(value instanceof Number)) {
                throw new ImageWriteException("Invalid compression parameter: " + value);
            }
            int compression = ((Number)value).intValue();
            if (compression == 0) {
                this.encoding = 0;
            }
        }
        this.rleWriter = this.encoding == 0 ? new RleWriter(false) : new RleWriter(true);
        if (params.containsKey("PCX_BIT_DEPTH") && (value = params.remove("PCX_BIT_DEPTH")) != null) {
            if (!(value instanceof Number)) {
                throw new ImageWriteException("Invalid bit depth parameter: " + value);
            }
            this.bitDepthWanted = ((Number)value).intValue();
        }
        if (params.containsKey("PCX_PLANES") && (value = params.remove("PCX_PLANES")) != null) {
            if (!(value instanceof Number)) {
                throw new ImageWriteException("Invalid planes parameter: " + value);
            }
            this.planesWanted = ((Number)value).intValue();
        }
        if (params.containsKey("PIXEL_DENSITY") && (value = params.remove("PIXEL_DENSITY")) != null) {
            if (!(value instanceof PixelDensity)) {
                throw new ImageWriteException("Invalid pixel density parameter");
            }
            this.pixelDensity = (PixelDensity)value;
        }
        if (this.pixelDensity == null) {
            this.pixelDensity = PixelDensity.createFromPixelsPerInch(72.0, 72.0);
        }
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
    }

    public void writeImage(BufferedImage src, OutputStream os) throws ImageWriteException, IOException {
        int rgb;
        int i;
        int planes;
        int bitDepth;
        PaletteFactory paletteFactory = new PaletteFactory();
        SimplePalette palette = paletteFactory.makeExactRgbPaletteSimple(src, 256);
        BinaryOutputStream bos = new BinaryOutputStream(os, ByteOrder.LITTLE_ENDIAN);
        if (palette == null || this.bitDepthWanted == 24 || this.bitDepthWanted == 32) {
            if (this.bitDepthWanted == 32) {
                bitDepth = 32;
                planes = 1;
            } else {
                bitDepth = 8;
                planes = 3;
            }
        } else if (palette.length() > 16 || this.bitDepthWanted == 8) {
            bitDepth = 8;
            planes = 1;
        } else if (palette.length() > 8 || this.bitDepthWanted == 4) {
            if (this.planesWanted == 1) {
                bitDepth = 4;
                planes = 1;
            } else {
                bitDepth = 1;
                planes = 4;
            }
        } else if (palette.length() > 4 || this.bitDepthWanted == 3) {
            bitDepth = 1;
            planes = 3;
        } else if (palette.length() > 2 || this.bitDepthWanted == 2) {
            if (this.planesWanted == 2) {
                bitDepth = 1;
                planes = 2;
            } else {
                bitDepth = 2;
                planes = 1;
            }
        } else {
            int rgb2;
            boolean onlyBlackAndWhite = true;
            if (palette.length() >= 1 && (rgb2 = palette.getEntry(0)) != 0 && rgb2 != 0xFFFFFF) {
                onlyBlackAndWhite = false;
            }
            if (palette.length() == 2 && (rgb2 = palette.getEntry(1)) != 0 && rgb2 != 0xFFFFFF) {
                onlyBlackAndWhite = false;
            }
            if (onlyBlackAndWhite) {
                bitDepth = 1;
                planes = 1;
            } else {
                bitDepth = 1;
                planes = 2;
            }
        }
        int bytesPerLine = (bitDepth * src.getWidth() + 7) / 8;
        if (bytesPerLine % 2 != 0) {
            ++bytesPerLine;
        }
        byte[] palette16 = new byte[48];
        for (i = 0; i < 16; ++i) {
            rgb = i < palette.length() ? palette.getEntry(i) : 0;
            palette16[3 * i + 0] = (byte)(0xFF & rgb >> 16);
            palette16[3 * i + 1] = (byte)(0xFF & rgb >> 8);
            palette16[3 * i + 2] = (byte)(0xFF & rgb);
        }
        bos.write(10);
        bos.write(bitDepth == 1 && planes == 1 ? 3 : 5);
        bos.write(this.encoding);
        bos.write(bitDepth);
        bos.write2Bytes(0);
        bos.write2Bytes(0);
        bos.write2Bytes(src.getWidth() - 1);
        bos.write2Bytes(src.getHeight() - 1);
        bos.write2Bytes((short)Math.round(this.pixelDensity.horizontalDensityInches()));
        bos.write2Bytes((short)Math.round(this.pixelDensity.verticalDensityInches()));
        bos.write(palette16);
        bos.write(0);
        bos.write(planes);
        bos.write2Bytes(bytesPerLine);
        bos.write2Bytes(1);
        bos.write2Bytes(0);
        bos.write2Bytes(0);
        bos.write(new byte[54]);
        if (bitDepth == 32) {
            this.writePixels32(src, bytesPerLine, bos);
        } else {
            this.writePixels(src, bitDepth, planes, bytesPerLine, palette, bos);
        }
        if (bitDepth == 8 && planes == 1) {
            bos.write(12);
            for (i = 0; i < 256; ++i) {
                rgb = i < palette.length() ? palette.getEntry(i) : 0;
                bos.write(rgb >> 16 & 0xFF);
                bos.write(rgb >> 8 & 0xFF);
                bos.write(rgb & 0xFF);
            }
        }
    }

    private void writePixels(BufferedImage src, int bitDepth, int planes, int bytesPerLine, SimplePalette palette, BinaryOutputStream bos) throws IOException, ImageWriteException {
        byte[] plane0 = new byte[bytesPerLine];
        byte[] plane1 = new byte[bytesPerLine];
        byte[] plane2 = new byte[bytesPerLine];
        byte[] plane3 = new byte[bytesPerLine];
        byte[][] allPlanes = new byte[][]{plane0, plane1, plane2, plane3};
        for (int y = 0; y < src.getHeight(); ++y) {
            int index;
            int argb;
            int x;
            int i;
            for (i = 0; i < planes; ++i) {
                Arrays.fill(allPlanes[i], (byte)0);
            }
            if (bitDepth == 1 && planes == 1) {
                for (x = 0; x < src.getWidth(); ++x) {
                    int rgb = 0xFFFFFF & src.getRGB(x, y);
                    int bit = rgb == 0 ? 0 : 1;
                    int n = x >>> 3;
                    plane0[n] = (byte)(plane0[n] | bit << 7 - (x & 7));
                }
            } else if (bitDepth == 1 && planes == 2) {
                for (x = 0; x < src.getWidth(); ++x) {
                    argb = src.getRGB(x, y);
                    index = palette.getPaletteIndex(0xFFFFFF & argb);
                    int n = x >>> 3;
                    plane0[n] = (byte)(plane0[n] | (index & 1) << 7 - (x & 7));
                    int n2 = x >>> 3;
                    plane1[n2] = (byte)(plane1[n2] | (index & 2) >> 1 << 7 - (x & 7));
                }
            } else if (bitDepth == 1 && planes == 3) {
                for (x = 0; x < src.getWidth(); ++x) {
                    argb = src.getRGB(x, y);
                    index = palette.getPaletteIndex(0xFFFFFF & argb);
                    int n = x >>> 3;
                    plane0[n] = (byte)(plane0[n] | (index & 1) << 7 - (x & 7));
                    int n3 = x >>> 3;
                    plane1[n3] = (byte)(plane1[n3] | (index & 2) >> 1 << 7 - (x & 7));
                    int n4 = x >>> 3;
                    plane2[n4] = (byte)(plane2[n4] | (index & 4) >> 2 << 7 - (x & 7));
                }
            } else if (bitDepth == 1 && planes == 4) {
                for (x = 0; x < src.getWidth(); ++x) {
                    argb = src.getRGB(x, y);
                    index = palette.getPaletteIndex(0xFFFFFF & argb);
                    int n = x >>> 3;
                    plane0[n] = (byte)(plane0[n] | (index & 1) << 7 - (x & 7));
                    int n5 = x >>> 3;
                    plane1[n5] = (byte)(plane1[n5] | (index & 2) >> 1 << 7 - (x & 7));
                    int n6 = x >>> 3;
                    plane2[n6] = (byte)(plane2[n6] | (index & 4) >> 2 << 7 - (x & 7));
                    int n7 = x >>> 3;
                    plane3[n7] = (byte)(plane3[n7] | (index & 8) >> 3 << 7 - (x & 7));
                }
            } else if (bitDepth == 2 && planes == 1) {
                for (x = 0; x < src.getWidth(); ++x) {
                    argb = src.getRGB(x, y);
                    index = palette.getPaletteIndex(0xFFFFFF & argb);
                    int n = x >>> 2;
                    plane0[n] = (byte)(plane0[n] | index << 2 * (3 - (x & 3)));
                }
            } else if (bitDepth == 4 && planes == 1) {
                for (x = 0; x < src.getWidth(); ++x) {
                    argb = src.getRGB(x, y);
                    index = palette.getPaletteIndex(0xFFFFFF & argb);
                    int n = x >>> 1;
                    plane0[n] = (byte)(plane0[n] | index << 4 * (1 - (x & 1)));
                }
            } else if (bitDepth == 8 && planes == 1) {
                for (x = 0; x < src.getWidth(); ++x) {
                    argb = src.getRGB(x, y);
                    index = palette.getPaletteIndex(0xFFFFFF & argb);
                    plane0[x] = (byte)index;
                }
            } else if (bitDepth == 8 && planes == 3) {
                for (x = 0; x < src.getWidth(); ++x) {
                    argb = src.getRGB(x, y);
                    plane0[x] = (byte)(argb >>> 16);
                    plane1[x] = (byte)(argb >>> 8);
                    plane2[x] = (byte)argb;
                }
            }
            for (i = 0; i < planes; ++i) {
                this.rleWriter.write(bos, allPlanes[i]);
            }
        }
        this.rleWriter.flush(bos);
    }

    private void writePixels32(BufferedImage src, int bytesPerLine, BinaryOutputStream bos) throws IOException, ImageWriteException {
        int[] rgbs = new int[src.getWidth()];
        byte[] plane = new byte[4 * bytesPerLine];
        for (int y = 0; y < src.getHeight(); ++y) {
            src.getRGB(0, y, src.getWidth(), 1, rgbs, 0, src.getWidth());
            for (int x = 0; x < rgbs.length; ++x) {
                plane[4 * x + 0] = (byte)rgbs[x];
                plane[4 * x + 1] = (byte)(rgbs[x] >> 8);
                plane[4 * x + 2] = (byte)(rgbs[x] >> 16);
                plane[4 * x + 3] = 0;
            }
            this.rleWriter.write(bos, plane);
        }
        this.rleWriter.flush(bos);
    }
}

