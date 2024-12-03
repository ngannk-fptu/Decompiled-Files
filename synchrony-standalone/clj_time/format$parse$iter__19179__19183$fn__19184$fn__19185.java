/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse$iter__19179__19183$fn__19184$fn__19185$fn__19186;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse$iter__19179__19183$fn__19184$fn__19185
extends AFunction {
    Object c__6371__auto__;
    int size__6372__auto__;
    Object s;
    Object b__19182;
    public static final Var const__3 = RT.var("clojure.core", "chunk-append");

    public format$parse$iter__19179__19183$fn__19184$fn__19185(Object object, int n, Object object2, Object object3) {
        this.c__6371__auto__ = object;
        this.size__6372__auto__ = n;
        this.s = object2;
        this.b__19182 = object3;
    }

    @Override
    public Object invoke() {
        long i__19181 = RT.intCast(0L);
        while (i__19181 < (long)this.size__6372__auto__) {
            Object d;
            Object f;
            Object object = f = ((Indexed)this.c__6371__auto__).nth(RT.intCast(i__19181));
            f = null;
            Object object2 = d = ((IFn)new format$parse$iter__19179__19183$fn__19184$fn__19185$fn__19186(this.s, object)).invoke();
            if (object2 != null && object2 != Boolean.FALSE) {
                Object object3 = d;
                d = null;
                ((IFn)const__3.getRawRoot()).invoke(this.b__19182, object3);
                ++i__19181;
                continue;
            }
            ++i__19181;
        }
        return Boolean.TRUE;
    }
}

