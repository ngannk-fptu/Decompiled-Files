/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentHashSet;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$route_keys
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "map");
    public static final Var const__1 = RT.var("clout.core", "find-route-key");
    public static final Var const__2 = RT.var("clojure.core", "filter");
    public static final Var const__3 = RT.var("clojure.core", "comp");
    public static final AFn const__6 = PersistentHashSet.create(RT.keyword(null, "wildcard"), RT.keyword(null, "param"));
    public static final Var const__7 = RT.var("clojure.core", "first");
    public static final Var const__8 = RT.var("clojure.core", "rest");

    public static Object invokeStatic(Object parse_tree) {
        Object object = parse_tree;
        parse_tree = null;
        return ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__6, const__7.getRawRoot()), ((IFn)const__8.getRawRoot()).invoke(object)));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$route_keys.invokeStatic(object2);
    }
}

