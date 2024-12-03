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

public final class haywire$win_msg
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async.haywire", "->WinMsg");
    public static final Keyword const__1 = RT.keyword(null, "win");
    public static final Keyword const__3 = RT.keyword(null, "type");
    public static final Var const__4 = RT.var("ginga.core", "lookup-sentinel");
    public static final Keyword const__6 = RT.keyword(null, "seq");
    public static final Keyword const__7 = RT.keyword(null, "cnt");
    public static final Keyword const__8 = RT.keyword(null, "size");

    public static Object invokeStatic(Object G__10800) {
        Object object;
        IFn iFn = (IFn)const__0.getRawRoot();
        Object v__8262__auto__10832 = RT.get(G__10800, const__3, const__4.getRawRoot());
        if (Util.identical(v__8262__auto__10832, const__4.getRawRoot())) {
            object = const__1;
        } else {
            object = v__8262__auto__10832;
            Object var1_1 = null;
        }
        Object object2 = RT.get(G__10800, const__6);
        Object object3 = RT.get(G__10800, const__7);
        Object object4 = G__10800;
        G__10800 = null;
        return iFn.invoke(object, object2, object3, RT.get(object4, const__8));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return haywire$win_msg.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(const__1, null, null, null);
    }

    @Override
    public Object invoke() {
        return haywire$win_msg.invokeStatic();
    }
}

