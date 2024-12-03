/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class types$merge_deftypes_STAR_$fn__26168
extends AFunction {
    Object fns;
    Object a_implements;
    public static final Var const__0 = RT.var("clojure.core", "concat");
    public static final Var const__1 = RT.var("clojure.core", "take");
    public static final Object const__2 = 5L;
    public static final Var const__3 = RT.var("clojure.core", "vec");
    public static final Var const__4 = RT.var("clojure.core", "distinct");

    public types$merge_deftypes_STAR_$fn__26168(Object object, Object object2) {
        this.fns = object;
        this.a_implements = object2;
    }

    @Override
    public Object invoke(Object p1__26167_SHARP_) {
        Object object = ((IFn)const__1.getRawRoot()).invoke(const__2, p1__26167_SHARP_);
        Object object2 = p1__26167_SHARP_;
        p1__26167_SHARP_ = null;
        types$merge_deftypes_STAR_$fn__26168 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, Tuple.create(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this_.a_implements, RT.nth(object2, RT.intCast(5L)))))), this_.fns);
    }
}

