/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class collections$compile_if
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "eval");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object test2, Object then, Object object) {
        Object object2;
        Object object3 = test2;
        test2 = null;
        Object object4 = ((IFn)const__0.getRawRoot()).invoke(object3);
        if (object4 != null && object4 != Boolean.FALSE) {
            object2 = then;
            then = null;
        } else {
            object2 = object;
            object = null;
        }
        return object2;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        Object object10 = object5;
        object5 = null;
        return collections$compile_if.invokeStatic(object6, object7, object8, object9, object10);
    }
}

