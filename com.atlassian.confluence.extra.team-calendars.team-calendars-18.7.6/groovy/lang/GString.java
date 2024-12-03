/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Buildable;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MissingMethodException;
import groovy.lang.StringWriterIOException;
import groovy.lang.Writable;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Pattern;
import org.apache.groovy.io.StringBuilderWriter;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.StringGroovyMethods;

public abstract class GString
extends GroovyObjectSupport
implements Comparable,
CharSequence,
Writable,
Buildable,
Serializable {
    private static final long serialVersionUID = -2638020355892246323L;
    private static final String MKP = "mkp";
    private static final String YIELD = "yield";
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final GString EMPTY = new GString(EMPTY_OBJECT_ARRAY){
        private static final long serialVersionUID = -7676746462783374250L;

        @Override
        public String[] getStrings() {
            return new String[]{""};
        }
    };
    private final Object[] values;

    public GString(Object values) {
        this.values = (Object[])values;
    }

    public GString(Object[] values) {
        this.values = values;
    }

    public abstract String[] getStrings();

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return super.invokeMethod(name, args);
        }
        catch (MissingMethodException e) {
            return InvokerHelper.invokeMethod(this.toString(), name, args);
        }
    }

    public Object[] getValues() {
        return this.values;
    }

    public GString plus(GString that) {
        Object[] values = this.getValues();
        return new GStringImpl(this.appendValues(values, that.getValues()), this.appendStrings(this.getStrings(), that.getStrings(), values.length));
    }

    private String[] appendStrings(String[] strings, String[] thatStrings, int valuesLength) {
        int stringsLength = strings.length;
        boolean isStringsLonger = stringsLength > valuesLength;
        int thatStringsLength = isStringsLonger ? thatStrings.length - 1 : thatStrings.length;
        String[] newStrings = new String[stringsLength + thatStringsLength];
        System.arraycopy(strings, 0, newStrings, 0, stringsLength);
        if (isStringsLonger) {
            System.arraycopy(thatStrings, 1, newStrings, stringsLength, thatStringsLength);
            int lastIndexOfStrings = stringsLength - 1;
            newStrings[lastIndexOfStrings] = strings[lastIndexOfStrings] + thatStrings[0];
        } else {
            System.arraycopy(thatStrings, 0, newStrings, stringsLength, thatStringsLength);
        }
        return newStrings;
    }

    private Object[] appendValues(Object[] values, Object[] thatValues) {
        int valuesLength = values.length;
        int thatValuesLength = thatValues.length;
        Object[] newValues = new Object[valuesLength + thatValuesLength];
        System.arraycopy(values, 0, newValues, 0, valuesLength);
        System.arraycopy(thatValues, 0, newValues, valuesLength, thatValuesLength);
        return newValues;
    }

    public GString plus(String that) {
        return this.plus(new GStringImpl(EMPTY_OBJECT_ARRAY, new String[]{that}));
    }

    public int getValueCount() {
        return this.values.length;
    }

    public Object getValue(int idx) {
        return this.values[idx];
    }

    @Override
    public String toString() {
        StringBuilderWriter buffer = new StringBuilderWriter(this.calcInitialCapacity());
        try {
            this.writeTo(buffer);
        }
        catch (IOException e) {
            throw new StringWriterIOException(e);
        }
        return ((Object)buffer).toString();
    }

    private int calcInitialCapacity() {
        String[] strings = this.getStrings();
        int initialCapacity = 0;
        for (String string : strings) {
            initialCapacity += string.length();
        }
        initialCapacity += this.values.length * Math.max(initialCapacity / strings.length, 8);
        return Math.max((int)((double)initialCapacity * 1.2), 16);
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        String[] s = this.getStrings();
        int numberOfValues = this.values.length;
        int size = s.length;
        for (int i = 0; i < size; ++i) {
            out.write(s[i]);
            if (i >= numberOfValues) continue;
            Object value = this.values[i];
            if (value instanceof Closure) {
                Closure c = (Closure)value;
                int maximumNumberOfParameters = c.getMaximumNumberOfParameters();
                if (maximumNumberOfParameters == 0) {
                    InvokerHelper.write(out, c.call());
                    continue;
                }
                if (maximumNumberOfParameters == 1) {
                    c.call((Object)out);
                    continue;
                }
                throw new GroovyRuntimeException("Trying to evaluate a GString containing a Closure taking " + maximumNumberOfParameters + " parameters");
            }
            InvokerHelper.write(out, value);
        }
        return out;
    }

    @Override
    public void build(GroovyObject builder) {
        String[] s = this.getStrings();
        int numberOfValues = this.values.length;
        int size = s.length;
        for (int i = 0; i < size; ++i) {
            builder.getProperty(MKP);
            builder.invokeMethod(YIELD, new Object[]{s[i]});
            if (i >= numberOfValues) continue;
            builder.getProperty(MKP);
            builder.invokeMethod(YIELD, new Object[]{this.values[i]});
        }
    }

    public int hashCode() {
        return 37 + this.toString().hashCode();
    }

    public boolean equals(Object that) {
        if (that instanceof GString) {
            return this.equals((GString)that);
        }
        return false;
    }

    public boolean equals(GString that) {
        return this.toString().equals(that.toString());
    }

    public int compareTo(Object that) {
        return this.toString().compareTo(that.toString());
    }

    @Override
    public char charAt(int index) {
        return this.toString().charAt(index);
    }

    @Override
    public int length() {
        return this.toString().length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.toString().subSequence(start, end);
    }

    public Pattern negate() {
        return StringGroovyMethods.bitwiseNegate(this.toString());
    }

    public byte[] getBytes() {
        return this.toString().getBytes();
    }

    public byte[] getBytes(String charset) throws UnsupportedEncodingException {
        return this.toString().getBytes(charset);
    }
}

