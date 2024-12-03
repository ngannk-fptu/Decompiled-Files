/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import clout.core.CompiledRoute;
import java.util.Arrays;

public final class core$route_compile
extends AFunction {
    public static final Var const__0 = RT.var("clout.core", "route-compile");
    public static final Var const__1 = RT.var("clout.core", "parse");
    public static final Var const__2 = RT.var("clout.core", "route-parser");
    public static final Var const__3 = RT.var("clout.core", "route-keys");
    public static final Var const__4 = RT.var("clojure.set", "subset?");
    public static final Var const__5 = RT.var("clojure.core", "set");
    public static final Var const__6 = RT.var("clojure.core", "keys");
    public static final Var const__7 = RT.var("clojure.core", "str");
    public static final Var const__8 = RT.var("clojure.core", "pr-str");
    public static final Object const__9 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern("set", "subset?"), ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "set"), ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "keys"), Symbol.intern(null, "regexs"))))).withMeta(RT.map(RT.keyword(null, "line"), 131, RT.keyword(null, "column"), 32)))))).withMeta(RT.map(RT.keyword(null, "line"), 131, RT.keyword(null, "column"), 27)), ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "set"), Symbol.intern(null, "ks"))))).withMeta(RT.map(RT.keyword(null, "line"), 131, RT.keyword(null, "column"), 47)))))).withMeta(RT.map(RT.keyword(null, "line"), 131, RT.keyword(null, "column"), 14));
    public static final Var const__10 = RT.var("clout.core", "route-regex");
    public static final Var const__11 = RT.var("clojure.core", "vec");
    public static final Var const__12 = RT.var("clout.core", "absolute-url?");

    public static Object invokeStatic(Object path2, Object regexs) {
        Object ast2 = ((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), path2);
        Object ks = ((IFn)const__3.getRawRoot()).invoke(ast2);
        Object object = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(regexs)), ((IFn)const__5.getRawRoot()).invoke(ks));
        if (object == null || object == Boolean.FALSE) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__7.getRawRoot()).invoke("Assert failed: ", "unused keys in regular expression map", "\n", ((IFn)const__8.getRawRoot()).invoke(const__9))));
        }
        Object object2 = path2;
        Object object3 = ast2;
        ast2 = null;
        Object object4 = regexs;
        regexs = null;
        Object object5 = ks;
        ks = null;
        Object object6 = path2;
        path2 = null;
        return new CompiledRoute(object2, ((IFn)const__10.getRawRoot()).invoke(object3, object4), ((IFn)const__11.getRawRoot()).invoke(object5), ((IFn)const__12.getRawRoot()).invoke(object6));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$route_compile.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object path2) {
        Object object = path2;
        path2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, PersistentArrayMap.EMPTY);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$route_compile.invokeStatic(object2);
    }
}

