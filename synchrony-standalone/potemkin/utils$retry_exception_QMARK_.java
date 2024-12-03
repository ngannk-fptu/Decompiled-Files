/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class utils$retry_exception_QMARK_
extends AFunction {
    public static final Var const__1 = RT.var("clojure.core", "class");

    public static Object invokeStatic(Object x) {
        Object object = x;
        x = null;
        return Util.equiv((Object)"clojure.lang.LockingTransaction$RetryEx", (Object)((Class)((IFn)const__1.getRawRoot()).invoke(object)).getName()) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return utils$retry_exception_QMARK_.invokeStatic(object2);
    }
}

