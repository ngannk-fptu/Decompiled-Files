/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.JaiI18N;
import com.sun.media.jai.codec.TIFFField;
import java.util.Iterator;

public class TIFFEncodeParam
implements ImageEncodeParam {
    public static final int COMPRESSION_NONE = 1;
    public static final int COMPRESSION_PACKBITS = 32773;
    public static final int COMPRESSION_GROUP3_1D = 2;
    public static final int COMPRESSION_GROUP3_2D = 3;
    public static final int COMPRESSION_GROUP4 = 4;
    public static final int COMPRESSION_LZW = 5;
    public static final int COMPRESSION_JPEG_TTN2 = 7;
    public static final int COMPRESSION_DEFLATE = 32946;
    private int compression = 1;
    private boolean reverseFillOrder = false;
    private boolean T4Encode2D = true;
    private boolean T4PadEOLs = false;
    private boolean writeTiled = false;
    private int tileWidth;
    private int tileHeight;
    private Iterator extraImages;
    private TIFFField[] extraFields;
    private boolean convertJPEGRGBToYCbCr = true;
    private JPEGEncodeParam jpegEncodeParam = null;
    private int deflateLevel = -1;
    private boolean isLittleEndian = false;

    public int getCompression() {
        return this.compression;
    }

    public void setCompression(int compression) {
        switch (compression) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 7: 
            case 32773: 
            case 32946: {
                break;
            }
            default: {
                throw new IllegalArgumentException(JaiI18N.getString("TIFFEncodeParam0"));
            }
        }
        this.compression = compression;
    }

    public boolean getReverseFillOrder() {
        return this.reverseFillOrder;
    }

    public void setReverseFillOrder(boolean reverseFillOrder) {
        this.reverseFillOrder = reverseFillOrder;
    }

    public boolean getT4Encode2D() {
        return this.T4Encode2D;
    }

    public void setT4Encode2D(boolean T4Encode2D) {
        this.T4Encode2D = T4Encode2D;
    }

    public boolean getT4PadEOLs() {
        return this.T4PadEOLs;
    }

    public void setT4PadEOLs(boolean T4PadEOLs) {
        this.T4PadEOLs = T4PadEOLs;
    }

    public boolean getWriteTiled() {
        return this.writeTiled;
    }

    public void setWriteTiled(boolean writeTiled) {
        this.writeTiled = writeTiled;
    }

    public void setTileSize(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public int getTileWidth() {
        return this.tileWidth;
    }

    public int getTileHeight() {
        return this.tileHeight;
    }

    public synchronized void setExtraImages(Iterator extraImages) {
        this.extraImages = extraImages;
    }

    public synchronized Iterator getExtraImages() {
        return this.extraImages;
    }

    public void setDeflateLevel(int deflateLevel) {
        if (deflateLevel < 1 && deflateLevel > 9 && deflateLevel != -1) {
            throw new IllegalArgumentException(JaiI18N.getString("TIFFEncodeParam1"));
        }
        this.deflateLevel = deflateLevel;
    }

    public int getDeflateLevel() {
        return this.deflateLevel;
    }

    public void setJPEGCompressRGBToYCbCr(boolean convertJPEGRGBToYCbCr) {
        this.convertJPEGRGBToYCbCr = convertJPEGRGBToYCbCr;
    }

    public boolean getJPEGCompressRGBToYCbCr() {
        return this.convertJPEGRGBToYCbCr;
    }

    public void setJPEGEncodeParam(JPEGEncodeParam jpegEncodeParam) {
        if (jpegEncodeParam != null) {
            jpegEncodeParam = (JPEGEncodeParam)jpegEncodeParam.clone();
            jpegEncodeParam.setWriteTablesOnly(false);
            jpegEncodeParam.setWriteJFIFHeader(false);
        }
        this.jpegEncodeParam = jpegEncodeParam;
    }

    public JPEGEncodeParam getJPEGEncodeParam() {
        if (this.jpegEncodeParam == null) {
            this.jpegEncodeParam = new JPEGEncodeParam();
            this.jpegEncodeParam.setWriteTablesOnly(false);
            this.jpegEncodeParam.setWriteImageOnly(true);
            this.jpegEncodeParam.setWriteJFIFHeader(false);
        }
        return this.jpegEncodeParam;
    }

    public void setExtraFields(TIFFField[] extraFields) {
        this.extraFields = extraFields;
    }

    public TIFFField[] getExtraFields() {
        return this.extraFields;
    }

    public void setLittleEndian(boolean isLittleEndian) {
        this.isLittleEndian = isLittleEndian;
    }

    public boolean getLittleEndian() {
        return this.isLittleEndian;
    }
}

