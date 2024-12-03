/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.zip.Traverser;

public final class zip$traverser
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "cons");

    public static Object invokeStatic(Object root2) {
        Object object = root2;
        root2 = null;
        return new Traverser(((IFn)const__0.getRawRoot()).invoke(object, null), null);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return zip$traverser.invokeStatic(object2);
    }
}

