/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class core$fn__34641
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "commute");
    public static final Var const__1 = RT.var("clojure.core", "deref");
    public static final Var const__2 = RT.var("clojure.core", "*loaded-libs*");
    public static final Var const__3 = RT.var("clojure.core", "conj");
    public static final AFn const__4 = (AFn)((Object)((IObj)Symbol.intern(null, "clout.core")).withMeta(RT.map(RT.keyword(null, "doc"), "A small language for routing.")));

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2), const__3.getRawRoot(), const__4);
    }

    @Override
    public Object invoke() {
        return core$fn__34641.invokeStatic();
    }
}

