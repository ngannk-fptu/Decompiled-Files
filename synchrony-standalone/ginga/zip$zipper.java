/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class zip$zipper
extends AFunction {
    public static final Var const__0 = RT.var("ginga.zip", "from-seqable");
    public static final Var const__1 = RT.var("clojure.core", "cons");

    public static Object invokeStatic(Object root2) {
        Object object = root2;
        root2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(object, null), null);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return zip$zipper.invokeStatic(object2);
    }
}

