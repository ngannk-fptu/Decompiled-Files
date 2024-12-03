/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Reflector;
import clojure.lang.Var;

public final class utils$secure_eq_QMARK_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.utils", "secure-eq-bytes?");

    public static Object invokeStatic(Object a, Object b) {
        Object object;
        Object and__5579__auto__20414;
        Object object2 = and__5579__auto__20414 = a;
        if (object2 != null && object2 != Boolean.FALSE) {
            Object and__5579__auto__20413;
            Object object3 = and__5579__auto__20413 = b;
            if (object3 != null && object3 != Boolean.FALSE) {
                Object object4 = a;
                a = null;
                Object object5 = b;
                b = null;
                object = ((IFn)const__0.getRawRoot()).invoke(Reflector.invokeNoArgInstanceMember(object4, "getBytes", false), Reflector.invokeNoArgInstanceMember(object5, "getBytes", false));
            } else {
                object = and__5579__auto__20413;
                Object var3_3 = null;
            }
        } else {
            object = and__5579__auto__20414;
            Object var2_2 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return utils$secure_eq_QMARK_.invokeStatic(object3, object4);
    }
}

