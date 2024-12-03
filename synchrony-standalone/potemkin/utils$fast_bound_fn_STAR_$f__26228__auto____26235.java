/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class utils$fast_bound_fn_STAR_$f__26228__auto____26235
extends RestFn {
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "apply");

    public utils$fast_bound_fn_STAR_$f__26228__auto____26235(Object object) {
        this.f = object;
    }

    @Override
    public Object doInvoke(Object args) {
        Object object = args;
        args = null;
        utils$fast_bound_fn_STAR_$f__26228__auto____26235 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(this_.f, object);
    }

    @Override
    public int getRequiredArity() {
        return 0;
    }
}

