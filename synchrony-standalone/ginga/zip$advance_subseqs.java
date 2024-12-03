/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;

public final class zip$advance_subseqs
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "not=");
    public static final Var const__1 = RT.var("clojure.core", "conj");
    public static final Var const__2 = RT.var("clojure.core", "subvec");

    public static Object invokeStatic(Object subseqs, Object v, Object prev_i, Object i) {
        Object object;
        Object object2;
        Object or__5581__auto__21206;
        Object object3 = subseqs;
        subseqs = null;
        Object object4 = or__5581__auto__21206 = object3;
        if (object4 != null && object4 != Boolean.FALSE) {
            object2 = or__5581__auto__21206;
            or__5581__auto__21206 = null;
        } else {
            object2 = PersistentVector.EMPTY;
        }
        Object G__21204 = object2;
        Object object5 = ((IFn)const__0.getRawRoot()).invoke(prev_i, i);
        if (object5 != null && object5 != Boolean.FALSE) {
            Object object6 = G__21204;
            G__21204 = null;
            Object object7 = v;
            v = null;
            Object object8 = prev_i;
            prev_i = null;
            Object object9 = i;
            i = null;
            object = ((IFn)const__1.getRawRoot()).invoke(object6, ((IFn)const__2.getRawRoot()).invoke(object7, object8, object9));
        } else {
            object = G__21204;
            G__21204 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return zip$advance_subseqs.invokeStatic(object5, object6, object7, object8);
    }
}

