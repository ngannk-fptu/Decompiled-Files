/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.StringUtil;

public final class WriteAccessRecord
extends StandardRecord {
    public static final short sid = 92;
    private static final BitField UTF16FLAG = BitFieldFactory.getInstance(1);
    private static final byte PAD_CHAR = 32;
    private static final int DATA_SIZE = 112;
    private static final int STRING_SIZE = 109;
    private static final byte[] PADDING = new byte[109];
    private String field_1_username;

    public WriteAccessRecord() {
        this.setUsername("");
    }

    public WriteAccessRecord(WriteAccessRecord other) {
        super(other);
        this.field_1_username = other.field_1_username;
    }

    public WriteAccessRecord(RecordInputStream in) {
        Charset charset;
        int byteCnt;
        byte[] data;
        if (in.remaining() > 112) {
            throw new RecordFormatException("Expected data size (112) but got (" + in.remaining() + ")");
        }
        int nChars = in.readUShort();
        int is16BitFlag = in.readUByte();
        if (nChars > 109 || (is16BitFlag & 0xFE) != 0) {
            if (in.isEncrypted()) {
                int i;
                data = IOUtils.safelyAllocate(in.remaining(), 109);
                in.readPlain(data, 0, data.length);
                for (i = data.length; i > 0 && data[i - 1] == 32; --i) {
                }
                byteCnt = i;
                charset = data.length > 1 && data[1] == 0 ? StandardCharsets.UTF_16LE : StandardCharsets.ISO_8859_1;
            } else {
                byteCnt = 3 + in.remaining();
                data = IOUtils.safelyAllocate(byteCnt, 112);
                LittleEndian.putUShort(data, 0, nChars);
                LittleEndian.putByte(data, 2, is16BitFlag);
                in.readFully(data, 3, byteCnt - 3);
                charset = StandardCharsets.UTF_8;
            }
        } else {
            data = IOUtils.safelyAllocate(in.remaining(), 109);
            in.readFully(data);
            if (UTF16FLAG.isSet(is16BitFlag)) {
                byteCnt = Math.min(nChars * 2, data.length);
                charset = StandardCharsets.UTF_16LE;
            } else {
                byteCnt = Math.min(nChars, data.length);
                charset = StandardCharsets.ISO_8859_1;
            }
        }
        String rawValue = new String(data, 0, byteCnt, charset);
        this.setUsername(rawValue.trim());
    }

    public void setUsername(String username) {
        boolean is16bit = StringUtil.hasMultibyte(username);
        int encodedByteCount = username.length() * (is16bit ? 2 : 1);
        if (encodedByteCount > 109) {
            throw new IllegalArgumentException("Name is too long: " + username);
        }
        this.field_1_username = username;
    }

    public String getUsername() {
        return this.field_1_username;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        String username = this.getUsername();
        boolean is16bit = StringUtil.hasMultibyte(username);
        out.writeShort(username.length());
        out.writeByte(is16bit ? 1 : 0);
        byte[] buf = (byte[])PADDING.clone();
        if (is16bit) {
            StringUtil.putUnicodeLE(username, buf, 0);
        } else {
            StringUtil.putCompressedUnicode(username, buf, 0);
        }
        out.write(buf);
    }

    @Override
    protected int getDataSize() {
        return 112;
    }

    @Override
    public short getSid() {
        return 92;
    }

    @Override
    public WriteAccessRecord copy() {
        return new WriteAccessRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.WRITE_ACCESS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("username", this::getUsername);
    }

    static {
        Arrays.fill(PADDING, (byte)32);
    }
}

