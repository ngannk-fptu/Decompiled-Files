/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import software.amazon.ion.Decimal;
import software.amazon.ion.IntegerSize;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonType;
import software.amazon.ion.NullValueException;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.impl.IonReaderBinaryRawX;
import software.amazon.ion.impl.PrivateReaderWriter;
import software.amazon.ion.impl.PrivateScalarConversions;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.SymbolTokenImpl;
import software.amazon.ion.impl.UnifiedInputStreamX;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class IonReaderBinarySystemX
extends IonReaderBinaryRawX
implements PrivateReaderWriter {
    IonSystem _system;
    SymbolTable _symbols;
    static final BigInteger MIN_LONG_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
    static final BigInteger MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

    IonReaderBinarySystemX(IonSystem system, UnifiedInputStreamX in) {
        this.init_raw(in);
        this._system = system;
        this._symbols = system.getSystemSymbolTable();
    }

    @Override
    public SymbolToken[] getTypeAnnotationSymbols() {
        this.load_annotations();
        int count = this._annotation_count;
        if (count == 0) {
            return SymbolToken.EMPTY_ARRAY;
        }
        SymbolTable symtab = this.getSymbolTable();
        SymbolToken[] result = new SymbolToken[count];
        for (int i = 0; i < count; ++i) {
            int sid = this._annotation_ids[i];
            String text = symtab.findKnownSymbol(sid);
            result[i] = new SymbolTokenImpl(text, sid);
        }
        return result;
    }

    private void load_once() {
        if (this._v.isEmpty()) {
            try {
                this.load_scalar_value();
            }
            catch (IOException e) {
                this.error(e);
            }
        }
    }

    protected final void prepare_value(int as_type) {
        this.load_once();
        if (as_type != 0 && !this._v.hasValueOfType(as_type)) {
            if (IonType.SYMBOL.equals((Object)this._value_type) && !PrivateScalarConversions.ValueVariant.isNumericType(as_type)) assert (IonType.SYMBOL.equals((Object)this._value_type) && !PrivateScalarConversions.ValueVariant.isNumericType(as_type));
            if (!this._v.can_convert(as_type)) {
                String message = "can't cast from " + PrivateScalarConversions.getValueTypeName(this._v.getAuthoritativeType()) + " to " + PrivateScalarConversions.getValueTypeName(as_type);
                throw new IllegalStateException(message);
            }
            int fnid = this._v.get_conversion_fnid(as_type);
            this._v.cast(fnid);
        }
    }

    protected final void load_cached_value(int value_type) throws IOException {
        if (this._v.isEmpty()) {
            this.load_scalar_value();
        }
    }

    private static BigInteger unsignedLongToBigInteger(int signum, long val) {
        byte[] magnitude = new byte[]{(byte)(val >> 56 & 0xFFL), (byte)(val >> 48 & 0xFFL), (byte)(val >> 40 & 0xFFL), (byte)(val >> 32 & 0xFFL), (byte)(val >> 24 & 0xFFL), (byte)(val >> 16 & 0xFFL), (byte)(val >> 8 & 0xFFL), (byte)(val & 0xFFL)};
        return new BigInteger(signum, magnitude);
    }

    private final void load_scalar_value() throws IOException {
        switch (this._value_type) {
            case NULL: 
            case BOOL: 
            case INT: 
            case FLOAT: 
            case DECIMAL: 
            case TIMESTAMP: 
            case SYMBOL: 
            case STRING: {
                break;
            }
            default: {
                return;
            }
        }
        if (this._value_is_null) {
            this._v.setValueToNull(this._value_type);
            this._v.setAuthoritativeType(1);
            return;
        }
        switch (this._value_type) {
            default: {
                return;
            }
            case BOOL: {
                this._v.setValue(this._value_is_true);
                this._v.setAuthoritativeType(2);
                break;
            }
            case INT: {
                if (this._value_len == 0) {
                    int v = 0;
                    this._v.setValue(v);
                    this._v.setAuthoritativeType(3);
                    break;
                }
                if (this._value_len <= 8) {
                    boolean is_negative;
                    long v = this.readULong(this._value_len);
                    boolean bl = is_negative = this._value_tid == 3;
                    if (v < 0L) {
                        int signum = !is_negative ? 1 : -1;
                        BigInteger big = IonReaderBinarySystemX.unsignedLongToBigInteger(signum, v);
                        this._v.setValue(big);
                        if (big.compareTo(MIN_LONG_VALUE) < 0 || big.compareTo(MAX_LONG_VALUE) > 0) {
                            this._v.setAuthoritativeType(5);
                            break;
                        }
                        this._v.addValue(big.longValue());
                        this._v.setAuthoritativeType(4);
                        break;
                    }
                    if (is_negative) {
                        v = -v;
                    }
                    if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) {
                        this._v.setValue(v);
                        this._v.setAuthoritativeType(4);
                        break;
                    }
                    this._v.setValue((int)v);
                    this._v.setAuthoritativeType(3);
                    break;
                }
                boolean is_negative = this._value_tid == 3;
                BigInteger v = this.readBigInteger(this._value_len, is_negative);
                this._v.setValue(v);
                this._v.setAuthoritativeType(5);
                break;
            }
            case FLOAT: {
                double d = this._value_len == 0 ? 0.0 : this.readFloat(this._value_len);
                this._v.setValue(d);
                this._v.setAuthoritativeType(7);
                break;
            }
            case DECIMAL: {
                Decimal dec = this.readDecimal(this._value_len);
                this._v.setValue(dec);
                this._v.setAuthoritativeType(6);
                break;
            }
            case TIMESTAMP: {
                Timestamp t = this.readTimestamp(this._value_len);
                this._v.setValue(t);
                this._v.setAuthoritativeType(10);
                break;
            }
            case SYMBOL: {
                long sid = this.readULong(this._value_len);
                if (sid < 1L || sid > Integer.MAX_VALUE) {
                    String message = "symbol id [" + sid + "] out of range " + "(1-" + Integer.MAX_VALUE + ")";
                    this.throwErrorAt(message);
                }
                this._v.setValue((int)sid);
                this._v.setAuthoritativeType(3);
                break;
            }
            case STRING: {
                String s = this.readString(this._value_len);
                this._v.setValue(s);
                this._v.setAuthoritativeType(8);
            }
        }
        this._state = IonReaderBinaryRawX.State.S_AFTER_VALUE;
    }

    @Override
    public boolean isNullValue() {
        return this._value_is_null;
    }

    @Override
    public boolean booleanValue() {
        this.prepare_value(2);
        return this._v.getBoolean();
    }

    @Override
    public double doubleValue() {
        this.prepare_value(7);
        return this._v.getDouble();
    }

    @Override
    public int intValue() {
        if (this._value_type != IonType.INT && this._value_type != IonType.DECIMAL && this._value_type != IonType.FLOAT) {
            throw new IllegalStateException();
        }
        this.prepare_value(3);
        return this._v.getInt();
    }

    @Override
    public long longValue() {
        if (this._value_type != IonType.INT && this._value_type != IonType.DECIMAL && this._value_type != IonType.FLOAT) {
            throw new IllegalStateException();
        }
        this.prepare_value(4);
        return this._v.getLong();
    }

    @Override
    public BigInteger bigIntegerValue() {
        if (this._value_type != IonType.INT && this._value_type != IonType.DECIMAL && this._value_type != IonType.FLOAT) {
            throw new IllegalStateException();
        }
        if (this._value_is_null) {
            return null;
        }
        this.prepare_value(5);
        return this._v.getBigInteger();
    }

    @Override
    public BigDecimal bigDecimalValue() {
        if (this._value_is_null) {
            return null;
        }
        this.prepare_value(6);
        return this._v.getBigDecimal();
    }

    @Override
    public Decimal decimalValue() {
        if (this._value_is_null) {
            return null;
        }
        this.prepare_value(6);
        return this._v.getDecimal();
    }

    @Override
    public Date dateValue() {
        if (this._value_is_null) {
            return null;
        }
        this.prepare_value(9);
        return this._v.getDate();
    }

    @Override
    public Timestamp timestampValue() {
        if (this._value_is_null) {
            return null;
        }
        this.prepare_value(10);
        return this._v.getTimestamp();
    }

    @Override
    public IntegerSize getIntegerSize() {
        this.load_once();
        if (this._value_type != IonType.INT || this._v.isNull()) {
            return null;
        }
        return PrivateScalarConversions.getIntegerSize(this._v.getAuthoritativeType());
    }

    @Override
    public final String stringValue() {
        if (!IonType.isText(this._value_type)) {
            throw new IllegalStateException();
        }
        if (this._value_is_null) {
            return null;
        }
        if (this._value_type == IonType.SYMBOL) {
            if (!this._v.hasValueOfType(8)) {
                int sid = this.getSymbolId();
                String name = this._symbols.findKnownSymbol(sid);
                if (name == null) {
                    throw new UnknownSymbolException(sid);
                }
                this._v.addValue(name);
            }
        } else {
            this.prepare_value(8);
        }
        return this._v.getString();
    }

    @Override
    public final SymbolToken symbolValue() {
        if (this._value_type != IonType.SYMBOL) {
            throw new IllegalStateException();
        }
        if (this._value_is_null) {
            return null;
        }
        int sid = this.getSymbolId();
        assert (sid != -1);
        String text = this._symbols.findKnownSymbol(sid);
        return new SymbolTokenImpl(text, sid);
    }

    int getSymbolId() {
        if (this._value_type != IonType.SYMBOL) {
            throw new IllegalStateException();
        }
        if (this._value_is_null) {
            throw new NullValueException();
        }
        this.prepare_value(3);
        return this._v.getInt();
    }

    @Override
    public final String getFieldName() {
        String name;
        if (this._value_field_id == -1) {
            name = null;
        } else {
            name = this._symbols.findKnownSymbol(this._value_field_id);
            if (name == null) {
                throw new UnknownSymbolException(this._value_field_id);
            }
        }
        return name;
    }

    @Override
    public final SymbolToken getFieldNameSymbol() {
        if (this._value_field_id == -1) {
            return null;
        }
        int sid = this._value_field_id;
        String text = this._symbols.findKnownSymbol(sid);
        return new SymbolTokenImpl(text, sid);
    }

    @Override
    public final Iterator<String> iterateTypeAnnotations() {
        String[] annotations = this.getTypeAnnotations();
        return PrivateUtils.stringIterator(annotations);
    }

    @Override
    public final String[] getTypeAnnotations() {
        String[] anns;
        this.load_annotations();
        if (this._annotation_count < 1) {
            anns = PrivateUtils.EMPTY_STRING_ARRAY;
        } else {
            anns = new String[this._annotation_count];
            for (int ii = 0; ii < this._annotation_count; ++ii) {
                anns[ii] = this._symbols.findKnownSymbol(this._annotation_ids[ii]);
                if (anns[ii] != null) continue;
                throw new UnknownSymbolException(this._annotation_ids[ii]);
            }
        }
        return anns;
    }

    @Override
    public final SymbolTable getSymbolTable() {
        return this._symbols;
    }

    @Override
    public SymbolTable pop_passed_symbol_table() {
        return null;
    }
}

