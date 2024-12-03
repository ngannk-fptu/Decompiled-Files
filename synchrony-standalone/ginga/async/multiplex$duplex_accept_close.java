/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class multiplex$duplex_accept_close
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core.async", "chan");
    public static final Var const__1 = RT.var("clojure.core.async", "close!");

    public static Object invokeStatic() {
        Object ch = ((IFn)const__0.getRawRoot()).invoke();
        ((IFn)const__1.getRawRoot()).invoke(ch);
        Object object = ch;
        Object object2 = ch;
        Object object3 = ch;
        ch = null;
        return Tuple.create(object, object2, object3);
    }

    @Override
    public Object invoke() {
        return multiplex$duplex_accept_close.invokeStatic();
    }
}

