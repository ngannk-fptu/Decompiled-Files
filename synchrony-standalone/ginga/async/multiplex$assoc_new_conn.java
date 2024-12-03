/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class multiplex$assoc_new_conn
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "contains?");
    public static final Var const__1 = RT.var("ginga.core", "raise");
    public static final Var const__2 = RT.var("clojure.core", "assoc");

    public static Object invokeStatic(Object m4, Object id2, Object conn) {
        Object object = ((IFn)const__0.getRawRoot()).invoke(m4, id2);
        if (object != null && object != Boolean.FALSE) {
            ((IFn)const__1.getRawRoot()).invoke("connection", id2, "already exists");
        }
        Object object2 = m4;
        m4 = null;
        Object object3 = id2;
        id2 = null;
        Object object4 = conn;
        conn = null;
        return ((IFn)const__2.getRawRoot()).invoke(object2, object3, object4);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return multiplex$assoc_new_conn.invokeStatic(object4, object5, object6);
    }
}

