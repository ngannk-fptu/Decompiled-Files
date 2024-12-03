/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;
import ginga.async$process$fn__8976;

public final class async$process
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core.async", "chan");
    public static final Object const__1 = 1L;
    public static final Var const__2 = RT.var("clojure.core.async.impl.dispatch", "run");

    public static Object invokeStatic(Object f, Object in_ch, ISeq args) {
        Object port__8940__auto__9012 = in_ch;
        Object c__5667__auto__9010 = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object captured_bindings__5668__auto__9011 = Var.getThreadBindingFrame();
        Object object = f;
        f = null;
        Object object2 = captured_bindings__5668__auto__9011;
        captured_bindings__5668__auto__9011 = null;
        ISeq iSeq = args;
        args = null;
        Object object3 = port__8940__auto__9012;
        port__8940__auto__9012 = null;
        Object object4 = in_ch;
        in_ch = null;
        ((IFn)const__2.getRawRoot()).invoke(new async$process$fn__8976(object, c__5667__auto__9010, object2, iSeq, object3, object4));
        Object object5 = c__5667__auto__9010;
        c__5667__auto__9010 = null;
        return object5;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return async$process.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

