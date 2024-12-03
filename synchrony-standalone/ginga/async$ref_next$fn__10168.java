/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class async$ref_next$fn__10168
extends AFunction {
    Object ch;
    public static final Var const__0 = RT.var("clojure.core.async", "put!");

    public async$ref_next$fn__10168(Object object) {
        this.ch = object;
    }

    @Override
    public Object invoke(Object key2, Object ref2, Object old, Object object) {
        Object object2 = object;
        object = null;
        async$ref_next$fn__10168 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(this_.ch, object2);
    }
}

