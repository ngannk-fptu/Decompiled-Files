/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$drop_last_while
extends AFunction {
    public static final Var const__0 = RT.var("ginga.core", "ensure-vector");
    public static final Var const__1 = RT.var("ginga.core", "last-index-of");
    public static final Var const__2 = RT.var("clojure.core", "complement");
    public static final Var const__4 = RT.var("clojure.core", "subvec");
    public static final Object const__5 = 0L;

    public static Object invokeStatic(Object pred2, Object s2) {
        Object object;
        Object object2 = s2;
        s2 = null;
        Object v = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object object3 = pred2;
        pred2 = null;
        Object off = ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(object3), v);
        if (Numbers.isNeg(off)) {
            object = PersistentVector.EMPTY;
        } else {
            Object object4 = v;
            v = null;
            Object object5 = off;
            off = null;
            object = ((IFn)const__4.getRawRoot()).invoke(object4, const__5, Numbers.inc(object5));
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$drop_last_while.invokeStatic(object3, object4);
    }
}

