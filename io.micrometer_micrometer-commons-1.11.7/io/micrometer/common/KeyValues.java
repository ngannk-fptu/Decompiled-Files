/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import java.lang.invoke.LambdaMetafactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class KeyValues
implements Iterable<KeyValue> {
    private static final KeyValues EMPTY = new KeyValues(new KeyValue[0]);
    private final KeyValue[] keyValues;
    private int last;

    private KeyValues(KeyValue[] keyValues) {
        this.keyValues = keyValues;
        Arrays.sort(this.keyValues);
        this.dedup();
    }

    private void dedup() {
        int n = this.keyValues.length;
        if (n == 0 || n == 1) {
            this.last = n;
            return;
        }
        int j = 0;
        for (int i = 0; i < n - 1; ++i) {
            if (this.keyValues[i].getKey().equals(this.keyValues[i + 1].getKey())) continue;
            this.keyValues[j++] = this.keyValues[i];
        }
        this.keyValues[j++] = this.keyValues[n - 1];
        this.last = j;
    }

    public KeyValues and(String key, String value) {
        return this.and(KeyValue.of(key, value));
    }

    public KeyValues and(String ... keyValues) {
        if (KeyValues.blankVarargs(keyValues)) {
            return this;
        }
        return this.and(KeyValues.of(keyValues));
    }

    public KeyValues and(KeyValue ... keyValues) {
        if (KeyValues.blankVarargs(keyValues)) {
            return this;
        }
        KeyValue[] newKeyValues = new KeyValue[this.last + keyValues.length];
        System.arraycopy(this.keyValues, 0, newKeyValues, 0, this.last);
        System.arraycopy(keyValues, 0, newKeyValues, this.last, keyValues.length);
        return new KeyValues(newKeyValues);
    }

    public <E> KeyValues and(@Nullable Iterable<E> elements, Function<E, String> keyExtractor, Function<E, String> valueExtractor) {
        if (elements == null || !elements.iterator().hasNext()) {
            return this;
        }
        Function<Object, KeyValue> mapper = element -> KeyValue.of(element, keyExtractor, valueExtractor);
        Iterable keyValues = () -> StreamSupport.stream(elements.spliterator(), false).map(mapper).iterator();
        return this.and(keyValues);
    }

    public KeyValues and(@Nullable Iterable<? extends KeyValue> keyValues) {
        if (keyValues == null || keyValues == EMPTY || !keyValues.iterator().hasNext()) {
            return this;
        }
        if (this.keyValues.length == 0) {
            return KeyValues.of(keyValues);
        }
        return this.and(KeyValues.of(keyValues).keyValues);
    }

    @Override
    public Iterator<KeyValue> iterator() {
        return new ArrayIterator();
    }

    @Override
    public Spliterator<KeyValue> spliterator() {
        return Spliterators.spliterator(this.keyValues, 0, this.last, 1301);
    }

    public Stream<KeyValue> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public int hashCode() {
        int result = 1;
        for (int i = 0; i < this.last; ++i) {
            result = 31 * result + this.keyValues[i].hashCode();
        }
        return result;
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || obj != null && this.getClass() == obj.getClass() && this.keyValuesEqual((KeyValues)obj);
    }

    private boolean keyValuesEqual(KeyValues obj) {
        if (this.keyValues == obj.keyValues) {
            return true;
        }
        if (this.last != obj.last) {
            return false;
        }
        for (int i = 0; i < this.last; ++i) {
            if (this.keyValues[i].equals(obj.keyValues[i])) continue;
            return false;
        }
        return true;
    }

    public static KeyValues concat(@Nullable Iterable<? extends KeyValue> keyValues, @Nullable Iterable<? extends KeyValue> otherKeyValues) {
        return KeyValues.of(keyValues).and(otherKeyValues);
    }

    public static KeyValues concat(@Nullable Iterable<? extends KeyValue> keyValues, String ... otherKeyValues) {
        return KeyValues.of(keyValues).and(otherKeyValues);
    }

    public static <E> KeyValues of(@Nullable Iterable<E> elements, Function<E, String> keyExtractor, Function<E, String> valueExtractor) {
        return KeyValues.empty().and(elements, keyExtractor, valueExtractor);
    }

    public static KeyValues of(@Nullable Iterable<? extends KeyValue> keyValues) {
        if (keyValues == null || keyValues == EMPTY || !keyValues.iterator().hasNext()) {
            return KeyValues.empty();
        }
        if (keyValues instanceof KeyValues) {
            return (KeyValues)keyValues;
        }
        if (keyValues instanceof Collection) {
            Collection keyValuesCollection = (Collection)keyValues;
            return new KeyValues(keyValuesCollection.toArray(new KeyValue[0]));
        }
        return new KeyValues((KeyValue[])StreamSupport.stream(keyValues.spliterator(), false).toArray(KeyValue[]::new));
    }

    public static KeyValues of(String key, String value) {
        return new KeyValues(new KeyValue[]{KeyValue.of(key, value)});
    }

    public static KeyValues of(String ... keyValues) {
        if (KeyValues.blankVarargs(keyValues)) {
            return KeyValues.empty();
        }
        if (keyValues.length % 2 == 1) {
            throw new IllegalArgumentException("size must be even, it is a set of key=value pairs");
        }
        KeyValue[] keyValueArray = new KeyValue[keyValues.length / 2];
        for (int i = 0; i < keyValues.length; i += 2) {
            keyValueArray[i / 2] = KeyValue.of(keyValues[i], keyValues[i + 1]);
        }
        return new KeyValues(keyValueArray);
    }

    private static boolean blankVarargs(@Nullable Object[] args) {
        return args == null || args.length == 0 || args.length == 1 && args[0] == null;
    }

    public static KeyValues of(KeyValue ... keyValues) {
        return KeyValues.empty().and(keyValues);
    }

    public static KeyValues empty() {
        return EMPTY;
    }

    public String toString() {
        return this.stream().map((Function<KeyValue, String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, toString(), (Lio/micrometer/common/KeyValue;)Ljava/lang/String;)()).collect(Collectors.joining(",", "[", "]"));
    }

    private class ArrayIterator
    implements Iterator<KeyValue> {
        private int currentIndex = 0;

        private ArrayIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.currentIndex < KeyValues.this.last;
        }

        @Override
        public KeyValue next() {
            return KeyValues.this.keyValues[this.currentIndex++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("cannot remove items from key values");
        }
    }
}

