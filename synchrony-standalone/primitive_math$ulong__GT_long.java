/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.BigInt;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class primitive_math$ulong__GT_long
extends AFunction
implements IFn.OL {
    public static final Var const__0 = RT.var("clojure.core", "bigint");

    public static long invokeStatic(Object x) {
        Object object = x;
        x = null;
        return ((BigInt)((IFn)const__0.getRawRoot()).invoke(object)).longValue();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return primitive_math$ulong__GT_long.invokeStatic(object2);
    }

    @Override
    public final long invokePrim(Object object) {
        Object object2 = object;
        object = null;
        return primitive_math$ulong__GT_long.invokeStatic(object2);
    }
}

