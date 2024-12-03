/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.extra;

import brave.internal.Nullable;
import brave.internal.extra.ExtraFactory;

public abstract class Extra<E extends Extra<E, F>, F extends ExtraFactory<E, F>> {
    protected final F factory;
    protected final Object lock = new Object();
    protected volatile Object state;
    long traceId;
    long spanId;

    protected Extra(F factory) {
        if (factory == null) {
            throw new NullPointerException("factory == null");
        }
        this.factory = factory;
        this.state = ((ExtraFactory)factory).initialState;
    }

    protected abstract void mergeStateKeepingOursOnConflict(E var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final boolean tryToClaim(long traceId, long spanId) {
        Object object = this.lock;
        synchronized (object) {
            if (this.traceId == 0L) {
                this.traceId = traceId;
                this.spanId = spanId;
                return true;
            }
            return this.traceId == traceId && this.spanId == spanId;
        }
    }

    protected abstract boolean stateEquals(Object var1);

    protected abstract int stateHashCode();

    protected abstract String stateString();

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!this.getClass().isInstance(o)) {
            return false;
        }
        return this.stateEquals(((Extra)o).state);
    }

    public final int hashCode() {
        return this.stateHashCode();
    }

    public final String toString() {
        return this.getClass().getSimpleName() + "{" + this.stateString() + "}";
    }

    static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == null ? b == null : a.equals(b);
    }
}

