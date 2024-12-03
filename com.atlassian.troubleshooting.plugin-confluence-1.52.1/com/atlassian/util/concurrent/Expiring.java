/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.concurrent.atomic.AtomicReference;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class Expiring<T>
implements Supplier<T> {
    private final AtomicReference<Alive<T>> r = new AtomicReference(Dead.instance());
    private final Supplier<T> factory;
    private final Supplier<Predicate<Void>> strategy;

    Expiring(Supplier<T> factory, Supplier<Predicate<Void>> strategy) {
        this.factory = (Supplier)Preconditions.checkNotNull(factory);
        this.strategy = (Supplier)Preconditions.checkNotNull(strategy);
    }

    @Override
    public T get() {
        int i = 0;
        Alive e;
        while (!(e = (Alive)this.r.get()).alive()) {
            if (i++ > 100) {
                throw new AssertionError((Object)"100 attempts to CAS update the next value, aborting!");
            }
            this.r.compareAndSet(e, new Value());
        }
        return e.get();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum Dead implements Alive<Object>
    {
        DEAD;


        @Override
        public boolean alive() {
            return false;
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException("dead");
        }

        static <T> Alive<T> instance() {
            Dead result = DEAD;
            return result;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    final class Value
    extends LazyReference<T>
    implements Alive<T> {
        final Predicate<Void> alive;

        Value() {
            this.alive = (Predicate)Preconditions.checkNotNull(Expiring.this.strategy.get());
        }

        @Override
        public boolean alive() {
            return this.alive.apply(null);
        }

        @Override
        public T create() {
            return Expiring.this.factory.get();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static interface Alive<T>
    extends Supplier<T> {
        public boolean alive();
    }
}

