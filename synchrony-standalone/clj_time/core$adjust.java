/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class core$adjust
extends RestFn {
    public static final Var const__0 = RT.var("clj-time.core", "interval");
    public static final Var const__1 = RT.var("clojure.core", "apply");
    public static final Var const__2 = RT.var("clj-time.core", "plus");
    public static final Var const__3 = RT.var("clj-time.core", "start");
    public static final Var const__4 = RT.var("clj-time.core", "end");

    public static Object invokeStatic(Object in2, ISeq by) {
        Object object = ((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(in2), by);
        Object object2 = in2;
        in2 = null;
        ISeq iSeq = by;
        by = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(object2), iSeq));
    }

    @Override
    public Object doInvoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        ISeq iSeq = (ISeq)object2;
        object2 = null;
        return core$adjust.invokeStatic(object3, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 1;
    }
}

