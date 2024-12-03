/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.CollectSpliterators;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.TransformedIterator;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.NonNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class MutableClassToInstanceMap<B>
extends ForwardingMap<Class<? extends B>, B>
implements ClassToInstanceMap<B>,
Serializable {
    private final Map<Class<? extends @NonNull B>, B> delegate;

    public static <B> MutableClassToInstanceMap<B> create() {
        return new MutableClassToInstanceMap(new HashMap());
    }

    public static <B> MutableClassToInstanceMap<B> create(Map<Class<? extends @NonNull B>, B> backingMap) {
        return new MutableClassToInstanceMap<B>(backingMap);
    }

    private MutableClassToInstanceMap(Map<Class<? extends @NonNull B>, B> delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    @Override
    protected Map<Class<? extends @NonNull B>, B> delegate() {
        return this.delegate;
    }

    private static <B> Map.Entry<Class<? extends @NonNull B>, B> checkedEntry(final Map.Entry<Class<? extends @NonNull B>, B> entry) {
        return new ForwardingMapEntry<Class<? extends B>, B>(){

            @Override
            protected Map.Entry<Class<? extends @NonNull B>, B> delegate() {
                return entry;
            }

            @Override
            @ParametricNullness
            public B setValue(@ParametricNullness B value) {
                MutableClassToInstanceMap.cast((Class)this.getKey(), value);
                return super.setValue(value);
            }
        };
    }

    @Override
    public Set<Map.Entry<Class<? extends @NonNull B>, B>> entrySet() {
        return new ForwardingSet<Map.Entry<Class<? extends B>, B>>(){

            @Override
            protected Set<Map.Entry<Class<? extends @NonNull B>, B>> delegate() {
                return MutableClassToInstanceMap.this.delegate().entrySet();
            }

            @Override
            public Spliterator<Map.Entry<Class<? extends @NonNull B>, B>> spliterator() {
                return CollectSpliterators.map(this.delegate().spliterator(), x$0 -> MutableClassToInstanceMap.checkedEntry(x$0));
            }

            @Override
            public Iterator<Map.Entry<Class<? extends @NonNull B>, B>> iterator() {
                return new TransformedIterator<Map.Entry<Class<? extends B>, B>, Map.Entry<Class<? extends B>, B>>(this, this.delegate().iterator()){

                    @Override
                    Map.Entry<Class<? extends @NonNull B>, B> transform(Map.Entry<Class<? extends @NonNull B>, B> from) {
                        return MutableClassToInstanceMap.checkedEntry(from);
                    }
                };
            }

            @Override
            public Object[] toArray() {
                Object[] result = this.standardToArray();
                return result;
            }

            @Override
            public <T> T[] toArray(T[] array) {
                return this.standardToArray(array);
            }
        };
    }

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public B put(Class<? extends @NonNull B> key, @ParametricNullness B value) {
        MutableClassToInstanceMap.cast(key, value);
        return super.put(key, value);
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    @Override
    public void putAll(Map<? extends Class<? extends @NonNull B>, ? extends B> map) {
        LinkedHashMap<Class<@NonNull B>, B> copy = new LinkedHashMap<Class<B>, B>(map);
        for (Map.Entry entry : copy.entrySet()) {
            MutableClassToInstanceMap.cast((Class)entry.getKey(), entry.getValue());
        }
        super.putAll(copy);
    }

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public <T extends B> T putInstance(Class<@NonNull T> type, @ParametricNullness T value) {
        return MutableClassToInstanceMap.cast(type, this.put(type, value));
    }

    @Override
    @CheckForNull
    public <T extends B> T getInstance(Class<T> type) {
        return MutableClassToInstanceMap.cast(type, this.get(type));
    }

    @CheckForNull
    @CanIgnoreReturnValue
    private static <T> T cast(Class<T> type, @CheckForNull Object value) {
        return Primitives.wrap(type).cast(value);
    }

    private Object writeReplace() {
        return new SerializedForm(this.delegate());
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    private static final class SerializedForm<B>
    implements Serializable {
        private final Map<Class<? extends @NonNull B>, B> backingMap;
        private static final long serialVersionUID = 0L;

        SerializedForm(Map<Class<? extends @NonNull B>, B> backingMap) {
            this.backingMap = backingMap;
        }

        Object readResolve() {
            return MutableClassToInstanceMap.create(this.backingMap);
        }
    }
}

