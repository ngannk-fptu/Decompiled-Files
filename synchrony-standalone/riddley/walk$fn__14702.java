/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class walk$fn__14702
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "commute");
    public static final Var const__1 = RT.var("clojure.core", "deref");
    public static final Var const__2 = RT.var("clojure.core", "*loaded-libs*");
    public static final Var const__3 = RT.var("clojure.core", "conj");
    public static final AFn const__4 = Symbol.intern(null, "riddley.walk");

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2), const__3.getRawRoot(), const__4);
    }

    @Override
    public Object invoke() {
        return walk$fn__14702.invokeStatic();
    }
}

