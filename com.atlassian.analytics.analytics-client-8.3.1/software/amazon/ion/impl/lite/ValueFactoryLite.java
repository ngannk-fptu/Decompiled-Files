/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonSexp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonBlobLite;
import software.amazon.ion.impl.lite.IonBoolLite;
import software.amazon.ion.impl.lite.IonClobLite;
import software.amazon.ion.impl.lite.IonDecimalLite;
import software.amazon.ion.impl.lite.IonFloatLite;
import software.amazon.ion.impl.lite.IonIntLite;
import software.amazon.ion.impl.lite.IonListLite;
import software.amazon.ion.impl.lite.IonNullLite;
import software.amazon.ion.impl.lite.IonSexpLite;
import software.amazon.ion.impl.lite.IonStringLite;
import software.amazon.ion.impl.lite.IonStructLite;
import software.amazon.ion.impl.lite.IonSymbolLite;
import software.amazon.ion.impl.lite.IonSystemLite;
import software.amazon.ion.impl.lite.IonTimestampLite;
import software.amazon.ion.impl.lite.IonValueLite;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class ValueFactoryLite
implements ValueFactory {
    private ContainerlessContext _context;

    ValueFactoryLite() {
    }

    protected void set_system(IonSystemLite system) {
        this._context = ContainerlessContext.wrap(system);
    }

    @Override
    public IonBlobLite newBlob(byte[] value) {
        IonBlobLite ionValue = this.newBlob(value, 0, value == null ? 0 : value.length);
        return ionValue;
    }

    @Override
    public IonBlobLite newBlob(byte[] value, int offset, int length) {
        IonBlobLite ionValue = new IonBlobLite(this._context, value == null);
        ionValue.setBytes(value, offset, length);
        return ionValue;
    }

    @Override
    public IonBoolLite newBool(boolean value) {
        IonBoolLite ionValue = new IonBoolLite(this._context, false);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonBoolLite newBool(Boolean value) {
        IonBoolLite ionValue = new IonBoolLite(this._context, value == null);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonClobLite newClob(byte[] value) {
        IonClobLite ionValue = this.newClob(value, 0, value == null ? 0 : value.length);
        return ionValue;
    }

    @Override
    public IonClobLite newClob(byte[] value, int offset, int length) {
        IonClobLite ionValue = new IonClobLite(this._context, value == null);
        ionValue.setBytes(value, offset, length);
        return ionValue;
    }

    @Override
    public IonDecimalLite newDecimal(long value) {
        IonDecimalLite ionValue = new IonDecimalLite(this._context, false);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonDecimalLite newDecimal(double value) {
        IonDecimalLite ionValue = new IonDecimalLite(this._context, false);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonDecimalLite newDecimal(BigInteger value) {
        boolean isNull = value == null;
        IonDecimalLite ionValue = new IonDecimalLite(this._context, isNull);
        if (value != null) {
            ionValue.setValue(Decimal.valueOf(value));
        }
        return ionValue;
    }

    @Override
    public IonDecimalLite newDecimal(BigDecimal value) {
        boolean isNull = value == null;
        IonDecimalLite ionValue = new IonDecimalLite(this._context, isNull);
        if (value != null) {
            ionValue.setValue(value);
        }
        return ionValue;
    }

    @Override
    public IonListLite newEmptyList() {
        IonListLite ionValue = new IonListLite(this._context, false);
        return ionValue;
    }

    @Override
    public IonSexpLite newEmptySexp() {
        IonSexpLite ionValue = new IonSexpLite(this._context, false);
        return ionValue;
    }

    @Override
    public IonStructLite newEmptyStruct() {
        IonStructLite ionValue = new IonStructLite(this._context, false);
        return ionValue;
    }

    @Override
    public IonFloatLite newFloat(long value) {
        IonFloatLite ionValue = new IonFloatLite(this._context, false);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonFloatLite newFloat(double value) {
        IonFloatLite ionValue = new IonFloatLite(this._context, false);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonIntLite newInt(int value) {
        IonIntLite ionValue = new IonIntLite(this._context, false);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonIntLite newInt(long value) {
        IonIntLite ionValue = new IonIntLite(this._context, false);
        ionValue.setValue(value);
        return ionValue;
    }

    @Override
    public IonIntLite newInt(Number value) {
        boolean isNull = value == null;
        IonIntLite ionValue = new IonIntLite(this._context, isNull);
        if (value != null) {
            ionValue.setValue(value);
        }
        return ionValue;
    }

    public IonListLite newList(Collection<? extends IonValue> values) throws ContainedValueException, NullPointerException {
        IonListLite ionValue = this.newEmptyList();
        if (values == null) {
            ionValue.makeNull();
        } else {
            ionValue.addAll(values);
        }
        return ionValue;
    }

    @Override
    public IonListLite newList(IonSequence child) throws ContainedValueException, NullPointerException {
        IonListLite ionValue = this.newEmptyList();
        ionValue.add(child);
        return ionValue;
    }

    @Override
    public IonListLite newList(IonValue ... values) throws ContainedValueException, NullPointerException {
        List<IonValue> e = values == null ? null : Arrays.asList(values);
        IonListLite ionValue = this.newEmptyList();
        if (e == null) {
            ionValue.makeNull();
        } else {
            ionValue.addAll((Collection<? extends IonValue>)e);
        }
        return ionValue;
    }

    @Override
    public IonListLite newList(int[] values) {
        ArrayList<IonIntLite> e = this.newInts(values);
        return this.newList(e);
    }

    @Override
    public IonListLite newList(long[] values) {
        ArrayList<IonIntLite> e = this.newInts(values);
        return this.newList(e);
    }

    @Override
    public IonNullLite newNull() {
        IonNullLite ionValue = new IonNullLite(this._context);
        return ionValue;
    }

    @Override
    public IonValueLite newNull(IonType type) {
        switch (type) {
            case NULL: {
                return this.newNull();
            }
            case BOOL: {
                return this.newNullBool();
            }
            case INT: {
                return this.newNullInt();
            }
            case FLOAT: {
                return this.newNullFloat();
            }
            case DECIMAL: {
                return this.newNullDecimal();
            }
            case TIMESTAMP: {
                return this.newNullTimestamp();
            }
            case SYMBOL: {
                return this.newNullSymbol();
            }
            case STRING: {
                return this.newNullString();
            }
            case CLOB: {
                return this.newNullClob();
            }
            case BLOB: {
                return this.newNullBlob();
            }
            case LIST: {
                return this.newNullList();
            }
            case SEXP: {
                return this.newNullSexp();
            }
            case STRUCT: {
                return this.newNullStruct();
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public IonBlobLite newNullBlob() {
        IonBlobLite ionValue = new IonBlobLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonBoolLite newNullBool() {
        IonBoolLite ionValue = new IonBoolLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonClobLite newNullClob() {
        IonClobLite ionValue = new IonClobLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonDecimalLite newNullDecimal() {
        IonDecimalLite ionValue = new IonDecimalLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonFloatLite newNullFloat() {
        IonFloatLite ionValue = new IonFloatLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonIntLite newNullInt() {
        IonIntLite ionValue = new IonIntLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonListLite newNullList() {
        IonListLite ionValue = new IonListLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonSexpLite newNullSexp() {
        IonSexpLite ionValue = new IonSexpLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonStringLite newNullString() {
        IonStringLite ionValue = new IonStringLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonStructLite newNullStruct() {
        IonStructLite ionValue = new IonStructLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonSymbolLite newNullSymbol() {
        IonSymbolLite ionValue = new IonSymbolLite(this._context, true);
        return ionValue;
    }

    @Override
    public IonTimestampLite newNullTimestamp() {
        IonTimestampLite ionValue = new IonTimestampLite(this._context, true);
        return ionValue;
    }

    public IonSexpLite newSexp(Collection<? extends IonValue> values) throws ContainedValueException, NullPointerException {
        IonSexpLite ionValue = this.newEmptySexp();
        if (values == null) {
            ionValue.makeNull();
        } else {
            ionValue.addAll(values);
        }
        return ionValue;
    }

    @Override
    public IonSexpLite newSexp(IonSequence child) throws ContainedValueException, NullPointerException {
        IonSexpLite ionValue = this.newEmptySexp();
        ionValue.add(child);
        return ionValue;
    }

    @Override
    public IonSexp newSexp(IonValue ... values) throws ContainedValueException, NullPointerException {
        List<IonValue> e = values == null ? null : Arrays.asList(values);
        IonSexpLite ionValue = this.newEmptySexp();
        if (e == null) {
            ionValue.makeNull();
        } else {
            ionValue.addAll((Collection<? extends IonValue>)e);
        }
        return ionValue;
    }

    @Override
    public IonSexpLite newSexp(int[] values) {
        ArrayList<IonIntLite> e = this.newInts(values);
        return this.newSexp(e);
    }

    @Override
    public IonSexpLite newSexp(long[] values) {
        ArrayList<IonIntLite> e = this.newInts(values);
        return this.newSexp(e);
    }

    @Override
    public IonStringLite newString(String value) {
        boolean isNull = value == null;
        IonStringLite ionValue = new IonStringLite(this._context, isNull);
        if (value != null) {
            ionValue.setValue(value);
        }
        return ionValue;
    }

    @Override
    public IonSymbolLite newSymbol(String value) {
        boolean isNull = value == null;
        IonSymbolLite ionValue = new IonSymbolLite(this._context, isNull);
        if (value != null) {
            ionValue.setValue(value);
        }
        return ionValue;
    }

    @Override
    public IonSymbolLite newSymbol(SymbolToken value) {
        return new IonSymbolLite(this._context, value);
    }

    @Override
    public IonTimestampLite newTimestamp(Timestamp value) {
        boolean isNull = value == null;
        IonTimestampLite ionValue = new IonTimestampLite(this._context, isNull);
        if (value != null) {
            ionValue.setValue(value);
        }
        return ionValue;
    }

    private ArrayList<IonIntLite> newInts(int[] elements) {
        ArrayList<IonIntLite> e = null;
        if (elements != null) {
            e = new ArrayList<IonIntLite>(elements.length);
            for (int i = 0; i < elements.length; ++i) {
                int value = elements[i];
                e.add(this.newInt(value));
            }
        }
        return e;
    }

    private ArrayList<IonIntLite> newInts(long[] elements) {
        ArrayList<IonIntLite> e = null;
        if (elements != null) {
            e = new ArrayList<IonIntLite>(elements.length);
            for (int i = 0; i < elements.length; ++i) {
                long value = elements[i];
                e.add(this.newInt(value));
            }
        }
        return e;
    }
}

