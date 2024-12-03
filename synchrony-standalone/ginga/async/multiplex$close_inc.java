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

public final class multiplex$close_inc
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "buf");
    public static final Var const__7 = RT.var("clojure.core.async", "close!");

    public static Object invokeStatic(Object p__13013) {
        Object buf;
        Object object;
        Object object2 = p__13013;
        p__13013 = null;
        Object map__13014 = object2;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(map__13014);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = ((IFn)const__1.getRawRoot()).invoke(map__13014);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = map__13014;
                map__13014 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object5));
            } else {
                Object object6 = ((IFn)const__3.getRawRoot()).invoke(map__13014);
                if (object6 != null && object6 != Boolean.FALSE) {
                    Object object7 = map__13014;
                    map__13014 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object7);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__13014;
            map__13014 = null;
        }
        Object map__130142 = object;
        Object object8 = map__130142;
        map__130142 = null;
        Object object9 = buf = RT.get(object8, const__6);
        buf = null;
        return ((IFn)const__7.getRawRoot()).invoke(object9);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return multiplex$close_inc.invokeStatic(object2);
    }
}

