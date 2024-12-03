/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StringUtil;

public class StringChunk
extends Chunk {
    private static final String DEFAULT_ENCODING = "CP1252";
    private String encoding7Bit = "CP1252";
    private byte[] rawValue;
    private String value;

    public StringChunk(String namePrefix, int chunkId, Types.MAPIType type) {
        super(namePrefix, chunkId, type);
    }

    public StringChunk(int chunkId, Types.MAPIType type) {
        super(chunkId, type);
    }

    public String get7BitEncoding() {
        return this.encoding7Bit;
    }

    public void set7BitEncoding(String encoding) {
        this.encoding7Bit = encoding;
        if (this.getType() == Types.ASCII_STRING) {
            this.parseString();
        }
    }

    @Override
    public void readValue(InputStream value) throws IOException {
        this.rawValue = IOUtils.toByteArray(value);
        this.parseString();
    }

    private void parseString() {
        String tmpValue;
        if (this.getType() == Types.ASCII_STRING) {
            tmpValue = StringChunk.parseAs7BitData(this.rawValue, this.encoding7Bit);
        } else if (this.getType() == Types.UNICODE_STRING) {
            tmpValue = StringUtil.getFromUnicodeLE(this.rawValue);
        } else {
            throw new IllegalArgumentException("Invalid type " + this.getType() + " for String Chunk");
        }
        this.value = tmpValue.replace("\u0000", "");
    }

    @Override
    public void writeValue(OutputStream out) throws IOException {
        out.write(this.rawValue);
    }

    private void storeString() {
        if (this.getType() == Types.ASCII_STRING) {
            this.rawValue = this.value.getBytes(Charset.forName(this.encoding7Bit));
        } else if (this.getType() == Types.UNICODE_STRING) {
            this.rawValue = StringUtil.getToUnicodeLE(this.value);
        } else {
            throw new IllegalArgumentException("Invalid type " + this.getType() + " for String Chunk");
        }
    }

    public String getValue() {
        return this.value;
    }

    public byte[] getRawValue() {
        return this.rawValue;
    }

    public void setValue(String str) {
        this.value = str;
        this.storeString();
    }

    public String toString() {
        return this.value;
    }

    protected static String parseAs7BitData(byte[] data) {
        return StringChunk.parseAs7BitData(data, DEFAULT_ENCODING);
    }

    protected static String parseAs7BitData(byte[] data, String encoding) {
        if ("ansi".equals(encoding)) {
            encoding = DEFAULT_ENCODING;
        }
        return new String(data, Charset.forName(encoding));
    }
}

