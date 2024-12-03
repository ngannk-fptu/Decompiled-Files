/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;

public final class async$retry_BANG_$fn__9562$G__9466__9567
extends AFunction {
    Object promise;

    public async$retry_BANG_$fn__9562$G__9466__9567(Object object) {
        this.promise = object;
    }

    @Override
    public Object invoke() {
        this.promise = null;
        return this.promise;
    }
}

