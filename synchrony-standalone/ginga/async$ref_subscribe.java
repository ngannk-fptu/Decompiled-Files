/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import ginga.async$ref_subscribe$fn__10171;
import java.util.Arrays;

public final class async$ref_subscribe
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "ref-subscribe");
    public static final Var const__1 = RT.var("clojure.core.async", "sliding-buffer");
    public static final Object const__2 = 1L;
    public static final Var const__3 = RT.var("clojure.core.async", "unblocking-buffer?");
    public static final Var const__4 = RT.var("clojure.core", "str");
    public static final Var const__5 = RT.var("clojure.core", "pr-str");
    public static final Object const__6 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern("async", "unblocking-buffer?"), Symbol.intern(null, "buf"))))).withMeta(RT.map(RT.keyword(null, "line"), 502, RT.keyword(null, "column"), 12));
    public static final Var const__7 = RT.var("clojure.core.async", "chan");
    public static final Var const__8 = RT.var("clojure.core", "add-watch");

    public static Object invokeStatic(Object ref2, Object k, Object buf) {
        Object object = ((IFn)const__3.getRawRoot()).invoke(buf);
        if (object == null || object == Boolean.FALSE) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__4.getRawRoot()).invoke("Assert failed: ", ((IFn)const__5.getRawRoot()).invoke(const__6))));
        }
        Object object2 = buf;
        buf = null;
        Object ch = ((IFn)const__7.getRawRoot()).invoke(object2);
        Object object3 = ref2;
        ref2 = null;
        Object object4 = k;
        Object object5 = k;
        k = null;
        ((IFn)const__8.getRawRoot()).invoke(object3, object4, new async$ref_subscribe$fn__10171(object5, ch));
        Object var3_3 = null;
        return ch;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return async$ref_subscribe.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object ref2, Object k) {
        Object object = ref2;
        ref2 = null;
        Object object2 = k;
        k = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, ((IFn)const__1.getRawRoot()).invoke(const__2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$ref_subscribe.invokeStatic(object3, object4);
    }
}

