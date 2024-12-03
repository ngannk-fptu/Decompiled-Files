/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class walk$do_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "list*");
    public static final AFn const__4 = Symbol.intern(null, "do");
    public static final Var const__5 = RT.var("clojure.core", "doall");
    public static final Var const__6 = RT.var("clojure.core", "map");

    public static Object invokeStatic(Object f, Object p__14715) {
        Object vec__14716;
        Object object = p__14715;
        p__14715 = null;
        Object object2 = vec__14716 = object;
        vec__14716 = null;
        Object seq__14717 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__14718 = ((IFn)const__1.getRawRoot()).invoke(seq__14717);
        Object object3 = seq__14717;
        seq__14717 = null;
        Object seq__147172 = ((IFn)const__2.getRawRoot()).invoke(object3);
        first__14718 = null;
        Object object4 = seq__147172;
        seq__147172 = null;
        Object body = object4;
        Object object5 = f;
        f = null;
        Object object6 = body;
        body = null;
        return ((IFn)const__3.getRawRoot()).invoke(const__4, ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(object5, object6)));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$do_handler.invokeStatic(object3, object4);
    }
}

