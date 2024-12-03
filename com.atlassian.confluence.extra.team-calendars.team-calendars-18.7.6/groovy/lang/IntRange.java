/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import groovy.lang.EmptyRange;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Range;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.runtime.IteratorClosureAdapter;
import org.codehaus.groovy.runtime.RangeInfo;

public class IntRange
extends AbstractList<Integer>
implements Range<Integer> {
    private int from;
    private int to;
    private boolean reverse;
    private Boolean inclusive;

    public IntRange(int from, int to) {
        this.inclusive = null;
        if (from > to) {
            this.from = to;
            this.to = from;
            this.reverse = true;
        } else {
            this.from = from;
            this.to = to;
        }
        this.checkSize();
    }

    protected IntRange(int from, int to, boolean reverse) {
        this.inclusive = null;
        if (from > to) {
            throw new IllegalArgumentException("'from' must be less than or equal to 'to'");
        }
        this.from = from;
        this.to = to;
        this.reverse = reverse;
        this.checkSize();
    }

    public IntRange(boolean inclusive, int from, int to) {
        this.from = from;
        this.to = to;
        this.inclusive = inclusive;
        this.reverse = false;
        this.checkSize();
    }

    private void checkSize() {
        Long size = (long)this.to - (long)this.from + 1L;
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("A range must have no more than 2147483647 elements but attempted " + size + " elements");
        }
    }

    public RangeInfo subListBorders(int size) {
        int tempTo;
        if (this.inclusive == null) {
            throw new IllegalStateException("Should not call subListBorders on a non-inclusive aware IntRange");
        }
        int tempFrom = this.from;
        if (tempFrom < 0) {
            tempFrom += size;
        }
        if ((tempTo = this.to) < 0) {
            tempTo += size;
        }
        if (tempFrom > tempTo) {
            return new RangeInfo(this.inclusive != false ? tempTo : tempTo + 1, tempFrom + 1, true);
        }
        return new RangeInfo(tempFrom, this.inclusive != false ? tempTo + 1 : tempTo, false);
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof IntRange ? this.equals((IntRange)that) : super.equals(that);
    }

    public boolean equals(IntRange that) {
        return that != null && (this.inclusive == null && this.reverse == that.reverse && this.from == that.from && this.to == that.to || this.inclusive != null && this.inclusive == that.inclusive && this.from == that.from && this.to == that.to);
    }

    @Override
    public Integer getFrom() {
        if (this.inclusive == null || this.from <= this.to) {
            return this.from;
        }
        return this.inclusive != false ? this.to : this.to + 1;
    }

    @Override
    public Integer getTo() {
        if (this.inclusive == null) {
            return this.to;
        }
        if (this.from <= this.to) {
            return this.inclusive != false ? this.to : this.to - 1;
        }
        return this.from;
    }

    public Boolean getInclusive() {
        return this.inclusive;
    }

    public int getFromInt() {
        return this.getFrom();
    }

    public int getToInt() {
        return this.getTo();
    }

    @Override
    public boolean isReverse() {
        return this.inclusive == null ? this.reverse : this.from > this.to;
    }

    @Override
    public boolean containsWithinBounds(Object o) {
        return this.contains(o);
    }

    @Override
    public Integer get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + " should not be negative");
        }
        if (index >= this.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + " too big for range: " + this);
        }
        return this.isReverse() ? this.getTo() - index : index + this.getFrom();
    }

    @Override
    public int size() {
        return this.getTo() - this.getFrom() + 1;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IntRangeIterator();
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
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
            return new EmptyRange(this.getFrom());
        }
        return new IntRange(fromIndex + this.getFrom(), toIndex + this.getFrom() - 1, this.isReverse());
    }

    @Override
    public String toString() {
        return this.inclusive != null ? "" + this.from + ".." + (this.inclusive != false ? "" : "<") + this.to : (this.reverse ? "" + this.to + ".." + this.from : "" + this.from + ".." + this.to);
    }

    @Override
    public String inspect() {
        return this.toString();
    }

    @Override
    public boolean contains(Object value) {
        if (value instanceof Integer) {
            return (Integer)value >= this.getFrom() && (Integer)value <= this.getTo();
        }
        if (value instanceof BigInteger) {
            BigInteger bigint = (BigInteger)value;
            return bigint.compareTo(BigInteger.valueOf(this.getFrom().intValue())) >= 0 && bigint.compareTo(BigInteger.valueOf(this.getTo().intValue())) <= 0;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection other) {
        if (other instanceof IntRange) {
            IntRange range = (IntRange)other;
            return this.getFrom() <= range.getFrom() && range.getTo() <= this.getTo();
        }
        return super.containsAll(other);
    }

    @Override
    public void step(int step, Closure closure) {
        if (step == 0) {
            if (!this.getFrom().equals(this.getTo())) {
                throw new GroovyRuntimeException("Infinite loop detected due to step size of 0");
            }
            return;
        }
        if (this.isReverse()) {
            step = -step;
        }
        if (step > 0) {
            for (int value = this.getFrom().intValue(); value <= this.getTo(); value += step) {
                closure.call((Object)value);
                if ((long)value + (long)step < Integer.MAX_VALUE) {
                    continue;
                }
                break;
            }
        } else {
            for (int value = this.getTo().intValue(); value >= this.getFrom(); value += step) {
                closure.call((Object)value);
                if ((long)value + (long)step > Integer.MIN_VALUE) {
                    continue;
                }
                break;
            }
        }
    }

    @Override
    public List<Integer> step(int step) {
        IteratorClosureAdapter adapter = new IteratorClosureAdapter(this);
        this.step(step, adapter);
        return adapter.asList();
    }

    @Override
    public int hashCode() {
        int from = this.getFrom();
        int to = this.getTo();
        int hashCode = (from + to + 1) * (from + to) / 2 + to;
        return hashCode;
    }

    private class IntRangeIterator
    implements Iterator<Integer> {
        private int index;
        private int size;
        private int value;

        private IntRangeIterator() {
            this.size = IntRange.this.size();
            this.value = IntRange.this.isReverse() ? IntRange.this.getTo() : IntRange.this.getFrom();
        }

        @Override
        public boolean hasNext() {
            return this.index < this.size;
        }

        @Override
        public Integer next() {
            if (!this.hasNext()) {
                return null;
            }
            if (this.index++ > 0) {
                this.value = IntRange.this.isReverse() ? --this.value : ++this.value;
            }
            return this.value;
        }

        @Override
        public void remove() {
            IntRange.this.remove(this.index);
        }
    }
}

