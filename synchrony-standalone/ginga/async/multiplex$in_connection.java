/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class multiplex$in_connection
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async.multiplex", "->InConnection");
    public static final Var const__1 = RT.var("ginga.async.win", "stats");
    public static final Keyword const__3 = RT.keyword(null, "win-stats");
    public static final Var const__4 = RT.var("ginga.core", "lookup-sentinel");
    public static final Keyword const__6 = RT.keyword(null, "close-promise");
    public static final Keyword const__7 = RT.keyword(null, "id");
    public static final Keyword const__8 = RT.keyword(null, "ch");
    public static final Keyword const__9 = RT.keyword(null, "buf");

    public static Object invokeStatic(Object G__12890) {
        Object object;
        IFn iFn = (IFn)const__0.getRawRoot();
        Object v__8262__auto__12925 = RT.get(G__12890, const__3, const__4.getRawRoot());
        if (Util.identical(v__8262__auto__12925, const__4.getRawRoot())) {
            object = ((IFn)const__1.getRawRoot()).invoke();
        } else {
            object = v__8262__auto__12925;
            Object var1_1 = null;
        }
        Object object2 = RT.get(G__12890, const__6);
        Object object3 = RT.get(G__12890, const__7);
        Object object4 = RT.get(G__12890, const__8);
        Object object5 = G__12890;
        G__12890 = null;
        return iFn.invoke(object, object2, object3, object4, RT.get(object5, const__9));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return multiplex$in_connection.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(), null, null, null, null);
    }

    @Override
    public Object invoke() {
        return multiplex$in_connection.invokeStatic();
    }
}

