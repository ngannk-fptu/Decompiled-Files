/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDIndexed;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

final class SampledImageReader {
    private static final Log LOG = LogFactory.getLog(SampledImageReader.class);

    private SampledImageReader() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BufferedImage getStencilImage(PDImage pdImage, Paint paint) throws IOException {
        int width = pdImage.getWidth();
        int height = pdImage.getHeight();
        BufferedImage masked = new BufferedImage(width, height, 2);
        Graphics2D g = masked.createGraphics();
        g.setPaint(paint);
        g.fillRect(0, 0, width, height);
        g.dispose();
        WritableRaster raster = masked.getRaster();
        int[] transparent = new int[4];
        MemoryCacheImageInputStream iis = null;
        try {
            iis = new MemoryCacheImageInputStream(pdImage.createInputStream());
            float[] decode = SampledImageReader.getDecodeArray(pdImage);
            int value = decode[0] < decode[1] ? 1 : 0;
            int rowLen = width / 8;
            if (width % 8 > 0) {
                ++rowLen;
            }
            byte[] buff = new byte[rowLen];
            for (int y = 0; y < height; ++y) {
                int x = 0;
                int readLen = iis.read(buff);
                block4: for (int r = 0; r < rowLen && r < readLen; ++r) {
                    byte byteValue = buff[r];
                    int mask = 128;
                    int shift = 7;
                    for (int i = 0; i < 8; ++i) {
                        int bit = (byteValue & mask) >> shift;
                        mask >>= 1;
                        --shift;
                        if (bit == value) {
                            raster.setPixel(x, y, transparent);
                        }
                        if (++x == width) continue block4;
                    }
                }
                if (readLen == rowLen) continue;
                LOG.warn((Object)"premature EOF, image will be incomplete");
                break;
            }
        }
        finally {
            if (iis != null) {
                iis.close();
            }
        }
        return masked;
    }

    public static BufferedImage getRGBImage(PDImage pdImage, COSArray colorKey) throws IOException {
        return SampledImageReader.getRGBImage(pdImage, null, 1, colorKey);
    }

    private static Rectangle clipRegion(PDImage pdImage, Rectangle region) {
        if (region == null) {
            return new Rectangle(0, 0, pdImage.getWidth(), pdImage.getHeight());
        }
        int x = Math.max(0, region.x);
        int y = Math.max(0, region.y);
        int width = Math.min(region.width, pdImage.getWidth() - x);
        int height = Math.min(region.height, pdImage.getHeight() - y);
        return new Rectangle(x, y, width, height);
    }

    public static BufferedImage getRGBImage(PDImage pdImage, Rectangle region, int subsampling, COSArray colorKey) throws IOException {
        if (pdImage.isEmpty()) {
            throw new IOException("Image stream is empty");
        }
        Rectangle clipped = SampledImageReader.clipRegion(pdImage, region);
        PDColorSpace colorSpace = pdImage.getColorSpace();
        int numComponents = colorSpace.getNumberOfComponents();
        int width = (int)Math.ceil(clipped.getWidth() / (double)subsampling);
        int height = (int)Math.ceil(clipped.getHeight() / (double)subsampling);
        int bitsPerComponent = pdImage.getBitsPerComponent();
        if (width <= 0 || height <= 0 || pdImage.getWidth() <= 0 || pdImage.getHeight() <= 0) {
            throw new IOException("image width and height must be positive");
        }
        try {
            if (bitsPerComponent == 1 && colorKey == null && numComponents == 1) {
                return SampledImageReader.from1Bit(pdImage, clipped, subsampling, width, height);
            }
            WritableRaster raster = Raster.createInterleavedRaster(0, width, height, numComponents, new Point(0, 0));
            float[] defaultDecode = pdImage.getColorSpace().getDefaultDecode(8);
            float[] decode = SampledImageReader.getDecodeArray(pdImage);
            if (bitsPerComponent == 8 && colorKey == null && Arrays.equals(decode, defaultDecode)) {
                return SampledImageReader.from8bit(pdImage, raster, clipped, subsampling, width, height);
            }
            return SampledImageReader.fromAny(pdImage, raster, colorKey, clipped, subsampling, width, height);
        }
        catch (NegativeArraySizeException ex) {
            throw new IOException(ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }

    public static WritableRaster getRawRaster(PDImage pdImage) throws IOException {
        if (pdImage.isEmpty()) {
            throw new IOException("Image stream is empty");
        }
        PDColorSpace colorSpace = pdImage.getColorSpace();
        int numComponents = colorSpace.getNumberOfComponents();
        int width = pdImage.getWidth();
        int height = pdImage.getHeight();
        int bitsPerComponent = pdImage.getBitsPerComponent();
        if (width <= 0 || height <= 0) {
            throw new IOException("image width and height must be positive");
        }
        try {
            int dataBufferType = 0;
            if (bitsPerComponent > 8) {
                dataBufferType = 1;
            }
            WritableRaster raster = Raster.createInterleavedRaster(dataBufferType, width, height, numComponents, new Point(0, 0));
            SampledImageReader.readRasterFromAny(pdImage, raster);
            return raster;
        }
        catch (NegativeArraySizeException ex) {
            throw new IOException(ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void readRasterFromAny(PDImage pdImage, WritableRaster raster) throws IOException {
        PDColorSpace colorSpace = pdImage.getColorSpace();
        int numComponents = colorSpace.getNumberOfComponents();
        int bitsPerComponent = pdImage.getBitsPerComponent();
        float[] decode = SampledImageReader.getDecodeArray(pdImage);
        DecodeOptions options = new DecodeOptions();
        ImageInputStream iis = null;
        try {
            boolean isShort;
            iis = new MemoryCacheImageInputStream(pdImage.createInputStream(options));
            int inputWidth = pdImage.getWidth();
            int scanWidth = pdImage.getWidth();
            int scanHeight = pdImage.getHeight();
            float sampleMax = (float)Math.pow(2.0, bitsPerComponent) - 1.0f;
            boolean isIndexed = colorSpace instanceof PDIndexed;
            int padding = 0;
            if (inputWidth * numComponents * bitsPerComponent % 8 > 0) {
                padding = 8 - inputWidth * numComponents * bitsPerComponent % 8;
            }
            boolean bl = isShort = raster.getDataBuffer().getDataType() == 1;
            assert (!isIndexed || !isShort);
            byte[] srcColorValuesBytes = isShort ? null : new byte[numComponents];
            short[] srcColorValuesShort = isShort ? new short[numComponents] : null;
            for (int y = 0; y < scanHeight; ++y) {
                for (int x = 0; x < scanWidth; ++x) {
                    for (int c = 0; c < numComponents; ++c) {
                        int value = (int)iis.readBits(bitsPerComponent);
                        float dMin = decode[c * 2];
                        float dMax = decode[c * 2 + 1];
                        float output = dMin + (float)value * ((dMax - dMin) / sampleMax);
                        if (isIndexed) {
                            srcColorValuesBytes[c] = (byte)Math.round(output);
                            continue;
                        }
                        if (isShort) {
                            int outputShort = Math.round((output - Math.min(dMin, dMax)) / Math.abs(dMax - dMin) * 65535.0f);
                            srcColorValuesShort[c] = (short)outputShort;
                            continue;
                        }
                        int outputByte = Math.round((output - Math.min(dMin, dMax)) / Math.abs(dMax - dMin) * 255.0f);
                        srcColorValuesBytes[c] = (byte)outputByte;
                    }
                    if (isShort) {
                        raster.setDataElements(x, y, srcColorValuesShort);
                        continue;
                    }
                    raster.setDataElements(x, y, srcColorValuesBytes);
                }
                iis.readBits(padding);
            }
        }
        finally {
            if (iis != null) {
                iis.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static BufferedImage from1Bit(PDImage pdImage, Rectangle clipped, int subsampling, int width, int height) throws IOException {
        int currentSubsampling = subsampling;
        PDColorSpace colorSpace = pdImage.getColorSpace();
        float[] decode = SampledImageReader.getDecodeArray(pdImage);
        BufferedImage bim = null;
        DecodeOptions options = new DecodeOptions(currentSubsampling);
        options.setSourceRegion(clipped);
        InputStream iis = null;
        try {
            WritableRaster raster;
            int scanHeight;
            int scanWidth;
            int starty;
            int startx;
            int inputWidth;
            iis = pdImage.createInputStream(options);
            if (options.isFilterSubsampled()) {
                inputWidth = width;
                startx = 0;
                starty = 0;
                scanWidth = width;
                scanHeight = height;
                currentSubsampling = 1;
            } else {
                inputWidth = pdImage.getWidth();
                startx = clipped.x;
                starty = clipped.y;
                scanWidth = clipped.width;
                scanHeight = clipped.height;
            }
            if (colorSpace instanceof PDDeviceGray) {
                bim = new BufferedImage(width, height, 10);
                raster = bim.getRaster();
            } else {
                raster = Raster.createBandedRaster(0, width, height, 1, new Point(0, 0));
            }
            byte[] output = ((DataBufferByte)raster.getDataBuffer()).getData();
            int idx = 0;
            boolean nosubsampling = currentSubsampling == 1;
            int stride = (inputWidth + 7) / 8;
            int invert = decode[0] < decode[1] ? 0 : -1;
            int endX = startx + scanWidth;
            byte[] buff = new byte[stride];
            for (int y = 0; y < starty + scanHeight; ++y) {
                int read = (int)IOUtils.populateBuffer(iis, buff);
                if (y >= starty && y % currentSubsampling == 0) {
                    int x = startx;
                    for (int r = x / 8; r < stride && r < read; ++r) {
                        int value = (buff[r] ^ invert) << 24 + (x & 7);
                        for (int count = Math.min(8 - (x & 7), endX - x); count > 0; --count) {
                            if (nosubsampling || x % currentSubsampling == 0) {
                                if (value < 0) {
                                    output[idx] = -1;
                                }
                                ++idx;
                            }
                            value <<= 1;
                            ++x;
                        }
                    }
                }
                if (read == stride) continue;
                LOG.warn((Object)"premature EOF, image will be incomplete");
                break;
            }
            if (bim != null) {
                BufferedImage bufferedImage = bim;
                return bufferedImage;
            }
            BufferedImage bufferedImage = colorSpace.toRGBImage(raster);
            return bufferedImage;
        }
        finally {
            if (iis != null) {
                iis.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static BufferedImage from8bit(PDImage pdImage, WritableRaster raster, Rectangle clipped, int subsampling, int width, int height) throws IOException {
        int currentSubsampling = subsampling;
        DecodeOptions options = new DecodeOptions(currentSubsampling);
        options.setSourceRegion(clipped);
        InputStream input = pdImage.createInputStream(options);
        try {
            int scanHeight;
            int scanWidth;
            int starty;
            int startx;
            int inputWidth;
            if (options.isFilterSubsampled()) {
                inputWidth = width;
                startx = 0;
                starty = 0;
                scanWidth = width;
                scanHeight = height;
                currentSubsampling = 1;
            } else {
                inputWidth = pdImage.getWidth();
                startx = clipped.x;
                starty = clipped.y;
                scanWidth = clipped.width;
                scanHeight = clipped.height;
            }
            int numComponents = pdImage.getColorSpace().getNumberOfComponents();
            byte[] bank = ((DataBufferByte)raster.getDataBuffer()).getData();
            if (startx == 0 && starty == 0 && scanWidth == width && scanHeight == height && currentSubsampling == 1) {
                long inputResult = IOUtils.populateBuffer(input, bank);
                if (inputResult != (long)width * (long)height * (long)numComponents) {
                    LOG.debug((Object)("Tried reading " + (long)width * (long)height * (long)numComponents + " bytes but only " + inputResult + " bytes read"));
                }
                BufferedImage bufferedImage = pdImage.getColorSpace().toRGBImage(raster);
                return bufferedImage;
            }
            byte[] tempBytes = new byte[numComponents * inputWidth];
            int i = 0;
            for (int y = 0; y < starty + scanHeight; ++y) {
                IOUtils.populateBuffer(input, tempBytes);
                if (y < starty || y % currentSubsampling > 0) continue;
                if (currentSubsampling == 1) {
                    System.arraycopy(tempBytes, startx * numComponents, bank, y * inputWidth * numComponents, scanWidth * numComponents);
                    continue;
                }
                for (int x = startx; x < startx + scanWidth; x += currentSubsampling) {
                    for (int c = 0; c < numComponents; ++c) {
                        bank[i] = tempBytes[x * numComponents + c];
                        ++i;
                    }
                }
            }
            BufferedImage bufferedImage = pdImage.getColorSpace().toRGBImage(raster);
            return bufferedImage;
        }
        finally {
            IOUtils.closeQuietly(input);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static BufferedImage fromAny(PDImage pdImage, WritableRaster raster, COSArray colorKey, Rectangle clipped, int subsampling, int width, int height) throws IOException {
        int currentSubsampling = subsampling;
        PDColorSpace colorSpace = pdImage.getColorSpace();
        int numComponents = colorSpace.getNumberOfComponents();
        int bitsPerComponent = pdImage.getBitsPerComponent();
        float[] decode = SampledImageReader.getDecodeArray(pdImage);
        DecodeOptions options = new DecodeOptions(currentSubsampling);
        options.setSourceRegion(clipped);
        ImageInputStream iis = null;
        try {
            int scanHeight;
            int scanWidth;
            int starty;
            int startx;
            int inputWidth;
            iis = new MemoryCacheImageInputStream(pdImage.createInputStream(options));
            if (options.isFilterSubsampled()) {
                inputWidth = width;
                startx = 0;
                starty = 0;
                scanWidth = width;
                scanHeight = height;
                currentSubsampling = 1;
            } else {
                inputWidth = pdImage.getWidth();
                startx = clipped.x;
                starty = clipped.y;
                scanWidth = clipped.width;
                scanHeight = clipped.height;
            }
            float sampleMax = (float)Math.pow(2.0, bitsPerComponent) - 1.0f;
            boolean isIndexed = colorSpace instanceof PDIndexed;
            float[] colorKeyRanges = null;
            BufferedImage colorKeyMask = null;
            if (colorKey != null) {
                if (colorKey.size() >= numComponents * 2) {
                    colorKeyRanges = colorKey.toFloatArray();
                    colorKeyMask = new BufferedImage(width, height, 10);
                } else {
                    LOG.warn((Object)("colorKey mask size is " + colorKey.size() + ", should be " + numComponents * 2 + ", ignored"));
                }
            }
            int padding = 0;
            if (inputWidth * numComponents * bitsPerComponent % 8 > 0) {
                padding = 8 - inputWidth * numComponents * bitsPerComponent % 8;
            }
            byte[] srcColorValues = new byte[numComponents];
            byte[] alpha = new byte[1];
            for (int y = 0; y < starty + scanHeight; ++y) {
                for (int x = 0; x < startx + scanWidth; ++x) {
                    boolean isMasked = true;
                    for (int c = 0; c < numComponents; ++c) {
                        int value = (int)iis.readBits(bitsPerComponent);
                        if (colorKeyRanges != null) {
                            isMasked &= (float)value >= colorKeyRanges[c * 2] && (float)value <= colorKeyRanges[c * 2 + 1];
                        }
                        float dMin = decode[c * 2];
                        float dMax = decode[c * 2 + 1];
                        float output = dMin + (float)value * ((dMax - dMin) / sampleMax);
                        if (isIndexed) {
                            srcColorValues[c] = (byte)Math.round(output);
                            continue;
                        }
                        int outputByte = Math.round((output - Math.min(dMin, dMax)) / Math.abs(dMax - dMin) * 255.0f);
                        srcColorValues[c] = (byte)outputByte;
                    }
                    if (x < startx || y < starty || x % currentSubsampling != 0 || y % currentSubsampling != 0) continue;
                    raster.setDataElements((x - startx) / currentSubsampling, (y - starty) / currentSubsampling, srcColorValues);
                    if (colorKeyMask == null) continue;
                    alpha[0] = (byte)(isMasked ? 255 : 0);
                    colorKeyMask.getRaster().setDataElements((x - startx) / currentSubsampling, (y - starty) / currentSubsampling, alpha);
                }
                iis.readBits(padding);
            }
            BufferedImage rgbImage = colorSpace.toRGBImage(raster);
            if (colorKeyMask != null) {
                BufferedImage bufferedImage = SampledImageReader.applyColorKeyMask(rgbImage, colorKeyMask);
                return bufferedImage;
            }
            BufferedImage bufferedImage = rgbImage;
            return bufferedImage;
        }
        finally {
            if (iis != null) {
                iis.close();
            }
        }
    }

    private static BufferedImage applyColorKeyMask(BufferedImage image, BufferedImage mask) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage masked = new BufferedImage(width, height, 2);
        WritableRaster src = image.getRaster();
        WritableRaster dest = masked.getRaster();
        WritableRaster alpha = mask.getRaster();
        float[] rgb = new float[3];
        float[] rgba = new float[4];
        float[] alphaPixel = null;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                src.getPixel(x, y, rgb);
                rgba[0] = rgb[0];
                rgba[1] = rgb[1];
                rgba[2] = rgb[2];
                alphaPixel = alpha.getPixel(x, y, alphaPixel);
                rgba[3] = 255.0f - alphaPixel[0];
                dest.setPixel(x, y, rgba);
            }
        }
        return masked;
    }

    private static float[] getDecodeArray(PDImage pdImage) throws IOException {
        COSArray cosDecode = pdImage.getDecode();
        float[] decode = null;
        if (cosDecode != null) {
            int numberOfComponents = pdImage.getColorSpace().getNumberOfComponents();
            if (cosDecode.size() != numberOfComponents * 2) {
                if (pdImage.isStencil() && cosDecode.size() >= 2 && cosDecode.get(0) instanceof COSNumber && cosDecode.get(1) instanceof COSNumber) {
                    float decode0 = ((COSNumber)cosDecode.get(0)).floatValue();
                    float decode1 = ((COSNumber)cosDecode.get(1)).floatValue();
                    if (decode0 >= 0.0f && decode0 <= 1.0f && decode1 >= 0.0f && decode1 <= 1.0f) {
                        LOG.warn((Object)("decode array " + cosDecode + " not compatible with color space, using the first two entries"));
                        return new float[]{decode0, decode1};
                    }
                }
                LOG.error((Object)("decode array " + cosDecode + " not compatible with color space, using default"));
            } else {
                decode = cosDecode.toFloatArray();
            }
        }
        if (decode == null) {
            return pdImage.getColorSpace().getDefaultDecode(pdImage.getBitsPerComponent());
        }
        return decode;
    }
}

