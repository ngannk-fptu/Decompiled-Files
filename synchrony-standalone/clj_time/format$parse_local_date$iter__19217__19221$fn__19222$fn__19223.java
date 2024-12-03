/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse_local_date$iter__19217__19221$fn__19222$fn__19223$fn__19224;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse_local_date$iter__19217__19221$fn__19222$fn__19223
extends AFunction {
    Object s;
    Object c__6371__auto__;
    int size__6372__auto__;
    Object b__19220;
    public static final Var const__3 = RT.var("clojure.core", "chunk-append");

    public format$parse_local_date$iter__19217__19221$fn__19222$fn__19223(Object object, Object object2, int n, Object object3) {
        this.s = object;
        this.c__6371__auto__ = object2;
        this.size__6372__auto__ = n;
        this.b__19220 = object3;
    }

    @Override
    public Object invoke() {
        long i__19219 = RT.intCast(0L);
        while (i__19219 < (long)this.size__6372__auto__) {
            Object d;
            Object f;
            Object object = f = ((Indexed)this.c__6371__auto__).nth(RT.intCast(i__19219));
            f = null;
            Object object2 = d = ((IFn)new format$parse_local_date$iter__19217__19221$fn__19222$fn__19223$fn__19224(object, this.s)).invoke();
            if (object2 != null && object2 != Boolean.FALSE) {
                Object object3 = d;
                d = null;
                ((IFn)const__3.getRawRoot()).invoke(this.b__19220, object3);
                ++i__19219;
                continue;
            }
            ++i__19219;
        }
        return Boolean.TRUE;
    }
}

