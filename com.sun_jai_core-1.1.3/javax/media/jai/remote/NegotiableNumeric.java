/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.Negotiable;

public class NegotiableNumeric
implements Negotiable {
    Number number;
    Class elementClass;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Double;

    public NegotiableNumeric(byte b) {
        this.number = new Byte(b);
        this.elementClass = this.number.getClass();
    }

    public NegotiableNumeric(short s) {
        this.number = new Short(s);
        this.elementClass = this.number.getClass();
    }

    public NegotiableNumeric(int i) {
        this.number = new Integer(i);
        this.elementClass = this.number.getClass();
    }

    public NegotiableNumeric(long l) {
        this.number = new Long(l);
        this.elementClass = this.number.getClass();
    }

    public NegotiableNumeric(float f) {
        this.number = new Float(f);
        this.elementClass = this.number.getClass();
    }

    public NegotiableNumeric(double d) {
        this.number = new Double(d);
        this.elementClass = this.number.getClass();
    }

    public NegotiableNumeric(Number n) {
        if (n == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableNumeric0"));
        }
        this.number = n;
        this.elementClass = this.number.getClass();
    }

    public Number getNumber() {
        return this.number;
    }

    public Negotiable negotiate(Negotiable other) {
        if (other == null) {
            return null;
        }
        if (!(other instanceof NegotiableNumeric) || other.getNegotiatedValueClass() != this.elementClass) {
            return null;
        }
        NegotiableNumeric otherNN = (NegotiableNumeric)other;
        if (this.number.equals(otherNN.getNumber())) {
            return new NegotiableNumeric(this.number);
        }
        return null;
    }

    public Object getNegotiatedValue() {
        return this.number;
    }

    public Class getNegotiatedValueClass() {
        return this.elementClass;
    }

    public byte getNegotiatedValueAsByte() {
        if (this.elementClass != (class$java$lang$Byte == null ? (class$java$lang$Byte = NegotiableNumeric.class$("java.lang.Byte")) : class$java$lang$Byte)) {
            throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
        }
        return this.number.byteValue();
    }

    public short getNegotiatedValueAsShort() {
        if (this.elementClass != (class$java$lang$Short == null ? (class$java$lang$Short = NegotiableNumeric.class$("java.lang.Short")) : class$java$lang$Short)) {
            throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
        }
        return this.number.shortValue();
    }

    public int getNegotiatedValueAsInt() {
        if (this.elementClass != (class$java$lang$Integer == null ? (class$java$lang$Integer = NegotiableNumeric.class$("java.lang.Integer")) : class$java$lang$Integer)) {
            throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
        }
        return this.number.intValue();
    }

    public long getNegotiatedValueAsLong() {
        if (this.elementClass != (class$java$lang$Long == null ? (class$java$lang$Long = NegotiableNumeric.class$("java.lang.Long")) : class$java$lang$Long)) {
            throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
        }
        return this.number.longValue();
    }

    public float getNegotiatedValueAsFloat() {
        if (this.elementClass != (class$java$lang$Float == null ? (class$java$lang$Float = NegotiableNumeric.class$("java.lang.Float")) : class$java$lang$Float)) {
            throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
        }
        return this.number.floatValue();
    }

    public double getNegotiatedValueAsDouble() {
        if (this.elementClass != (class$java$lang$Double == null ? (class$java$lang$Double = NegotiableNumeric.class$("java.lang.Double")) : class$java$lang$Double)) {
            throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
        }
        return this.number.doubleValue();
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

