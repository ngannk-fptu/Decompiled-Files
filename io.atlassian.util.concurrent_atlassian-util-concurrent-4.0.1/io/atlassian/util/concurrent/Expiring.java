/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.LazyReference;
import io.atlassian.util.concurrent.atomic.AtomicReference;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class Expiring<T>
implements Supplier<T> {
    private final AtomicReference<Alive<T>> r = new AtomicReference(Dead.instance());
    private final Supplier<T> factory;
    private final Supplier<Predicate<Void>> strategy;

    Expiring(Supplier<T> factory, Supplier<Predicate<Void>> strategy) {
        this.factory = Objects.requireNonNull(factory);
        this.strategy = Objects.requireNonNull(strategy);
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

    final class Value
    extends LazyReference<T>
    implements Alive<T> {
        final Predicate<Void> alive;

        Value() {
            this.alive = (Predicate)Objects.requireNonNull(Expiring.this.strategy.get());
        }

        @Override
        public boolean alive() {
            return this.alive.test(null);
        }

        @Override
        public T create() {
            return Expiring.this.factory.get();
        }
    }

    static interface Alive<T>
    extends Supplier<T> {
        public boolean alive();
    }
}

