/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;
import ginga.async$retry_BANG_$fn__9562;

public final class async$retry_BANG_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "ms");
    public static final Object const__7 = 0L;
    public static final Keyword const__8 = RT.keyword(null, "max");
    public static final Keyword const__9 = RT.keyword(null, "promise");
    public static final Var const__10 = RT.var("clojure.core.async", "chan");
    public static final Keyword const__11 = RT.keyword(null, "retry?");
    public static final Var const__12 = RT.var("ginga.core", "error?");
    public static final Object const__13 = 1L;
    public static final Var const__14 = RT.var("clojure.core.async.impl.dispatch", "run");

    public static Object invokeStatic(Object p__9462, Object f, ISeq args) {
        Object map__9463;
        Object object;
        Object map__94632 = p__9462;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(map__94632);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = ((IFn)const__1.getRawRoot()).invoke(map__94632);
            if (object3 != null && object3 != Boolean.FALSE) {
                Object object4 = map__94632;
                map__94632 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object4));
            } else {
                Object object5 = ((IFn)const__3.getRawRoot()).invoke(map__94632);
                if (object5 != null && object5 != Boolean.FALSE) {
                    Object object6 = map__94632;
                    map__94632 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object6);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__94632;
            map__94632 = null;
        }
        Object retry_opts = map__9463 = object;
        Object ms = RT.get(map__9463, const__6, const__7);
        Object max2 = RT.get(map__9463, const__8, null);
        Object promise2 = RT.get(map__9463, const__9, ((IFn)const__10.getRawRoot()).invoke());
        Object retry_QMARK_2 = RT.get(map__9463, const__11, const__12.getRawRoot());
        Object c__5667__auto__9630 = ((IFn)const__10.getRawRoot()).invoke(const__13);
        Object captured_bindings__5668__auto__9631 = Var.getThreadBindingFrame();
        Object object7 = retry_QMARK_2;
        retry_QMARK_2 = null;
        Object object8 = max2;
        max2 = null;
        Object object9 = map__9463;
        map__9463 = null;
        Object object10 = p__9462;
        p__9462 = null;
        Object object11 = promise2;
        promise2 = null;
        Object object12 = ms;
        ms = null;
        ISeq iSeq = args;
        args = null;
        Object object13 = f;
        f = null;
        Object object14 = retry_opts;
        retry_opts = null;
        Object object15 = captured_bindings__5668__auto__9631;
        captured_bindings__5668__auto__9631 = null;
        ((IFn)const__14.getRawRoot()).invoke(new async$retry_BANG_$fn__9562(c__5667__auto__9630, object7, object8, object9, object10, object11, object12, iSeq, object13, object14, object15));
        Object object16 = c__5667__auto__9630;
        c__5667__auto__9630 = null;
        return object16;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return async$retry_BANG_.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

