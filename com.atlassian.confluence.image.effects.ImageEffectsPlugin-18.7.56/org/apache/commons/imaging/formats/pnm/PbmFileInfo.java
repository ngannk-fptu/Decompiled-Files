/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pnm;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.formats.pnm.FileInfo;
import org.apache.commons.imaging.formats.pnm.WhiteSpaceReader;

class PbmFileInfo
extends FileInfo {
    private int bitcache;
    private int bitsInCache;

    PbmFileInfo(int width, int height, boolean rawbits) {
        super(width, height, rawbits);
    }

    @Override
    public boolean hasAlpha() {
        return false;
    }

    @Override
    public int getNumComponents() {
        return 1;
    }

    @Override
    public int getBitDepth() {
        return 1;
    }

    @Override
    public ImageFormat getImageType() {
        return ImageFormats.PBM;
    }

    @Override
    public ImageInfo.ColorType getColorType() {
        return ImageInfo.ColorType.BW;
    }

    @Override
    public String getImageTypeDescription() {
        return "PBM: portable bitmap fileformat";
    }

    @Override
    public String getMIMEType() {
        return "image/x-portable-bitmap";
    }

    @Override
    protected void newline() {
        this.bitcache = 0;
        this.bitsInCache = 0;
    }

    @Override
    public int getRGB(InputStream is) throws IOException {
        if (this.bitsInCache < 1) {
            int bits = is.read();
            if (bits < 0) {
                throw new IOException("PBM: Unexpected EOF");
            }
            this.bitcache = 0xFF & bits;
            this.bitsInCache += 8;
        }
        int bit = 1 & this.bitcache >> 7;
        this.bitcache <<= 1;
        --this.bitsInCache;
        if (bit == 0) {
            return -1;
        }
        if (bit == 1) {
            return -16777216;
        }
        throw new IOException("PBM: bad bit: " + bit);
    }

    @Override
    public int getRGB(WhiteSpaceReader wsr) throws IOException {
        int bit = Integer.parseInt(wsr.readtoWhiteSpace());
        if (bit == 0) {
            return -16777216;
        }
        if (bit == 1) {
            return -1;
        }
        throw new IOException("PBM: bad bit: " + bit);
    }
}

