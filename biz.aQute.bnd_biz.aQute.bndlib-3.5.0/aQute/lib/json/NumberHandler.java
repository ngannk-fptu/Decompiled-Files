/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.json.Decoder;
import aQute.lib.json.Encoder;
import aQute.lib.json.Handler;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class NumberHandler
extends Handler {
    final Class<?> type;

    NumberHandler(Class<?> clazz) {
        this.type = clazz;
    }

    @Override
    public void encode(Encoder app, Object object, Map<Object, Type> visited) throws Exception {
        String s = object.toString();
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        app.append(s);
    }

    @Override
    public Object decode(Decoder dec, boolean s) {
        return this.decode(dec, s ? 1.0 : 0.0);
    }

    @Override
    public Object decode(Decoder dec, String s) {
        double d = Double.parseDouble(s);
        return this.decode(dec, d);
    }

    @Override
    public Object decode(Decoder dec) {
        return this.decode(dec, 0.0);
    }

    @Override
    public Object decode(Decoder dec, Number s) {
        double dd = s.doubleValue();
        if (this.type == Double.TYPE || this.type == Double.class) {
            return s.doubleValue();
        }
        if ((this.type == Integer.TYPE || this.type == Integer.class) && this.within(dd, -2.147483648E9, 2.147483647E9)) {
            return s.intValue();
        }
        if ((this.type == Long.TYPE || this.type == Long.class) && this.within(dd, -9.223372036854776E18, 9.223372036854776E18)) {
            return s.longValue();
        }
        if ((this.type == Byte.TYPE || this.type == Byte.class) && this.within(dd, -128.0, 127.0)) {
            return s.byteValue();
        }
        if ((this.type == Short.TYPE || this.type == Short.class) && this.within(dd, -32768.0, 32767.0)) {
            return s.shortValue();
        }
        if (this.type == Float.TYPE || this.type == Float.class) {
            return Float.valueOf(s.floatValue());
        }
        if (this.type == BigDecimal.class) {
            return BigDecimal.valueOf(dd);
        }
        if (this.type == BigInteger.class) {
            return BigInteger.valueOf(s.longValue());
        }
        throw new IllegalArgumentException("Unknown number format: " + this.type);
    }

    private boolean within(double s, double minValue, double maxValue) {
        return s >= minValue && s <= maxValue;
    }
}

