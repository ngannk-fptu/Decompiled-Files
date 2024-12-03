/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$extend_protocol_PLUS_$protocol_QMARK___26111
extends AFunction {
    public static final Var const__2 = RT.var("clojure.core", "resolve");
    public static final Var const__3 = RT.var("clojure.core", "var?");
    public static final Var const__4 = RT.var("potemkin.types", "protocol?");
    public static final Var const__5 = RT.var("clojure.core", "deref");

    @Override
    public Object invoke(Object p__26110) {
        Object object;
        Object and__5579__auto__26116;
        Object sym;
        Object vec__26112;
        Object object2 = p__26110;
        p__26110 = null;
        Object object3 = vec__26112 = object2;
        vec__26112 = null;
        Object object4 = sym = RT.nth(object3, RT.intCast(0L), null);
        sym = null;
        Object x = ((IFn)const__2.getRawRoot()).invoke(object4);
        Object object5 = and__5579__auto__26116 = ((IFn)const__3.getRawRoot()).invoke(x);
        if (object5 != null && object5 != Boolean.FALSE) {
            Object object6 = x;
            x = null;
            types$extend_protocol_PLUS_$protocol_QMARK___26111 this_ = null;
            object = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(object6));
        } else {
            object = and__5579__auto__26116;
            and__5579__auto__26116 = null;
        }
        return object;
    }
}

