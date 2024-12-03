/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local_date$iter__19217__19221$fn__19222;
import clojure.lang.AFunction;
import clojure.lang.LazySeq;

public final class format$parse_local_date$iter__19217__19221
extends AFunction {
    Object s;

    public format$parse_local_date$iter__19217__19221(Object object) {
        this.s = object;
    }

    @Override
    public Object invoke(Object s__19218) {
        Object object = s__19218;
        s__19218 = null;
        return new LazySeq(new format$parse_local_date$iter__19217__19221$fn__19222(this.s, object, this));
    }
}

