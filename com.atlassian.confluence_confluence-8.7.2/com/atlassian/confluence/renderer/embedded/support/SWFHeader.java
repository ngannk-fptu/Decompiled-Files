/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.renderer.embedded.support;

import com.atlassian.confluence.renderer.embedded.support.PackedBitObj;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SWFHeader {
    private static Logger log = LoggerFactory.getLogger(SWFHeader.class);
    public static final String COMPRESSED = "compressed";
    public static final String UNCOMPRESSED = "uncompressed";
    private String signature;
    private String compressionType;
    private int version;
    private long size;
    private int nbits;
    private int xmax;
    private int ymax;
    private int width;
    private int height;
    private int frameRate;
    private int frameCount;

    private SWFHeader() {
    }

    public static SWFHeader loadHeader(InputStream is) {
        SWFHeader header = new SWFHeader();
        header.manageInputStreamAndParseHeader(is, null);
        return header;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean manageInputStreamAndParseHeader(InputStream is, File file) {
        boolean inputIsSWF = false;
        try {
            if (is == null && file != null) {
                is = new FileInputStream(file);
            }
            if (is != null) {
                inputIsSWF = this.doParseHeader(is);
            }
        }
        catch (FileNotFoundException fnfEx) {
            log.error("SWF file could not be found", (Throwable)fnfEx);
            inputIsSWF = false;
        }
        catch (Exception e) {
            log.error("Failed to parse SWF input", (Throwable)e);
            inputIsSWF = false;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception ex) {
                log.error("Failed to close SWF InputStream", (Throwable)ex);
                inputIsSWF = false;
            }
        }
        return inputIsSWF;
    }

    private boolean doParseHeader(InputStream is) throws Exception {
        byte[] temp = new byte[128];
        byte[] swf = null;
        is.read(temp);
        if (!this.isSWF(temp)) {
            log.error("Input does not match SWF format - incorrect file signature");
            return false;
        }
        this.signature = "" + (char)temp[0] + (char)temp[1] + (char)temp[2];
        if (this.isCompressed(temp[0])) {
            swf = this.uncompressHeader(temp);
            this.compressionType = COMPRESSED;
        } else {
            swf = temp;
            this.compressionType = UNCOMPRESSED;
        }
        this.version = swf[3];
        this.size = this.readSize(swf);
        this.nbits = (swf[8] & 0xFF) >> 3;
        PackedBitObj pbo = this.readPackedBits(swf, 8, 5, this.nbits);
        PackedBitObj pbo2 = this.readPackedBits(swf, pbo.nextByteIndex, pbo.nextBitIndex, this.nbits);
        PackedBitObj pbo3 = this.readPackedBits(swf, pbo2.nextByteIndex, pbo2.nextBitIndex, this.nbits);
        PackedBitObj pbo4 = this.readPackedBits(swf, pbo3.nextByteIndex, pbo3.nextBitIndex, this.nbits);
        this.xmax = pbo2.value;
        this.ymax = pbo4.value;
        this.width = this.convertTwipsToPixels(this.xmax);
        this.height = this.convertTwipsToPixels(this.ymax);
        int bytePointer = pbo4.nextByteIndex + 2;
        this.frameRate = swf[bytePointer];
        int fc1 = swf[++bytePointer] & 0xFF;
        int fc2 = swf[++bytePointer] & 0xFF;
        ++bytePointer;
        this.frameCount = (fc2 << 8) + fc1;
        return true;
    }

    private void read(byte[] output, byte[] input, int offset) {
        System.arraycopy(input, offset, output, 0, output.length - offset);
    }

    private PackedBitObj readPackedBits(byte[] bytes, int byteMarker, int bitMarker, int length) {
        int total = 0;
        int shift = 7 - bitMarker;
        int counter = 0;
        int bitIndex = bitMarker;
        int byteIndex = byteMarker;
        while (counter < length) {
            int i = bitMarker;
            while (i < 8) {
                int bit = (bytes[byteMarker] & 0xFF) >> shift & 1;
                total = (total << 1) + bit;
                bitIndex = i++;
                --shift;
                if (++counter == length) break;
            }
            byteIndex = byteMarker++;
            bitMarker = 0;
            shift = 7;
        }
        return new PackedBitObj(bitIndex, byteIndex, total);
    }

    private int convertTwipsToPixels(int twips) {
        return twips / 20;
    }

    private int convertPixelsToTwips(int pixels) {
        return pixels * 20;
    }

    private boolean isSWF(byte[] signature) {
        String sig = "" + (char)signature[0] + (char)signature[1] + (char)signature[2];
        return sig.equals("FWS") || sig.equals("CWS");
    }

    private boolean isCompressed(int firstByte) {
        return firstByte == 67;
    }

    public boolean isCompressed() {
        boolean result = false;
        if (this.signature.equalsIgnoreCase("CWS")) {
            result = true;
        }
        return result;
    }

    protected byte[] uncompressHeader(byte[] bytes) throws DataFormatException {
        Inflater decompressor = new Inflater();
        byte[] compressed = this.strip(bytes);
        decompressor.setInput(compressed);
        byte[] buffer = new byte[56];
        int count = decompressor.inflate(buffer);
        decompressor.end();
        byte[] swf = new byte[8 + count];
        System.arraycopy(bytes, 0, swf, 0, 8);
        System.arraycopy(buffer, 0, swf, 8, count);
        swf[0] = 70;
        return swf;
    }

    private int readSize(byte[] bytes) {
        int s = 0;
        for (int i = 0; i < 4; ++i) {
            s = (s << 8) + bytes[i + 4];
        }
        s = (s >>> 24 | s >> 8 & 0xFF00 | s << 8 & 0xFF0000 | s << 24) - 1;
        return s;
    }

    private byte[] strip(byte[] bytes) {
        byte[] compressable = new byte[bytes.length - 8];
        System.arraycopy(bytes, 8, compressable, 0, bytes.length - 8);
        return compressable;
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder((Object)this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("signature", (Object)this.getSignature());
        builder.append("version", this.getVersion());
        builder.append("compression", (Object)this.getCompressionType());
        builder.append("size", this.getSize());
        builder.append("nbits", this.getNbits());
        builder.append("xmax", this.getXmax());
        builder.append("ymax", this.getYmax());
        builder.append("width", this.getWidth());
        builder.append("height", this.getHeight());
        builder.append("frameRate", this.getFrameRate());
        builder.append("frameCount", this.getFrameCount());
        return builder.toString();
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public int getFrameRate() {
        return this.frameRate;
    }

    public int getNbits() {
        return this.nbits;
    }

    public String getSignature() {
        return this.signature;
    }

    public long getSize() {
        return this.size;
    }

    public int getVersion() {
        return this.version;
    }

    public int getXmax() {
        return this.xmax;
    }

    public int getYmax() {
        return this.ymax;
    }

    public String getCompressionType() {
        return this.compressionType;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
}

