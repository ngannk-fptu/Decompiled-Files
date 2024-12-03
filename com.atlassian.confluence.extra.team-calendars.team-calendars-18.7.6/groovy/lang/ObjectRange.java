/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import groovy.lang.EmptyRange;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Range;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.IteratorClosureAdapter;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ObjectRange
extends AbstractList
implements Range {
    private Comparable from;
    private Comparable to;
    private int size = -1;
    private final boolean reverse;

    public ObjectRange(Comparable from, Comparable to) {
        if (from == null) {
            throw new IllegalArgumentException("Must specify a non-null value for the 'from' index in a Range");
        }
        if (to == null) {
            throw new IllegalArgumentException("Must specify a non-null value for the 'to' index in a Range");
        }
        try {
            this.reverse = ScriptBytecodeAdapter.compareGreaterThan(from, to);
        }
        catch (ClassCastException cce) {
            throw new IllegalArgumentException("Unable to create range due to incompatible types: " + from.getClass().getSimpleName() + ".." + to.getClass().getSimpleName() + " (possible missing brackets around range?)", cce);
        }
        if (this.reverse) {
            this.constructorHelper(to, from);
        } else {
            this.constructorHelper(from, to);
        }
    }

    public ObjectRange(Comparable from, Comparable to, boolean reverse) {
        this.constructorHelper(from, to);
        this.reverse = reverse;
    }

    private void constructorHelper(Comparable from, Comparable to) {
        if (from instanceof Short) {
            from = Integer.valueOf(((Short)from).intValue());
        } else if (from instanceof Float) {
            from = Double.valueOf(((Float)from).doubleValue());
        }
        if (to instanceof Short) {
            to = Integer.valueOf(((Short)to).intValue());
        } else if (to instanceof Float) {
            to = Double.valueOf(((Float)to).doubleValue());
        }
        if (from instanceof Integer && to instanceof Long) {
            from = Long.valueOf(((Integer)from).longValue());
        } else if (to instanceof Integer && from instanceof Long) {
            to = Long.valueOf(((Integer)to).longValue());
        }
        if (from.getClass() == to.getClass()) {
            this.from = from;
            this.to = to;
        } else {
            this.from = ObjectRange.normaliseStringType(from);
            this.to = ObjectRange.normaliseStringType(to);
        }
        if (from instanceof String || to instanceof String) {
            int i;
            String start = from.toString();
            String end = to.toString();
            if (start.length() > end.length()) {
                throw new IllegalArgumentException("Incompatible Strings for Range: starting String is longer than ending string");
            }
            int length = Math.min(start.length(), end.length());
            for (i = 0; i < length && start.charAt(i) == end.charAt(i); ++i) {
            }
            if (i < length - 1) {
                throw new IllegalArgumentException("Incompatible Strings for Range: String#next() will not reach the expected value");
            }
        }
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof ObjectRange ? this.equals((ObjectRange)that) : super.equals(that);
    }

    public boolean equals(ObjectRange that) {
        return that != null && this.reverse == that.reverse && DefaultTypeTransformation.compareEqual(this.from, that.from) && DefaultTypeTransformation.compareEqual(this.to, that.to);
    }

    public Comparable getFrom() {
        return this.from;
    }

    public Comparable getTo() {
        return this.to;
    }

    @Override
    public boolean isReverse() {
        return this.reverse;
    }

    @Override
    public Object get(int index) {
        Object value;
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + " should not be negative");
        }
        if (index >= this.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + " is too big for range: " + this);
        }
        if (this.reverse) {
            value = this.to;
            for (int i = 0; i < index; ++i) {
                value = this.decrement(value);
            }
        } else {
            value = this.from;
            for (int i = 0; i < index; ++i) {
                value = this.increment(value);
            }
        }
        return value;
    }

    @Override
    public Iterator iterator() {
        return new Iterator(){
            private int index;
            private Object value;
            {
                this.value = ObjectRange.this.reverse ? ObjectRange.this.to : ObjectRange.this.from;
            }

            @Override
            public boolean hasNext() {
                return this.index < ObjectRange.this.size();
            }

            public Object next() {
                if (this.index++ > 0) {
                    this.value = this.index > ObjectRange.this.size() ? null : (ObjectRange.this.reverse ? ObjectRange.this.decrement(this.value) : ObjectRange.this.increment(this.value));
                }
                return this.value;
            }

            @Override
            public void remove() {
                ObjectRange.this.remove(this.index);
            }
        };
    }

    @Override
    public boolean containsWithinBounds(Object value) {
        if (value instanceof Comparable) {
            int result = this.compareTo(this.from, (Comparable)value);
            return result == 0 || result < 0 && this.compareTo(this.to, (Comparable)value) >= 0;
        }
        return this.contains(value);
    }

    private int compareTo(Comparable first, Comparable second) {
        return DefaultGroovyMethods.numberAwareCompareTo(first, second);
    }

    @Override
    public int size() {
        if (this.size == -1) {
            if ((this.from instanceof Integer || this.from instanceof Long) && (this.to instanceof Integer || this.to instanceof Long)) {
                long fromNum = ((Number)((Object)this.from)).longValue();
                long toNum = ((Number)((Object)this.to)).longValue();
                this.size = (int)(toNum - fromNum + 1L);
            } else if (this.from instanceof Character && this.to instanceof Character) {
                char fromNum = ((Character)this.from).charValue();
                char toNum = ((Character)this.to).charValue();
                this.size = toNum - fromNum + 1;
            } else if (this.from instanceof BigDecimal || this.to instanceof BigDecimal || this.from instanceof BigInteger || this.to instanceof BigInteger) {
                BigDecimal fromNum = new BigDecimal(this.from.toString());
                BigDecimal toNum = new BigDecimal(this.to.toString());
                BigInteger sizeNum = toNum.subtract(fromNum).add(new BigDecimal(1.0)).toBigInteger();
                this.size = sizeNum.intValue();
            } else {
                this.size = 0;
                Comparable first = this.from;
                Comparable value = this.from;
                while (this.compareTo(this.to, value) >= 0) {
                    value = (Comparable)this.increment(value);
                    ++this.size;
                    if (this.compareTo(first, value) < 0) continue;
                    break;
                }
            }
        }
        return this.size;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > this.size()) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex == toIndex) {
            return new EmptyRange(this.from);
        }
        return new ObjectRange((Comparable)this.get(fromIndex), (Comparable)this.get(--toIndex), this.reverse);
    }

    @Override
    public String toString() {
        return this.reverse ? "" + this.to + ".." + this.from : "" + this.from + ".." + this.to;
    }

    @Override
    public String inspect() {
        String toText = InvokerHelper.inspect(this.to);
        String fromText = InvokerHelper.inspect(this.from);
        return this.reverse ? "" + toText + ".." + fromText : "" + fromText + ".." + toText;
    }

    @Override
    public boolean contains(Object value) {
        Iterator iter = this.iterator();
        if (value == null) {
            return false;
        }
        while (iter.hasNext()) {
            try {
                if (!DefaultTypeTransformation.compareEqual(value, iter.next())) continue;
                return true;
            }
            catch (ClassCastException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void step(int step, Closure closure) {
        if (step == 0) {
            if (this.compareTo(this.from, this.to) != 0) {
                throw new GroovyRuntimeException("Infinite loop detected due to step size of 0");
            }
            return;
        }
        if (this.reverse) {
            step = -step;
        }
        if (step > 0) {
            Comparable first = this.from;
            Comparable value = this.from;
            while (this.compareTo(value, this.to) <= 0) {
                closure.call((Object)value);
                for (int i = 0; i < step; ++i) {
                    if (this.compareTo(value = (Comparable)this.increment(value), first) > 0) continue;
                    return;
                }
            }
        } else {
            step = -step;
            Comparable first = this.to;
            Comparable value = this.to;
            while (this.compareTo(value, this.from) >= 0) {
                closure.call((Object)value);
                for (int i = 0; i < step; ++i) {
                    if (this.compareTo(value = (Comparable)this.decrement(value), first) < 0) continue;
                    return;
                }
            }
        }
    }

    public List step(int step) {
        IteratorClosureAdapter adapter = new IteratorClosureAdapter(this);
        this.step(step, adapter);
        return adapter.asList();
    }

    protected Object increment(Object value) {
        return InvokerHelper.invokeMethod(value, "next", null);
    }

    protected Object decrement(Object value) {
        return InvokerHelper.invokeMethod(value, "previous", null);
    }

    private static Comparable normaliseStringType(Comparable operand) {
        if (operand instanceof Character) {
            return Integer.valueOf(((Character)operand).charValue());
        }
        if (operand instanceof String) {
            String string = (String)((Object)operand);
            if (string.length() == 1) {
                return Integer.valueOf(string.charAt(0));
            }
            return string;
        }
        return operand;
    }
}

