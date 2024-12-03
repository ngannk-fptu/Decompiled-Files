/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local_time$iter__19236__19240$fn__19241;
import clojure.lang.AFunction;
import clojure.lang.LazySeq;

public final class format$parse_local_time$iter__19236__19240
extends AFunction {
    Object s;

    public format$parse_local_time$iter__19236__19240(Object object) {
        this.s = object;
    }

    @Override
    public Object invoke(Object s__19237) {
        Object object = s__19237;
        s__19237 = null;
        return new LazySeq(new format$parse_local_time$iter__19236__19240$fn__19241(this.s, object, this));
    }
}

