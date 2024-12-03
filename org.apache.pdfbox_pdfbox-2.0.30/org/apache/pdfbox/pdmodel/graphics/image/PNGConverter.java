/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDIndexed;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

final class PNGConverter {
    private static final Log LOG = LogFactory.getLog(PNGConverter.class);
    private static final int CHUNK_IHDR = 1229472850;
    private static final int CHUNK_IDAT = 1229209940;
    private static final int CHUNK_PLTE = 1347179589;
    private static final int CHUNK_IEND = 1229278788;
    private static final int CHUNK_TRNS = 1951551059;
    private static final int CHUNK_CHRM = 1665684045;
    private static final int CHUNK_GAMA = 1732332865;
    private static final int CHUNK_ICCP = 1766015824;
    private static final int CHUNK_SBIT = 1933723988;
    private static final int CHUNK_SRGB = 1934772034;
    private static final int CHUNK_TEXT = 1950701684;
    private static final int CHUNK_ZTXT = 2052348020;
    private static final int CHUNK_ITXT = 1767135348;
    private static final int CHUNK_KBKG = 1799506759;
    private static final int CHUNK_HIST = 1749635924;
    private static final int CHUNK_PHYS = 1883789683;
    private static final int CHUNK_SPLT = 1934642260;
    private static final int CHUNK_TIME = 1950960965;
    private static final int[] CRC_TABLE = new int[256];

    private PNGConverter() {
    }

    static PDImageXObject convertPNGImage(PDDocument doc, byte[] imageData) throws IOException {
        PNGConverterState state = PNGConverter.parsePNGChunks(imageData);
        if (!PNGConverter.checkConverterState(state)) {
            return null;
        }
        return PNGConverter.convertPng(doc, state);
    }

    private static PDImageXObject convertPng(PDDocument doc, PNGConverterState state) throws IOException {
        Chunk ihdr = state.IHDR;
        int ihdrStart = ihdr.start;
        int width = PNGConverter.readInt(ihdr.bytes, ihdrStart);
        int height = PNGConverter.readInt(ihdr.bytes, ihdrStart + 4);
        int bitDepth = ihdr.bytes[ihdrStart + 8] & 0xFF;
        int colorType = ihdr.bytes[ihdrStart + 9] & 0xFF;
        int compressionMethod = ihdr.bytes[ihdrStart + 10] & 0xFF;
        int filterMethod = ihdr.bytes[ihdrStart + 11] & 0xFF;
        int interlaceMethod = ihdr.bytes[ihdrStart + 12] & 0xFF;
        if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8 && bitDepth != 16) {
            LOG.error((Object)String.format("Invalid bit depth %d.", bitDepth));
            return null;
        }
        if (width <= 0 || height <= 0) {
            LOG.error((Object)String.format("Invalid image size %d x %d", width, height));
            return null;
        }
        if (compressionMethod != 0) {
            LOG.error((Object)String.format("Unknown PNG compression method %d.", compressionMethod));
            return null;
        }
        if (filterMethod != 0) {
            LOG.error((Object)String.format("Unknown PNG filtering method %d.", compressionMethod));
            return null;
        }
        if (interlaceMethod != 0) {
            LOG.debug((Object)String.format("Can't handle interlace method %d.", interlaceMethod));
            return null;
        }
        state.width = width;
        state.height = height;
        state.bitsPerComponent = bitDepth;
        switch (colorType) {
            case 0: {
                LOG.debug((Object)"Can't handle grayscale yet.");
                return null;
            }
            case 2: {
                if (state.tRNS != null) {
                    LOG.debug((Object)"Can't handle images with transparent colors.");
                    return null;
                }
                return PNGConverter.buildImageObject(doc, state);
            }
            case 3: {
                return PNGConverter.buildIndexImage(doc, state);
            }
            case 4: {
                LOG.debug((Object)"Can't handle grayscale with alpha, would need to separate alpha from image data");
                return null;
            }
            case 6: {
                LOG.debug((Object)"Can't handle truecolor with alpha, would need to separate alpha from image data");
                return null;
            }
        }
        LOG.error((Object)("Unknown PNG color type " + colorType));
        return null;
    }

    private static PDImageXObject buildIndexImage(PDDocument doc, PNGConverterState state) throws IOException {
        Chunk plte = state.PLTE;
        if (plte == null) {
            LOG.error((Object)"Indexed image without PLTE chunk.");
            return null;
        }
        if (plte.length % 3 != 0) {
            LOG.error((Object)"PLTE table corrupted, last (r,g,b) tuple is not complete.");
            return null;
        }
        if (state.bitsPerComponent > 8) {
            LOG.debug((Object)String.format("Can only convert indexed images with bit depth <= 8, not %d.", state.bitsPerComponent));
            return null;
        }
        PDImageXObject image = PNGConverter.buildImageObject(doc, state);
        if (image == null) {
            return null;
        }
        int highVal = plte.length / 3 - 1;
        if (highVal > 255) {
            LOG.error((Object)String.format("Too much colors in PLTE, only 256 allowed, found %d colors.", highVal + 1));
            return null;
        }
        PNGConverter.setupIndexedColorSpace(doc, plte, image, highVal);
        if (state.tRNS != null) {
            image.getCOSObject().setItem(COSName.SMASK, (COSObjectable)PNGConverter.buildTransparencyMaskFromIndexedData(doc, image, state));
        }
        return image;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static PDImageXObject buildTransparencyMaskFromIndexedData(PDDocument doc, PDImageXObject image, PNGConverterState state) throws IOException {
        Filter flateDecode = FilterFactory.INSTANCE.getFilter(COSName.FLATE_DECODE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        COSDictionary decodeParams = PNGConverter.buildDecodeParams(state, PDDeviceGray.INSTANCE);
        COSDictionary imageDict = new COSDictionary();
        imageDict.setItem(COSName.FILTER, (COSBase)COSName.FLATE_DECODE);
        imageDict.setItem(COSName.DECODE_PARMS, (COSBase)decodeParams);
        flateDecode.decode(PNGConverter.getIDATInputStream(state), outputStream, imageDict, 0);
        int length = image.getWidth() * image.getHeight();
        byte[] bytes = new byte[length];
        byte[] transparencyTable = state.tRNS.getData();
        byte[] decodedIDAT = outputStream.toByteArray();
        MemoryCacheImageInputStream iis = new MemoryCacheImageInputStream(new ByteArrayInputStream(decodedIDAT));
        try {
            int bitsPerComponent = state.bitsPerComponent;
            int w = 0;
            int neededBits = bitsPerComponent * state.width;
            int bitPadding = neededBits % 8;
            for (int i = 0; i < bytes.length; ++i) {
                int idx = (int)iis.readBits(bitsPerComponent);
                bytes[i] = idx < transparencyTable.length ? transparencyTable[idx] : -1;
                if (++w != state.width) continue;
                w = 0;
                iis.readBits(bitPadding);
            }
        }
        finally {
            iis.close();
        }
        return LosslessFactory.prepareImageXObject(doc, bytes, image.getWidth(), image.getHeight(), 8, PDDeviceGray.INSTANCE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void setupIndexedColorSpace(PDDocument doc, Chunk lookupTable, PDImageXObject image, int highVal) throws IOException {
        COSArray indexedArray = new COSArray();
        indexedArray.add(COSName.INDEXED);
        indexedArray.add(image.getColorSpace());
        ((COSDictionary)image.getCOSObject().getItem(COSName.DECODE_PARMS)).setItem(COSName.COLORS, (COSBase)COSInteger.ONE);
        indexedArray.add(COSInteger.get(highVal));
        PDStream colorTable = new PDStream(doc);
        OutputStream colorTableStream = colorTable.createOutputStream(COSName.FLATE_DECODE);
        try {
            colorTableStream.write(lookupTable.bytes, lookupTable.start, lookupTable.length);
        }
        finally {
            colorTableStream.close();
        }
        indexedArray.add(colorTable);
        PDIndexed indexed = new PDIndexed(indexedArray);
        image.setColorSpace(indexed);
    }

    private static PDImageXObject buildImageObject(PDDocument document, PNGConverterState state) throws IOException {
        boolean hasICCColorProfile;
        InputStream encodedByteStream = PNGConverter.getIDATInputStream(state);
        PDDeviceRGB colorSpace = PDDeviceRGB.INSTANCE;
        PDImageXObject imageXObject = new PDImageXObject(document, encodedByteStream, COSName.FLATE_DECODE, state.width, state.height, state.bitsPerComponent, colorSpace);
        COSDictionary decodeParams = PNGConverter.buildDecodeParams(state, colorSpace);
        imageXObject.getCOSObject().setItem(COSName.DECODE_PARMS, (COSBase)decodeParams);
        boolean bl = hasICCColorProfile = state.sRGB != null || state.iCCP != null;
        if (state.gAMA != null && !hasICCColorProfile) {
            if (state.gAMA.length != 4) {
                LOG.error((Object)("Invalid gAMA chunk length " + state.gAMA.length));
                return null;
            }
            float gamma = PNGConverter.readPNGFloat(state.gAMA.bytes, state.gAMA.start);
            if ((double)Math.abs(gamma - 0.45454544f) > 1.0E-5) {
                LOG.debug((Object)String.format("We can't handle gamma of %f yet.", Float.valueOf(gamma)));
                return null;
            }
        }
        if (state.sRGB != null) {
            if (state.sRGB.length != 1) {
                LOG.error((Object)String.format("sRGB chunk has an invalid length of %d", state.sRGB.length));
                return null;
            }
            byte renderIntent = state.sRGB.bytes[state.sRGB.start];
            COSName value = PNGConverter.mapPNGRenderIntent(renderIntent);
            imageXObject.getCOSObject().setItem(COSName.INTENT, (COSBase)value);
        }
        if (state.cHRM != null && !hasICCColorProfile) {
            if (state.cHRM.length != 32) {
                LOG.error((Object)("Invalid cHRM chunk length " + state.cHRM.length));
                return null;
            }
            LOG.debug((Object)"We can not handle cHRM chunks yet.");
            return null;
        }
        if (state.iCCP != null || state.sRGB != null) {
            COSStream cosStream = PNGConverter.createCOSStreamwithIccProfile(document, colorSpace, state);
            if (cosStream == null) {
                return null;
            }
            COSArray array = new COSArray();
            array.add(COSName.ICCBASED);
            array.add(cosStream);
            PDICCBased profile = PDICCBased.create(array, null);
            imageXObject.setColorSpace(profile);
        }
        return imageXObject;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static COSStream createCOSStreamwithIccProfile(PDDocument document, PDColorSpace colorSpace, PNGConverterState state) throws IOException {
        int numberOfComponents = colorSpace.getNumberOfComponents();
        COSStream cosStream = document.getDocument().createCOSStream();
        cosStream.setInt(COSName.N, numberOfComponents);
        cosStream.setItem(COSName.ALTERNATE, (COSBase)(numberOfComponents == 1 ? COSName.DEVICEGRAY : COSName.DEVICERGB));
        cosStream.setItem(COSName.FILTER, (COSBase)COSName.FLATE_DECODE);
        if (state.iCCP != null) {
            int iccProfileDataStart;
            for (iccProfileDataStart = 0; iccProfileDataStart < 80 && iccProfileDataStart < state.iCCP.length && state.iCCP.bytes[state.iCCP.start + iccProfileDataStart] != 0; ++iccProfileDataStart) {
            }
            if (++iccProfileDataStart >= state.iCCP.length) {
                LOG.error((Object)"Invalid iCCP chunk, to few bytes");
                return null;
            }
            byte compressionMethod = state.iCCP.bytes[state.iCCP.start + iccProfileDataStart];
            if (compressionMethod != 0) {
                LOG.error((Object)String.format("iCCP chunk: invalid compression method %d", compressionMethod));
                return null;
            }
            ++iccProfileDataStart;
            OutputStream rawOutputStream = cosStream.createRawOutputStream();
            try {
                rawOutputStream.write(state.iCCP.bytes, state.iCCP.start + iccProfileDataStart, state.iCCP.length - iccProfileDataStart);
            }
            finally {
                rawOutputStream.close();
            }
        }
        ICC_Profile rgbProfile = ICC_Profile.getInstance(1000);
        OutputStream outputStream = cosStream.createOutputStream();
        try {
            outputStream.write(rgbProfile.getData());
        }
        finally {
            outputStream.close();
        }
        return cosStream;
    }

    private static COSDictionary buildDecodeParams(PNGConverterState state, PDColorSpace colorSpace) {
        COSDictionary decodeParms = new COSDictionary();
        decodeParms.setItem(COSName.BITS_PER_COMPONENT, (COSBase)COSInteger.get(state.bitsPerComponent));
        decodeParms.setItem(COSName.PREDICTOR, (COSBase)COSInteger.get(15L));
        decodeParms.setItem(COSName.COLUMNS, (COSBase)COSInteger.get(state.width));
        decodeParms.setItem(COSName.COLORS, (COSBase)COSInteger.get(colorSpace.getNumberOfComponents()));
        return decodeParms;
    }

    private static InputStream getIDATInputStream(PNGConverterState state) {
        MultipleInputStream inputStream = new MultipleInputStream();
        for (Chunk idat : state.IDATs) {
            inputStream.inputStreams.add(new ByteArrayInputStream(idat.bytes, idat.start, idat.length));
        }
        return inputStream;
    }

    static COSName mapPNGRenderIntent(int renderIntent) {
        COSName value;
        switch (renderIntent) {
            case 0: {
                value = COSName.PERCEPTUAL;
                break;
            }
            case 1: {
                value = COSName.RELATIVE_COLORIMETRIC;
                break;
            }
            case 2: {
                value = COSName.SATURATION;
                break;
            }
            case 3: {
                value = COSName.ABSOLUTE_COLORIMETRIC;
                break;
            }
            default: {
                value = null;
            }
        }
        return value;
    }

    static boolean checkConverterState(PNGConverterState state) {
        if (state == null) {
            return false;
        }
        if (state.IHDR == null || !PNGConverter.checkChunkSane(state.IHDR)) {
            LOG.error((Object)"Invalid IHDR chunk.");
            return false;
        }
        if (!PNGConverter.checkChunkSane(state.PLTE)) {
            LOG.error((Object)"Invalid PLTE chunk.");
            return false;
        }
        if (!PNGConverter.checkChunkSane(state.iCCP)) {
            LOG.error((Object)"Invalid iCCP chunk.");
            return false;
        }
        if (!PNGConverter.checkChunkSane(state.tRNS)) {
            LOG.error((Object)"Invalid tRNS chunk.");
            return false;
        }
        if (!PNGConverter.checkChunkSane(state.sRGB)) {
            LOG.error((Object)"Invalid sRGB chunk.");
            return false;
        }
        if (!PNGConverter.checkChunkSane(state.cHRM)) {
            LOG.error((Object)"Invalid cHRM chunk.");
            return false;
        }
        if (!PNGConverter.checkChunkSane(state.gAMA)) {
            LOG.error((Object)"Invalid gAMA chunk.");
            return false;
        }
        if (state.IDATs.isEmpty()) {
            LOG.error((Object)"No IDAT chunks.");
            return false;
        }
        for (Chunk idat : state.IDATs) {
            if (PNGConverter.checkChunkSane(idat)) continue;
            LOG.error((Object)"Invalid IDAT chunk.");
            return false;
        }
        return true;
    }

    static boolean checkChunkSane(Chunk chunk) {
        if (chunk == null) {
            return true;
        }
        if (chunk.start + chunk.length > chunk.bytes.length) {
            return false;
        }
        if (chunk.start < 4) {
            return false;
        }
        int ourCRC = PNGConverter.crc(chunk.bytes, chunk.start - 4, chunk.length + 4);
        if (ourCRC != chunk.crc) {
            LOG.error((Object)String.format("Invalid CRC %08X on chunk %08X, expected %08X.", ourCRC, chunk.chunkType, chunk.crc));
            return false;
        }
        return true;
    }

    private static int readInt(byte[] data, int offset) {
        int b1 = (data[offset] & 0xFF) << 24;
        int b2 = (data[offset + 1] & 0xFF) << 16;
        int b3 = (data[offset + 2] & 0xFF) << 8;
        int b4 = data[offset + 3] & 0xFF;
        return b1 | b2 | b3 | b4;
    }

    private static float readPNGFloat(byte[] bytes, int offset) {
        int v = PNGConverter.readInt(bytes, offset);
        return (float)v / 100000.0f;
    }

    private static PNGConverterState parsePNGChunks(byte[] imageData) {
        if (imageData.length < 20) {
            LOG.error((Object)("ByteArray way to small: " + imageData.length));
            return null;
        }
        PNGConverterState state = new PNGConverterState();
        int ptr = 8;
        int firstChunkType = PNGConverter.readInt(imageData, ptr + 4);
        if (firstChunkType != 1229472850) {
            LOG.error((Object)String.format("First Chunktype was %08X, not IHDR", firstChunkType));
            return null;
        }
        while (ptr + 12 <= imageData.length) {
            int chunkLength = PNGConverter.readInt(imageData, ptr);
            int chunkType = PNGConverter.readInt(imageData, ptr + 4);
            if ((ptr += 8) + chunkLength + 4 > imageData.length) {
                LOG.error((Object)("Not enough bytes. At offset " + ptr + " are " + chunkLength + " bytes expected. Overall length is " + imageData.length));
                return null;
            }
            Chunk chunk = new Chunk();
            chunk.chunkType = chunkType;
            chunk.bytes = imageData;
            chunk.start = ptr;
            chunk.length = chunkLength;
            switch (chunkType) {
                case 1229472850: {
                    if (state.IHDR != null) {
                        LOG.error((Object)"Two IHDR chunks? There is something wrong.");
                        return null;
                    }
                    state.IHDR = chunk;
                    break;
                }
                case 1229209940: {
                    state.IDATs.add(chunk);
                    break;
                }
                case 1347179589: {
                    if (state.PLTE != null) {
                        LOG.error((Object)"Two PLTE chunks? There is something wrong.");
                        return null;
                    }
                    state.PLTE = chunk;
                    break;
                }
                case 1229278788: {
                    return state;
                }
                case 1951551059: {
                    if (state.tRNS != null) {
                        LOG.error((Object)"Two tRNS chunks? There is something wrong.");
                        return null;
                    }
                    state.tRNS = chunk;
                    break;
                }
                case 1732332865: {
                    state.gAMA = chunk;
                    break;
                }
                case 1665684045: {
                    state.cHRM = chunk;
                    break;
                }
                case 1766015824: {
                    state.iCCP = chunk;
                    break;
                }
                case 1933723988: {
                    LOG.debug((Object)"Can't convert PNGs with sBIT chunk.");
                    break;
                }
                case 1934772034: {
                    state.sRGB = chunk;
                    break;
                }
                case 1767135348: 
                case 1950701684: 
                case 2052348020: {
                    break;
                }
                case 1799506759: {
                    break;
                }
                case 1749635924: {
                    break;
                }
                case 1883789683: {
                    break;
                }
                case 1934642260: {
                    break;
                }
                case 1950960965: {
                    break;
                }
                default: {
                    LOG.debug((Object)String.format("Unknown chunk type %08X, skipping.", chunkType));
                }
            }
            chunk.crc = PNGConverter.readInt(imageData, ptr += chunkLength);
            ptr += 4;
        }
        LOG.error((Object)"No IEND chunk found.");
        return null;
    }

    private static void makeCrcTable() {
        for (int n = 0; n < 256; ++n) {
            int c = n;
            for (int k = 0; k < 8; ++k) {
                if ((c & 1) != 0) {
                    c = 0xEDB88320 ^ c >>> 1;
                    continue;
                }
                c >>>= 1;
            }
            PNGConverter.CRC_TABLE[n] = c;
        }
    }

    private static int updateCrc(byte[] buf, int offset, int len) {
        int c = -1;
        int end = offset + len;
        for (int n = offset; n < end; ++n) {
            c = CRC_TABLE[(c ^ buf[n]) & 0xFF] ^ c >>> 8;
        }
        return c;
    }

    static int crc(byte[] buf, int offset, int len) {
        return ~PNGConverter.updateCrc(buf, offset, len);
    }

    static {
        PNGConverter.makeCrcTable();
    }

    static final class PNGConverterState {
        List<Chunk> IDATs = new ArrayList<Chunk>();
        Chunk IHDR;
        Chunk PLTE;
        Chunk iCCP;
        Chunk tRNS;
        Chunk sRGB;
        Chunk gAMA;
        Chunk cHRM;
        int width;
        int height;
        int bitsPerComponent;

        PNGConverterState() {
        }
    }

    static final class Chunk {
        byte[] bytes;
        int chunkType;
        int crc;
        int start;
        int length;

        Chunk() {
        }

        byte[] getData() {
            return Arrays.copyOfRange(this.bytes, this.start, this.start + this.length);
        }
    }

    private static class MultipleInputStream
    extends InputStream {
        List<InputStream> inputStreams = new ArrayList<InputStream>();
        int currentStreamIdx;
        InputStream currentStream;

        private MultipleInputStream() {
        }

        private boolean ensureStream() {
            if (this.currentStream == null) {
                if (this.currentStreamIdx >= this.inputStreams.size()) {
                    return false;
                }
                this.currentStream = this.inputStreams.get(this.currentStreamIdx++);
            }
            return true;
        }

        @Override
        public int read() throws IOException {
            if (!this.ensureStream()) {
                return -1;
            }
            int ret = this.currentStream.read();
            if (ret == -1) {
                this.currentStream = null;
                return this.read();
            }
            return ret;
        }

        @Override
        public int available() throws IOException {
            if (!this.ensureStream()) {
                return 0;
            }
            return 1;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (!this.ensureStream()) {
                return -1;
            }
            int ret = this.currentStream.read(b, off, len);
            if (ret == -1) {
                this.currentStream = null;
                return this.read(b, off, len);
            }
            return ret;
        }
    }
}

