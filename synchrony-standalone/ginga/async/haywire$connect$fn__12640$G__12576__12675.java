/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;

public final class haywire$connect$fn__12640$G__12576__12675
extends AFunction {
    Object close_promise;

    public haywire$connect$fn__12640$G__12576__12675(Object object) {
        this.close_promise = object;
    }

    @Override
    public Object invoke() {
        this.close_promise = null;
        return this.close_promise;
    }
}

