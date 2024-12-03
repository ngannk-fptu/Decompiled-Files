/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.math.BigDecimal;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonDecimal;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.NullValueException;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

final class IonDecimalLite
extends IonValueLite
implements IonDecimal {
    private static final int HASH_SIGNATURE = IonType.DECIMAL.toString().hashCode();
    private static final int NEGATIVE_ZERO_HASH_SIGNATURE = "NEGATIVE ZERO".hashCode();
    private BigDecimal _decimal_value;

    public static boolean isNegativeZero(float value) {
        if (value != 0.0f) {
            return false;
        }
        return (Float.floatToRawIntBits(value) & Integer.MIN_VALUE) != 0;
    }

    public static boolean isNegativeZero(double value) {
        if (value != 0.0) {
            return false;
        }
        return (Double.doubleToLongBits(value) & Long.MIN_VALUE) != 0L;
    }

    IonDecimalLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonDecimalLite(IonDecimalLite existing, IonContext context) {
        super(existing, context);
        this._decimal_value = existing._decimal_value;
    }

    IonDecimalLite clone(IonContext parentContext) {
        return new IonDecimalLite(this, parentContext);
    }

    public IonDecimalLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            Decimal dec = this.decimalValue();
            result ^= dec.hashCode();
            if (dec.isNegativeZero()) {
                result ^= NEGATIVE_ZERO_HASH_SIGNATURE;
            }
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.DECIMAL;
    }

    public float floatValue() throws NullValueException {
        if (this._isNullValue()) {
            throw new NullValueException();
        }
        float f = this._decimal_value.floatValue();
        return f;
    }

    public double doubleValue() throws NullValueException {
        if (this._isNullValue()) {
            throw new NullValueException();
        }
        double d = this._decimal_value.doubleValue();
        return d;
    }

    public BigDecimal bigDecimalValue() throws NullValueException {
        return Decimal.bigDecimalValue(this._decimal_value);
    }

    public Decimal decimalValue() throws NullValueException {
        return Decimal.valueOf(this._decimal_value);
    }

    public void setValue(long value) {
        this.setValue(Decimal.valueOf(value));
    }

    public void setValue(float value) {
        this.setValue(Decimal.valueOf(value));
    }

    public void setValue(double value) {
        this.setValue(Decimal.valueOf(value));
    }

    public void setValue(BigDecimal value) {
        this.checkForLock();
        this._decimal_value = value;
        this._isNullValue(value == null);
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        writer.writeDecimal(this._decimal_value);
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

