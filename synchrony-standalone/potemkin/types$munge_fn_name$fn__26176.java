/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$munge_fn_name$fn__26176
extends AFunction {
    public static final Var const__3 = RT.var("clojure.string", "replace");

    @Override
    public Object invoke(Object s2, Object p__26175) {
        Object object = p__26175;
        p__26175 = null;
        Object vec__26177 = object;
        Object regex2 = RT.nth(vec__26177, RT.intCast(0L), null);
        Object object2 = vec__26177;
        vec__26177 = null;
        Object replacement = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = s2;
        s2 = null;
        Object object4 = regex2;
        regex2 = null;
        Object object5 = replacement;
        replacement = null;
        types$munge_fn_name$fn__26176 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object3, object4, object5);
    }
}

