/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class core$assoc_conj
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "assoc");
    public static final Var const__2 = RT.var("clojure.core", "vector?");
    public static final Var const__3 = RT.var("clojure.core", "conj");

    public static Object invokeStatic(Object m4, Object k, Object v) {
        Object object;
        Object temp__5802__auto__34663;
        IFn iFn = (IFn)const__0.getRawRoot();
        Object object2 = m4;
        Object object3 = k;
        Object object4 = m4;
        m4 = null;
        Object object5 = k;
        k = null;
        Object object6 = temp__5802__auto__34663 = RT.get(object4, object5);
        if (object6 != null && object6 != Boolean.FALSE) {
            Object object7 = temp__5802__auto__34663;
            temp__5802__auto__34663 = null;
            Object cur = object7;
            Object object8 = ((IFn)const__2.getRawRoot()).invoke(cur);
            if (object8 != null && object8 != Boolean.FALSE) {
                Object object9 = cur;
                cur = null;
                Object object10 = v;
                v = null;
                object = ((IFn)const__3.getRawRoot()).invoke(object9, object10);
            } else {
                Object object11 = cur;
                cur = null;
                Object object12 = v;
                v = null;
                object = Tuple.create(object11, object12);
            }
        } else {
            object = v;
            v = null;
        }
        return iFn.invoke(object2, object3, object);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$assoc_conj.invokeStatic(object4, object5, object6);
    }
}

