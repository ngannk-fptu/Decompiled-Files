/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;
import org.apache.commons.imaging.ColorTools;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.GenericImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.XmpEmbeddable;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.png.ChunkType;
import org.apache.commons.imaging.formats.png.GammaCorrection;
import org.apache.commons.imaging.formats.png.PhysicalScale;
import org.apache.commons.imaging.formats.png.PngColorType;
import org.apache.commons.imaging.formats.png.PngConstants;
import org.apache.commons.imaging.formats.png.PngImageInfo;
import org.apache.commons.imaging.formats.png.PngText;
import org.apache.commons.imaging.formats.png.PngWriter;
import org.apache.commons.imaging.formats.png.ScanExpediter;
import org.apache.commons.imaging.formats.png.ScanExpediterInterlaced;
import org.apache.commons.imaging.formats.png.ScanExpediterSimple;
import org.apache.commons.imaging.formats.png.chunks.PngChunk;
import org.apache.commons.imaging.formats.png.chunks.PngChunkGama;
import org.apache.commons.imaging.formats.png.chunks.PngChunkIccp;
import org.apache.commons.imaging.formats.png.chunks.PngChunkIdat;
import org.apache.commons.imaging.formats.png.chunks.PngChunkIhdr;
import org.apache.commons.imaging.formats.png.chunks.PngChunkItxt;
import org.apache.commons.imaging.formats.png.chunks.PngChunkPhys;
import org.apache.commons.imaging.formats.png.chunks.PngChunkPlte;
import org.apache.commons.imaging.formats.png.chunks.PngChunkScal;
import org.apache.commons.imaging.formats.png.chunks.PngChunkText;
import org.apache.commons.imaging.formats.png.chunks.PngChunkZtxt;
import org.apache.commons.imaging.formats.png.chunks.PngTextChunk;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilter;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilterGrayscale;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilterIndexedColor;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilterTrueColor;
import org.apache.commons.imaging.icc.IccProfileParser;

public class PngImageParser
extends ImageParser
implements XmpEmbeddable {
    private static final Logger LOGGER = Logger.getLogger(PngImageParser.class.getName());
    private static final String DEFAULT_EXTENSION = ".png";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".png"};

    @Override
    public String getName() {
        return "Png-Custom";
    }

    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    protected String[] getAcceptedExtensions() {
        return (String[])ACCEPTED_EXTENSIONS.clone();
    }

    @Override
    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{ImageFormats.PNG};
    }

    public static String getChunkTypeName(int chunkType) {
        StringBuilder result = new StringBuilder();
        result.append((char)(0xFF & chunkType >> 24));
        result.append((char)(0xFF & chunkType >> 16));
        result.append((char)(0xFF & chunkType >> 8));
        result.append((char)(0xFF & chunkType >> 0));
        return result.toString();
    }

    public List<String> getChunkTypes(InputStream is) throws ImageReadException, IOException {
        List<PngChunk> chunks = this.readChunks(is, null, false);
        ArrayList<String> chunkTypes = new ArrayList<String>(chunks.size());
        for (PngChunk chunk : chunks) {
            chunkTypes.add(PngImageParser.getChunkTypeName(chunk.chunkType));
        }
        return chunkTypes;
    }

    public boolean hasChunkType(ByteSource byteSource, ChunkType chunkType) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            this.readSignature(is);
            List<PngChunk> chunks = this.readChunks(is, new ChunkType[]{chunkType}, true);
            boolean bl = !chunks.isEmpty();
            return bl;
        }
    }

    private boolean keepChunk(int chunkType, ChunkType[] chunkTypes) {
        if (chunkTypes == null) {
            return true;
        }
        for (ChunkType chunkType2 : chunkTypes) {
            if (chunkType2.value != chunkType) continue;
            return true;
        }
        return false;
    }

    private List<PngChunk> readChunks(InputStream is, ChunkType[] chunkTypes, boolean returnAfterFirst) throws ImageReadException, IOException {
        int chunkType;
        ArrayList<PngChunk> result = new ArrayList<PngChunk>();
        do {
            int length;
            if ((length = BinaryFunctions.read4Bytes("Length", is, "Not a Valid PNG File", this.getByteOrder())) < 0) {
                throw new ImageReadException("Invalid PNG chunk length: " + length);
            }
            chunkType = BinaryFunctions.read4Bytes("ChunkType", is, "Not a Valid PNG File", this.getByteOrder());
            if (LOGGER.isLoggable(Level.FINEST)) {
                BinaryFunctions.printCharQuad("ChunkType", chunkType);
                this.debugNumber("Length", length, 4);
            }
            boolean keep = this.keepChunk(chunkType, chunkTypes);
            byte[] bytes = null;
            if (keep) {
                bytes = BinaryFunctions.readBytes("Chunk Data", is, length, "Not a Valid PNG File: Couldn't read Chunk Data.");
            } else {
                BinaryFunctions.skipBytes(is, length, "Not a Valid PNG File");
            }
            if (LOGGER.isLoggable(Level.FINEST) && bytes != null) {
                this.debugNumber("bytes", bytes.length, 4);
            }
            int crc = BinaryFunctions.read4Bytes("CRC", is, "Not a Valid PNG File", this.getByteOrder());
            if (!keep) continue;
            if (chunkType == ChunkType.iCCP.value) {
                result.add(new PngChunkIccp(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.tEXt.value) {
                result.add(new PngChunkText(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.zTXt.value) {
                result.add(new PngChunkZtxt(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.IHDR.value) {
                result.add(new PngChunkIhdr(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.PLTE.value) {
                result.add(new PngChunkPlte(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.pHYs.value) {
                result.add(new PngChunkPhys(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.sCAL.value) {
                result.add(new PngChunkScal(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.IDAT.value) {
                result.add(new PngChunkIdat(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.gAMA.value) {
                result.add(new PngChunkGama(length, chunkType, crc, bytes));
            } else if (chunkType == ChunkType.iTXt.value) {
                result.add(new PngChunkItxt(length, chunkType, crc, bytes));
            } else {
                result.add(new PngChunk(length, chunkType, crc, bytes));
            }
            if (!returnAfterFirst) continue;
            return result;
        } while (chunkType != ChunkType.IEND.value);
        return result;
    }

    public void readSignature(InputStream is) throws ImageReadException, IOException {
        BinaryFunctions.readAndVerifyBytes(is, PngConstants.PNG_SIGNATURE, "Not a Valid PNG Segment: Incorrect Signature");
    }

    private List<PngChunk> readChunks(ByteSource byteSource, ChunkType[] chunkTypes, boolean returnAfterFirst) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            this.readSignature(is);
            List<PngChunk> list = this.readChunks(is, chunkTypes, returnAfterFirst);
            return list;
        }
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<PngChunk> chunks = this.readChunks(byteSource, new ChunkType[]{ChunkType.iCCP}, true);
        if (chunks == null || chunks.isEmpty()) {
            return null;
        }
        if (chunks.size() > 1) {
            throw new ImageReadException("PNG contains more than one ICC Profile ");
        }
        PngChunkIccp pngChunkiCCP = (PngChunkIccp)chunks.get(0);
        byte[] bytes = pngChunkiCCP.getUncompressedProfile();
        return bytes;
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<PngChunk> chunks = this.readChunks(byteSource, new ChunkType[]{ChunkType.IHDR}, true);
        if (chunks == null || chunks.isEmpty()) {
            throw new ImageReadException("Png: No chunks");
        }
        if (chunks.size() > 1) {
            throw new ImageReadException("PNG contains more than one Header");
        }
        PngChunkIhdr pngChunkIHDR = (PngChunkIhdr)chunks.get(0);
        return new Dimension(pngChunkIHDR.width, pngChunkIHDR.height);
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<PngChunk> chunks = this.readChunks(byteSource, new ChunkType[]{ChunkType.tEXt, ChunkType.zTXt}, false);
        if (chunks == null || chunks.isEmpty()) {
            return null;
        }
        GenericImageMetadata result = new GenericImageMetadata();
        for (PngChunk chunk : chunks) {
            PngTextChunk textChunk = (PngTextChunk)chunk;
            result.add(textChunk.getKeyword(), textChunk.getText());
        }
        return result;
    }

    private List<PngChunk> filterChunks(List<PngChunk> chunks, ChunkType type) {
        ArrayList<PngChunk> result = new ArrayList<PngChunk>();
        for (PngChunk chunk : chunks) {
            if (chunk.chunkType != type.value) continue;
            result.add(chunk);
        }
        return result;
    }

    private TransparencyFilter getTransparencyFilter(PngColorType pngColorType, PngChunk pngChunktRNS) throws ImageReadException, IOException {
        switch (pngColorType) {
            case GREYSCALE: {
                return new TransparencyFilterGrayscale(pngChunktRNS.getBytes());
            }
            case TRUE_COLOR: {
                return new TransparencyFilterTrueColor(pngChunktRNS.getBytes());
            }
            case INDEXED_COLOR: {
                return new TransparencyFilterIndexedColor(pngChunktRNS.getBytes());
            }
        }
        throw new ImageReadException("Simple Transparency not compatible with ColorType: " + (Object)((Object)pngColorType));
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        ImageInfo.ColorType colorType;
        List<PngChunk> chunks = this.readChunks(byteSource, new ChunkType[]{ChunkType.IHDR, ChunkType.pHYs, ChunkType.sCAL, ChunkType.tEXt, ChunkType.zTXt, ChunkType.tRNS, ChunkType.PLTE, ChunkType.iTXt}, false);
        if (chunks == null || chunks.isEmpty()) {
            throw new ImageReadException("PNG: no chunks");
        }
        List<PngChunk> IHDRs = this.filterChunks(chunks, ChunkType.IHDR);
        if (IHDRs.size() != 1) {
            throw new ImageReadException("PNG contains more than one Header");
        }
        PngChunkIhdr pngChunkIHDR = (PngChunkIhdr)IHDRs.get(0);
        boolean transparent = false;
        List<PngChunk> tRNSs = this.filterChunks(chunks, ChunkType.tRNS);
        transparent = !tRNSs.isEmpty() ? true : pngChunkIHDR.pngColorType.hasAlpha();
        PngChunkPhys pngChunkpHYs = null;
        List<PngChunk> pHYss = this.filterChunks(chunks, ChunkType.pHYs);
        if (pHYss.size() > 1) {
            throw new ImageReadException("PNG contains more than one pHYs: " + pHYss.size());
        }
        if (pHYss.size() == 1) {
            pngChunkpHYs = (PngChunkPhys)pHYss.get(0);
        }
        PhysicalScale physicalScale = PhysicalScale.UNDEFINED;
        List<PngChunk> sCALs = this.filterChunks(chunks, ChunkType.sCAL);
        if (sCALs.size() > 1) {
            throw new ImageReadException("PNG contains more than one sCAL:" + sCALs.size());
        }
        if (sCALs.size() == 1) {
            PngChunkScal pngChunkScal = (PngChunkScal)sCALs.get(0);
            physicalScale = pngChunkScal.unitSpecifier == 1 ? PhysicalScale.createFromMeters(pngChunkScal.unitsPerPixelXAxis, pngChunkScal.unitsPerPixelYAxis) : PhysicalScale.createFromRadians(pngChunkScal.unitsPerPixelXAxis, pngChunkScal.unitsPerPixelYAxis);
        }
        List<PngChunk> tEXts = this.filterChunks(chunks, ChunkType.tEXt);
        List<PngChunk> zTXts = this.filterChunks(chunks, ChunkType.zTXt);
        List<PngChunk> iTXts = this.filterChunks(chunks, ChunkType.iTXt);
        int chunkCount = tEXts.size() + zTXts.size() + iTXts.size();
        ArrayList<String> comments = new ArrayList<String>(chunkCount);
        ArrayList<PngText> textChunks = new ArrayList<PngText>(chunkCount);
        for (PngChunk tEXt : tEXts) {
            PngChunkText pngChunktEXt = (PngChunkText)tEXt;
            comments.add(pngChunktEXt.keyword + ": " + pngChunktEXt.text);
            textChunks.add(pngChunktEXt.getContents());
        }
        for (PngChunk zTXt : zTXts) {
            PngChunkZtxt pngChunkzTXt = (PngChunkZtxt)zTXt;
            comments.add(pngChunkzTXt.keyword + ": " + pngChunkzTXt.text);
            textChunks.add(pngChunkzTXt.getContents());
        }
        for (PngChunk iTXt : iTXts) {
            PngChunkItxt pngChunkiTXt = (PngChunkItxt)iTXt;
            comments.add(pngChunkiTXt.keyword + ": " + pngChunkiTXt.text);
            textChunks.add(pngChunkiTXt.getContents());
        }
        int bitsPerPixel = pngChunkIHDR.bitDepth * pngChunkIHDR.pngColorType.getSamplesPerPixel();
        ImageFormats format = ImageFormats.PNG;
        String formatName = "PNG Portable Network Graphics";
        int height = pngChunkIHDR.height;
        String mimeType = "image/png";
        boolean numberOfImages = true;
        int width = pngChunkIHDR.width;
        boolean progressive = pngChunkIHDR.interlaceMethod.isProgressive();
        int physicalHeightDpi = -1;
        float physicalHeightInch = -1.0f;
        int physicalWidthDpi = -1;
        float physicalWidthInch = -1.0f;
        if (pngChunkpHYs != null && pngChunkpHYs.unitSpecifier == 1) {
            double metersPerInch = 0.0254;
            physicalWidthDpi = (int)Math.round((double)pngChunkpHYs.pixelsPerUnitXAxis * 0.0254);
            physicalWidthInch = (float)((double)width / ((double)pngChunkpHYs.pixelsPerUnitXAxis * 0.0254));
            physicalHeightDpi = (int)Math.round((double)pngChunkpHYs.pixelsPerUnitYAxis * 0.0254);
            physicalHeightInch = (float)((double)height / ((double)pngChunkpHYs.pixelsPerUnitYAxis * 0.0254));
        }
        boolean usesPalette = false;
        List<PngChunk> PLTEs = this.filterChunks(chunks, ChunkType.PLTE);
        if (PLTEs.size() > 1) {
            usesPalette = true;
        }
        switch (pngChunkIHDR.pngColorType) {
            case GREYSCALE: 
            case GREYSCALE_WITH_ALPHA: {
                colorType = ImageInfo.ColorType.GRAYSCALE;
                break;
            }
            case TRUE_COLOR: 
            case INDEXED_COLOR: 
            case TRUE_COLOR_WITH_ALPHA: {
                colorType = ImageInfo.ColorType.RGB;
                break;
            }
            default: {
                throw new ImageReadException("Png: Unknown ColorType: " + (Object)((Object)pngChunkIHDR.pngColorType));
            }
        }
        String formatDetails = "Png";
        ImageInfo.CompressionAlgorithm compressionAlgorithm = ImageInfo.CompressionAlgorithm.PNG_FILTER;
        return new PngImageInfo("Png", bitsPerPixel, comments, format, "PNG Portable Network Graphics", height, "image/png", 1, physicalHeightDpi, physicalHeightInch, physicalWidthDpi, physicalWidthInch, width, progressive, transparent, usesPalette, colorType, compressionAlgorithm, textChunks, physicalScale);
    }

    @Override
    public BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        Boolean is_srgb;
        ScanExpediter scanExpediter;
        List<PngChunk> IDATs;
        params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        List<PngChunk> chunks = this.readChunks(byteSource, new ChunkType[]{ChunkType.IHDR, ChunkType.PLTE, ChunkType.IDAT, ChunkType.tRNS, ChunkType.iCCP, ChunkType.gAMA, ChunkType.sRGB}, false);
        if (chunks == null || chunks.isEmpty()) {
            throw new ImageReadException("PNG: no chunks");
        }
        List<PngChunk> IHDRs = this.filterChunks(chunks, ChunkType.IHDR);
        if (IHDRs.size() != 1) {
            throw new ImageReadException("PNG contains more than one Header");
        }
        PngChunkIhdr pngChunkIHDR = (PngChunkIhdr)IHDRs.get(0);
        List<PngChunk> PLTEs = this.filterChunks(chunks, ChunkType.PLTE);
        if (PLTEs.size() > 1) {
            throw new ImageReadException("PNG contains more than one Palette");
        }
        PngChunkPlte pngChunkPLTE = null;
        if (PLTEs.size() == 1) {
            pngChunkPLTE = (PngChunkPlte)PLTEs.get(0);
        }
        if ((IDATs = this.filterChunks(chunks, ChunkType.IDAT)).isEmpty()) {
            throw new ImageReadException("PNG missing image data");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (PngChunk IDAT : IDATs) {
            PngChunkIdat pngChunkIDAT = (PngChunkIdat)IDAT;
            byte[] bytes = pngChunkIDAT.getBytes();
            baos.write(bytes);
        }
        byte[] compressed = baos.toByteArray();
        baos = null;
        TransparencyFilter transparencyFilter = null;
        List<PngChunk> tRNSs = this.filterChunks(chunks, ChunkType.tRNS);
        if (!tRNSs.isEmpty()) {
            PngChunk pngChunktRNS = tRNSs.get(0);
            transparencyFilter = this.getTransparencyFilter(pngChunkIHDR.pngColorType, pngChunktRNS);
        }
        ICC_Profile iccProfile = null;
        GammaCorrection gammaCorrection = null;
        List<PngChunk> sRGBs = this.filterChunks(chunks, ChunkType.sRGB);
        List<PngChunk> gAMAs = this.filterChunks(chunks, ChunkType.gAMA);
        List<PngChunk> iCCPs = this.filterChunks(chunks, ChunkType.iCCP);
        if (sRGBs.size() > 1) {
            throw new ImageReadException("PNG: unexpected sRGB chunk");
        }
        if (gAMAs.size() > 1) {
            throw new ImageReadException("PNG: unexpected gAMA chunk");
        }
        if (iCCPs.size() > 1) {
            throw new ImageReadException("PNG: unexpected iCCP chunk");
        }
        if (sRGBs.size() == 1) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("sRGB, no color management necessary.");
            }
        } else if (iCCPs.size() == 1) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("iCCP.");
            }
            PngChunkIccp pngChunkiCCP = (PngChunkIccp)iCCPs.get(0);
            byte[] bytes = pngChunkiCCP.getUncompressedProfile();
            iccProfile = ICC_Profile.getInstance(bytes);
        } else if (gAMAs.size() == 1) {
            PngChunkGama pngChunkgAMA = (PngChunkGama)gAMAs.get(0);
            double gamma = pngChunkgAMA.getGamma();
            double targetGamma = 1.0;
            double diff = Math.abs(1.0 - gamma);
            if (diff >= 0.5) {
                gammaCorrection = new GammaCorrection(gamma, 1.0);
            }
            if (gammaCorrection != null && pngChunkPLTE != null) {
                pngChunkPLTE.correct(gammaCorrection);
            }
        }
        int width = pngChunkIHDR.width;
        int height = pngChunkIHDR.height;
        PngColorType pngColorType = pngChunkIHDR.pngColorType;
        int bitDepth = pngChunkIHDR.bitDepth;
        if (pngChunkIHDR.filterMethod != 0) {
            throw new ImageReadException("PNG: unknown FilterMethod: " + pngChunkIHDR.filterMethod);
        }
        int bitsPerPixel = bitDepth * pngColorType.getSamplesPerPixel();
        boolean hasAlpha = pngColorType.hasAlpha() || transparencyFilter != null;
        BufferedImage result = pngColorType.isGreyscale() ? this.getBufferedImageFactory(params).getGrayscaleBufferedImage(width, height, hasAlpha) : this.getBufferedImageFactory(params).getColorBufferedImage(width, height, hasAlpha);
        ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
        InflaterInputStream iis = new InflaterInputStream(bais);
        switch (pngChunkIHDR.interlaceMethod) {
            case NONE: {
                scanExpediter = new ScanExpediterSimple(width, height, iis, result, pngColorType, bitDepth, bitsPerPixel, pngChunkPLTE, gammaCorrection, transparencyFilter);
                break;
            }
            case ADAM7: {
                scanExpediter = new ScanExpediterInterlaced(width, height, iis, result, pngColorType, bitDepth, bitsPerPixel, pngChunkPLTE, gammaCorrection, transparencyFilter);
                break;
            }
            default: {
                throw new ImageReadException("Unknown InterlaceMethod: " + (Object)((Object)pngChunkIHDR.interlaceMethod));
            }
        }
        scanExpediter.drive();
        if (!(iccProfile == null || (is_srgb = Boolean.valueOf(new IccProfileParser().issRGB(iccProfile))) != null && is_srgb.booleanValue())) {
            ICC_ColorSpace cs = new ICC_ColorSpace(iccProfile);
            ColorModel srgbCM = ColorModel.getRGBdefault();
            ColorSpace cs_sRGB = srgbCM.getColorSpace();
            result = new ColorTools().convertBetweenColorSpaces(result, cs, cs_sRGB);
        }
        return result;
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        ImageInfo imageInfo = this.getImageInfo(byteSource);
        if (imageInfo == null) {
            return false;
        }
        imageInfo.toString(pw, "");
        List<PngChunk> chunks = this.readChunks(byteSource, null, false);
        List<PngChunk> IHDRs = this.filterChunks(chunks, ChunkType.IHDR);
        if (IHDRs.size() != 1) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("PNG contains more than one Header");
            }
            return false;
        }
        PngChunkIhdr pngChunkIHDR = (PngChunkIhdr)IHDRs.get(0);
        pw.println("Color: " + pngChunkIHDR.pngColorType.name());
        pw.println("chunks: " + chunks.size());
        if (chunks.isEmpty()) {
            return false;
        }
        for (int i = 0; i < chunks.size(); ++i) {
            PngChunk chunk = chunks.get(i);
            BinaryFunctions.printCharQuad(pw, "\t" + i + ": ", chunk.chunkType);
        }
        pw.println("");
        pw.flush();
        return true;
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        new PngWriter().writeImage(src, os, params);
    }

    @Override
    public String getXmpXml(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<PngChunk> chunks = this.readChunks(byteSource, new ChunkType[]{ChunkType.iTXt}, false);
        if (chunks == null || chunks.isEmpty()) {
            return null;
        }
        ArrayList<PngChunkItxt> xmpChunks = new ArrayList<PngChunkItxt>();
        for (PngChunk chunk : chunks) {
            PngChunkItxt itxtChunk = (PngChunkItxt)chunk;
            if (!itxtChunk.getKeyword().equals("XML:com.adobe.xmp")) continue;
            xmpChunks.add(itxtChunk);
        }
        if (xmpChunks.isEmpty()) {
            return null;
        }
        if (xmpChunks.size() > 1) {
            throw new ImageReadException("PNG contains more than one XMP chunk.");
        }
        PngChunkItxt chunk = (PngChunkItxt)xmpChunks.get(0);
        return chunk.getText();
    }
}

