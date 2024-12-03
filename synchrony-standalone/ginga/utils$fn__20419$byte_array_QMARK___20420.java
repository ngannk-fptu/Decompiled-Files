/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class utils$fn__20419$byte_array_QMARK___20420
extends AFunction {
    Object byte_type;
    public static final Var const__0 = RT.var("clojure.core", "some?");

    public utils$fn__20419$byte_array_QMARK___20420(Object object) {
        this.byte_type = object;
    }

    @Override
    public Object invoke(Object v) {
        Object object;
        Object and__5579__auto__20422;
        Object object2 = and__5579__auto__20422 = ((IFn)const__0.getRawRoot()).invoke(v);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = v;
            v = null;
            utils$fn__20419$byte_array_QMARK___20420 this_ = null;
            object = Util.equiv(this_.byte_type, object3.getClass()) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            object = and__5579__auto__20422;
            Object var2_2 = null;
        }
        return object;
    }
}

