/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import groovy.lang.Range;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;

public class EmptyRange
extends AbstractList
implements Range {
    protected Comparable at;

    public EmptyRange(Comparable at) {
        this.at = at;
    }

    public Comparable getFrom() {
        return this.at;
    }

    public Comparable getTo() {
        return this.at;
    }

    @Override
    public boolean isReverse() {
        return false;
    }

    @Override
    public boolean containsWithinBounds(Object o) {
        return false;
    }

    @Override
    public String inspect() {
        return InvokerHelper.inspect(this.at) + "..<" + InvokerHelper.inspect(this.at);
    }

    @Override
    public String toString() {
        return null == this.at ? "null..<null" : this.at + "..<" + this.at;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Object get(int index) {
        throw new IndexOutOfBoundsException("can't get values from Empty Ranges");
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException("cannot add to Empty Ranges");
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException("cannot add to Empty Ranges");
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("cannot add to Empty Ranges");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("cannot remove from Empty Ranges");
    }

    @Override
    public Object remove(int index) {
        throw new UnsupportedOperationException("cannot remove from Empty Ranges");
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("cannot remove from Empty Ranges");
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("cannot retainAll in Empty Ranges");
    }

    @Override
    public Object set(int index, Object element) {
        throw new UnsupportedOperationException("cannot set in Empty Ranges");
    }

    @Override
    public void step(int step, Closure closure) {
    }

    public List step(int step) {
        return new ArrayList();
    }
}

