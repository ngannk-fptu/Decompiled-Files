/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.propagation;

import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StringPropagationAdapter<K>
implements Propagation<K> {
    final Propagation<String> delegate;
    final Propagation.KeyFactory<K> keyFactory;
    final Map<String, K> map;
    final List<K> keysList;

    public static <K> Propagation<K> create(Propagation<String> delegate, Propagation.KeyFactory<K> keyFactory) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }
        if (keyFactory == null) {
            throw new NullPointerException("keyFactory == null");
        }
        if (keyFactory == Propagation.KeyFactory.STRING) {
            return delegate;
        }
        return new StringPropagationAdapter<K>(delegate, keyFactory);
    }

    StringPropagationAdapter(Propagation<String> delegate, Propagation.KeyFactory<K> keyFactory) {
        this.delegate = delegate;
        this.keyFactory = keyFactory;
        this.map = new LinkedHashMap<String, K>();
        this.keysList = this.toKeyList(delegate.keys(), keyFactory);
    }

    List<K> toKeyList(List<String> keyNames, Propagation.KeyFactory<K> keyFactory) {
        int length = keyNames.size();
        Object[] keys = new Object[length];
        for (int i = 0; i < length; ++i) {
            String keyName = keyNames.get(i);
            K key = keyFactory.create(keyName);
            keys[i] = key;
            this.map.put(keyName, (String)key);
        }
        return Collections.unmodifiableList(Arrays.asList(keys));
    }

    @Override
    public List<K> keys() {
        return this.keysList;
    }

    @Override
    public <R> TraceContext.Injector<R> injector(Propagation.Setter<R, K> setter) {
        return this.delegate.injector(new SetterAdapter<R, K>(setter, this.map));
    }

    @Override
    public <R> TraceContext.Extractor<R> extractor(Propagation.Getter<R, K> getter) {
        return this.delegate.extractor(new GetterAdapter<R, K>(getter, this.map));
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        return this.delegate.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof StringPropagationAdapter) {
            return this.delegate.equals(((StringPropagationAdapter)obj).delegate);
        }
        if (obj instanceof Propagation) {
            return this.delegate.equals(obj);
        }
        return false;
    }

    static final class SetterAdapter<R, K>
    implements Propagation.Setter<R, String> {
        final Propagation.Setter<R, K> setter;
        final Map<String, K> map;

        SetterAdapter(Propagation.Setter<R, K> setter, Map<String, K> map) {
            if (setter == null) {
                throw new NullPointerException("setter == null");
            }
            this.setter = setter;
            this.map = map;
        }

        @Override
        public void put(R request, String keyName, String value) {
            K key = this.map.get(keyName);
            if (key == null) {
                return;
            }
            this.setter.put(request, key, value);
        }

        public int hashCode() {
            return this.setter.hashCode();
        }

        public String toString() {
            return this.setter.toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof SetterAdapter) {
                return this.setter.equals(((SetterAdapter)obj).setter);
            }
            if (obj instanceof Propagation.Setter) {
                return this.setter.equals(obj);
            }
            return false;
        }
    }

    static final class GetterAdapter<R, K>
    implements Propagation.Getter<R, String> {
        final Propagation.Getter<R, K> getter;
        final Map<String, K> map;

        GetterAdapter(Propagation.Getter<R, K> getter, Map<String, K> map) {
            if (getter == null) {
                throw new NullPointerException("getter == null");
            }
            this.getter = getter;
            this.map = map;
        }

        @Override
        public String get(R request, String keyName) {
            K key = this.map.get(keyName);
            if (key == null) {
                return null;
            }
            return this.getter.get(request, key);
        }

        public int hashCode() {
            return this.getter.hashCode();
        }

        public String toString() {
            return this.getter.toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof GetterAdapter) {
                return this.getter.equals(((GetterAdapter)obj).getter);
            }
            if (obj instanceof Propagation.Getter) {
                return this.getter.equals(obj);
            }
            return false;
        }
    }
}

