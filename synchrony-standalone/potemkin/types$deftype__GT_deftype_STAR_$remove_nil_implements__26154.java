/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class types$deftype__GT_deftype_STAR_$remove_nil_implements__26154
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "concat");
    public static final Var const__1 = RT.var("clojure.core", "take");
    public static final Object const__2 = 5L;
    public static final Var const__3 = RT.var("clojure.core", "vec");
    public static final Var const__4 = RT.var("clojure.core", "remove");
    public static final Var const__5 = RT.var("clojure.core", "nil?");
    public static final Var const__7 = RT.var("clojure.core", "drop");
    public static final Object const__8 = 6L;

    @Override
    public Object invoke(Object x) {
        Object object = ((IFn)const__1.getRawRoot()).invoke(const__2, x);
        IPersistentVector iPersistentVector = Tuple.create(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), RT.nth(x, RT.intCast(5L)))));
        Object object2 = x;
        x = null;
        types$deftype__GT_deftype_STAR_$remove_nil_implements__26154 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, iPersistentVector, ((IFn)const__7.getRawRoot()).invoke(const__8, object2));
    }
}

