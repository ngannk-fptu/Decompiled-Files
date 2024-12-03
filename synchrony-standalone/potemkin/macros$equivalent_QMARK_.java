/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class macros$equivalent_QMARK_
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "not");
    public static final Var const__2 = RT.var("potemkin.macros", "normalize-gensyms");
    public static final Var const__3 = RT.var("riddley.walk", "macroexpand-all");

    public static Object invokeStatic(Object a, Object b) {
        Boolean bl;
        Object object;
        Object and__5579__auto__26083;
        IFn iFn = (IFn)const__0.getRawRoot();
        Object object2 = and__5579__auto__26083 = a;
        if (object2 != null && object2 != Boolean.FALSE) {
            object = b;
        } else {
            object = and__5579__auto__26083;
            Object var2_2 = null;
        }
        Object object3 = iFn.invoke(object);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = a;
            a = null;
            Object object5 = b;
            b = null;
            bl = Util.equiv(object4, object5) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            Object object6 = a;
            a = null;
            Object object7 = b;
            b = null;
            bl = Util.equiv(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object6)), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object7))) ? Boolean.TRUE : Boolean.FALSE;
        }
        return bl;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return macros$equivalent_QMARK_.invokeStatic(object3, object4);
    }
}

