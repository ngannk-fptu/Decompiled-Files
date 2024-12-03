/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class async$ref_subscribe$fn__10171
extends AFunction {
    Object k;
    Object ch;
    public static final Var const__0 = RT.var("clojure.core.async", "put!");
    public static final Var const__1 = RT.var("clojure.core", "remove-watch");

    public async$ref_subscribe$fn__10171(Object object, Object object2) {
        this.k = object;
        this.ch = object2;
    }

    @Override
    public Object invoke(Object key2, Object ref2, Object old, Object object) {
        Object object2;
        Object object3 = old;
        old = null;
        Object object4 = object;
        object = null;
        Object object5 = ((IFn)const__0.getRawRoot()).invoke(this_.ch, Tuple.create(object3, object4));
        if (object5 != null && object5 != Boolean.FALSE) {
            object2 = null;
        } else {
            Object object6 = ref2;
            ref2 = null;
            async$ref_subscribe$fn__10171 this_ = null;
            object2 = ((IFn)const__1.getRawRoot()).invoke(object6, this_.k);
        }
        return object2;
    }
}

