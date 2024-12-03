/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.Negotiable;
import javax.media.jai.util.Range;

public class NegotiableNumericRange
implements Negotiable {
    private Range range;
    static /* synthetic */ Class class$java$lang$Number;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$math$BigInteger;
    static /* synthetic */ Class class$java$math$BigDecimal;

    public NegotiableNumericRange(Range range) {
        if (range == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableNumericRange0"));
        }
        if (!(class$java$lang$Number == null ? (class$java$lang$Number = NegotiableNumericRange.class$("java.lang.Number")) : class$java$lang$Number).isAssignableFrom(range.getElementClass())) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableNumericRange1"));
        }
        this.range = range;
    }

    public Range getRange() {
        if (this.range.isEmpty()) {
            return null;
        }
        return this.range;
    }

    public Negotiable negotiate(Negotiable other) {
        if (other == null) {
            return null;
        }
        if (!(other instanceof NegotiableNumericRange)) {
            return null;
        }
        NegotiableNumericRange otherNNRange = (NegotiableNumericRange)other;
        Range otherRange = otherNNRange.getRange();
        if (otherRange == null) {
            return null;
        }
        if (otherRange.getElementClass() != this.range.getElementClass()) {
            return null;
        }
        Range result = this.range.intersect(otherRange);
        if (result.isEmpty()) {
            return null;
        }
        return new NegotiableNumericRange(result);
    }

    public Object getNegotiatedValue() {
        if (this.range.isEmpty()) {
            return null;
        }
        Number minValue = (Number)((Object)this.range.getMinValue());
        if (minValue == null) {
            Number maxValue = (Number)((Object)this.range.getMaxValue());
            if (maxValue == null) {
                Class elementClass = this.range.getElementClass();
                if (elementClass == (class$java$lang$Byte == null ? (class$java$lang$Byte = NegotiableNumericRange.class$("java.lang.Byte")) : class$java$lang$Byte)) {
                    return new Byte(0);
                }
                if (elementClass == (class$java$lang$Short == null ? (class$java$lang$Short = NegotiableNumericRange.class$("java.lang.Short")) : class$java$lang$Short)) {
                    return new Short(0);
                }
                if (elementClass == (class$java$lang$Integer == null ? (class$java$lang$Integer = NegotiableNumericRange.class$("java.lang.Integer")) : class$java$lang$Integer)) {
                    return new Integer(0);
                }
                if (elementClass == (class$java$lang$Long == null ? (class$java$lang$Long = NegotiableNumericRange.class$("java.lang.Long")) : class$java$lang$Long)) {
                    return new Long(0L);
                }
                if (elementClass == (class$java$lang$Float == null ? (class$java$lang$Float = NegotiableNumericRange.class$("java.lang.Float")) : class$java$lang$Float)) {
                    return new Float(0.0f);
                }
                if (elementClass == (class$java$lang$Double == null ? (class$java$lang$Double = NegotiableNumericRange.class$("java.lang.Double")) : class$java$lang$Double)) {
                    return new Double(0.0);
                }
                if (elementClass == (class$java$math$BigInteger == null ? (class$java$math$BigInteger = NegotiableNumericRange.class$("java.math.BigInteger")) : class$java$math$BigInteger)) {
                    return BigInteger.ZERO;
                }
                if (elementClass == (class$java$math$BigDecimal == null ? (class$java$math$BigDecimal = NegotiableNumericRange.class$("java.math.BigDecimal")) : class$java$math$BigDecimal)) {
                    return new BigDecimal(BigInteger.ZERO);
                }
            } else {
                return maxValue;
            }
        }
        return minValue;
    }

    public Class getNegotiatedValueClass() {
        return this.range.getElementClass();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

