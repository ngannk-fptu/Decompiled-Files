/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$walk$fn__26058
extends AFunction {
    Object inner;
    public static final Var const__0 = RT.var("clojure.core", "conj");

    public walk$walk$fn__26058(Object object) {
        this.inner = object;
    }

    @Override
    public Object invoke(Object r, Object x) {
        Object object = r;
        r = null;
        Object object2 = x;
        x = null;
        walk$walk$fn__26058 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)this_.inner).invoke(object2));
    }
}

