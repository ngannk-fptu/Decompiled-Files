/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$assoc_pnil
extends AFunction {
    public static final Var const__1 = RT.var("clojure.core", "dissoc");
    public static final Var const__2 = RT.var("clojure.core", "assoc");

    public static Object invokeStatic(Object m4, Object key2, Object maybe_val) {
        Object object;
        if (Util.identical(maybe_val, null)) {
            Object object2 = m4;
            m4 = null;
            Object object3 = key2;
            key2 = null;
            object = ((IFn)const__1.getRawRoot()).invoke(object2, object3);
        } else {
            Object object4 = m4;
            m4 = null;
            Object object5 = key2;
            key2 = null;
            Object object6 = maybe_val;
            maybe_val = null;
            object = ((IFn)const__2.getRawRoot()).invoke(object4, object5, object6);
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$assoc_pnil.invokeStatic(object4, object5, object6);
    }
}

