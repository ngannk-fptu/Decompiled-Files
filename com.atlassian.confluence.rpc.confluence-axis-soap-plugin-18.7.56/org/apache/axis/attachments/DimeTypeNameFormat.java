/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.attachments;

import org.apache.axis.utils.Messages;

public final class DimeTypeNameFormat {
    private byte format = 0;
    static final byte NOCHANGE_VALUE = 0;
    static final byte MIME_VALUE = 1;
    static final byte URI_VALUE = 2;
    static final byte UNKNOWN_VALUE = 3;
    static final byte NODATA_VALUE = 4;
    static final DimeTypeNameFormat NOCHANGE = new DimeTypeNameFormat(0);
    public static final DimeTypeNameFormat MIME = new DimeTypeNameFormat(1);
    public static final DimeTypeNameFormat URI = new DimeTypeNameFormat(2);
    public static final DimeTypeNameFormat UNKNOWN = new DimeTypeNameFormat(3);
    static final DimeTypeNameFormat NODATA = new DimeTypeNameFormat(4);
    private static String[] toEnglish = new String[]{"NOCHANGE", "MIME", "URI", "UNKNOWN", "NODATA"};
    private static DimeTypeNameFormat[] fromByte = new DimeTypeNameFormat[]{NOCHANGE, MIME, URI, UNKNOWN, NODATA};

    private DimeTypeNameFormat() {
    }

    private DimeTypeNameFormat(byte f) {
        this.format = f;
    }

    public final String toString() {
        return toEnglish[this.format];
    }

    public final byte toByte() {
        return this.format;
    }

    public int hashCode() {
        return this.format;
    }

    public final boolean equals(Object x) {
        if (x == null) {
            return false;
        }
        if (!(x instanceof DimeTypeNameFormat)) {
            return false;
        }
        return ((DimeTypeNameFormat)x).format == this.format;
    }

    public static DimeTypeNameFormat parseByte(byte x) {
        if (x < 0 || x > fromByte.length) {
            throw new IllegalArgumentException(Messages.getMessage("attach.DimeStreamBadType", "" + x));
        }
        return fromByte[x];
    }

    public static DimeTypeNameFormat parseByte(Byte x) {
        return DimeTypeNameFormat.parseByte((byte)x);
    }
}

