/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local$iter__19198__19202$fn__19203;
import clojure.lang.AFunction;
import clojure.lang.LazySeq;

public final class format$parse_local$iter__19198__19202
extends AFunction {
    Object s;

    public format$parse_local$iter__19198__19202(Object object) {
        this.s = object;
    }

    @Override
    public Object invoke(Object s__19199) {
        Object object = s__19199;
        s__19199 = null;
        return new LazySeq(new format$parse_local$iter__19198__19202$fn__19203(this, this.s, object));
    }
}

