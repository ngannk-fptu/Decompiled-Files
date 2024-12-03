/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class compiler$fn__14686$local_id__14687
extends AFunction {
    Object n;
    public static final Var const__0 = RT.var("clojure.core", "swap!");
    public static final Var const__1 = RT.var("clojure.core", "inc");

    public compiler$fn__14686$local_id__14687(Object object) {
        this.n = object;
    }

    @Override
    public Object invoke() {
        compiler$fn__14686$local_id__14687 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(this_.n, const__1.getRawRoot());
    }
}

