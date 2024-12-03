/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class chash$get_node
extends AFunction {
    public static final Var const__0 = RT.var("ginga.chash", "find-replica");
    public static final Var const__1 = RT.var("ginga.chash", "b64-hash");
    public static final Var const__3 = RT.var("clojure.core", "val");

    public static Object invokeStatic(Object rring, Object topic) {
        Object object;
        Object object2 = rring;
        rring = null;
        Object object3 = topic;
        topic = null;
        Object G__14664 = ((IFn)const__0.getRawRoot()).invoke(object2, ((IFn)const__1.getRawRoot()).invoke(object3));
        if (Util.identical(G__14664, null)) {
            object = null;
        } else {
            Object object4 = G__14664;
            G__14664 = null;
            object = ((IFn)const__3.getRawRoot()).invoke(object4);
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return chash$get_node.invokeStatic(object3, object4);
    }
}

