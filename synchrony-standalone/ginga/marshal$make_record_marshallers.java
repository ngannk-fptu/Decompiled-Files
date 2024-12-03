/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class marshal$make_record_marshallers
extends AFunction {
    public static final Var const__0 = RT.var("ginga.marshal", "make-record-marshallers*");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object specs_by_type) {
        Object object = specs_by_type;
        specs_by_type = null;
        return ((IFn)const__0.getRawRoot()).invoke(object);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return marshal$make_record_marshallers.invokeStatic(object4, object5, object6);
    }
}

