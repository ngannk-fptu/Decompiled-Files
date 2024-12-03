/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$resize_map
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "apply");
    public static final Var const__1 = RT.var("clojure.core", "dissoc");
    public static final Var const__2 = RT.var("clojure.core", "take");
    public static final Object const__6 = 0L;
    public static final Var const__7 = RT.var("clojure.core", "keys");

    public static Object invokeStatic(Object max_size, Object m4) {
        Object object;
        IFn iFn = (IFn)const__0.getRawRoot();
        Object object2 = const__1.getRawRoot();
        Object object3 = m4;
        IFn iFn2 = (IFn)const__2.getRawRoot();
        if (Numbers.lt(max_size, (long)RT.count(m4))) {
            Object object4 = max_size;
            max_size = null;
            object = Numbers.minus((long)RT.count(m4), object4);
        } else {
            object = const__6;
        }
        Object object5 = m4;
        m4 = null;
        return iFn.invoke(object2, object3, iFn2.invoke(object, ((IFn)const__7.getRawRoot()).invoke(object5)));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$resize_map.invokeStatic(object3, object4);
    }
}

