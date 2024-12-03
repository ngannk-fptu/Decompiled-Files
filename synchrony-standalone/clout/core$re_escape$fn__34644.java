/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$re_escape$fn__34644
extends AFunction {
    public static final Var const__0 = RT.var("clout.core", "re-chars");
    public static final Var const__1 = RT.var("clojure.core", "str");
    public static final Object const__2 = Character.valueOf('\\');

    @Override
    public Object invoke(Object p1__34643_SHARP_) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(p1__34643_SHARP_);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = p1__34643_SHARP_;
            p1__34643_SHARP_ = null;
            core$re_escape$fn__34644 this_ = null;
            object = ((IFn)const__1.getRawRoot()).invoke(const__2, object3);
        } else {
            object = null;
        }
        return object;
    }
}

