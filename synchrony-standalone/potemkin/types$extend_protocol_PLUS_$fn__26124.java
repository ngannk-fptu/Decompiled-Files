/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$extend_protocol_PLUS_$fn__26124
extends AFunction {
    Object body;
    Object proto;
    public static final Var const__0 = RT.var("potemkin.types", "extend-implementations");

    public types$extend_protocol_PLUS_$fn__26124(Object object, Object object2) {
        this.body = object;
        this.proto = object2;
    }

    @Override
    public Object invoke(Object new_impls) {
        Object object = new_impls;
        new_impls = null;
        types$extend_protocol_PLUS_$fn__26124 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(this_.proto, object, this_.body);
    }
}

