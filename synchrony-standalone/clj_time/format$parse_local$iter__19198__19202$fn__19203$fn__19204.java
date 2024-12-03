/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local$iter__19198__19202$fn__19203$fn__19204$fn__19205;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse_local$iter__19198__19202$fn__19203$fn__19204
extends AFunction {
    Object b__19201;
    Object s;
    int size__6372__auto__;
    Object c__6371__auto__;
    public static final Var const__3 = RT.var("clojure.core", "chunk-append");

    public format$parse_local$iter__19198__19202$fn__19203$fn__19204(Object object, Object object2, int n, Object object3) {
        this.b__19201 = object;
        this.s = object2;
        this.size__6372__auto__ = n;
        this.c__6371__auto__ = object3;
    }

    @Override
    public Object invoke() {
        long i__19200 = RT.intCast(0L);
        while (i__19200 < (long)this.size__6372__auto__) {
            Object d;
            Object f;
            Object object = f = ((Indexed)this.c__6371__auto__).nth(RT.intCast(i__19200));
            f = null;
            Object object2 = d = ((IFn)new format$parse_local$iter__19198__19202$fn__19203$fn__19204$fn__19205(object, this.s)).invoke();
            if (object2 != null && object2 != Boolean.FALSE) {
                Object object3 = d;
                d = null;
                ((IFn)const__3.getRawRoot()).invoke(this.b__19201, object3);
                ++i__19200;
                continue;
            }
            ++i__19200;
        }
        return Boolean.TRUE;
    }
}

