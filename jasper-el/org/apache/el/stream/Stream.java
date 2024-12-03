/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 *  javax.el.LambdaExpression
 */
package org.apache.el.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.el.ELException;
import javax.el.LambdaExpression;
import org.apache.el.lang.ELArithmetic;
import org.apache.el.lang.ELSupport;
import org.apache.el.stream.Optional;
import org.apache.el.util.MessageFactory;

public class Stream {
    private final Iterator<Object> iterator;

    public Stream(Iterator<Object> iterator) {
        this.iterator = iterator;
    }

    public Stream filter(final LambdaExpression le) {
        OpIterator downStream = new OpIterator(){

            @Override
            protected void findNext() {
                while (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    if (!ELSupport.coerceToBoolean(null, le.invoke(new Object[]{obj}), true).booleanValue()) continue;
                    this.next = obj;
                    this.foundNext = true;
                    break;
                }
            }
        };
        return new Stream(downStream);
    }

    public Stream map(final LambdaExpression le) {
        OpIterator downStream = new OpIterator(){

            @Override
            protected void findNext() {
                if (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    this.next = le.invoke(new Object[]{obj});
                    this.foundNext = true;
                }
            }
        };
        return new Stream(downStream);
    }

    public Stream flatMap(final LambdaExpression le) {
        OpIterator downStream = new OpIterator(){
            private Iterator<?> inner;

            @Override
            protected void findNext() {
                while (Stream.this.iterator.hasNext() || this.inner != null && this.inner.hasNext()) {
                    if (this.inner == null || !this.inner.hasNext()) {
                        this.inner = ((Stream)le.invoke(new Object[]{Stream.this.iterator.next()})).iterator;
                    }
                    if (!this.inner.hasNext()) continue;
                    this.next = this.inner.next();
                    this.foundNext = true;
                    break;
                }
            }
        };
        return new Stream(downStream);
    }

    public Stream distinct() {
        OpIterator downStream = new OpIterator(){
            private Set<Object> values;
            {
                this.values = new HashSet<Object>();
            }

            @Override
            protected void findNext() {
                while (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    if (!this.values.add(obj)) continue;
                    this.next = obj;
                    this.foundNext = true;
                    break;
                }
            }
        };
        return new Stream(downStream);
    }

    public Stream sorted() {
        OpIterator downStream = new OpIterator(){
            private Iterator<Object> sorted;
            {
                this.sorted = null;
            }

            @Override
            protected void findNext() {
                if (this.sorted == null) {
                    this.sort();
                }
                if (this.sorted.hasNext()) {
                    this.next = this.sorted.next();
                    this.foundNext = true;
                }
            }

            private void sort() {
                ArrayList list = new ArrayList();
                while (Stream.this.iterator.hasNext()) {
                    list.add(Stream.this.iterator.next());
                }
                Collections.sort(list);
                this.sorted = list.iterator();
            }
        };
        return new Stream(downStream);
    }

    public Stream sorted(final LambdaExpression le) {
        OpIterator downStream = new OpIterator(){
            private Iterator<Object> sorted;
            {
                this.sorted = null;
            }

            @Override
            protected void findNext() {
                if (this.sorted == null) {
                    this.sort(le);
                }
                if (this.sorted.hasNext()) {
                    this.next = this.sorted.next();
                    this.foundNext = true;
                }
            }

            private void sort(LambdaExpression le2) {
                ArrayList<Object> list = new ArrayList<Object>();
                LambdaExpressionComparator c = new LambdaExpressionComparator(le2);
                while (Stream.this.iterator.hasNext()) {
                    list.add(Stream.this.iterator.next());
                }
                list.sort(c);
                this.sorted = list.iterator();
            }
        };
        return new Stream(downStream);
    }

    public Object forEach(LambdaExpression le) {
        while (this.iterator.hasNext()) {
            le.invoke(new Object[]{this.iterator.next()});
        }
        return null;
    }

    public Stream peek(final LambdaExpression le) {
        OpIterator downStream = new OpIterator(){

            @Override
            protected void findNext() {
                if (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    le.invoke(new Object[]{obj});
                    this.next = obj;
                    this.foundNext = true;
                }
            }
        };
        return new Stream(downStream);
    }

    public Iterator<?> iterator() {
        return this.iterator;
    }

    public Stream limit(Number count) {
        return this.substream(0, count);
    }

    public Stream substream(Number start) {
        return this.substream(start, Integer.MAX_VALUE);
    }

    public Stream substream(final Number start, final Number end) {
        OpIterator downStream = new OpIterator(){
            private final int startPos;
            private final int endPos;
            private int itemCount;
            {
                this.startPos = start.intValue();
                this.endPos = end.intValue();
                this.itemCount = 0;
            }

            @Override
            protected void findNext() {
                while (this.itemCount < this.startPos && Stream.this.iterator.hasNext()) {
                    Stream.this.iterator.next();
                    ++this.itemCount;
                }
                if (this.itemCount < this.endPos && Stream.this.iterator.hasNext()) {
                    ++this.itemCount;
                    this.next = Stream.this.iterator.next();
                    this.foundNext = true;
                }
            }
        };
        return new Stream(downStream);
    }

    public List<Object> toList() {
        ArrayList<Object> result = new ArrayList<Object>();
        while (this.iterator.hasNext()) {
            result.add(this.iterator.next());
        }
        return result;
    }

    public Object[] toArray() {
        ArrayList<Object> result = new ArrayList<Object>();
        while (this.iterator.hasNext()) {
            result.add(this.iterator.next());
        }
        return result.toArray(new Object[0]);
    }

    public Optional reduce(LambdaExpression le) {
        Object seed = null;
        if (this.iterator.hasNext()) {
            seed = this.iterator.next();
        }
        if (seed == null) {
            return Optional.EMPTY;
        }
        return new Optional(this.reduce(seed, le));
    }

    public Object reduce(Object seed, LambdaExpression le) {
        Object result = seed;
        while (this.iterator.hasNext()) {
            result = le.invoke(new Object[]{result, this.iterator.next()});
        }
        return result;
    }

    public Optional max() {
        return this.compare(true);
    }

    public Optional max(LambdaExpression le) {
        return this.compare(true, le);
    }

    public Optional min() {
        return this.compare(false);
    }

    public Optional min(LambdaExpression le) {
        return this.compare(false, le);
    }

    public Optional average() {
        long count = 0L;
        Number sum = 0L;
        while (this.iterator.hasNext()) {
            ++count;
            sum = ELArithmetic.add(sum, this.iterator.next());
        }
        if (count == 0L) {
            return Optional.EMPTY;
        }
        return new Optional(ELArithmetic.divide(sum, count));
    }

    public Number sum() {
        Number sum = 0L;
        while (this.iterator.hasNext()) {
            sum = ELArithmetic.add(sum, this.iterator.next());
        }
        return sum;
    }

    public Long count() {
        long count = 0L;
        while (this.iterator.hasNext()) {
            this.iterator.next();
            ++count;
        }
        return count;
    }

    public Optional anyMatch(LambdaExpression le) {
        if (!this.iterator.hasNext()) {
            return Optional.EMPTY;
        }
        Boolean match = Boolean.FALSE;
        while (!match.booleanValue() && this.iterator.hasNext()) {
            match = (Boolean)le.invoke(new Object[]{this.iterator.next()});
        }
        return new Optional(match);
    }

    public Optional allMatch(LambdaExpression le) {
        if (!this.iterator.hasNext()) {
            return Optional.EMPTY;
        }
        Boolean match = Boolean.TRUE;
        while (match.booleanValue() && this.iterator.hasNext()) {
            match = (Boolean)le.invoke(new Object[]{this.iterator.next()});
        }
        return new Optional(match);
    }

    public Optional noneMatch(LambdaExpression le) {
        if (!this.iterator.hasNext()) {
            return Optional.EMPTY;
        }
        Boolean match = Boolean.FALSE;
        while (!match.booleanValue() && this.iterator.hasNext()) {
            match = (Boolean)le.invoke(new Object[]{this.iterator.next()});
        }
        return new Optional(match == false);
    }

    public Optional findFirst() {
        if (this.iterator.hasNext()) {
            return new Optional(this.iterator.next());
        }
        return Optional.EMPTY;
    }

    private Optional compare(boolean isMax) {
        Object obj;
        Comparable result = null;
        if (this.iterator.hasNext()) {
            obj = this.iterator.next();
            if (obj instanceof Comparable) {
                result = (Comparable)obj;
            } else {
                throw new ELException(MessageFactory.get("stream.compare.notComparable"));
            }
        }
        while (this.iterator.hasNext()) {
            obj = this.iterator.next();
            if (obj instanceof Comparable) {
                if (isMax && ((Comparable)obj).compareTo(result) > 0) {
                    result = (Comparable)obj;
                    continue;
                }
                if (isMax || ((Comparable)obj).compareTo(result) >= 0) continue;
                result = (Comparable)obj;
                continue;
            }
            throw new ELException(MessageFactory.get("stream.compare.notComparable"));
        }
        if (result == null) {
            return Optional.EMPTY;
        }
        return new Optional(result);
    }

    private Optional compare(boolean isMax, LambdaExpression le) {
        Object obj;
        Object result = null;
        if (this.iterator.hasNext()) {
            result = obj = this.iterator.next();
        }
        while (this.iterator.hasNext()) {
            obj = this.iterator.next();
            if (isMax && ELSupport.coerceToNumber(null, le.invoke(new Object[]{obj, result}), Integer.class).intValue() > 0) {
                result = obj;
                continue;
            }
            if (isMax || ELSupport.coerceToNumber(null, le.invoke(new Object[]{obj, result}), Integer.class).intValue() >= 0) continue;
            result = obj;
        }
        if (result == null) {
            return Optional.EMPTY;
        }
        return new Optional(result);
    }

    private static abstract class OpIterator
    implements Iterator<Object> {
        protected boolean foundNext = false;
        protected Object next;

        private OpIterator() {
        }

        @Override
        public boolean hasNext() {
            if (this.foundNext) {
                return true;
            }
            this.findNext();
            return this.foundNext;
        }

        @Override
        public Object next() {
            if (this.foundNext) {
                this.foundNext = false;
                return this.next;
            }
            this.findNext();
            if (this.foundNext) {
                this.foundNext = false;
                return this.next;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        protected abstract void findNext();
    }

    private static class LambdaExpressionComparator
    implements Comparator<Object> {
        private final LambdaExpression le;

        LambdaExpressionComparator(LambdaExpression le) {
            this.le = le;
        }

        @Override
        public int compare(Object o1, Object o2) {
            return ELSupport.coerceToNumber(null, this.le.invoke(new Object[]{o1, o2}), Integer.class).intValue();
        }
    }
}

