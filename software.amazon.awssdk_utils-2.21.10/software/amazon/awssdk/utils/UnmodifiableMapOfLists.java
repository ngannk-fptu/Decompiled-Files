/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
class UnmodifiableMapOfLists<T, U>
implements Map<T, List<U>>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<T, List<U>> delegate;

    UnmodifiableMapOfLists(Map<T, List<U>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public List<U> get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public List<U> getOrDefault(Object key, List<U> defaultValue) {
        return UnmodifiableMapOfLists.unmodifiableList(this.delegate.getOrDefault(key, defaultValue));
    }

    @Override
    public List<U> put(T key, List<U> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<U> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends T, ? extends List<U>> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> keySet() {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }

    @Override
    public Collection<List<U>> values() {
        return new UnmodifiableCollection(this.delegate.values());
    }

    @Override
    public Set<Map.Entry<T, List<U>>> entrySet() {
        Set<Map.Entry<T, List<U>>> entries = this.delegate.entrySet();
        return new UnmodifiableEntrySet(entries);
    }

    @Override
    public void forEach(BiConsumer<? super T, ? super List<U>> action) {
        this.delegate.forEach((? super K k, ? super V v) -> action.accept(k, UnmodifiableMapOfLists.unmodifiableList(v)));
    }

    @Override
    public void replaceAll(BiFunction<? super T, ? super List<U>, ? extends List<U>> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<U> putIfAbsent(T key, List<U> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(T key, List<U> oldValue, List<U> newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<U> replace(T key, List<U> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<U> computeIfAbsent(T key, Function<? super T, ? extends List<U>> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<U> computeIfPresent(T key, BiFunction<? super T, ? super List<U>, ? extends List<U>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<U> compute(T key, BiFunction<? super T, ? super List<U>, ? extends List<U>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<U> merge(T key, List<U> value, BiFunction<? super List<U>, ? super List<U>, ? extends List<U>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    public String toString() {
        return this.delegate.toString();
    }

    private static <T> List<T> unmodifiableList(List<T> list) {
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    private static class UnmodifiableListIterator<U>
    implements Iterator<List<U>> {
        private final Iterator<? extends List<U>> delegate;

        private UnmodifiableListIterator(Iterator<? extends List<U>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public List<U> next() {
            return UnmodifiableMapOfLists.unmodifiableList(this.delegate.next());
        }

        public int hashCode() {
            return this.delegate.hashCode();
        }

        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    private static class UnmodifiableCollection<U>
    implements Collection<List<U>> {
        private final Collection<? extends List<U>> delegate;

        private UnmodifiableCollection(Collection<? extends List<U>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.delegate.contains(o);
        }

        @Override
        public Iterator<List<U>> iterator() {
            return new UnmodifiableListIterator(this.delegate.iterator());
        }

        @Override
        public Object[] toArray() {
            Object[] result = this.delegate.toArray();
            for (int i = 0; i < result.length; ++i) {
                result[i] = UnmodifiableMapOfLists.unmodifiableList((List)result[i]);
            }
            return result;
        }

        @Override
        public <A> A[] toArray(A[] a) {
            A[] result = this.delegate.toArray(a);
            for (int i = 0; i < result.length; ++i) {
                result[i] = UnmodifiableMapOfLists.unmodifiableList((List)result[i]);
            }
            return result;
        }

        @Override
        public boolean add(List<U> us) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.delegate.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends List<U>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return this.delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    private static class UnmodifiableEntryIterator<T, U>
    implements Iterator<Map.Entry<T, List<U>>> {
        private final Iterator<? extends Map.Entry<T, ? extends List<U>>> delegate;

        private UnmodifiableEntryIterator(Iterator<? extends Map.Entry<T, ? extends List<U>>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public Map.Entry<T, List<U>> next() {
            Map.Entry<T, List<U>> next = this.delegate.next();
            return new AbstractMap.SimpleImmutableEntry<T, List>(next.getKey(), UnmodifiableMapOfLists.unmodifiableList(next.getValue()));
        }
    }

    private static class UnmodifiableEntrySet<T, U>
    implements Set<Map.Entry<T, List<U>>> {
        private final Set<? extends Map.Entry<T, ? extends List<U>>> delegate;

        private UnmodifiableEntrySet(Set<? extends Map.Entry<T, ? extends List<U>>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.delegate.contains(o);
        }

        @Override
        public Iterator<Map.Entry<T, List<U>>> iterator() {
            return new UnmodifiableEntryIterator(this.delegate.iterator());
        }

        @Override
        public void forEach(Consumer<? super Map.Entry<T, List<U>>> action) {
            this.delegate.forEach((? super T e) -> action.accept(new AbstractMap.SimpleImmutableEntry(e.getKey(), UnmodifiableMapOfLists.unmodifiableList((List)e.getValue()))));
        }

        @Override
        public Object[] toArray() {
            Object[] result = this.delegate.toArray();
            for (int i = 0; i < result.length; ++i) {
                Map.Entry e = (Map.Entry)result[i];
                result[i] = new AbstractMap.SimpleImmutableEntry(e.getKey(), UnmodifiableMapOfLists.unmodifiableList((List)e.getValue()));
            }
            return result;
        }

        @Override
        public <A> A[] toArray(A[] a) {
            A[] result = this.delegate.toArray(a);
            for (int i = 0; i < result.length; ++i) {
                Map.Entry e = (Map.Entry)result[i];
                result[i] = new AbstractMap.SimpleImmutableEntry(e.getKey(), UnmodifiableMapOfLists.unmodifiableList((List)e.getValue()));
            }
            return result;
        }

        @Override
        public boolean add(Map.Entry<T, List<U>> tListEntry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.delegate.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<T, List<U>>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return this.delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        public String toString() {
            return this.delegate.toString();
        }
    }
}

