/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class marshal$fn__20569
extends AFunction {
    public static final Var const__0 = RT.var("ginga.marshal", "marshal-datetime");

    public static Object invokeStatic(Object value, Object marshaller) {
        Object object = value;
        value = null;
        Object object2 = marshaller;
        marshaller = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return marshal$fn__20569.invokeStatic(object3, object4);
    }
}

