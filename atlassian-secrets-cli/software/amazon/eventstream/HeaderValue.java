/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.eventstream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import software.amazon.eventstream.HeaderType;
import software.amazon.eventstream.Utils;

public abstract class HeaderValue {
    public static HeaderValue fromBoolean(boolean value) {
        return new BooleanValue(value);
    }

    public static HeaderValue fromByte(byte value) {
        return new ByteValue(value);
    }

    public static HeaderValue fromShort(short value) {
        return new ShortValue(value);
    }

    public static HeaderValue fromInteger(int value) {
        return new IntegerValue(value);
    }

    public static HeaderValue fromLong(long value) {
        return new LongValue(value);
    }

    public static HeaderValue fromByteArray(byte[] bytes) {
        return new ByteArrayValue(bytes);
    }

    public static HeaderValue fromByteBuffer(ByteBuffer buf) {
        buf = buf.duplicate();
        byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes);
        return HeaderValue.fromByteArray(bytes);
    }

    public static HeaderValue fromString(String string) {
        return new StringValue(string);
    }

    public static HeaderValue fromTimestamp(Instant value) {
        return new TimestampValue(value);
    }

    public static HeaderValue fromDate(Date value) {
        return new TimestampValue(value.toInstant());
    }

    public static HeaderValue fromUuid(UUID value) {
        return new UuidValue(value);
    }

    protected HeaderValue() {
    }

    public abstract HeaderType getType();

    public boolean getBoolean() {
        throw new IllegalStateException();
    }

    public byte getByte() {
        throw new IllegalStateException("Expected byte, but type was " + this.getType().name());
    }

    public short getShort() {
        throw new IllegalStateException("Expected short, but type was " + this.getType().name());
    }

    public int getInteger() {
        throw new IllegalStateException("Expected integer, but type was " + this.getType().name());
    }

    public long getLong() {
        throw new IllegalStateException("Expected long, but type was " + this.getType().name());
    }

    public byte[] getByteArray() {
        throw new IllegalStateException();
    }

    public final ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(this.getByteArray());
    }

    public String getString() {
        throw new IllegalStateException();
    }

    public Instant getTimestamp() {
        throw new IllegalStateException("Expected timestamp, but type was " + this.getType().name());
    }

    public Date getDate() {
        return Date.from(this.getTimestamp());
    }

    public UUID getUuid() {
        throw new IllegalStateException("Expected UUID, but type was " + this.getType().name());
    }

    void encode(DataOutputStream dos) throws IOException {
        dos.writeByte(this.getType().headerTypeId);
        this.encodeValue(dos);
    }

    abstract void encodeValue(DataOutputStream var1) throws IOException;

    static HeaderValue decode(ByteBuffer buf) {
        HeaderType type = HeaderType.fromTypeId(buf.get());
        switch (type) {
            case TRUE: {
                return new BooleanValue(true);
            }
            case FALSE: {
                return new BooleanValue(false);
            }
            case BYTE: {
                return new ByteValue(buf.get());
            }
            case SHORT: {
                return new ShortValue(buf.getShort());
            }
            case INTEGER: {
                return HeaderValue.fromInteger(buf.getInt());
            }
            case LONG: {
                return new LongValue(buf.getLong());
            }
            case BYTE_ARRAY: {
                return HeaderValue.fromByteArray(Utils.readBytes(buf));
            }
            case STRING: {
                return HeaderValue.fromString(Utils.readString(buf));
            }
            case TIMESTAMP: {
                return TimestampValue.decode(buf);
            }
            case UUID: {
                return UuidValue.decode(buf);
            }
        }
        throw new IllegalStateException();
    }

    private static final class UuidValue
    extends HeaderValue {
        private final UUID value;

        private UuidValue(UUID value) {
            this.value = Objects.requireNonNull(value);
        }

        static UuidValue decode(ByteBuffer buf) {
            long msb = buf.getLong();
            long lsb = buf.getLong();
            return new UuidValue(new UUID(msb, lsb));
        }

        @Override
        public HeaderType getType() {
            return HeaderType.UUID;
        }

        @Override
        public UUID getUuid() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) throws IOException {
            dos.writeLong(this.value.getMostSignificantBits());
            dos.writeLong(this.value.getLeastSignificantBits());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UuidValue uuidValue = (UuidValue)o;
            return this.value.equals(uuidValue.value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return this.value.toString();
        }
    }

    private static final class TimestampValue
    extends HeaderValue {
        private final Instant value;

        private TimestampValue(Instant value) {
            this.value = Objects.requireNonNull(value);
        }

        static TimestampValue decode(ByteBuffer buf) {
            long epochMillis = buf.getLong();
            return new TimestampValue(Instant.ofEpochMilli(epochMillis));
        }

        @Override
        public HeaderType getType() {
            return HeaderType.TIMESTAMP;
        }

        @Override
        public Instant getTimestamp() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) throws IOException {
            dos.writeLong(this.value.toEpochMilli());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            TimestampValue that = (TimestampValue)o;
            return this.value.equals(that.value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return this.value.toString();
        }
    }

    private static final class StringValue
    extends HeaderValue {
        private final String value;

        private StringValue(String value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public HeaderType getType() {
            return HeaderType.STRING;
        }

        @Override
        public String getString() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) throws IOException {
            Utils.writeString(dos, this.value);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            StringValue that = (StringValue)o;
            return this.value.equals(that.value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return '\"' + this.value + '\"';
        }
    }

    private static final class ByteArrayValue
    extends HeaderValue {
        private final byte[] value;

        private ByteArrayValue(byte[] value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public HeaderType getType() {
            return HeaderType.BYTE_ARRAY;
        }

        @Override
        public byte[] getByteArray() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) throws IOException {
            Utils.writeBytes(dos, this.value);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ByteArrayValue that = (ByteArrayValue)o;
            return Arrays.equals(this.value, that.value);
        }

        public int hashCode() {
            return Arrays.hashCode(this.value);
        }

        public String toString() {
            return Base64.getEncoder().encodeToString(this.value);
        }
    }

    private static final class LongValue
    extends HeaderValue {
        private final long value;

        private LongValue(long value) {
            this.value = value;
        }

        @Override
        public HeaderType getType() {
            return HeaderType.LONG;
        }

        @Override
        public long getLong() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) throws IOException {
            dos.writeLong(this.value);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            LongValue longValue = (LongValue)o;
            return this.value == longValue.value;
        }

        public int hashCode() {
            return (int)(this.value ^ this.value >>> 32);
        }

        public String toString() {
            return String.valueOf(this.value);
        }
    }

    private static final class IntegerValue
    extends HeaderValue {
        private final int value;

        private IntegerValue(int value) {
            this.value = value;
        }

        @Override
        public HeaderType getType() {
            return HeaderType.INTEGER;
        }

        @Override
        public int getInteger() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) throws IOException {
            dos.writeInt(this.value);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            IntegerValue that = (IntegerValue)o;
            return this.value == that.value;
        }

        public int hashCode() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }
    }

    private static final class ShortValue
    extends HeaderValue {
        private final short value;

        private ShortValue(short value) {
            this.value = value;
        }

        @Override
        public HeaderType getType() {
            return HeaderType.SHORT;
        }

        @Override
        public short getShort() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) {
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ShortValue that = (ShortValue)o;
            return this.value == that.value;
        }

        public int hashCode() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }
    }

    private static final class ByteValue
    extends HeaderValue {
        private final byte value;

        private ByteValue(byte value) {
            this.value = value;
        }

        @Override
        public HeaderType getType() {
            return HeaderType.BYTE;
        }

        @Override
        public byte getByte() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) {
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ByteValue that = (ByteValue)o;
            return this.value == that.value;
        }

        public int hashCode() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }
    }

    private static final class BooleanValue
    extends HeaderValue {
        private final boolean value;

        private BooleanValue(boolean value) {
            this.value = value;
        }

        @Override
        public HeaderType getType() {
            if (this.value) {
                return HeaderType.TRUE;
            }
            return HeaderType.FALSE;
        }

        @Override
        public boolean getBoolean() {
            return this.value;
        }

        @Override
        void encodeValue(DataOutputStream dos) {
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BooleanValue that = (BooleanValue)o;
            return this.value == that.value;
        }

        public int hashCode() {
            if (this.value) {
                return 1;
            }
            return 0;
        }

        public String toString() {
            return String.valueOf(this.value);
        }
    }
}

