/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;

public final class primitive_math$double
extends AFunction
implements IFn.OD {
    public static double invokeStatic(Object x) {
        Object object = x;
        x = null;
        return RT.uncheckedDoubleCast(object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return primitive_math$double.invokeStatic(object2);
    }

    @Override
    public final double invokePrim(Object object) {
        Object object2 = object;
        object = null;
        return primitive_math$double.invokeStatic(object2);
    }
}

