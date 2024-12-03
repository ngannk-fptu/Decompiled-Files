/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.math.BigInteger;
import org.apache.tomcat.util.res.StringManager;

public class Asn1Parser {
    private static final StringManager sm = StringManager.getManager(Asn1Parser.class);
    public static final int TAG_INTEGER = 2;
    public static final int TAG_OCTET_STRING = 4;
    public static final int TAG_NULL = 5;
    public static final int TAG_OID = 6;
    public static final int TAG_SEQUENCE = 48;
    public static final int TAG_ATTRIBUTE_BASE = 160;
    private final byte[] source;
    private int pos = 0;

    public Asn1Parser(byte[] source) {
        this.source = source;
    }

    public boolean eof() {
        return this.pos == this.source.length;
    }

    public int peekTag() {
        return this.source[this.pos] & 0xFF;
    }

    public void parseTagSequence() {
        this.parseTag(48);
    }

    public void parseTag(int tag) {
        int value = this.next();
        if (value != tag) {
            throw new IllegalArgumentException(sm.getString("asn1Parser.tagMismatch", tag, value));
        }
    }

    public void parseFullLength() {
        int len = this.parseLength();
        if (len + this.pos != this.source.length) {
            throw new IllegalArgumentException(sm.getString("asn1Parser.lengthInvalid", len, this.source.length - this.pos));
        }
    }

    public int parseLength() {
        int len = this.next();
        if (len > 127) {
            int bytes = len - 128;
            len = 0;
            for (int i = 0; i < bytes; ++i) {
                len <<= 8;
                len += this.next();
            }
        }
        return len;
    }

    public BigInteger parseInt() {
        byte[] val = this.parseBytes(2);
        return new BigInteger(val);
    }

    public byte[] parseOctetString() {
        return this.parseBytes(4);
    }

    public void parseNull() {
        this.parseBytes(5);
    }

    public byte[] parseOIDAsBytes() {
        return this.parseBytes(6);
    }

    public byte[] parseAttributeAsBytes(int index) {
        return this.parseBytes(160 + index);
    }

    private byte[] parseBytes(int tag) {
        this.parseTag(tag);
        int len = this.parseLength();
        byte[] result = new byte[len];
        System.arraycopy(this.source, this.pos, result, 0, result.length);
        this.pos += result.length;
        return result;
    }

    public void parseBytes(byte[] dest) {
        System.arraycopy(this.source, this.pos, dest, 0, dest.length);
        this.pos += dest.length;
    }

    private int next() {
        return this.source[this.pos++] & 0xFF;
    }
}

