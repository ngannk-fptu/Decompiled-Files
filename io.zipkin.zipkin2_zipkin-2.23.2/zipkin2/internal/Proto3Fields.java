/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.internal.ReadBuffer;
import zipkin2.internal.WriteBuffer;

final class Proto3Fields {
    static final int WIRETYPE_VARINT = 0;
    static final int WIRETYPE_FIXED64 = 1;
    static final int WIRETYPE_LENGTH_DELIMITED = 2;
    static final int WIRETYPE_FIXED32 = 5;

    Proto3Fields() {
    }

    static int sizeOfLengthDelimitedField(int sizeInBytes) {
        return 1 + WriteBuffer.varintSizeInBytes(sizeInBytes) + sizeInBytes;
    }

    static final class Fixed32Field
    extends Field {
        Fixed32Field(int key) {
            super(key);
            assert (this.wireType == 5);
        }

        int sizeInBytes(int number) {
            if (number == 0) {
                return 0;
            }
            return 5;
        }
    }

    static final class BooleanField
    extends Field {
        BooleanField(int key) {
            super(key);
            assert (this.wireType == 0);
        }

        int sizeInBytes(boolean bool) {
            return bool ? 2 : 0;
        }

        void write(WriteBuffer b, boolean bool) {
            if (!bool) {
                return;
            }
            b.writeByte(this.key);
            b.writeByte(1);
        }

        boolean read(ReadBuffer b) {
            byte bool = b.readByte();
            if (bool < 0 || bool > 1) {
                throw new IllegalArgumentException("Malformed: invalid boolean value at byte " + b.pos());
            }
            return bool == 1;
        }
    }

    static class VarintField
    extends Field {
        VarintField(int key) {
            super(key);
            assert (this.wireType == 0);
        }

        int sizeInBytes(int number) {
            return number != 0 ? 1 + WriteBuffer.varintSizeInBytes(number) : 0;
        }

        void write(WriteBuffer b, int number) {
            if (number == 0) {
                return;
            }
            b.writeByte(this.key);
            b.writeVarint(number);
        }

        int sizeInBytes(long number) {
            return number != 0L ? 1 + WriteBuffer.varintSizeInBytes(number) : 0;
        }

        void write(WriteBuffer b, long number) {
            if (number == 0L) {
                return;
            }
            b.writeByte(this.key);
            b.writeVarint(number);
        }
    }

    static final class Fixed64Field
    extends Field {
        Fixed64Field(int key) {
            super(key);
            assert (this.wireType == 1);
        }

        void write(WriteBuffer b, long number) {
            if (number == 0L) {
                return;
            }
            b.writeByte(this.key);
            b.writeLongLe(number);
        }

        int sizeInBytes(long number) {
            if (number == 0L) {
                return 0;
            }
            return 9;
        }

        long readValue(ReadBuffer buffer) {
            return buffer.readLongLe();
        }
    }

    static class Utf8Field
    extends LengthDelimitedField<String> {
        Utf8Field(int key) {
            super(key);
        }

        @Override
        int sizeOfValue(String utf8) {
            return utf8 != null ? WriteBuffer.utf8SizeInBytes(utf8) : 0;
        }

        @Override
        void writeValue(WriteBuffer b, String utf8) {
            b.writeUtf8(utf8);
        }

        @Override
        String readValue(ReadBuffer buffer, int length) {
            return buffer.readUtf8(length);
        }
    }

    static class HexField
    extends LengthDelimitedField<String> {
        HexField(int key) {
            super(key);
        }

        @Override
        int sizeOfValue(String hex) {
            if (hex == null) {
                return 0;
            }
            return hex.length() / 2;
        }

        @Override
        void writeValue(WriteBuffer b, String hex) {
            int length = hex.length();
            for (int i = 0; i < length; ++i) {
                int d1 = HexField.decodeLowerHex(hex.charAt(i++)) << 4;
                int d2 = HexField.decodeLowerHex(hex.charAt(i));
                b.writeByte((byte)(d1 + d2));
            }
        }

        static int decodeLowerHex(char c) {
            if (c >= '0' && c <= '9') {
                return c - 48;
            }
            if (c >= 'a' && c <= 'f') {
                return c - 97 + 10;
            }
            throw new AssertionError((Object)("not lowerHex " + c));
        }

        @Override
        String readValue(ReadBuffer buffer, int length) {
            return buffer.readBytesAsHex(length);
        }
    }

    static class BytesField
    extends LengthDelimitedField<byte[]> {
        BytesField(int key) {
            super(key);
        }

        @Override
        int sizeOfValue(byte[] bytes) {
            return bytes.length;
        }

        @Override
        void writeValue(WriteBuffer b, byte[] bytes) {
            b.write(bytes);
        }

        @Override
        byte[] readValue(ReadBuffer b, int length) {
            return b.readBytes(length);
        }
    }

    static abstract class LengthDelimitedField<T>
    extends Field {
        LengthDelimitedField(int key) {
            super(key);
            assert (this.wireType == 2);
        }

        final int sizeInBytes(T value) {
            if (value == null) {
                return 0;
            }
            int sizeOfValue = this.sizeOfValue(value);
            return Proto3Fields.sizeOfLengthDelimitedField(sizeOfValue);
        }

        final void write(WriteBuffer b, T value) {
            if (value == null) {
                return;
            }
            int sizeOfValue = this.sizeOfValue(value);
            b.writeByte(this.key);
            b.writeVarint(sizeOfValue);
            this.writeValue(b, value);
        }

        final T readLengthPrefixAndValue(ReadBuffer b) {
            int length = b.readVarint32();
            if (length == 0) {
                return null;
            }
            return this.readValue(b, length);
        }

        abstract int sizeOfValue(T var1);

        abstract void writeValue(WriteBuffer var1, T var2);

        abstract T readValue(ReadBuffer var1, int var2);
    }

    static class Field {
        final int fieldNumber;
        final int wireType;
        final int key;

        Field(int key) {
            this(key >>> 3, key & 7, key);
        }

        Field(int fieldNumber, int wireType, int key) {
            this.fieldNumber = fieldNumber;
            this.wireType = wireType;
            this.key = key;
        }

        static int fieldNumber(int key, int byteL) {
            int fieldNumber = key >>> 3;
            if (fieldNumber != 0) {
                return fieldNumber;
            }
            throw new IllegalArgumentException("Malformed: fieldNumber was zero at byte " + byteL);
        }

        static int wireType(int key, int byteL) {
            int wireType = key & 7;
            if (wireType != 0 && wireType != 1 && wireType != 2 && wireType != 5) {
                throw new IllegalArgumentException("Malformed: invalid wireType " + wireType + " at byte " + byteL);
            }
            return wireType;
        }

        static boolean skipValue(ReadBuffer buffer, int wireType) {
            int remaining = buffer.available();
            switch (wireType) {
                case 0: {
                    for (int i = 0; i < remaining; ++i) {
                        if (buffer.readByte() < 0) continue;
                        return true;
                    }
                    return false;
                }
                case 1: {
                    return buffer.skip(8L) == 8L;
                }
                case 2: {
                    int length = buffer.readVarint32();
                    return buffer.skip(length) == (long)length;
                }
                case 5: {
                    return buffer.skip(4L) == 4L;
                }
            }
            throw new IllegalArgumentException("Malformed: invalid wireType " + wireType + " at byte " + buffer.pos());
        }
    }
}

