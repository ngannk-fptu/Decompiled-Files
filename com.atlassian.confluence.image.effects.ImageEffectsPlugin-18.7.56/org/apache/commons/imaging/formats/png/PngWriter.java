/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.PixelDensity;
import org.apache.commons.imaging.formats.png.ChunkType;
import org.apache.commons.imaging.formats.png.FilterType;
import org.apache.commons.imaging.formats.png.InterlaceMethod;
import org.apache.commons.imaging.formats.png.PhysicalScale;
import org.apache.commons.imaging.formats.png.PngColorType;
import org.apache.commons.imaging.formats.png.PngConstants;
import org.apache.commons.imaging.formats.png.PngCrc;
import org.apache.commons.imaging.formats.png.PngText;
import org.apache.commons.imaging.internal.Debug;
import org.apache.commons.imaging.palette.Palette;
import org.apache.commons.imaging.palette.PaletteFactory;

class PngWriter {
    PngWriter() {
    }

    private void writeInt(OutputStream os, int value) throws IOException {
        os.write(0xFF & value >> 24);
        os.write(0xFF & value >> 16);
        os.write(0xFF & value >> 8);
        os.write(0xFF & value >> 0);
    }

    private void writeChunk(OutputStream os, ChunkType chunkType, byte[] data) throws IOException {
        int dataLength = data == null ? 0 : data.length;
        this.writeInt(os, dataLength);
        os.write(chunkType.array);
        if (data != null) {
            os.write(data);
        }
        PngCrc png_crc = new PngCrc();
        long crc1 = png_crc.start_partial_crc(chunkType.array, chunkType.array.length);
        long crc2 = data == null ? crc1 : png_crc.continue_partial_crc(crc1, data, data.length);
        int crc = (int)png_crc.finish_partial_crc(crc2);
        this.writeInt(os, crc);
    }

    private void writeChunkIHDR(OutputStream os, ImageHeader value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.writeInt(baos, value.width);
        this.writeInt(baos, value.height);
        baos.write(0xFF & value.bitDepth);
        baos.write(0xFF & value.pngColorType.getValue());
        baos.write(0xFF & value.compressionMethod);
        baos.write(0xFF & value.filterMethod);
        baos.write(0xFF & value.interlaceMethod.ordinal());
        this.writeChunk(os, ChunkType.IHDR, baos.toByteArray());
    }

    private void writeChunkiTXt(OutputStream os, PngText.Itxt text) throws IOException, ImageWriteException {
        if (!this.isValidISO_8859_1(text.keyword)) {
            throw new ImageWriteException("Png tEXt chunk keyword is not ISO-8859-1: " + text.keyword);
        }
        if (!this.isValidISO_8859_1(text.languageTag)) {
            throw new ImageWriteException("Png tEXt chunk language tag is not ISO-8859-1: " + text.languageTag);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(text.keyword.getBytes(StandardCharsets.ISO_8859_1));
        baos.write(0);
        baos.write(1);
        baos.write(0);
        baos.write(text.languageTag.getBytes(StandardCharsets.ISO_8859_1));
        baos.write(0);
        baos.write(text.translatedKeyword.getBytes(StandardCharsets.UTF_8));
        baos.write(0);
        baos.write(this.deflate(text.text.getBytes(StandardCharsets.UTF_8)));
        this.writeChunk(os, ChunkType.iTXt, baos.toByteArray());
    }

    private void writeChunkzTXt(OutputStream os, PngText.Ztxt text) throws IOException, ImageWriteException {
        if (!this.isValidISO_8859_1(text.keyword)) {
            throw new ImageWriteException("Png zTXt chunk keyword is not ISO-8859-1: " + text.keyword);
        }
        if (!this.isValidISO_8859_1(text.text)) {
            throw new ImageWriteException("Png zTXt chunk text is not ISO-8859-1: " + text.text);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(text.keyword.getBytes(StandardCharsets.ISO_8859_1));
        baos.write(0);
        baos.write(0);
        baos.write(this.deflate(text.text.getBytes(StandardCharsets.ISO_8859_1)));
        this.writeChunk(os, ChunkType.zTXt, baos.toByteArray());
    }

    private void writeChunktEXt(OutputStream os, PngText.Text text) throws IOException, ImageWriteException {
        if (!this.isValidISO_8859_1(text.keyword)) {
            throw new ImageWriteException("Png tEXt chunk keyword is not ISO-8859-1: " + text.keyword);
        }
        if (!this.isValidISO_8859_1(text.text)) {
            throw new ImageWriteException("Png tEXt chunk text is not ISO-8859-1: " + text.text);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(text.keyword.getBytes(StandardCharsets.ISO_8859_1));
        baos.write(0);
        baos.write(text.text.getBytes(StandardCharsets.ISO_8859_1));
        this.writeChunk(os, ChunkType.tEXt, baos.toByteArray());
    }

    private byte[] deflate(byte[] bytes) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            try (DeflaterOutputStream dos = new DeflaterOutputStream(baos);){
                dos.write(bytes);
            }
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
    }

    private boolean isValidISO_8859_1(String s) {
        String roundtrip = new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        return s.equals(roundtrip);
    }

    private void writeChunkXmpiTXt(OutputStream os, String xmpXml) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("XML:com.adobe.xmp".getBytes(StandardCharsets.ISO_8859_1));
        baos.write(0);
        baos.write(1);
        baos.write(0);
        baos.write(0);
        baos.write("XML:com.adobe.xmp".getBytes(StandardCharsets.UTF_8));
        baos.write(0);
        baos.write(this.deflate(xmpXml.getBytes(StandardCharsets.UTF_8)));
        this.writeChunk(os, ChunkType.iTXt, baos.toByteArray());
    }

    private void writeChunkPLTE(OutputStream os, Palette palette) throws IOException {
        int length = palette.length();
        byte[] bytes = new byte[length * 3];
        for (int i = 0; i < length; ++i) {
            int rgb = palette.getEntry(i);
            int index = i * 3;
            bytes[index + 0] = (byte)(0xFF & rgb >> 16);
            bytes[index + 1] = (byte)(0xFF & rgb >> 8);
            bytes[index + 2] = (byte)(0xFF & rgb >> 0);
        }
        this.writeChunk(os, ChunkType.PLTE, bytes);
    }

    private void writeChunkTRNS(OutputStream os, Palette palette) throws IOException {
        byte[] bytes = new byte[palette.length()];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)(0xFF & palette.getEntry(i) >> 24);
        }
        this.writeChunk(os, ChunkType.tRNS, bytes);
    }

    private void writeChunkIEND(OutputStream os) throws IOException {
        this.writeChunk(os, ChunkType.IEND, null);
    }

    private void writeChunkIDAT(OutputStream os, byte[] bytes) throws IOException {
        this.writeChunk(os, ChunkType.IDAT, bytes);
    }

    private void writeChunkPHYS(OutputStream os, int xPPU, int yPPU, byte units) throws IOException {
        byte[] bytes = new byte[]{(byte)(0xFF & xPPU >> 24), (byte)(0xFF & xPPU >> 16), (byte)(0xFF & xPPU >> 8), (byte)(0xFF & xPPU >> 0), (byte)(0xFF & yPPU >> 24), (byte)(0xFF & yPPU >> 16), (byte)(0xFF & yPPU >> 8), (byte)(0xFF & yPPU >> 0), units};
        this.writeChunk(os, ChunkType.pHYs, bytes);
    }

    private void writeChunkSCAL(OutputStream os, double xUPP, double yUPP, byte units) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(units);
        baos.write(String.valueOf(xUPP).getBytes(StandardCharsets.ISO_8859_1));
        baos.write(0);
        baos.write(String.valueOf(yUPP).getBytes(StandardCharsets.ISO_8859_1));
        this.writeChunk(os, ChunkType.sCAL, baos.toByteArray());
    }

    private byte getBitDepth(PngColorType pngColorType, Map<String, Object> params) {
        byte depth = 8;
        Object o = params.get("PNG_BIT_DEPTH");
        if (o instanceof Number) {
            depth = ((Number)o).byteValue();
        }
        return pngColorType.isBitDepthAllowed(depth) ? depth : (byte)8;
    }

    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        Object physcialScaleObj;
        Object pixelDensityObj;
        PngColorType pngColorType;
        if ((params = new HashMap<String, Object>(params)).containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        int compressionLevel = -1;
        HashMap<String, Object> rawParams = new HashMap<String, Object>(params);
        if (params.containsKey("PNG_FORCE_TRUE_COLOR")) {
            params.remove("PNG_FORCE_TRUE_COLOR");
        }
        if (params.containsKey("PNG_FORCE_INDEXED_COLOR")) {
            params.remove("PNG_FORCE_INDEXED_COLOR");
        }
        if (params.containsKey("PNG_BIT_DEPTH")) {
            params.remove("PNG_BIT_DEPTH");
        }
        if (params.containsKey("XMP_XML")) {
            params.remove("XMP_XML");
        }
        if (params.containsKey("PNG_TEXT_CHUNKS")) {
            params.remove("PNG_TEXT_CHUNKS");
        }
        if (params.containsKey("PNG_COMPRESSION_LEVEL")) {
            compressionLevel = (Integer)params.remove("PNG_COMPRESSION_LEVEL");
        }
        params.remove("PIXEL_DENSITY");
        params.remove("PHYSICAL_SCALE_CHUNK");
        params.remove("PNG_COMPRESSION_LEVEL");
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        params = rawParams;
        int width = src.getWidth();
        int height = src.getHeight();
        boolean hasAlpha = new PaletteFactory().hasTransparency(src);
        Debug.debug("hasAlpha: " + hasAlpha);
        boolean isGrayscale = new PaletteFactory().isGrayscale(src);
        Debug.debug("isGrayscale: " + isGrayscale);
        boolean forceIndexedColor = Boolean.TRUE.equals(params.get("PNG_FORCE_INDEXED_COLOR"));
        boolean forceTrueColor = Boolean.TRUE.equals(params.get("PNG_FORCE_TRUE_COLOR"));
        if (forceIndexedColor && forceTrueColor) {
            throw new ImageWriteException("Params: Cannot force both indexed and true color modes");
        }
        if (forceIndexedColor) {
            pngColorType = PngColorType.INDEXED_COLOR;
        } else if (forceTrueColor) {
            pngColorType = hasAlpha ? PngColorType.TRUE_COLOR_WITH_ALPHA : PngColorType.TRUE_COLOR;
            isGrayscale = false;
        } else {
            pngColorType = PngColorType.getColorType(hasAlpha, isGrayscale);
        }
        Debug.debug("colorType: " + (Object)((Object)pngColorType));
        byte bitDepth = this.getBitDepth(pngColorType, params);
        Debug.debug("bitDepth: " + bitDepth);
        int sampleDepth = pngColorType == PngColorType.INDEXED_COLOR ? 8 : (int)bitDepth;
        Debug.debug("sampleDepth: " + sampleDepth);
        PngConstants.PNG_SIGNATURE.writeTo(os);
        boolean compressionMethod = false;
        boolean filterMethod = false;
        InterlaceMethod interlaceMethod = InterlaceMethod.NONE;
        ImageHeader imageHeader = new ImageHeader(width, height, bitDepth, pngColorType, 0, 0, interlaceMethod);
        this.writeChunkIHDR(os, imageHeader);
        Palette palette = null;
        if (pngColorType == PngColorType.INDEXED_COLOR) {
            int maxColors = 256;
            PaletteFactory paletteFactory = new PaletteFactory();
            if (hasAlpha) {
                palette = paletteFactory.makeQuantizedRgbaPalette(src, hasAlpha, 256);
                this.writeChunkPLTE(os, palette);
                this.writeChunkTRNS(os, palette);
            } else {
                palette = paletteFactory.makeQuantizedRgbPalette(src, 256);
                this.writeChunkPLTE(os, palette);
            }
        }
        if ((pixelDensityObj = params.get("PIXEL_DENSITY")) instanceof PixelDensity) {
            PixelDensity pixelDensity = (PixelDensity)pixelDensityObj;
            if (pixelDensity.isUnitless()) {
                this.writeChunkPHYS(os, (int)Math.round(pixelDensity.getRawHorizontalDensity()), (int)Math.round(pixelDensity.getRawVerticalDensity()), (byte)0);
            } else {
                this.writeChunkPHYS(os, (int)Math.round(pixelDensity.horizontalDensityMetres()), (int)Math.round(pixelDensity.verticalDensityMetres()), (byte)1);
            }
        }
        if ((physcialScaleObj = params.get("PHYSICAL_SCALE_CHUNK")) instanceof PhysicalScale) {
            PhysicalScale physicalScale = (PhysicalScale)physcialScaleObj;
            this.writeChunkSCAL(os, physicalScale.getHorizontalUnitsPerPixel(), physicalScale.getVerticalUnitsPerPixel(), physicalScale.isInMeters() ? (byte)1 : 2);
        }
        if (params.containsKey("XMP_XML")) {
            String xmpXml = (String)params.get("XMP_XML");
            this.writeChunkXmpiTXt(os, xmpXml);
        }
        if (params.containsKey("PNG_TEXT_CHUNKS")) {
            List outputTexts = (List)params.get("PNG_TEXT_CHUNKS");
            for (Object outputText : outputTexts) {
                PngText text = (PngText)outputText;
                if (text instanceof PngText.Text) {
                    this.writeChunktEXt(os, (PngText.Text)text);
                    continue;
                }
                if (text instanceof PngText.Ztxt) {
                    this.writeChunkzTXt(os, (PngText.Ztxt)text);
                    continue;
                }
                if (text instanceof PngText.Itxt) {
                    this.writeChunkiTXt(os, (PngText.Itxt)text);
                    continue;
                }
                throw new ImageWriteException("Unknown text to embed in PNG: " + text);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean useAlpha = pngColorType == PngColorType.GREYSCALE_WITH_ALPHA || pngColorType == PngColorType.TRUE_COLOR_WITH_ALPHA;
        int[] row = new int[width];
        for (int y = 0; y < height; ++y) {
            src.getRGB(0, y, width, 1, row, 0, width);
            baos.write(FilterType.NONE.ordinal());
            for (int x = 0; x < width; ++x) {
                int argb = row[x];
                if (palette != null) {
                    int index = palette.getPaletteIndex(argb);
                    baos.write(0xFF & index);
                    continue;
                }
                int alpha = 0xFF & argb >> 24;
                int red = 0xFF & argb >> 16;
                int green = 0xFF & argb >> 8;
                int blue = 0xFF & argb >> 0;
                if (isGrayscale) {
                    int gray = (red + green + blue) / 3;
                    baos.write(gray);
                } else {
                    baos.write(red);
                    baos.write(green);
                    baos.write(blue);
                }
                if (!useAlpha) continue;
                baos.write(alpha);
            }
        }
        byte[] uncompressed = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        int chunkSize = 262144;
        Deflater deflater = new Deflater(compressionLevel);
        DeflaterOutputStream dos = new DeflaterOutputStream((OutputStream)baos, deflater, 262144);
        for (int index = 0; index < uncompressed.length; index += 262144) {
            int end = Math.min(uncompressed.length, index + 262144);
            int length = end - index;
            dos.write(uncompressed, index, length);
            dos.flush();
            baos.flush();
            byte[] compressed = baos.toByteArray();
            baos.reset();
            if (compressed.length <= 0) continue;
            this.writeChunkIDAT(os, compressed);
        }
        dos.finish();
        byte[] compressed = baos.toByteArray();
        if (compressed.length > 0) {
            this.writeChunkIDAT(os, compressed);
        }
        this.writeChunkIEND(os);
        os.close();
    }

    private static class ImageHeader {
        public final int width;
        public final int height;
        public final byte bitDepth;
        public final PngColorType pngColorType;
        public final byte compressionMethod;
        public final byte filterMethod;
        public final InterlaceMethod interlaceMethod;

        ImageHeader(int width, int height, byte bitDepth, PngColorType pngColorType, byte compressionMethod, byte filterMethod, InterlaceMethod interlaceMethod) {
            this.width = width;
            this.height = height;
            this.bitDepth = bitDepth;
            this.pngColorType = pngColorType;
            this.compressionMethod = compressionMethod;
            this.filterMethod = filterMethod;
            this.interlaceMethod = interlaceMethod;
        }
    }
}

