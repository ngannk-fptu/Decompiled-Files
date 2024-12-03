/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class haywire$disconnect
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "upstream");
    public static final Keyword const__7 = RT.keyword(null, "downstream");
    public static final Keyword const__8 = RT.keyword(null, "close-promise");
    public static final Var const__9 = RT.var("clojure.core.async", "close!");

    public static Object invokeStatic(Object connection2) {
        Object object;
        Object object2 = connection2;
        connection2 = null;
        Object map__11007 = object2;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(map__11007);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = ((IFn)const__1.getRawRoot()).invoke(map__11007);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = map__11007;
                map__11007 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object5));
            } else {
                Object object6 = ((IFn)const__3.getRawRoot()).invoke(map__11007);
                if (object6 != null && object6 != Boolean.FALSE) {
                    Object object7 = map__11007;
                    map__11007 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object7);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__11007;
            map__11007 = null;
        }
        Object map__110072 = object;
        Object upstream = RT.get(map__110072, const__6);
        Object downstream2 = RT.get(map__110072, const__7);
        Object object8 = map__110072;
        map__110072 = null;
        Object close_promise = RT.get(object8, const__8);
        Object object9 = upstream;
        upstream = null;
        ((IFn)const__9.getRawRoot()).invoke(object9);
        Object object10 = downstream2;
        downstream2 = null;
        ((IFn)const__9.getRawRoot()).invoke(object10);
        Object object11 = close_promise;
        close_promise = null;
        return ((IFn)const__9.getRawRoot()).invoke(object11);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return haywire$disconnect.invokeStatic(object2);
    }
}

