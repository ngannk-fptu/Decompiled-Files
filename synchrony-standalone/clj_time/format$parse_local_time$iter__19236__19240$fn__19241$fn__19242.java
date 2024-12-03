/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local_time$iter__19236__19240$fn__19241$fn__19242$fn__19243;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse_local_time$iter__19236__19240$fn__19241$fn__19242
extends AFunction {
    Object c__6371__auto__;
    Object b__19239;
    Object s;
    int size__6372__auto__;
    public static final Var const__3 = RT.var("clojure.core", "chunk-append");

    public format$parse_local_time$iter__19236__19240$fn__19241$fn__19242(Object object, Object object2, Object object3, int n) {
        this.c__6371__auto__ = object;
        this.b__19239 = object2;
        this.s = object3;
        this.size__6372__auto__ = n;
    }

    @Override
    public Object invoke() {
        long i__19238 = RT.intCast(0L);
        while (i__19238 < (long)this.size__6372__auto__) {
            Object d;
            Object f;
            Object object = f = ((Indexed)this.c__6371__auto__).nth(RT.intCast(i__19238));
            f = null;
            Object object2 = d = ((IFn)new format$parse_local_time$iter__19236__19240$fn__19241$fn__19242$fn__19243(object, this.s)).invoke();
            if (object2 != null && object2 != Boolean.FALSE) {
                Object object3 = d;
                d = null;
                ((IFn)const__3.getRawRoot()).invoke(this.b__19239, object3);
                ++i__19238;
                continue;
            }
            ++i__19238;
        }
        return Boolean.TRUE;
    }
}

