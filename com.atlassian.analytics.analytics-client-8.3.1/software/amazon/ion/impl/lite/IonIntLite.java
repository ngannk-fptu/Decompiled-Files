/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.amazon.ion.IntegerSize;
import software.amazon.ion.IonInt;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.NullValueException;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

final class IonIntLite
extends IonValueLite
implements IonInt {
    private static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
    private static final int HASH_SIGNATURE = IonType.INT.toString().hashCode();
    private static final int INT_SIZE_MASK = 24;
    private static final int INT_SIZE_SHIFT = 3;
    private static final IntegerSize[] SIZES = IntegerSize.values();
    private long _long_value;
    private BigInteger _big_int_value;

    IonIntLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonIntLite(IonIntLite existing, IonContext context) {
        super(existing, context);
        this._long_value = existing._long_value;
        this._big_int_value = existing._big_int_value;
    }

    IonIntLite clone(IonContext context) {
        return new IonIntLite(this, context);
    }

    public IonIntLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            if (this._big_int_value == null) {
                long lv = this.longValue();
                result ^= (int)lv;
                int hi_word = (int)(lv >>> 32);
                if (hi_word != 0 && hi_word != -1) {
                    result ^= hi_word;
                }
            } else {
                result = this._big_int_value.hashCode();
            }
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.INT;
    }

    public int intValue() throws NullValueException {
        this.validateThisNotNull();
        if (this._big_int_value == null) {
            return (int)this._long_value;
        }
        return this._big_int_value.intValue();
    }

    public long longValue() throws NullValueException {
        this.validateThisNotNull();
        if (this._big_int_value == null) {
            return this._long_value;
        }
        return this._big_int_value.longValue();
    }

    public BigInteger bigIntegerValue() throws NullValueException {
        if (this.isNullValue()) {
            return null;
        }
        if (this._big_int_value == null) {
            return BigInteger.valueOf(this._long_value);
        }
        return this._big_int_value;
    }

    public void setValue(int value) {
        this.setValue((long)value);
    }

    public void setValue(long value) {
        this.checkForLock();
        this.doSetValue(value, false);
    }

    public void setValue(Number value) {
        this.checkForLock();
        if (value == null) {
            this.doSetValue(0L, true);
        } else if (value instanceof BigInteger) {
            BigInteger big = (BigInteger)value;
            this.doSetValue(big);
        } else if (value instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal)value;
            this.doSetValue(bd.toBigInteger());
        } else {
            this.doSetValue(value.longValue(), false);
        }
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        if (this.isNullValue()) {
            writer.writeNull(IonType.INT);
        } else if (this._big_int_value != null) {
            writer.writeInt(this._big_int_value);
        } else {
            writer.writeInt(this._long_value);
        }
    }

    private void doSetValue(long value, boolean isNull) {
        this._long_value = value;
        this._big_int_value = null;
        this._isNullValue(isNull);
        if (!isNull) {
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                this.setSize(IntegerSize.LONG);
            } else {
                this.setSize(IntegerSize.INT);
            }
        }
    }

    private void doSetValue(BigInteger value) {
        if (value.compareTo(LONG_MIN_VALUE) < 0 || value.compareTo(LONG_MAX_VALUE) > 0) {
            this.setSize(IntegerSize.BIG_INTEGER);
            this._long_value = 0L;
            this._big_int_value = value;
            this._isNullValue(false);
        } else {
            this.doSetValue(value.longValue(), false);
        }
    }

    private void setSize(IntegerSize size) {
        this._setMetadata(size.ordinal(), 24, 3);
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public IntegerSize getIntegerSize() {
        if (this.isNullValue()) {
            return null;
        }
        return SIZES[this._getMetadata(24, 3)];
    }
}

