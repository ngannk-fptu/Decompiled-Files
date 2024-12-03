/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;

public final class async$try_put_BANG_$fn__9161$G__9112__9162
extends AFunction {
    Object sink;

    public async$try_put_BANG_$fn__9161$G__9112__9162(Object object) {
        this.sink = object;
    }

    @Override
    public Object invoke() {
        this.sink = null;
        return this.sink;
    }
}

