/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.extra;

import brave.internal.Platform;
import brave.internal.extra.Extra;
import brave.propagation.TraceContext;

public abstract class ExtraFactory<E extends Extra<E, F>, F extends ExtraFactory<E, F>> {
    final Object initialState;

    protected ExtraFactory(Object initialState) {
        if (initialState == null) {
            throw new NullPointerException("initialState == null");
        }
        this.initialState = initialState;
    }

    protected abstract E create();

    public final TraceContext decorate(TraceContext context) {
        long traceId = context.traceId();
        long spanId = context.spanId();
        Extra claimed = null;
        int existingIndex = -1;
        int extraLength = context.extra().size();
        for (int i = 0; i < extraLength; ++i) {
            Object next = context.extra().get(i);
            if (!(next instanceof Extra)) continue;
            Extra nextExtra = (Extra)next;
            if (nextExtra.factory != this) continue;
            if (claimed == null && nextExtra.tryToClaim(traceId, spanId)) {
                claimed = nextExtra;
                continue;
            }
            if (existingIndex == -1) {
                existingIndex = i;
                continue;
            }
            Platform.get().log("BUG: something added redundant extra instances %s", context, null);
            return context;
        }
        if (claimed != null && existingIndex == -1) {
            return context;
        }
        if (claimed == null) {
            claimed = (Extra)this.create();
            if (claimed == null) {
                Platform.get().log("BUG: create() returned null", null);
                return context;
            }
            claimed.tryToClaim(traceId, spanId);
        }
        TraceContext.Builder builder = context.toBuilder().clearExtra().addExtra(claimed);
        for (int i = 0; i < extraLength; ++i) {
            Object next = context.extra().get(i);
            if (i == existingIndex) {
                Extra existing = (Extra)next;
                if (claimed.state == this.initialState) {
                    claimed.state = existing.state;
                    continue;
                }
                if (existing.state == this.initialState) continue;
                claimed.mergeStateKeepingOursOnConflict(existing);
                continue;
            }
            if (next.equals(claimed)) continue;
            builder.addExtra(next);
        }
        return builder.build();
    }
}

