/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class Thumbnail {
    public static final int OFFSET_CFTAG = 4;
    public static final int OFFSET_CF = 8;
    public static final int OFFSET_WMFDATA = 20;
    public static final int CFTAG_WINDOWS = -1;
    public static final int CFTAG_MACINTOSH = -2;
    public static final int CFTAG_FMTID = -3;
    public static final int CFTAG_NODATA = 0;
    public static final int CF_METAFILEPICT = 3;
    public static final int CF_DIB = 8;
    public static final int CF_ENHMETAFILE = 14;
    public static final int CF_BITMAP = 2;
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    private byte[] _thumbnailData;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public Thumbnail() {
    }

    public Thumbnail(byte[] thumbnailData) {
        this._thumbnailData = thumbnailData;
    }

    public byte[] getThumbnail() {
        return this._thumbnailData;
    }

    public void setThumbnail(byte[] thumbnail) {
        this._thumbnailData = thumbnail;
    }

    public long getClipboardFormatTag() {
        return LittleEndian.getInt(this.getThumbnail(), 4);
    }

    public long getClipboardFormat() throws HPSFException {
        if (this.getClipboardFormatTag() != -1L) {
            throw new HPSFException("Clipboard Format Tag of Thumbnail must be CFTAG_WINDOWS.");
        }
        return LittleEndian.getInt(this.getThumbnail(), 8);
    }

    public byte[] getThumbnailAsWMF() throws HPSFException {
        if (this.getClipboardFormatTag() != -1L) {
            throw new HPSFException("Clipboard Format Tag of Thumbnail must be CFTAG_WINDOWS.");
        }
        if (this.getClipboardFormat() != 3L) {
            throw new HPSFException("Clipboard Format of Thumbnail must be CF_METAFILEPICT.");
        }
        byte[] thumbnail = this.getThumbnail();
        return IOUtils.safelyClone(thumbnail, 20, thumbnail.length - 20, MAX_RECORD_LENGTH);
    }
}

