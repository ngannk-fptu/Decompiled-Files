/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse$iter__19179__19183$fn__19184;
import clojure.lang.AFunction;
import clojure.lang.LazySeq;

public final class format$parse$iter__19179__19183
extends AFunction {
    Object s;

    public format$parse$iter__19179__19183(Object object) {
        this.s = object;
    }

    @Override
    public Object invoke(Object s__19180) {
        Object object = s__19180;
        s__19180 = null;
        return new LazySeq(new format$parse$iter__19179__19183$fn__19184(this.s, object, this));
    }
}

