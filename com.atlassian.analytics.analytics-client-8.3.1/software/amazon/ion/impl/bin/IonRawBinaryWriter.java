/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonType;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.impl.bin.AbstractIonWriter;
import software.amazon.ion.impl.bin.BlockAllocator;
import software.amazon.ion.impl.bin.BlockAllocatorProvider;
import software.amazon.ion.impl.bin.Symbols;
import software.amazon.ion.impl.bin.WriteBuffer;

final class IonRawBinaryWriter
extends AbstractIonWriter {
    private static final byte[] IVM = IonRawBinaryWriter.bytes(224, 1, 0, 234);
    private static final byte[] NULLS;
    private static final byte NULL_NULL;
    private static final byte BOOL_FALSE = 16;
    private static final byte BOOL_TRUE = 17;
    private static final byte INT_ZERO = 32;
    private static final byte POS_INT_TYPE = 32;
    private static final byte NEG_INT_TYPE = 48;
    private static final byte FLOAT_TYPE = 64;
    private static final byte DECIMAL_TYPE = 80;
    private static final byte TIMESTAMP_TYPE = 96;
    private static final byte SYMBOL_TYPE = 112;
    private static final byte STRING_TYPE = -128;
    private static final byte CLOB_TYPE = -112;
    private static final byte BLOB_TYPE = -96;
    private static final byte DECIMAL_POS_ZERO = 80;
    private static final byte DECIMAL_NEGATIVE_ZERO_MANTISSA = -128;
    private static final BigInteger BIG_INT_LONG_MAX_VALUE;
    private static final BigInteger BIG_INT_LONG_MIN_VALUE;
    private static final byte VARINT_NEG_ZERO = -64;
    private static final byte STRING_TYPE_EXTENDED_LENGTH = -114;
    private static final byte[] STRING_TYPED_PREALLOCATED_2;
    private static final byte[] STRING_TYPED_PREALLOCATED_3;
    private static final int MAX_ANNOTATION_LENGTH = 127;
    private final BlockAllocator allocator;
    private final OutputStream out;
    private final StreamCloseMode streamCloseMode;
    private final StreamFlushMode streamFlushMode;
    private final PreallocationMode preallocationMode;
    private final boolean isFloatBinary32Enabled;
    private final WriteBuffer buffer;
    private final WriteBuffer patchBuffer;
    private final PatchList patchPoints;
    private final LinkedList<ContainerInfo> containers;
    private int depth;
    private boolean hasWrittenValuesSinceFinished;
    private boolean hasWrittenValuesSinceConstructed;
    private SymbolToken currentFieldName;
    private final List<SymbolToken> currentAnnotations;
    private boolean hasTopLevelSymbolTableAnnotation;
    private boolean closed;

    private static byte[] bytes(int ... vals) {
        byte[] octets = new byte[vals.length];
        for (int i = 0; i < vals.length; ++i) {
            octets[i] = (byte)vals[i];
        }
        return octets;
    }

    private static final byte[] makeTypedPreallocatedBytes(int typeDesc, int length) {
        byte[] bytes = new byte[length];
        bytes[0] = (byte)typeDesc;
        if (length > 1) {
            bytes[length - 1] = -128;
        }
        return bytes;
    }

    private static byte[][] makeContainerTypedPreallocatedTable(int length) {
        IonType[] types = IonType.values();
        byte[][] extendedSizes = new byte[types.length][];
        extendedSizes[IonType.LIST.ordinal()] = IonRawBinaryWriter.makeTypedPreallocatedBytes(190, length);
        extendedSizes[IonType.SEXP.ordinal()] = IonRawBinaryWriter.makeTypedPreallocatedBytes(206, length);
        extendedSizes[IonType.STRUCT.ordinal()] = IonRawBinaryWriter.makeTypedPreallocatedBytes(222, length);
        return extendedSizes;
    }

    IonRawBinaryWriter(BlockAllocatorProvider provider, int blockSize, OutputStream out, AbstractIonWriter.WriteValueOptimization optimization, StreamCloseMode streamCloseMode, StreamFlushMode streamFlushMode, PreallocationMode preallocationMode, boolean isFloatBinary32Enabled) throws IOException {
        super(optimization);
        if (out == null) {
            throw new NullPointerException();
        }
        this.allocator = provider.vendAllocator(blockSize);
        this.out = out;
        this.streamCloseMode = streamCloseMode;
        this.streamFlushMode = streamFlushMode;
        this.preallocationMode = preallocationMode;
        this.isFloatBinary32Enabled = isFloatBinary32Enabled;
        this.buffer = new WriteBuffer(this.allocator);
        this.patchBuffer = new WriteBuffer(this.allocator);
        this.patchPoints = new PatchList();
        this.containers = new LinkedList();
        this.depth = 0;
        this.hasWrittenValuesSinceFinished = false;
        this.hasWrittenValuesSinceConstructed = false;
        this.currentFieldName = null;
        this.currentAnnotations = new ArrayList<SymbolToken>();
        this.hasTopLevelSymbolTableAnnotation = false;
        this.closed = false;
    }

    public SymbolTable getSymbolTable() {
        return Symbols.systemSymbolTable();
    }

    public void setFieldName(String name) {
        throw new UnsupportedOperationException("Cannot set field name on a low-level binary writer via string");
    }

    public void setFieldNameSymbol(SymbolToken name) {
        if (!this.isInStruct()) {
            throw new IonException("Cannot set field name outside of struct context");
        }
        this.currentFieldName = name;
    }

    public void setTypeAnnotations(String ... annotations) {
        throw new UnsupportedOperationException("Cannot set annotations on a low-level binary writer via string");
    }

    public void setTypeAnnotationSymbols(SymbolToken ... annotations) {
        this.currentAnnotations.clear();
        this.hasTopLevelSymbolTableAnnotation = false;
        if (annotations != null) {
            for (SymbolToken annotation : annotations) {
                this.addTypeAnnotationSymbol(annotation);
            }
        }
    }

    public void addTypeAnnotation(String annotation) {
        throw new UnsupportedOperationException("Cannot add annotations on a low-level binary writer via string");
    }

    void addTypeAnnotationSymbol(SymbolToken annotation) {
        if (this.depth == 0 && annotation.getSid() == 3) {
            this.hasTopLevelSymbolTableAnnotation = true;
        }
        this.currentAnnotations.add(annotation);
    }

    boolean hasAnnotations() {
        return !this.currentAnnotations.isEmpty();
    }

    boolean hasWrittenValuesSinceFinished() {
        return this.hasWrittenValuesSinceFinished;
    }

    boolean hasWrittenValuesSinceConstructed() {
        return this.hasWrittenValuesSinceConstructed;
    }

    boolean hasTopLevelSymbolTableAnnotation() {
        return this.hasTopLevelSymbolTableAnnotation;
    }

    int getFieldId() {
        return this.currentFieldName.getSid();
    }

    public IonCatalog getCatalog() {
        throw new UnsupportedOperationException();
    }

    public boolean isFieldNameSet() {
        return this.currentFieldName != null;
    }

    public void writeIonVersionMarker() throws IOException {
        this.buffer.writeBytes(IVM);
    }

    public int getDepth() {
        return this.depth;
    }

    private void updateLength(long length) {
        if (this.containers.isEmpty()) {
            return;
        }
        this.containers.getLast().length += length;
    }

    private void pushContainer(ContainerType type) {
        this.containers.add(new ContainerInfo(type, this.buffer.position() + 1L));
    }

    private ContainerInfo currentContainer() {
        return this.containers.isEmpty() ? null : this.containers.getLast();
    }

    private void addPatchPoint(long position, int oldLength, long value) {
        long patchPosition = this.patchBuffer.position();
        int patchLength = this.patchBuffer.writeVarUInt(value);
        PatchPoint patch = new PatchPoint(position, oldLength, patchPosition, patchLength);
        ContainerInfo container = this.currentContainer();
        if (container == null) {
            this.patchPoints.append(patch);
        } else {
            container.appendPatch(patch);
        }
        this.updateLength(patchLength - oldLength);
    }

    private void extendPatchPoints(PatchList patches) {
        ContainerInfo container = this.currentContainer();
        if (container == null) {
            this.patchPoints.extend(patches);
        } else {
            container.extendPatches(patches);
        }
    }

    private ContainerInfo popContainer() {
        ContainerInfo current = this.currentContainer();
        if (current == null) {
            throw new IllegalStateException("Tried to pop container state without said container");
        }
        this.containers.removeLast();
        long length = current.length;
        if (current.type != ContainerType.VALUE) {
            long position = current.position;
            if (current.length <= (long)this.preallocationMode.contentMaxLength && this.preallocationMode != PreallocationMode.PREALLOCATE_0) {
                this.preallocationMode.patchLength(this.buffer, position, length);
            } else if (current.length <= 13L && this.preallocationMode == PreallocationMode.PREALLOCATE_0) {
                long typePosition = position - 1L;
                long type = (long)(this.buffer.getUInt8At(typePosition) & 0xF0) | current.length;
                this.buffer.writeUInt8At(typePosition, type);
            } else {
                this.addPatchPoint(position, this.preallocationMode.typedLength - 1, length);
            }
        }
        if (current.patches != null) {
            this.extendPatchPoints(current.patches);
        }
        this.updateLength(length);
        return current;
    }

    private void writeVarUInt(long value) {
        if (value < 0L) {
            throw new IonException("Cannot write negative value as unsigned");
        }
        int len = this.buffer.writeVarUInt(value);
        this.updateLength(len);
    }

    private void writeVarInt(long value) {
        int len = this.buffer.writeVarInt(value);
        this.updateLength(len);
    }

    private static int checkSid(SymbolToken symbol) {
        int sid = symbol.getSid();
        if (sid < 1) {
            throw new IllegalArgumentException("Invalid symbol: " + symbol.getText() + " SID: " + sid);
        }
        return sid;
    }

    private void prepareValue() {
        if (this.isInStruct() && this.currentFieldName == null) {
            throw new IllegalStateException("IonWriter.setFieldName() must be called before writing a value into a struct.");
        }
        if (this.currentFieldName != null) {
            this.writeVarUInt(IonRawBinaryWriter.checkSid(this.currentFieldName));
            this.currentFieldName = null;
        }
        if (!this.currentAnnotations.isEmpty()) {
            this.updateLength(this.preallocationMode.typedLength);
            this.pushContainer(ContainerType.ANNOTATION);
            this.buffer.writeBytes(this.preallocationMode.annotationsTypedPreallocatedBytes);
            long annotationsLengthPosition = this.buffer.position();
            this.buffer.writeVarUInt(0L);
            int annotationsLength = 0;
            for (SymbolToken symbol : this.currentAnnotations) {
                int sid = IonRawBinaryWriter.checkSid(symbol);
                int symbolLength = this.buffer.writeVarUInt(sid);
                annotationsLength += symbolLength;
            }
            if (annotationsLength > 127) {
                throw new IonException("Annotations too large: " + this.currentAnnotations);
            }
            this.updateLength(1 + annotationsLength);
            this.buffer.writeVarUIntDirect1At(annotationsLengthPosition, annotationsLength);
            this.currentAnnotations.clear();
            this.hasTopLevelSymbolTableAnnotation = false;
        }
    }

    private void finishValue() {
        ContainerInfo current = this.currentContainer();
        if (current != null && current.type == ContainerType.ANNOTATION) {
            this.popContainer();
        }
        this.hasWrittenValuesSinceFinished = true;
        this.hasWrittenValuesSinceConstructed = true;
    }

    public void stepIn(IonType containerType) throws IOException {
        if (!IonType.isContainer(containerType)) {
            throw new IonException("Cannot step into " + (Object)((Object)containerType));
        }
        this.prepareValue();
        this.updateLength(this.preallocationMode.typedLength);
        this.pushContainer(containerType == IonType.STRUCT ? ContainerType.STRUCT : ContainerType.SEQUENCE);
        ++this.depth;
        this.buffer.writeBytes(this.preallocationMode.containerTypedPreallocatedBytes[containerType.ordinal()]);
    }

    public void stepOut() throws IOException {
        if (this.currentFieldName != null) {
            throw new IonException("Cannot step out with field name set");
        }
        if (!this.currentAnnotations.isEmpty()) {
            throw new IonException("Cannot step out with field name set");
        }
        ContainerInfo container = this.currentContainer();
        if (container == null || !container.type.allowedInStepOut) {
            throw new IonException("Cannot step out when not in container");
        }
        this.popContainer();
        --this.depth;
        this.finishValue();
    }

    public boolean isInStruct() {
        return !this.containers.isEmpty() && this.currentContainer().type == ContainerType.STRUCT;
    }

    public void writeNull() throws IOException {
        this.prepareValue();
        this.updateLength(1L);
        this.buffer.writeByte(NULL_NULL);
        this.finishValue();
    }

    public void writeNull(IonType type) throws IOException {
        byte data = NULL_NULL;
        if (type != null && (data = NULLS[type.ordinal()]) == 0) {
            throw new IllegalArgumentException("Cannot write a null for: " + (Object)((Object)type));
        }
        this.prepareValue();
        this.updateLength(1L);
        this.buffer.writeByte(data);
        this.finishValue();
    }

    public void writeBool(boolean value) throws IOException {
        this.prepareValue();
        this.updateLength(1L);
        if (value) {
            this.buffer.writeByte((byte)17);
        } else {
            this.buffer.writeByte((byte)16);
        }
        this.finishValue();
    }

    private void writeTypedUInt(int type, long value) {
        if (value <= 255L) {
            this.updateLength(2L);
            this.buffer.writeUInt8(type | 1);
            this.buffer.writeUInt8(value);
        } else if (value <= 65535L) {
            this.updateLength(3L);
            this.buffer.writeUInt8(type | 2);
            this.buffer.writeUInt16(value);
        } else if (value <= 0xFFFFFFL) {
            this.updateLength(4L);
            this.buffer.writeUInt8(type | 3);
            this.buffer.writeUInt24(value);
        } else if (value <= 0xFFFFFFFFL) {
            this.updateLength(5L);
            this.buffer.writeUInt8(type | 4);
            this.buffer.writeUInt32(value);
        } else if (value <= 0xFFFFFFFFFFL) {
            this.updateLength(6L);
            this.buffer.writeUInt8(type | 5);
            this.buffer.writeUInt40(value);
        } else if (value <= 0xFFFFFFFFFFFFL) {
            this.updateLength(7L);
            this.buffer.writeUInt8(type | 6);
            this.buffer.writeUInt48(value);
        } else if (value <= 0xFFFFFFFFFFFFFFL) {
            this.updateLength(8L);
            this.buffer.writeUInt8(type | 7);
            this.buffer.writeUInt56(value);
        } else {
            this.updateLength(9L);
            this.buffer.writeUInt8(type | 8);
            this.buffer.writeUInt64(value);
        }
    }

    public void writeInt(long value) throws IOException {
        this.prepareValue();
        if (value == 0L) {
            this.updateLength(1L);
            this.buffer.writeByte((byte)32);
        } else {
            int type = 32;
            if (value < 0L) {
                type = 48;
                if (value == Long.MIN_VALUE) {
                    this.updateLength(9L);
                    this.buffer.writeUInt8(56L);
                    this.buffer.writeUInt64(value);
                } else {
                    value = -value;
                    this.writeTypedUInt(type, value);
                }
            } else {
                this.writeTypedUInt(type, value);
            }
        }
        this.finishValue();
    }

    private void writeTypedBytes(int type, byte[] data, int offset, int length) {
        int totalLength = 1 + length;
        if (length < 14) {
            this.buffer.writeUInt8(type | length);
        } else {
            this.buffer.writeUInt8(type | 0xE);
            int sizeLength = this.buffer.writeVarUInt(length);
            totalLength += sizeLength;
        }
        this.updateLength(totalLength);
        this.buffer.writeBytes(data, offset, length);
    }

    public void writeInt(BigInteger value) throws IOException {
        if (value == null) {
            this.writeNull(IonType.INT);
            return;
        }
        if (value.compareTo(BIG_INT_LONG_MIN_VALUE) >= 0 && value.compareTo(BIG_INT_LONG_MAX_VALUE) <= 0) {
            this.writeInt(value.longValue());
            return;
        }
        this.prepareValue();
        int type = 32;
        if (value.signum() < 0) {
            type = 48;
            value = value.negate();
        }
        byte[] magnitude = value.toByteArray();
        this.writeTypedBytes(type, magnitude, 0, magnitude.length);
        this.finishValue();
    }

    public void writeFloat(double value) throws IOException {
        this.prepareValue();
        if (this.isFloatBinary32Enabled && value == (double)((float)value)) {
            this.updateLength(5L);
            this.buffer.writeUInt8(68L);
            this.buffer.writeUInt32(Float.floatToRawIntBits((float)value));
        } else {
            this.updateLength(9L);
            this.buffer.writeUInt8(72L);
            this.buffer.writeUInt64(Double.doubleToRawLongBits(value));
        }
        this.finishValue();
    }

    private void writeDecimalValue(BigDecimal value) {
        boolean isNegZero = Decimal.isNegativeZero(value);
        int signum = value.signum();
        int exponent = -value.scale();
        this.writeVarInt(exponent);
        BigInteger mantissaBigInt = value.unscaledValue();
        if (mantissaBigInt.compareTo(BIG_INT_LONG_MIN_VALUE) >= 0 && mantissaBigInt.compareTo(BIG_INT_LONG_MAX_VALUE) <= 0) {
            long mantissa = mantissaBigInt.longValue();
            if (signum != 0 || isNegZero) {
                if (isNegZero) {
                    this.updateLength(1L);
                    this.buffer.writeByte((byte)-128);
                } else if (mantissa == Long.MIN_VALUE) {
                    this.updateLength(9L);
                    this.buffer.writeUInt8(128L);
                    this.buffer.writeUInt64(mantissa);
                } else if (mantissa >= -127L && mantissa <= 127L) {
                    this.updateLength(1L);
                    this.buffer.writeInt8(mantissa);
                } else if (mantissa >= -32767L && mantissa <= 32767L) {
                    this.updateLength(2L);
                    this.buffer.writeInt16(mantissa);
                } else if (mantissa >= -8388607L && mantissa <= 0x7FFFFFL) {
                    this.updateLength(3L);
                    this.buffer.writeInt24(mantissa);
                } else if (mantissa >= -2147483647L && mantissa <= Integer.MAX_VALUE) {
                    this.updateLength(4L);
                    this.buffer.writeInt32(mantissa);
                } else if (mantissa >= -549755813887L && mantissa <= 0x7FFFFFFFFFL) {
                    this.updateLength(5L);
                    this.buffer.writeInt40(mantissa);
                } else if (mantissa >= -140737488355327L && mantissa <= 0x7FFFFFFFFFFFL) {
                    this.updateLength(6L);
                    this.buffer.writeInt48(mantissa);
                } else if (mantissa >= -36028797018963967L && mantissa <= 0x7FFFFFFFFFFFFFL) {
                    this.updateLength(7L);
                    this.buffer.writeInt56(mantissa);
                } else {
                    this.updateLength(8L);
                    this.buffer.writeInt64(mantissa);
                }
            }
        } else {
            BigInteger magnitude = signum > 0 ? mantissaBigInt : mantissaBigInt.negate();
            byte[] bits = magnitude.toByteArray();
            if (signum < 0) {
                if ((bits[0] & 0x80) == 0) {
                    bits[0] = (byte)(bits[0] | 0x80);
                } else {
                    this.updateLength(1L);
                    this.buffer.writeUInt8(128L);
                }
            }
            this.updateLength(bits.length);
            this.buffer.writeBytes(bits);
        }
    }

    private void patchSingleByteTypedOptimisticValue(byte type, ContainerInfo info) {
        if (info.length <= 13L) {
            this.buffer.writeUInt8At(info.position - 1L, (long)type | info.length);
        } else {
            this.buffer.writeUInt8At(info.position - 1L, type | 0xE);
            this.addPatchPoint(info.position, 0, info.length);
        }
    }

    public void writeDecimal(BigDecimal value) throws IOException {
        if (value == null) {
            this.writeNull(IonType.DECIMAL);
            return;
        }
        this.prepareValue();
        if (value.signum() == 0 && value.scale() == 0 && !Decimal.isNegativeZero(value)) {
            this.updateLength(1L);
            this.buffer.writeUInt8(80L);
        } else {
            this.updateLength(1L);
            this.pushContainer(ContainerType.VALUE);
            this.buffer.writeByte((byte)80);
            this.writeDecimalValue(value);
            ContainerInfo info = this.popContainer();
            this.patchSingleByteTypedOptimisticValue((byte)80, info);
        }
        this.finishValue();
    }

    public void writeTimestamp(Timestamp value) throws IOException {
        if (value == null) {
            this.writeNull(IonType.TIMESTAMP);
            return;
        }
        this.prepareValue();
        this.updateLength(1L);
        this.pushContainer(ContainerType.VALUE);
        this.buffer.writeByte((byte)96);
        Integer offset = value.getLocalOffset();
        if (offset == null) {
            this.updateLength(1L);
            this.buffer.writeByte((byte)-64);
        } else {
            this.writeVarInt(offset.intValue());
        }
        int year = value.getZYear();
        this.writeVarUInt(year);
        int precision = value.getPrecision().ordinal();
        if (precision >= Timestamp.Precision.MONTH.ordinal()) {
            int month = value.getZMonth();
            this.writeVarUInt(month);
        }
        if (precision >= Timestamp.Precision.DAY.ordinal()) {
            int day = value.getZDay();
            this.writeVarUInt(day);
        }
        if (precision >= Timestamp.Precision.MINUTE.ordinal()) {
            int hour = value.getZHour();
            this.writeVarUInt(hour);
            int minute = value.getZMinute();
            this.writeVarUInt(minute);
        }
        if (precision >= Timestamp.Precision.SECOND.ordinal()) {
            int second = value.getZSecond();
            this.writeVarUInt(second);
            BigDecimal fraction = value.getZFractionalSecond();
            if (fraction != null && !BigDecimal.ZERO.equals(fraction)) {
                this.writeDecimalValue(fraction);
            }
        }
        ContainerInfo info = this.popContainer();
        this.patchSingleByteTypedOptimisticValue((byte)96, info);
        this.finishValue();
    }

    public void writeSymbol(String content) throws IOException {
        throw new UnsupportedOperationException("Symbol writing via string is not supported in low-level binary writer");
    }

    public void writeSymbolToken(SymbolToken content) throws IOException {
        if (content == null) {
            this.writeNull(IonType.SYMBOL);
            return;
        }
        int sid = IonRawBinaryWriter.checkSid(content);
        this.prepareValue();
        this.writeTypedUInt(112, sid);
        this.finishValue();
    }

    public void writeString(String value) throws IOException {
        if (value == null) {
            this.writeNull(IonType.STRING);
            return;
        }
        this.prepareValue();
        int estUtf8Length = value.length();
        int preallocatedLength = 1;
        long lengthPosition = this.buffer.position() + 1L;
        if (estUtf8Length <= 13) {
            estUtf8Length = 13;
            this.buffer.writeUInt8(-128L);
        } else if (estUtf8Length <= 127) {
            estUtf8Length = 127;
            preallocatedLength = 2;
            this.buffer.writeBytes(STRING_TYPED_PREALLOCATED_2);
        } else {
            estUtf8Length = 16383;
            preallocatedLength = 3;
            this.buffer.writeBytes(STRING_TYPED_PREALLOCATED_3);
        }
        this.updateLength(preallocatedLength);
        int utf8Length = this.buffer.writeUTF8(value);
        if (utf8Length <= estUtf8Length) {
            if (utf8Length <= 13) {
                this.buffer.writeUInt8At(lengthPosition - 1L, 0xFFFFFF80 | utf8Length);
            } else if (utf8Length <= 127) {
                this.buffer.writeVarUIntDirect1At(lengthPosition, utf8Length);
            } else {
                this.buffer.writeVarUIntDirect2At(lengthPosition, utf8Length);
            }
        } else {
            if (estUtf8Length == 13) {
                this.buffer.writeUInt8At(lengthPosition - 1L, -114L);
            }
            this.addPatchPoint(lengthPosition, preallocatedLength - 1, utf8Length);
        }
        this.updateLength(utf8Length);
        this.finishValue();
    }

    public void writeClob(byte[] data) throws IOException {
        if (data == null) {
            this.writeNull(IonType.CLOB);
            return;
        }
        this.writeClob(data, 0, data.length);
    }

    public void writeClob(byte[] data, int offset, int length) throws IOException {
        if (data == null) {
            this.writeNull(IonType.CLOB);
            return;
        }
        this.prepareValue();
        this.writeTypedBytes(-112, data, offset, length);
        this.finishValue();
    }

    public void writeBlob(byte[] data) throws IOException {
        if (data == null) {
            this.writeNull(IonType.BLOB);
            return;
        }
        this.writeBlob(data, 0, data.length);
    }

    public void writeBlob(byte[] data, int offset, int length) throws IOException {
        if (data == null) {
            this.writeNull(IonType.BLOB);
            return;
        }
        this.prepareValue();
        this.writeTypedBytes(-96, data, offset, length);
        this.finishValue();
    }

    public void writeBytes(byte[] data, int offset, int length) throws IOException {
        this.prepareValue();
        this.updateLength(length);
        this.buffer.writeBytes(data, offset, length);
        this.finishValue();
    }

    long position() {
        return this.buffer.position();
    }

    void truncate(long position) {
        this.buffer.truncate(position);
        PatchPoint patch = this.patchPoints.truncate(position);
        if (patch != null) {
            this.patchBuffer.truncate(patch.patchPosition);
        }
    }

    public void flush() throws IOException {
    }

    public void finish() throws IOException {
        if (!this.containers.isEmpty()) {
            throw new IllegalStateException("Cannot finish within container: " + this.containers);
        }
        if (this.patchPoints.isEmpty()) {
            this.buffer.writeTo(this.out);
        } else {
            long bufferPosition = 0L;
            for (PatchPoint patch : this.patchPoints) {
                long bufferLength = patch.oldPosition - bufferPosition;
                this.buffer.writeTo(this.out, bufferPosition, bufferLength);
                this.patchBuffer.writeTo(this.out, patch.patchPosition, patch.patchLength);
                bufferPosition = patch.oldPosition;
                bufferPosition += (long)patch.oldLength;
            }
            this.buffer.writeTo(this.out, bufferPosition, this.buffer.position() - bufferPosition);
        }
        this.patchPoints.clear();
        this.patchBuffer.reset();
        this.buffer.reset();
        if (this.streamFlushMode == StreamFlushMode.FLUSH) {
            this.out.flush();
        }
        this.hasWrittenValuesSinceFinished = false;
    }

    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            try {
                this.finish();
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            this.buffer.close();
            this.patchBuffer.close();
            this.allocator.close();
        }
        finally {
            if (this.streamCloseMode == StreamCloseMode.CLOSE) {
                this.out.close();
            }
        }
    }

    static {
        IonType[] types = IonType.values();
        NULLS = new byte[types.length];
        IonRawBinaryWriter.NULLS[IonType.NULL.ordinal()] = 15;
        IonRawBinaryWriter.NULLS[IonType.BOOL.ordinal()] = 31;
        IonRawBinaryWriter.NULLS[IonType.INT.ordinal()] = 47;
        IonRawBinaryWriter.NULLS[IonType.FLOAT.ordinal()] = 79;
        IonRawBinaryWriter.NULLS[IonType.DECIMAL.ordinal()] = 95;
        IonRawBinaryWriter.NULLS[IonType.TIMESTAMP.ordinal()] = 111;
        IonRawBinaryWriter.NULLS[IonType.SYMBOL.ordinal()] = 127;
        IonRawBinaryWriter.NULLS[IonType.STRING.ordinal()] = -113;
        IonRawBinaryWriter.NULLS[IonType.CLOB.ordinal()] = -97;
        IonRawBinaryWriter.NULLS[IonType.BLOB.ordinal()] = -81;
        IonRawBinaryWriter.NULLS[IonType.LIST.ordinal()] = -65;
        IonRawBinaryWriter.NULLS[IonType.SEXP.ordinal()] = -49;
        IonRawBinaryWriter.NULLS[IonType.STRUCT.ordinal()] = -33;
        NULL_NULL = NULLS[IonType.NULL.ordinal()];
        BIG_INT_LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
        BIG_INT_LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
        STRING_TYPED_PREALLOCATED_2 = IonRawBinaryWriter.makeTypedPreallocatedBytes(142, 2);
        STRING_TYPED_PREALLOCATED_3 = IonRawBinaryWriter.makeTypedPreallocatedBytes(142, 3);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum StreamFlushMode {
        NO_FLUSH,
        FLUSH;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum StreamCloseMode {
        NO_CLOSE,
        CLOSE;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class PatchList
    implements Iterable<PatchPoint> {
        private Node head = null;
        private Node tail = null;

        public boolean isEmpty() {
            return this.head == null && this.tail == null;
        }

        public void clear() {
            this.head = null;
            this.tail = null;
        }

        public void append(PatchPoint patch) {
            Node node = new Node(patch);
            if (this.head == null) {
                this.head = node;
                this.tail = node;
            } else {
                this.tail.next = node;
                this.tail = node;
            }
        }

        public void extend(PatchList end) {
            if (end != null) {
                if (this.head == null) {
                    if (end.head != null) {
                        this.head = end.head;
                        this.tail = end.tail;
                    }
                } else {
                    this.tail.next = end.head;
                    this.tail = end.tail;
                }
            }
        }

        public PatchPoint truncate(long oldPosition) {
            Node prev = null;
            Node curr = this.head;
            while (curr != null) {
                PatchPoint patch = curr.value;
                if (patch.oldPosition >= oldPosition) {
                    this.tail = prev;
                    if (this.tail == null) {
                        this.head = null;
                    } else {
                        this.tail.next = null;
                    }
                    return patch;
                }
                prev = curr;
                curr = curr.next;
            }
            return null;
        }

        @Override
        public Iterator<PatchPoint> iterator() {
            return new Iterator<PatchPoint>(){
                Node curr;
                {
                    this.curr = PatchList.this.head;
                }

                @Override
                public boolean hasNext() {
                    return this.curr != null;
                }

                @Override
                public PatchPoint next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    PatchPoint value = this.curr.value;
                    this.curr = this.curr.next;
                    return value;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("(PATCHES");
            for (PatchPoint patch : this) {
                buf.append(" ");
                buf.append(patch);
            }
            buf.append(")");
            return buf.toString();
        }

        private static class Node {
            public final PatchPoint value;
            public Node next;

            public Node(PatchPoint value) {
                this.value = value;
            }
        }
    }

    private static class PatchPoint {
        public final long oldPosition;
        public final int oldLength;
        public final long patchPosition;
        public final int patchLength;

        public PatchPoint(long oldPosition, int oldLength, long patchPosition, int patchLength) {
            this.oldPosition = oldPosition;
            this.oldLength = oldLength;
            this.patchPosition = patchPosition;
            this.patchLength = patchLength;
        }

        public String toString() {
            return "(PP old::(" + this.oldPosition + " " + this.oldLength + ") patch::(" + this.patchPosition + " " + this.patchLength + ")";
        }
    }

    private static class ContainerInfo {
        public final ContainerType type;
        public final long position;
        public long length;
        public PatchList patches;

        public ContainerInfo(ContainerType type, long offset) {
            this.type = type;
            this.position = offset;
            this.patches = null;
        }

        public void appendPatch(PatchPoint patch) {
            if (this.patches == null) {
                this.patches = new PatchList();
            }
            this.patches.append(patch);
        }

        public void extendPatches(PatchList newPatches) {
            if (this.patches == null) {
                this.patches = newPatches;
            } else {
                this.patches.extend(newPatches);
            }
        }

        public String toString() {
            return "(CI " + (Object)((Object)this.type) + " pos:" + this.position + " len:" + this.length + ")";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum ContainerType {
        SEQUENCE(true),
        STRUCT(true),
        VALUE(false),
        ANNOTATION(false);

        public final boolean allowedInStepOut;

        private ContainerType(boolean allowedInStepOut) {
            this.allowedInStepOut = allowedInStepOut;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum PreallocationMode {
        PREALLOCATE_0(0, 1){

            void patchLength(WriteBuffer buffer, long position, long lengthValue) {
                throw new IllegalStateException("Cannot patch in PREALLOCATE 0 mode");
            }
        }
        ,
        PREALLOCATE_1(127, 2){

            void patchLength(WriteBuffer buffer, long position, long lengthValue) {
                buffer.writeVarUIntDirect1At(position, lengthValue);
            }
        }
        ,
        PREALLOCATE_2(16383, 3){

            void patchLength(WriteBuffer buffer, long position, long lengthValue) {
                buffer.writeVarUIntDirect2At(position, lengthValue);
            }
        };

        private final int contentMaxLength;
        private final int typedLength;
        private final byte[][] containerTypedPreallocatedBytes;
        private final byte[] annotationsTypedPreallocatedBytes;

        private PreallocationMode(int contentMaxLength, int typedLength) {
            this.contentMaxLength = contentMaxLength;
            this.typedLength = typedLength;
            this.containerTypedPreallocatedBytes = IonRawBinaryWriter.makeContainerTypedPreallocatedTable(typedLength);
            this.annotationsTypedPreallocatedBytes = IonRawBinaryWriter.makeTypedPreallocatedBytes(238, typedLength);
        }

        abstract void patchLength(WriteBuffer var1, long var2, long var4);

        static PreallocationMode withPadSize(int pad) {
            switch (pad) {
                case 0: {
                    return PREALLOCATE_0;
                }
                case 1: {
                    return PREALLOCATE_1;
                }
                case 2: {
                    return PREALLOCATE_2;
                }
            }
            throw new IllegalArgumentException("No such preallocation mode for: " + pad);
        }
    }
}

