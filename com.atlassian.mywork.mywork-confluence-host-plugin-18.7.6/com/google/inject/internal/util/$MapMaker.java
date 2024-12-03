/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$AsynchronousComputationException;
import com.google.inject.internal.util.$ComputationException;
import com.google.inject.internal.util.$CustomConcurrentHashMap;
import com.google.inject.internal.util.$ExpirationTimer;
import com.google.inject.internal.util.$FinalizableReferenceQueue;
import com.google.inject.internal.util.$FinalizableSoftReference;
import com.google.inject.internal.util.$FinalizableWeakReference;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$NullOutputException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $MapMaker {
    private Strength keyStrength = Strength.STRONG;
    private Strength valueStrength = Strength.STRONG;
    private long expirationNanos = 0L;
    private boolean useCustomMap;
    private final $CustomConcurrentHashMap.Builder builder = new $CustomConcurrentHashMap.Builder();
    private static final ValueReference<Object, Object> COMPUTING = new ValueReference<Object, Object>(){

        @Override
        public Object get() {
            return null;
        }

        @Override
        public ValueReference<Object, Object> copyFor(ReferenceEntry<Object, Object> entry) {
            throw new AssertionError();
        }

        @Override
        public Object waitForValue() {
            throw new AssertionError();
        }
    };

    public $MapMaker initialCapacity(int initialCapacity) {
        this.builder.initialCapacity(initialCapacity);
        return this;
    }

    public $MapMaker loadFactor(float loadFactor) {
        this.builder.loadFactor(loadFactor);
        return this;
    }

    public $MapMaker concurrencyLevel(int concurrencyLevel) {
        this.builder.concurrencyLevel(concurrencyLevel);
        return this;
    }

    public $MapMaker weakKeys() {
        return this.setKeyStrength(Strength.WEAK);
    }

    public $MapMaker softKeys() {
        return this.setKeyStrength(Strength.SOFT);
    }

    private $MapMaker setKeyStrength(Strength strength) {
        if (this.keyStrength != Strength.STRONG) {
            throw new IllegalStateException("Key strength was already set to " + (Object)((Object)this.keyStrength) + ".");
        }
        this.keyStrength = strength;
        this.useCustomMap = true;
        return this;
    }

    public $MapMaker weakValues() {
        return this.setValueStrength(Strength.WEAK);
    }

    public $MapMaker softValues() {
        return this.setValueStrength(Strength.SOFT);
    }

    private $MapMaker setValueStrength(Strength strength) {
        if (this.valueStrength != Strength.STRONG) {
            throw new IllegalStateException("Value strength was already set to " + (Object)((Object)this.valueStrength) + ".");
        }
        this.valueStrength = strength;
        this.useCustomMap = true;
        return this;
    }

    public $MapMaker expiration(long duration, TimeUnit unit) {
        if (this.expirationNanos != 0L) {
            throw new IllegalStateException("expiration time of " + this.expirationNanos + " ns was already set");
        }
        if (duration <= 0L) {
            throw new IllegalArgumentException("invalid duration: " + duration);
        }
        this.expirationNanos = unit.toNanos(duration);
        this.useCustomMap = true;
        return this;
    }

    public <K, V> ConcurrentMap<K, V> makeMap() {
        return this.useCustomMap ? new StrategyImpl<K, V>(($MapMaker)this).map : new ConcurrentHashMap(this.builder.initialCapacity, this.builder.loadFactor, this.builder.concurrencyLevel);
    }

    public <K, V> ConcurrentMap<K, V> makeComputingMap($Function<? super K, ? extends V> computer) {
        return new StrategyImpl<K, V>(($MapMaker)this, computer).map;
    }

    private static <K, V> ValueReference<K, V> computing() {
        return COMPUTING;
    }

    static /* synthetic */ ValueReference access$600() {
        return $MapMaker.computing();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class StrongValueReference<K, V>
    implements ValueReference<K, V> {
        final V referent;

        StrongValueReference(V referent) {
            this.referent = referent;
        }

        @Override
        public V get() {
            return this.referent;
        }

        @Override
        public ValueReference<K, V> copyFor(ReferenceEntry<K, V> entry) {
            return this;
        }

        @Override
        public V waitForValue() {
            return this.get();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SoftValueReference<K, V>
    extends $FinalizableSoftReference<V>
    implements ValueReference<K, V> {
        final ReferenceEntry<K, V> entry;

        SoftValueReference(V referent, ReferenceEntry<K, V> entry) {
            super(referent, QueueHolder.queue);
            this.entry = entry;
        }

        @Override
        public void finalizeReferent() {
            this.entry.valueReclaimed();
        }

        @Override
        public ValueReference<K, V> copyFor(ReferenceEntry<K, V> entry) {
            return new SoftValueReference(this.get(), entry);
        }

        @Override
        public V waitForValue() {
            return (V)this.get();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class WeakValueReference<K, V>
    extends $FinalizableWeakReference<V>
    implements ValueReference<K, V> {
        final ReferenceEntry<K, V> entry;

        WeakValueReference(V referent, ReferenceEntry<K, V> entry) {
            super(referent, QueueHolder.queue);
            this.entry = entry;
        }

        @Override
        public void finalizeReferent() {
            this.entry.valueReclaimed();
        }

        @Override
        public ValueReference<K, V> copyFor(ReferenceEntry<K, V> entry) {
            return new WeakValueReference(this.get(), entry);
        }

        @Override
        public V waitForValue() throws InterruptedException {
            return (V)this.get();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class LinkedWeakEntry<K, V>
    extends WeakEntry<K, V> {
        final ReferenceEntry<K, V> next;

        LinkedWeakEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash, ReferenceEntry<K, V> next) {
            super(internals, key, hash);
            this.next = next;
        }

        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class WeakEntry<K, V>
    extends $FinalizableWeakReference<K>
    implements ReferenceEntry<K, V> {
        final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        final int hash;
        volatile ValueReference<K, V> valueReference = $MapMaker.access$600();

        WeakEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash) {
            super(key, QueueHolder.queue);
            this.internals = internals;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return (K)this.get();
        }

        @Override
        public void finalizeReferent() {
            this.internals.removeEntry(this);
        }

        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }

        @Override
        public void valueReclaimed() {
            this.internals.removeEntry(this, null);
        }

        @Override
        public ReferenceEntry<K, V> getNext() {
            return null;
        }

        @Override
        public int getHash() {
            return this.hash;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class LinkedSoftEntry<K, V>
    extends SoftEntry<K, V> {
        final ReferenceEntry<K, V> next;

        LinkedSoftEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash, ReferenceEntry<K, V> next) {
            super(internals, key, hash);
            this.next = next;
        }

        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SoftEntry<K, V>
    extends $FinalizableSoftReference<K>
    implements ReferenceEntry<K, V> {
        final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        final int hash;
        volatile ValueReference<K, V> valueReference = $MapMaker.access$600();

        SoftEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash) {
            super(key, QueueHolder.queue);
            this.internals = internals;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return (K)this.get();
        }

        @Override
        public void finalizeReferent() {
            this.internals.removeEntry(this);
        }

        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }

        @Override
        public void valueReclaimed() {
            this.internals.removeEntry(this, null);
        }

        @Override
        public ReferenceEntry<K, V> getNext() {
            return null;
        }

        @Override
        public int getHash() {
            return this.hash;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class LinkedStrongEntry<K, V>
    extends StrongEntry<K, V> {
        final ReferenceEntry<K, V> next;

        LinkedStrongEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash, ReferenceEntry<K, V> next) {
            super(internals, key, hash);
            this.next = next;
        }

        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class StrongEntry<K, V>
    implements ReferenceEntry<K, V> {
        final K key;
        final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        final int hash;
        volatile ValueReference<K, V> valueReference = $MapMaker.access$600();

        StrongEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash) {
            this.internals = internals;
            this.key = key;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override
        public void setValueReference(ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }

        @Override
        public void valueReclaimed() {
            this.internals.removeEntry(this, null);
        }

        @Override
        public ReferenceEntry<K, V> getNext() {
            return null;
        }

        @Override
        public int getHash() {
            return this.hash;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static interface ReferenceEntry<K, V> {
        public ValueReference<K, V> getValueReference();

        public void setValueReference(ValueReference<K, V> var1);

        public void valueReclaimed();

        public ReferenceEntry<K, V> getNext();

        public int getHash();

        public K getKey();
    }

    private static class QueueHolder {
        static final $FinalizableReferenceQueue queue = new $FinalizableReferenceQueue();

        private QueueHolder() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ComputationExceptionReference<K, V>
    implements ValueReference<K, V> {
        final Throwable t;

        ComputationExceptionReference(Throwable t) {
            this.t = t;
        }

        @Override
        public V get() {
            return null;
        }

        @Override
        public ValueReference<K, V> copyFor(ReferenceEntry<K, V> entry) {
            return this;
        }

        @Override
        public V waitForValue() {
            throw new $AsynchronousComputationException(this.t);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class NullOutputExceptionReference<K, V>
    implements ValueReference<K, V> {
        final String message;

        NullOutputExceptionReference(String message) {
            this.message = message;
        }

        @Override
        public V get() {
            return null;
        }

        @Override
        public ValueReference<K, V> copyFor(ReferenceEntry<K, V> entry) {
            return this;
        }

        @Override
        public V waitForValue() {
            throw new $NullOutputException(this.message);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static interface ValueReference<K, V> {
        public V get();

        public ValueReference<K, V> copyFor(ReferenceEntry<K, V> var1);

        public V waitForValue() throws InterruptedException;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class StrategyImpl<K, V>
    implements Serializable,
    $CustomConcurrentHashMap.ComputingStrategy<K, V, ReferenceEntry<K, V>> {
        final Strength keyStrength;
        final Strength valueStrength;
        final ConcurrentMap<K, V> map;
        final long expirationNanos;
        $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        private static final long serialVersionUID = 0L;

        StrategyImpl($MapMaker maker) {
            this.keyStrength = maker.keyStrength;
            this.valueStrength = maker.valueStrength;
            this.expirationNanos = maker.expirationNanos;
            this.map = maker.builder.buildMap(this);
        }

        StrategyImpl($MapMaker maker, $Function<? super K, ? extends V> computer) {
            this.keyStrength = maker.keyStrength;
            this.valueStrength = maker.valueStrength;
            this.expirationNanos = maker.expirationNanos;
            this.map = maker.builder.buildComputingMap(this, computer);
        }

        @Override
        public void setValue(ReferenceEntry<K, V> entry, V value) {
            this.setValueReference(entry, this.valueStrength.referenceValue(entry, value));
            if (this.expirationNanos > 0L) {
                this.scheduleRemoval(entry.getKey(), value);
            }
        }

        void scheduleRemoval(K key, V value) {
            final WeakReference<K> keyReference = new WeakReference<K>(key);
            final WeakReference<V> valueReference = new WeakReference<V>(value);
            $ExpirationTimer.instance.schedule(new TimerTask(){

                public void run() {
                    Object key = keyReference.get();
                    if (key != null) {
                        StrategyImpl.this.map.remove(key, valueReference.get());
                    }
                }
            }, TimeUnit.NANOSECONDS.toMillis(this.expirationNanos));
        }

        @Override
        public boolean equalKeys(K a, Object b) {
            return this.keyStrength.equal(a, b);
        }

        @Override
        public boolean equalValues(V a, Object b) {
            return this.valueStrength.equal(a, b);
        }

        @Override
        public int hashKey(Object key) {
            return this.keyStrength.hash(key);
        }

        @Override
        public K getKey(ReferenceEntry<K, V> entry) {
            return entry.getKey();
        }

        @Override
        public int getHash(ReferenceEntry entry) {
            return entry.getHash();
        }

        @Override
        public ReferenceEntry<K, V> newEntry(K key, int hash, ReferenceEntry<K, V> next) {
            return this.keyStrength.newEntry(this.internals, key, hash, next);
        }

        @Override
        public ReferenceEntry<K, V> copyEntry(K key, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext) {
            ValueReference<K, V> valueReference = original.getValueReference();
            if (valueReference == COMPUTING) {
                ReferenceEntry<K, V> newEntry = this.newEntry(key, original.getHash(), newNext);
                newEntry.setValueReference(new FutureValueReference(original, newEntry));
                return newEntry;
            }
            ReferenceEntry<K, V> newEntry = this.newEntry(key, original.getHash(), newNext);
            newEntry.setValueReference(valueReference.copyFor(newEntry));
            return newEntry;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V waitForValue(ReferenceEntry<K, V> entry) throws InterruptedException {
            ValueReference<K, V> valueReference = entry.getValueReference();
            if (valueReference == COMPUTING) {
                ReferenceEntry<K, V> referenceEntry = entry;
                synchronized (referenceEntry) {
                    while ((valueReference = entry.getValueReference()) == COMPUTING) {
                        entry.wait();
                    }
                }
            }
            return valueReference.waitForValue();
        }

        @Override
        public V getValue(ReferenceEntry<K, V> entry) {
            ValueReference<K, V> valueReference = entry.getValueReference();
            return valueReference.get();
        }

        @Override
        public V compute(K key, ReferenceEntry<K, V> entry, $Function<? super K, ? extends V> computer) {
            V value;
            try {
                value = computer.apply(key);
            }
            catch (Throwable t) {
                this.setValueReference(entry, new ComputationExceptionReference(t));
                throw new $ComputationException(t);
            }
            if (value == null) {
                String message = computer + " returned null for key " + key + ".";
                this.setValueReference(entry, new NullOutputExceptionReference(message));
                throw new $NullOutputException(message);
            }
            this.setValue(entry, value);
            return value;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void setValueReference(ReferenceEntry<K, V> entry, ValueReference<K, V> valueReference) {
            boolean notifyOthers = entry.getValueReference() == COMPUTING;
            entry.setValueReference(valueReference);
            if (notifyOthers) {
                ReferenceEntry<K, V> referenceEntry = entry;
                synchronized (referenceEntry) {
                    entry.notifyAll();
                }
            }
        }

        @Override
        public ReferenceEntry<K, V> getNext(ReferenceEntry<K, V> entry) {
            return entry.getNext();
        }

        @Override
        public void setInternals($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals) {
            this.internals = internals;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject((Object)this.keyStrength);
            out.writeObject((Object)this.valueStrength);
            out.writeLong(this.expirationNanos);
            out.writeObject(this.internals);
            out.writeObject(this.map);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                Fields.keyStrength.set(this, in.readObject());
                Fields.valueStrength.set(this, in.readObject());
                Fields.expirationNanos.set(this, in.readLong());
                Fields.internals.set(this, in.readObject());
                Fields.map.set(this, in.readObject());
            }
            catch (IllegalAccessException e) {
                throw new AssertionError((Object)e);
            }
        }

        private static class Fields {
            static final Field keyStrength = Fields.findField("keyStrength");
            static final Field valueStrength = Fields.findField("valueStrength");
            static final Field expirationNanos = Fields.findField("expirationNanos");
            static final Field internals = Fields.findField("internals");
            static final Field map = Fields.findField("map");

            private Fields() {
            }

            static Field findField(String name) {
                try {
                    Field f = StrategyImpl.class.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                }
                catch (NoSuchFieldException e) {
                    throw new AssertionError((Object)e);
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private class FutureValueReference
        implements ValueReference<K, V> {
            final ReferenceEntry<K, V> original;
            final ReferenceEntry<K, V> newEntry;

            FutureValueReference(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newEntry) {
                this.original = original;
                this.newEntry = newEntry;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public V get() {
                boolean success = false;
                try {
                    Object value = this.original.getValueReference().get();
                    success = true;
                    Object v = value;
                    return v;
                }
                finally {
                    if (!success) {
                        this.removeEntry();
                    }
                }
            }

            @Override
            public ValueReference<K, V> copyFor(ReferenceEntry<K, V> entry) {
                return new FutureValueReference(this.original, entry);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public V waitForValue() throws InterruptedException {
                boolean success = false;
                try {
                    Object value = StrategyImpl.this.waitForValue(this.original);
                    success = true;
                    Object v = value;
                    return v;
                }
                finally {
                    if (!success) {
                        this.removeEntry();
                    }
                }
            }

            void removeEntry() {
                StrategyImpl.this.internals.removeEntry(this.newEntry);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum Strength {
        WEAK{

            @Override
            boolean equal(Object a, Object b) {
                return a == b;
            }

            @Override
            int hash(Object o) {
                return System.identityHashCode(o);
            }

            @Override
            <K, V> ValueReference<K, V> referenceValue(ReferenceEntry<K, V> entry, V value) {
                return new WeakValueReference<K, V>(value, entry);
            }

            @Override
            <K, V> ReferenceEntry<K, V> newEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash, ReferenceEntry<K, V> next) {
                return next == null ? new WeakEntry<K, V>(internals, key, hash) : new LinkedWeakEntry<K, V>(internals, key, hash, next);
            }

            @Override
            <K, V> ReferenceEntry<K, V> copyEntry(K key, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext) {
                WeakEntry from = (WeakEntry)original;
                return newNext == null ? new WeakEntry(from.internals, key, from.hash) : new LinkedWeakEntry(from.internals, key, from.hash, newNext);
            }
        }
        ,
        SOFT{

            @Override
            boolean equal(Object a, Object b) {
                return a == b;
            }

            @Override
            int hash(Object o) {
                return System.identityHashCode(o);
            }

            @Override
            <K, V> ValueReference<K, V> referenceValue(ReferenceEntry<K, V> entry, V value) {
                return new SoftValueReference<K, V>(value, entry);
            }

            @Override
            <K, V> ReferenceEntry<K, V> newEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash, ReferenceEntry<K, V> next) {
                return next == null ? new SoftEntry<K, V>(internals, key, hash) : new LinkedSoftEntry<K, V>(internals, key, hash, next);
            }

            @Override
            <K, V> ReferenceEntry<K, V> copyEntry(K key, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext) {
                SoftEntry from = (SoftEntry)original;
                return newNext == null ? new SoftEntry(from.internals, key, from.hash) : new LinkedSoftEntry(from.internals, key, from.hash, newNext);
            }
        }
        ,
        STRONG{

            @Override
            boolean equal(Object a, Object b) {
                return a.equals(b);
            }

            @Override
            int hash(Object o) {
                return o.hashCode();
            }

            @Override
            <K, V> ValueReference<K, V> referenceValue(ReferenceEntry<K, V> entry, V value) {
                return new StrongValueReference(value);
            }

            @Override
            <K, V> ReferenceEntry<K, V> newEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, K key, int hash, ReferenceEntry<K, V> next) {
                return next == null ? new StrongEntry<K, V>(internals, key, hash) : new LinkedStrongEntry<K, V>(internals, key, hash, next);
            }

            @Override
            <K, V> ReferenceEntry<K, V> copyEntry(K key, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext) {
                StrongEntry from = (StrongEntry)original;
                return newNext == null ? new StrongEntry(from.internals, key, from.hash) : new LinkedStrongEntry(from.internals, key, from.hash, newNext);
            }
        };


        abstract boolean equal(Object var1, Object var2);

        abstract int hash(Object var1);

        abstract <K, V> ValueReference<K, V> referenceValue(ReferenceEntry<K, V> var1, V var2);

        abstract <K, V> ReferenceEntry<K, V> newEntry($CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> var1, K var2, int var3, ReferenceEntry<K, V> var4);

        abstract <K, V> ReferenceEntry<K, V> copyEntry(K var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3);
    }
}

