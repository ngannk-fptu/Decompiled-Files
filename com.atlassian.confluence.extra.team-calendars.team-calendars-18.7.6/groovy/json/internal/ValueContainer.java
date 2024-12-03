/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.Exceptions;
import groovy.json.internal.Type;
import groovy.json.internal.Value;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ValueContainer
implements CharSequence,
Value {
    public static final Value TRUE = new ValueContainer(Type.TRUE);
    public static final Value FALSE = new ValueContainer(Type.FALSE);
    public static final Value NULL = new ValueContainer(Type.NULL);
    public Object value;
    public Type type;
    private boolean container;
    public boolean decodeStrings;

    public ValueContainer(Object value, Type type, boolean decodeStrings) {
        this.value = value;
        this.type = type;
        this.decodeStrings = decodeStrings;
    }

    public ValueContainer(Type type) {
        this.type = type;
    }

    public ValueContainer(Map<String, Object> map) {
        this.value = map;
        this.type = Type.MAP;
        this.container = true;
    }

    public ValueContainer(List<Object> list) {
        this.value = list;
        this.type = Type.LIST;
        this.container = true;
    }

    @Override
    public int intValue() {
        return Exceptions.die(Integer.TYPE, Exceptions.sputs(new Object[]{"intValue not supported for type ", this.type}));
    }

    @Override
    public long longValue() {
        return Exceptions.die(Integer.TYPE, Exceptions.sputs(new Object[]{"intValue not supported for type ", this.type})).intValue();
    }

    @Override
    public boolean booleanValue() {
        switch (this.type) {
            case FALSE: {
                return false;
            }
            case TRUE: {
                return true;
            }
        }
        Exceptions.die();
        return false;
    }

    @Override
    public String stringValue() {
        if (this.type == Type.NULL) {
            return null;
        }
        return this.type.toString();
    }

    @Override
    public String stringValueEncoded() {
        return this.toString();
    }

    @Override
    public String toString() {
        return this.type.toString();
    }

    @Override
    public Object toValue() {
        if (this.value != null) {
            return this.value;
        }
        switch (this.type) {
            case FALSE: {
                this.value = false;
                return this.value;
            }
            case TRUE: {
                this.value = true;
                return this.value;
            }
            case NULL: {
                return null;
            }
        }
        Exceptions.die();
        return null;
    }

    @Override
    public <T extends Enum> T toEnum(Class<T> cls) {
        return (T)((Enum)this.value);
    }

    @Override
    public boolean isContainer() {
        return this.container;
    }

    @Override
    public void chop() {
    }

    @Override
    public char charValue() {
        return '\u0000';
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return '0';
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return "";
    }

    @Override
    public Date dateValue() {
        return null;
    }

    @Override
    public byte byteValue() {
        return 0;
    }

    @Override
    public short shortValue() {
        return 0;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return null;
    }

    @Override
    public BigInteger bigIntegerValue() {
        return null;
    }

    @Override
    public double doubleValue() {
        return 0.0;
    }

    @Override
    public float floatValue() {
        return 0.0f;
    }
}

