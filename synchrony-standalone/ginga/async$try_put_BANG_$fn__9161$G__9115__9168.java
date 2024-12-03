/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;

public final class async$try_put_BANG_$fn__9161$G__9115__9168
extends AFunction {
    Object timeout_val;

    public async$try_put_BANG_$fn__9161$G__9115__9168(Object object) {
        this.timeout_val = object;
    }

    @Override
    public Object invoke() {
        this.timeout_val = null;
        return this.timeout_val;
    }
}

