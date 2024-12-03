/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;

public final class async$convey_BANG_$fn__9342$G__9293__9347
extends AFunction {
    Object close_promise;

    public async$convey_BANG_$fn__9342$G__9293__9347(Object object) {
        this.close_promise = object;
    }

    @Override
    public Object invoke() {
        this.close_promise = null;
        return this.close_promise;
    }
}

