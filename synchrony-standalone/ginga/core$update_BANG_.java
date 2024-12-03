/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class core$update_BANG_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "assoc!");
    public static final Var const__2 = RT.var("clojure.core", "apply");

    public static Object invokeStatic(Object m4, Object k, Object f, Object x, ISeq more) {
        Object object = m4;
        Object object2 = k;
        Object object3 = f;
        f = null;
        Object object4 = m4;
        m4 = null;
        Object object5 = k;
        k = null;
        Object object6 = x;
        x = null;
        ISeq iSeq = more;
        more = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, ((IFn)const__2.getRawRoot()).invoke(object3, RT.get(object4, object5), object6, iSeq));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        ISeq iSeq = (ISeq)object5;
        object5 = null;
        return core$update_BANG_.invokeStatic(object6, object7, object8, object9, iSeq);
    }

    public static Object invokeStatic(Object m4, Object k, Object f, Object x) {
        Object object = m4;
        Object object2 = k;
        Object object3 = f;
        f = null;
        Object object4 = m4;
        m4 = null;
        Object object5 = k;
        k = null;
        Object object6 = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, ((IFn)object3).invoke(RT.get(object4, object5), object6));
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
        return core$update_BANG_.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object m4, Object k, Object f) {
        Object object = m4;
        Object object2 = k;
        Object object3 = f;
        f = null;
        Object object4 = m4;
        m4 = null;
        Object object5 = k;
        k = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, ((IFn)object3).invoke(RT.get(object4, object5)));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$update_BANG_.invokeStatic(object4, object5, object6);
    }

    @Override
    public int getRequiredArity() {
        return 4;
    }
}

