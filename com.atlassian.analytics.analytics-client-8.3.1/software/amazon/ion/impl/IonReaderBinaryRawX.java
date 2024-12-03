/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.math.MathContext;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonException;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonType;
import software.amazon.ion.Timestamp;
import software.amazon.ion.impl.IonUTF8;
import software.amazon.ion.impl.PrivateIonConstants;
import software.amazon.ion.impl.PrivateScalarConversions;
import software.amazon.ion.impl.UnifiedInputStreamX;
import software.amazon.ion.impl.UnifiedSavePointManagerX;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class IonReaderBinaryRawX
implements IonReader {
    static final int DEFAULT_CONTAINER_STACK_SIZE = 12;
    static final int DEFAULT_ANNOTATION_SIZE = 10;
    static final int NO_LIMIT = Integer.MIN_VALUE;
    State _state;
    UnifiedInputStreamX _input;
    int _local_remaining;
    boolean _eof;
    boolean _has_next_needed;
    PrivateScalarConversions.ValueVariant _v;
    IonType _value_type;
    boolean _value_is_null;
    boolean _value_is_true;
    int _value_field_id;
    int _value_tid;
    int _value_len;
    int _value_lob_remaining;
    boolean _value_lob_is_ready;
    long _position_start;
    long _position_len;
    UnifiedSavePointManagerX.SavePoint _annotations;
    int[] _annotation_ids;
    int _annotation_count;
    boolean _is_in_struct;
    boolean _struct_is_ordered;
    int _parent_tid;
    int _container_top;
    long[] _container_stack;
    private static final int POS_OFFSET = 0;
    private static final int TYPE_LIMIT_OFFSET = 1;
    private static final long TYPE_MASK = -1L;
    private static final int LIMIT_SHIFT = 32;
    private static final int POS_STACK_STEP = 2;
    private static final int BINARY_VERSION_MARKER_TID = PrivateIonConstants.getTypeCode(PrivateIonConstants.BINARY_VERSION_MARKER_1_0[0] & 0xFF);
    private static final int BINARY_VERSION_MARKER_LEN = PrivateIonConstants.getLowNibble(PrivateIonConstants.BINARY_VERSION_MARKER_1_0[0] & 0xFF);

    protected IonReaderBinaryRawX() {
    }

    @Override
    public <T> T asFacet(Class<T> facetType) {
        return null;
    }

    protected final void init_raw(UnifiedInputStreamX uis) {
        this._input = uis;
        this._container_stack = new long[12];
        this._annotations = uis.savePointAllocate();
        this._v = new PrivateScalarConversions.ValueVariant();
        this._annotation_ids = new int[10];
        this.re_init_raw();
        this._position_start = -1L;
    }

    final void re_init_raw() {
        this._local_remaining = Integer.MIN_VALUE;
        this._parent_tid = 16;
        this._value_field_id = -1;
        this._state = State.S_BEFORE_TID;
        this._has_next_needed = true;
        this._eof = false;
        this._value_type = null;
        this._value_is_null = false;
        this._value_is_true = false;
        this._value_len = 0;
        this._value_lob_remaining = 0;
        this._value_lob_is_ready = false;
        this._annotation_count = 0;
        this._is_in_struct = false;
        this._struct_is_ordered = false;
        this._parent_tid = 0;
        this._container_top = 0;
    }

    @Override
    public void close() throws IOException {
        this._input.close();
    }

    private final void push(int type, long position, int local_remaining) {
        int oldlen = this._container_stack.length;
        if (this._container_top + 2 >= oldlen) {
            int newlen = oldlen * 2;
            long[] temp = new long[newlen];
            System.arraycopy(this._container_stack, 0, temp, 0, oldlen);
            this._container_stack = temp;
        }
        this._container_stack[this._container_top + 0] = position;
        long type_limit = local_remaining;
        type_limit <<= 32;
        this._container_stack[this._container_top + 1] = type_limit |= (long)type & 0xFFFFFFFFFFFFFFFFL;
        this._container_top += 2;
    }

    private final long get_top_position() {
        assert (this._container_top > 0);
        long pos = this._container_stack[this._container_top - 2 + 0];
        return pos;
    }

    private final int get_top_type() {
        assert (this._container_top > 0);
        long type_limit = this._container_stack[this._container_top - 2 + 1];
        int type = (int)(type_limit & 0xFFFFFFFFFFFFFFFFL);
        if (type < 0 || type > 16) {
            this.throwErrorAt("invalid type id in parent stack");
        }
        return type;
    }

    private final int get_top_local_remaining() {
        assert (this._container_top > 0);
        long type_limit = this._container_stack[this._container_top - 2 + 1];
        int local_remaining = (int)(type_limit >> 32 & 0xFFFFFFFFFFFFFFFFL);
        return local_remaining;
    }

    private final void pop() {
        assert (this._container_top > 0);
        this._container_top -= 2;
    }

    boolean hasNext() {
        if (!this._eof && this._has_next_needed) {
            try {
                this.has_next_helper_raw();
            }
            catch (IOException e) {
                this.error(e);
            }
        }
        return !this._eof;
    }

    @Override
    public IonType next() {
        if (this._eof) {
            return null;
        }
        if (this._has_next_needed) {
            try {
                this.has_next_helper_raw();
            }
            catch (IOException e) {
                this.error(e);
            }
        }
        this._has_next_needed = true;
        assert (this._value_type != null || this._eof);
        return this._value_type;
    }

    private final void has_next_helper_raw() throws IOException {
        this.clear_value();
        block7: while (this._value_tid == -1 && !this._eof) {
            switch (this._state) {
                case S_BEFORE_FIELD: {
                    assert (this._value_field_id == -1);
                    this._value_field_id = this.read_field_id();
                    if (this._value_field_id == -1) {
                        this._eof = true;
                        continue block7;
                    }
                }
                case S_BEFORE_TID: {
                    this._state = State.S_BEFORE_VALUE;
                    this._value_tid = this.read_type_id();
                    if (this._value_tid == -1) {
                        this._state = State.S_EOF;
                        this._eof = true;
                        continue block7;
                    }
                    if (this._value_tid == 14) {
                        assert (this._value_tid == (BINARY_VERSION_MARKER_TID & 0xFF));
                        if (this._value_len == BINARY_VERSION_MARKER_LEN) {
                            this.load_version_marker();
                            this._value_type = IonType.SYMBOL;
                            continue block7;
                        }
                        long wrapperStart = this._position_start;
                        long wrapperLen = this._position_len;
                        this._value_type = this.load_annotation_start_with_value_type();
                        long wrapperFinish = wrapperStart + wrapperLen;
                        long wrappedValueFinish = this._position_start + this._position_len;
                        if (wrapperFinish != wrappedValueFinish) {
                            throw this.newErrorAt(String.format("Wrapper length mismatch: wrapper %s wrapped value %s", wrapperFinish, wrappedValueFinish));
                        }
                        this._position_start = wrapperStart;
                        this._position_len = wrapperLen;
                        continue block7;
                    }
                    this._value_type = this.get_iontype_from_tid(this._value_tid);
                    continue block7;
                }
                case S_BEFORE_VALUE: {
                    this.skip(this._value_len);
                }
                case S_AFTER_VALUE: {
                    if (this.isInStruct()) {
                        this._state = State.S_BEFORE_FIELD;
                        continue block7;
                    }
                    this._state = State.S_BEFORE_TID;
                    continue block7;
                }
                case S_EOF: {
                    continue block7;
                }
            }
            this.error("internal error: raw binary reader in invalid state!");
        }
        this._has_next_needed = false;
    }

    private final void load_version_marker() throws IOException {
        for (int ii = 1; ii < PrivateIonConstants.BINARY_VERSION_MARKER_1_0.length; ++ii) {
            int b = this.read();
            if (b == (PrivateIonConstants.BINARY_VERSION_MARKER_1_0[ii] & 0xFF)) continue;
            this.throwErrorAt("invalid binary image");
        }
        this._value_tid = 7;
        this._value_len = 0;
        this._v.setValue(2);
        this._v.setAuthoritativeType(3);
        this._value_is_null = false;
        this._value_lob_is_ready = false;
        this._annotations.clear();
        this._value_field_id = -1;
        this._state = State.S_AFTER_VALUE;
    }

    private final IonType load_annotation_start_with_value_type() throws IOException {
        int alen = this.readVarUInt();
        this._annotations.start(this.getPosition(), 0L);
        this.skip(alen);
        this._annotations.markEnd();
        this._value_tid = this.read_type_id();
        if (this._value_tid == -1) {
            this.throwErrorAt("unexpected EOF encountered where a type descriptor byte was expected");
        }
        IonType value_type = this.get_iontype_from_tid(this._value_tid);
        assert (value_type != null);
        return value_type;
    }

    protected final int load_annotations() {
        switch (this._state) {
            case S_BEFORE_VALUE: 
            case S_AFTER_VALUE: {
                if (!this._annotations.isDefined()) break;
                int local_remaining_save = this._local_remaining;
                this._input._save_points.savePointPushActive(this._annotations, this.getPosition(), 0L);
                this._local_remaining = Integer.MIN_VALUE;
                this._annotation_count = 0;
                try {
                    int a;
                    while ((a = this.readVarUIntOrEOF()) != -1) {
                        this.load_annotation_append(a);
                        if (!this.isEOF()) continue;
                        break;
                    }
                }
                catch (IOException e) {
                    this.error(e);
                }
                this._input._save_points.savePointPopActive(this._annotations);
                this._local_remaining = local_remaining_save;
                this._annotations.clear();
                break;
            }
            default: {
                throw new IllegalStateException("annotations require the value to be ready");
            }
        }
        return this._annotation_count;
    }

    private final void load_annotation_append(int a) {
        int oldlen = this._annotation_ids.length;
        if (this._annotation_count >= oldlen) {
            int newlen = oldlen * 2;
            int[] temp = new int[newlen];
            System.arraycopy(this._annotation_ids, 0, temp, 0, oldlen);
            this._annotation_ids = temp;
        }
        this._annotation_ids[this._annotation_count++] = a;
    }

    private final void clear_value() {
        this._value_type = null;
        this._value_tid = -1;
        this._value_is_null = false;
        this._value_lob_is_ready = false;
        this._annotations.clear();
        this._v.clear();
        this._annotation_count = 0;
        this._value_field_id = -1;
    }

    private final int read_field_id() throws IOException {
        int field_id = this.readVarUIntOrEOF();
        return field_id;
    }

    private final int read_type_id() throws IOException {
        long start_of_tid = this._input.getPosition();
        long start_of_value = start_of_tid + 1L;
        int td = this.read();
        if (td < 0) {
            return -1;
        }
        int tid = PrivateIonConstants.getTypeCode(td);
        int len = PrivateIonConstants.getLowNibble(td);
        if (len == 14) {
            len = this.readVarUInt();
            start_of_value = this._input.getPosition();
        } else if (tid == 0) {
            if (len != 15) {
                this.throwErrorAt("invalid null type descriptor");
            }
            this._value_is_null = true;
            len = 0;
            this._state = State.S_AFTER_VALUE;
        } else if (len == 15) {
            this._value_is_null = true;
            len = 0;
            this._state = State.S_AFTER_VALUE;
        } else if (tid == 1) {
            switch (len) {
                case 0: {
                    this._value_is_true = false;
                    break;
                }
                case 1: {
                    this._value_is_true = true;
                    break;
                }
                default: {
                    this.throwErrorAt("invalid length nibble in boolean value: " + len);
                }
            }
            len = 0;
            this._state = State.S_AFTER_VALUE;
        } else if (tid == 13 && (this._struct_is_ordered = len == 1)) {
            len = this.readVarUInt();
            start_of_value = this._input.getPosition();
        }
        this._value_tid = tid;
        this._value_len = len;
        this._position_len = (long)len + (start_of_value - start_of_tid);
        this._position_start = start_of_tid;
        return tid;
    }

    private final IonType get_iontype_from_tid(int tid) {
        IonType t = null;
        switch (tid) {
            case 0: {
                t = IonType.NULL;
                break;
            }
            case 1: {
                t = IonType.BOOL;
                break;
            }
            case 2: 
            case 3: {
                t = IonType.INT;
                break;
            }
            case 4: {
                t = IonType.FLOAT;
                break;
            }
            case 5: {
                t = IonType.DECIMAL;
                break;
            }
            case 6: {
                t = IonType.TIMESTAMP;
                break;
            }
            case 7: {
                t = IonType.SYMBOL;
                break;
            }
            case 8: {
                t = IonType.STRING;
                break;
            }
            case 9: {
                t = IonType.CLOB;
                break;
            }
            case 10: {
                t = IonType.BLOB;
                break;
            }
            case 11: {
                t = IonType.LIST;
                break;
            }
            case 12: {
                t = IonType.SEXP;
                break;
            }
            case 13: {
                t = IonType.STRUCT;
                break;
            }
            case 14: {
                t = null;
                break;
            }
            default: {
                throw this.newErrorAt("unrecognized value type encountered: " + tid);
            }
        }
        return t;
    }

    @Override
    public void stepIn() {
        if (this._value_type == null || this._eof) {
            throw new IllegalStateException();
        }
        switch (this._value_type) {
            case STRUCT: 
            case LIST: 
            case SEXP: {
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        if (this._value_is_null ? this._state != State.S_AFTER_VALUE && !$assertionsDisabled && this._state != State.S_AFTER_VALUE : this._state != State.S_BEFORE_VALUE && !$assertionsDisabled && this._state != State.S_BEFORE_VALUE) {
            throw new AssertionError();
        }
        long curr_position = this.getPosition();
        long next_position = curr_position + (long)this._value_len;
        int next_remaining = this._local_remaining;
        if (next_remaining != Integer.MIN_VALUE && (next_remaining -= this._value_len) < 0) {
            next_remaining = 0;
        }
        this.push(this._parent_tid, next_position, next_remaining);
        this._is_in_struct = this._value_tid == 13;
        this._local_remaining = this._value_len;
        this._state = this._is_in_struct ? State.S_BEFORE_FIELD : State.S_BEFORE_TID;
        this._parent_tid = this._value_tid;
        this.clear_value();
        this._has_next_needed = true;
    }

    @Override
    public void stepOut() {
        if (this.getDepth() < 1) {
            throw new IllegalStateException("Cannot stepOut any further, already at top level.");
        }
        long next_position = this.get_top_position();
        int local_remaining = this.get_top_local_remaining();
        int parent_tid = this.get_top_type();
        this.pop();
        this._eof = false;
        this._parent_tid = parent_tid;
        if (this._parent_tid == 13) {
            this._is_in_struct = true;
            this._state = State.S_BEFORE_FIELD;
        } else {
            this._is_in_struct = false;
            this._state = State.S_BEFORE_TID;
        }
        this._has_next_needed = true;
        this.clear_value();
        long curr_position = this.getPosition();
        if (next_position > curr_position) {
            try {
                long distance;
                int max_skip = 0x7FFFFFFE;
                for (distance = next_position - curr_position; distance > (long)max_skip; distance -= (long)max_skip) {
                    this.skip(max_skip);
                }
                if (distance > 0L) {
                    assert (distance < Integer.MAX_VALUE);
                    this.skip((int)distance);
                }
            }
            catch (IOException e) {
                this.error(e);
            }
        } else if (next_position < curr_position) {
            String message = "invalid position during stepOut, current position " + curr_position + " next value at " + next_position;
            this.error(message);
        }
        assert (next_position == this.getPosition());
        this._local_remaining = local_remaining;
    }

    @Override
    public int byteSize() {
        switch (this._value_type) {
            case BLOB: 
            case CLOB: {
                break;
            }
            default: {
                throw new IllegalStateException("only valid for LOB values");
            }
        }
        if (!this._value_lob_is_ready) {
            int len = this._value_is_null ? 0 : this._value_len;
            this._value_lob_remaining = len;
            this._value_lob_is_ready = true;
        }
        return this._value_lob_remaining;
    }

    @Override
    public byte[] newBytes() {
        byte[] bytes;
        int len = this.byteSize();
        if (this._value_is_null) {
            bytes = null;
        } else {
            bytes = new byte[len];
            this.getBytes(bytes, 0, len);
        }
        return bytes;
    }

    @Override
    public int getBytes(byte[] buffer, int offset, int len) {
        int value_len = this.byteSize();
        if (value_len > len) {
            value_len = len;
        }
        int read_len = this.readBytes(buffer, offset, value_len);
        return read_len;
    }

    public int readBytes(byte[] buffer, int offset, int len) {
        int read_len;
        if (offset < 0 || len < 0) {
            throw new IllegalArgumentException();
        }
        int value_len = this.byteSize();
        if (this._value_lob_remaining > len) {
            len = this._value_lob_remaining;
        }
        if (len < 1) {
            return 0;
        }
        try {
            read_len = this.read(buffer, offset, value_len);
            this._value_lob_remaining -= read_len;
        }
        catch (IOException e) {
            read_len = -1;
            this.error(e);
        }
        if (this._value_lob_remaining == 0) {
            this._state = State.S_AFTER_VALUE;
        } else {
            this._value_len = this._value_lob_remaining;
        }
        return read_len;
    }

    @Override
    public int getDepth() {
        return this._container_top / 2;
    }

    @Override
    public IonType getType() {
        return this._value_type;
    }

    @Override
    public boolean isInStruct() {
        return this._is_in_struct;
    }

    @Override
    public boolean isNullValue() {
        return this._value_is_null;
    }

    private final int read() throws IOException {
        if (this._local_remaining != Integer.MIN_VALUE) {
            if (this._local_remaining < 1) {
                return -1;
            }
            --this._local_remaining;
        }
        return this._input.read();
    }

    private final int read(byte[] dst, int start, int len) throws IOException {
        int read;
        if (dst == null || start < 0 || len < 0 || start + len > dst.length) {
            throw new IllegalArgumentException();
        }
        if (this._local_remaining == Integer.MIN_VALUE) {
            read = this._input.read(dst, start, len);
        } else {
            if (len > this._local_remaining) {
                if (this._local_remaining < 1) {
                    this.throwUnexpectedEOFException();
                }
                len = this._local_remaining;
            }
            read = this._input.read(dst, start, len);
            this._local_remaining -= read;
        }
        return read;
    }

    public void readAll(byte[] buf, int offset, int len) throws IOException {
        int rem = len;
        while (rem > 0) {
            int amount = this.read(buf, offset, rem);
            if (amount <= 0) {
                this.throwUnexpectedEOFException();
            }
            rem -= amount;
            offset += amount;
        }
    }

    private final boolean isEOF() {
        if (this._local_remaining > 0) {
            return false;
        }
        if (this._local_remaining == Integer.MIN_VALUE) {
            return this._input.isEOF();
        }
        return true;
    }

    private final long getPosition() {
        long pos = this._input.getPosition();
        return pos;
    }

    private final void skip(int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException();
        }
        if (this._local_remaining == Integer.MIN_VALUE) {
            this._input.skip(len);
        } else {
            if (len > this._local_remaining) {
                if (this._local_remaining < 1) {
                    this.throwUnexpectedEOFException();
                }
                len = this._local_remaining;
            }
            this._input.skip(len);
            this._local_remaining -= len;
        }
    }

    protected final long readULong(int len) throws IOException {
        long retvalue = 0L;
        switch (len) {
            default: {
                throw new IonException("value too large for Java long");
            }
            case 8: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 7: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 6: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 5: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 4: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 3: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 2: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 1: {
                int b = this.read();
                if (b < 0) {
                    this.throwUnexpectedEOFException();
                }
                retvalue = retvalue << 8 | (long)b;
            }
            case 0: 
        }
        return retvalue;
    }

    protected final BigInteger readBigInteger(int len, boolean is_negative) throws IOException {
        BigInteger value;
        if (len > 0) {
            byte[] bits = new byte[len];
            this.readAll(bits, 0, len);
            int signum = is_negative ? -1 : 1;
            value = new BigInteger(signum, bits);
        } else {
            value = BigInteger.ZERO;
        }
        return value;
    }

    protected final int readVarInt() throws IOException {
        int retvalue = 0;
        boolean is_negative = false;
        while (true) {
            int b;
            if ((b = this.read()) < 0) {
                this.throwUnexpectedEOFException();
            }
            if ((b & 0x40) != 0) {
                is_negative = true;
            }
            retvalue = b & 0x3F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            this.throwIntOverflowExeption();
        }
        if (is_negative) {
            retvalue = -retvalue;
        }
        return retvalue;
    }

    protected final long readVarLong() throws IOException {
        long retvalue = 0L;
        boolean is_negative = false;
        int b = this.read();
        if (b < 0) {
            this.throwUnexpectedEOFException();
        }
        if ((b & 0x40) != 0) {
            is_negative = true;
        }
        retvalue = b & 0x3F;
        if ((b & 0x80) == 0) {
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | (long)(b & 0x7F);
            if ((b & 0x80) == 0) {
                do {
                    if ((b = this.read()) < 0) {
                        this.throwUnexpectedEOFException();
                    }
                    if ((retvalue & 0xFE00000000000000L) != 0L) {
                        this.throwIntOverflowExeption();
                    }
                    retvalue = retvalue << 7 | (long)(b & 0x7F);
                } while ((b & 0x80) == 0);
            }
        }
        if (is_negative) {
            retvalue = -retvalue;
        }
        return retvalue;
    }

    protected final Integer readVarInteger() throws IOException {
        int retvalue = 0;
        boolean is_negative = false;
        while (true) {
            int b;
            if ((b = this.read()) < 0) {
                this.throwUnexpectedEOFException();
            }
            if ((b & 0x40) != 0) {
                is_negative = true;
            }
            retvalue = b & 0x3F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            this.throwIntOverflowExeption();
        }
        Integer retInteger = null;
        if (is_negative) {
            if (retvalue != 0) {
                retInteger = new Integer(-retvalue);
            }
        } else {
            retInteger = new Integer(retvalue);
        }
        return retInteger;
    }

    protected final int readVarUIntOrEOF() throws IOException {
        int retvalue = 0;
        while (true) {
            int b;
            if ((b = this.read()) < 0) {
                return -1;
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            this.throwIntOverflowExeption();
        }
        return retvalue;
    }

    protected final int readVarUInt() throws IOException {
        int retvalue = 0;
        while (true) {
            int b;
            if ((b = this.read()) < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            b = this.read();
            if (b < 0) {
                this.throwUnexpectedEOFException();
            }
            retvalue = retvalue << 7 | b & 0x7F;
            if ((b & 0x80) != 0) break;
            this.throwIntOverflowExeption();
        }
        return retvalue;
    }

    protected final double readFloat(int len) throws IOException {
        if (len == 0) {
            return 0.0;
        }
        if (len != 4 && len != 8) {
            throw new IOException("Length of float read must be 0, 4, or 8");
        }
        long dBits = this.readULong(len);
        return len == 4 ? (double)Float.intBitsToFloat((int)(dBits & 0xFFFFFFFFL)) : Double.longBitsToDouble(dBits);
    }

    protected final long readVarULong() throws IOException {
        int b;
        long retvalue = 0L;
        do {
            if ((b = this.read()) < 0) {
                this.throwUnexpectedEOFException();
            }
            if ((retvalue & 0xFE00000000000000L) != 0L) {
                this.throwIntOverflowExeption();
            }
            retvalue = retvalue << 7 | (long)(b & 0x7F);
        } while ((b & 0x80) == 0);
        return retvalue;
    }

    protected final Decimal readDecimal(int len) throws IOException {
        Decimal bd;
        MathContext mathContext = MathContext.UNLIMITED;
        if (len == 0) {
            bd = Decimal.valueOf(0, mathContext);
        } else {
            BigInteger value;
            int signum;
            int save_limit = this._local_remaining - len;
            this._local_remaining = len;
            int exponent = this.readVarInt();
            if (this._local_remaining > 0) {
                byte[] bits = new byte[this._local_remaining];
                this.readAll(bits, 0, this._local_remaining);
                signum = 1;
                if (bits[0] < 0) {
                    bits[0] = (byte)(bits[0] & 0x7F);
                    signum = -1;
                }
                value = new BigInteger(signum, bits);
            } else {
                signum = 0;
                value = BigInteger.ZERO;
            }
            int scale = -exponent;
            if (value.signum() == 0 && signum == -1) {
                assert (value.equals(BigInteger.ZERO));
                bd = Decimal.negativeZero(scale, mathContext);
            } else {
                bd = Decimal.valueOf(value, scale, mathContext);
            }
            this._local_remaining = save_limit;
        }
        return bd;
    }

    protected final Timestamp readTimestamp(int len) throws IOException {
        if (len < 1) {
            return null;
        }
        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        Decimal frac = null;
        int save_limit = this._local_remaining - len;
        this._local_remaining = len;
        Integer offset = this.readVarInteger();
        year = this.readVarUInt();
        Timestamp.Precision p = Timestamp.Precision.YEAR;
        if (this._local_remaining > 0) {
            month = this.readVarUInt();
            p = Timestamp.Precision.MONTH;
            if (this._local_remaining > 0) {
                day = this.readVarUInt();
                p = Timestamp.Precision.DAY;
                if (this._local_remaining > 0) {
                    hour = this.readVarUInt();
                    minute = this.readVarUInt();
                    p = Timestamp.Precision.MINUTE;
                    if (this._local_remaining > 0) {
                        second = this.readVarUInt();
                        p = Timestamp.Precision.SECOND;
                        if (this._local_remaining > 0) {
                            frac = this.readDecimal(this._local_remaining);
                        }
                    }
                }
            }
        }
        this._local_remaining = save_limit;
        try {
            Timestamp val = Timestamp.createFromUtcFields(p, year, month, day, hour, minute, second, frac, offset);
            return val;
        }
        catch (IllegalArgumentException e) {
            throw this.newErrorAt("Invalid timestamp encoding: " + e.getMessage());
        }
    }

    protected final String readString(int len) throws IOException {
        char[] chars = new char[len];
        int ii = 0;
        int save_limit = this._local_remaining - len;
        this._local_remaining = len;
        while (!this.isEOF()) {
            int c = this.readUnicodeScalar();
            if (c < 0) {
                this.throwUnexpectedEOFException();
            }
            if (c < 65536) {
                chars[ii++] = (char)c;
                continue;
            }
            chars[ii++] = (char)PrivateIonConstants.makeHighSurrogate(c);
            chars[ii++] = (char)PrivateIonConstants.makeLowSurrogate(c);
        }
        this._local_remaining = save_limit;
        return new String(chars, 0, ii);
    }

    private final int readUnicodeScalar() throws IOException {
        int c = -1;
        int b = this.read();
        if (IonUTF8.isOneByteUTF8(b)) {
            return b;
        }
        switch (IonUTF8.getUTF8LengthFromFirstByte(b)) {
            case 2: {
                assert ((b & 0xE0) == 192);
                int b2 = this.read();
                if (!IonUTF8.isContinueByteUTF8(b2)) {
                    this.throwUTF8Exception();
                }
                c = IonUTF8.twoByteScalar(b, b2);
                break;
            }
            case 3: {
                int b3;
                assert ((b & 0xF0) == 224);
                int b2 = this.read();
                if (!IonUTF8.isContinueByteUTF8(b2)) {
                    this.throwUTF8Exception();
                }
                if (!IonUTF8.isContinueByteUTF8(b3 = this.read())) {
                    this.throwUTF8Exception();
                }
                c = IonUTF8.threeByteScalar(b, b2, b3);
                break;
            }
            case 4: {
                int b4;
                int b3;
                assert ((b & 0xF8) == 240);
                int b2 = this.read();
                if (!IonUTF8.isContinueByteUTF8(b2)) {
                    this.throwUTF8Exception();
                }
                if (!IonUTF8.isContinueByteUTF8(b3 = this.read())) {
                    this.throwUTF8Exception();
                }
                if (!IonUTF8.isContinueByteUTF8(b4 = this.read())) {
                    this.throwUTF8Exception();
                }
                if ((c = IonUTF8.fourByteScalar(b, b2, b3, b4)) <= 0x10FFFF) break;
                throw new IonException("illegal utf value encountered in input utf-8 stream");
            }
            default: {
                this.throwUTF8Exception();
            }
        }
        return c;
    }

    private final void throwUTF8Exception() throws IOException {
        this.throwErrorAt("Invalid UTF-8 character encounter in a string at position ");
    }

    private final void throwUnexpectedEOFException() throws IOException {
        this.throwErrorAt("unexpected EOF in value");
    }

    private final void throwIntOverflowExeption() throws IOException {
        this.throwErrorAt("int in stream is too long for a Java int 32 use readLong()");
    }

    protected IonException newErrorAt(String msg) {
        String msg2 = msg + " at position " + this.getPosition();
        return new IonException(msg2);
    }

    protected void throwErrorAt(String msg) {
        throw this.newErrorAt(msg);
    }

    protected void error(String msg) {
        throw new IonException(msg);
    }

    protected void error(Exception e) {
        throw new IonException(e);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum State {
        S_INVALID,
        S_BEFORE_FIELD,
        S_BEFORE_TID,
        S_BEFORE_VALUE,
        S_AFTER_VALUE,
        S_EOF;

    }
}

