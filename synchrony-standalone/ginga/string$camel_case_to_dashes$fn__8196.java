/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class string$camel_case_to_dashes$fn__8196
extends AFunction {
    public static final Var const__4 = RT.var("clojure.core", "str");
    public static final Object const__5 = Character.valueOf('-');

    @Override
    public Object invoke(Object p__8195) {
        Object object = p__8195;
        p__8195 = null;
        Object vec__8197 = object;
        RT.nth(vec__8197, RT.intCast(0L), null);
        Object a = RT.nth(vec__8197, RT.intCast(1L), null);
        Object object2 = vec__8197;
        vec__8197 = null;
        Object b = RT.nth(object2, RT.intCast(2L), null);
        Object object3 = a;
        a = null;
        Object object4 = b;
        b = null;
        string$camel_case_to_dashes$fn__8196 this_ = null;
        return ((IFn)const__4.getRawRoot()).invoke(object3, const__5, object4);
    }
}

