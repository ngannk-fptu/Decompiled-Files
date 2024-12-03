/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$contextual_map
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "transduce");
    public static final Var const__1 = RT.var("ginga.core", "contextual-transducer");
    public static final Var const__2 = RT.var("clojure.core", "constantly");
    public static final Var const__3 = RT.var("ginga.core", "contextual-return-persistent-result");
    public static final Var const__4 = RT.var("clojure.core", "conj!");
    public static final Var const__5 = RT.var("clojure.core", "transient");

    public static Object invokeStatic(Object f, Object ctx2, Object coll) {
        Object object = ctx2;
        ctx2 = null;
        Object object2 = f;
        f = null;
        Object object3 = coll;
        coll = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(object), const__3.getRawRoot(), object2), const__4.getRawRoot(), ((IFn)const__5.getRawRoot()).invoke(PersistentVector.EMPTY), object3);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$contextual_map.invokeStatic(object4, object5, object6);
    }
}

