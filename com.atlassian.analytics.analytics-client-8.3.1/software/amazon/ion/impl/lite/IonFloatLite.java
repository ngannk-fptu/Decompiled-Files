/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.math.BigDecimal;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonFloat;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.NullValueException;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

final class IonFloatLite
extends IonValueLite
implements IonFloat {
    private static final int HASH_SIGNATURE = IonType.FLOAT.toString().hashCode();
    private Double _float_value;

    IonFloatLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonFloatLite(IonFloatLite existing, IonContext context) {
        super(existing, context);
        this._float_value = existing._float_value;
    }

    IonFloatLite clone(IonContext context) {
        return new IonFloatLite(this, context);
    }

    public IonFloatLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            long bits = Double.doubleToLongBits(this.doubleValue());
            result ^= (int)(bits >>> 32 ^ bits);
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.FLOAT;
    }

    public float floatValue() throws NullValueException {
        this.validateThisNotNull();
        return this._float_value.floatValue();
    }

    public double doubleValue() throws NullValueException {
        this.validateThisNotNull();
        return this._float_value;
    }

    public BigDecimal bigDecimalValue() throws NullValueException {
        if (this.isNullValue()) {
            return null;
        }
        return Decimal.valueOf(this._float_value);
    }

    public void setValue(float value) {
        this.setValue(new Double(value));
    }

    public void setValue(double value) {
        this.setValue(new Double(value));
    }

    public void setValue(BigDecimal value) {
        this.checkForLock();
        if (value == null) {
            this._float_value = null;
            this._isNullValue(true);
        } else {
            this.setValue(value.doubleValue());
        }
    }

    public void setValue(Double d) {
        this.checkForLock();
        this._float_value = d;
        this._isNullValue(d == null);
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        if (this.isNullValue()) {
            writer.writeNull(IonType.FLOAT);
        } else {
            writer.writeFloat(this._float_value);
        }
    }

    public boolean isNumericValue() {
        return !this.isNullValue() && !this._float_value.isNaN() && !this._float_value.isInfinite();
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

