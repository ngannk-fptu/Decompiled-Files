/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.internal.ReadBuffer;
import zipkin2.internal.WriteBuffer;

final class ThriftField {
    static final byte TYPE_STOP = 0;
    static final byte TYPE_BOOL = 2;
    static final byte TYPE_BYTE = 3;
    static final byte TYPE_DOUBLE = 4;
    static final byte TYPE_I16 = 6;
    static final byte TYPE_I32 = 8;
    static final byte TYPE_I64 = 10;
    static final byte TYPE_STRING = 11;
    static final byte TYPE_STRUCT = 12;
    static final byte TYPE_MAP = 13;
    static final byte TYPE_SET = 14;
    static final byte TYPE_LIST = 15;
    final byte type;
    final int id;

    ThriftField(byte type, int id) {
        this.type = type;
        this.id = id;
    }

    void write(WriteBuffer buffer) {
        buffer.writeByte(this.type);
        buffer.writeByte(this.id >>> 8 & 0xFF);
        buffer.writeByte(this.id & 0xFF);
    }

    static ThriftField read(ReadBuffer bytes) {
        byte type;
        return new ThriftField(type, (type = bytes.readByte()) == 0 ? (short)0 : bytes.readShort());
    }

    boolean isEqualTo(ThriftField that) {
        return this.type == that.type && this.id == that.id;
    }
}

