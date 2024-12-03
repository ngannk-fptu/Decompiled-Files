/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Reflector;
import clojure.lang.Util;
import clojure.lang.Var;

public final class utils$secure_eq_bytes_QMARK_
extends AFunction {
    public static final Var const__3 = RT.var("clojure.core", "reduce");
    public static final Var const__4 = RT.var("clojure.core", "bit-or");
    public static final Var const__5 = RT.var("clojure.core", "map");
    public static final Var const__6 = RT.var("clojure.core", "bit-xor");

    public static Object invokeStatic(Object a, Object b) {
        Boolean bl;
        boolean and__5579__auto__20411 = Util.equiv(Reflector.invokeStaticMethod(RT.classForName("clojure.lang.RT"), "alength", new Object[]{a}), Reflector.invokeStaticMethod(RT.classForName("clojure.lang.RT"), "alength", new Object[]{b}));
        if (and__5579__auto__20411) {
            Object object = a;
            a = null;
            Object object2 = b;
            b = null;
            bl = Numbers.isZero(((IFn)const__3.getRawRoot()).invoke(const__4.getRawRoot(), ((IFn)const__5.getRawRoot()).invoke(const__6.getRawRoot(), object, object2))) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            bl = and__5579__auto__20411 ? Boolean.TRUE : Boolean.FALSE;
        }
        return bl;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return utils$secure_eq_bytes_QMARK_.invokeStatic(object3, object4);
    }
}

