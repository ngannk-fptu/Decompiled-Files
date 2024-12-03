/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$trim_pattern
extends AFunction {
    public static final Var const__1 = RT.var("clojure.core", "subs");
    public static final Object const__2 = 1L;

    public static Object invokeStatic(Object pattern) {
        Object object;
        Object G__34728 = pattern;
        if (Util.identical(G__34728, null)) {
            object = null;
        } else {
            Object object2 = G__34728;
            G__34728 = null;
            Object object3 = pattern;
            pattern = null;
            object = ((IFn)const__1.getRawRoot()).invoke(object2, const__2, Numbers.num(Numbers.dec(RT.count(object3))));
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$trim_pattern.invokeStatic(object2);
    }
}

