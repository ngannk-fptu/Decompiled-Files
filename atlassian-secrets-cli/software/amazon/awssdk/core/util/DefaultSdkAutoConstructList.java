/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.util.SdkAutoConstructList;

@SdkProtectedApi
public final class DefaultSdkAutoConstructList<T>
implements SdkAutoConstructList<T> {
    private static final DefaultSdkAutoConstructList INSTANCE = new DefaultSdkAutoConstructList();
    private final List impl = Collections.emptyList();

    private DefaultSdkAutoConstructList() {
    }

    public static <T> DefaultSdkAutoConstructList<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public int size() {
        return this.impl.size();
    }

    @Override
    public boolean isEmpty() {
        return this.impl.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.impl.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.impl.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.impl.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.impl.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.impl.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.impl.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.impl.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.impl.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return this.impl.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.impl.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.impl.retainAll(c);
    }

    @Override
    public void clear() {
        this.impl.clear();
    }

    @Override
    public T get(int index) {
        return (T)this.impl.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.impl.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.impl.add(index, element);
    }

    @Override
    public T remove(int index) {
        return (T)this.impl.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.impl.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.impl.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.impl.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.impl.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.impl.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        return this.impl.equals(o);
    }

    @Override
    public int hashCode() {
        return this.impl.hashCode();
    }

    public String toString() {
        return this.impl.toString();
    }
}

