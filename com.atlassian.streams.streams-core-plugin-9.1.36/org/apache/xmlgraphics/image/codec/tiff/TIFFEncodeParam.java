/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

import java.util.Iterator;
import org.apache.xmlgraphics.image.codec.tiff.CompressionValue;
import org.apache.xmlgraphics.image.codec.tiff.TIFFField;
import org.apache.xmlgraphics.image.codec.util.ImageEncodeParam;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;

public class TIFFEncodeParam
implements ImageEncodeParam {
    private static final long serialVersionUID = 2471949735040024055L;
    private CompressionValue compression = CompressionValue.NONE;
    private boolean writeTiled;
    private int tileWidth;
    private int tileHeight;
    private Iterator extraImages;
    private TIFFField[] extraFields;
    private boolean convertJPEGRGBToYCbCr = true;
    private int deflateLevel = -1;

    public CompressionValue getCompression() {
        return this.compression;
    }

    public void setCompression(CompressionValue compression) {
        switch (compression) {
            case NONE: 
            case PACKBITS: 
            case DEFLATE: {
                break;
            }
            default: {
                throw new RuntimeException(PropertyUtil.getString("TIFFEncodeParam0"));
            }
        }
        this.compression = compression;
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
        if (deflateLevel != -1) {
            throw new RuntimeException(PropertyUtil.getString("TIFFEncodeParam1"));
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

    public void setExtraFields(TIFFField[] extraFields) {
        this.extraFields = extraFields;
    }

    public TIFFField[] getExtraFields() {
        if (this.extraFields == null) {
            return new TIFFField[0];
        }
        return this.extraFields;
    }
}

