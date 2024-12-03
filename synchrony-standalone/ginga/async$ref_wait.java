/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$ref_wait$fn__10165;

public final class async$ref_wait
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core.async", "promise-chan");
    public static final Var const__1 = RT.var("clojure.core", "deref");
    public static final Var const__2 = RT.var("clojure.core.async", "put!");
    public static final Var const__3 = RT.var("clojure.core", "add-watch");
    public static final Var const__4 = RT.var("clojure.core", "remove-watch");

    public static Object invokeStatic(Object ref2, Object key2, Object pred_QMARK_) {
        Object ch = ((IFn)const__0.getRawRoot()).invoke();
        Object v = ((IFn)const__1.getRawRoot()).invoke(ref2);
        Object object = ((IFn)pred_QMARK_).invoke(v);
        if (object != null && object != Boolean.FALSE) {
            Object object2 = v;
            v = null;
            ((IFn)const__2.getRawRoot()).invoke(ch, object2);
        } else {
            ((IFn)const__3.getRawRoot()).invoke(ref2, key2, new async$ref_wait$fn__10165(ref2, ch, pred_QMARK_, key2));
            Object v2 = ((IFn)const__1.getRawRoot()).invoke(ref2);
            Object object3 = pred_QMARK_;
            pred_QMARK_ = null;
            Object object4 = ((IFn)object3).invoke(v2);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = ref2;
                ref2 = null;
                Object object6 = key2;
                key2 = null;
                ((IFn)const__4.getRawRoot()).invoke(object5, object6);
                Object object7 = v2;
                v2 = null;
                ((IFn)const__2.getRawRoot()).invoke(ch, object7);
            }
        }
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
        return async$ref_wait.invokeStatic(object4, object5, object6);
    }
}

