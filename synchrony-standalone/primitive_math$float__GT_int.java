/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;

public final class primitive_math$float__GT_int
extends AFunction
implements IFn.DL {
    public static long invokeStatic(double x) {
        return Float.floatToRawIntBits((float)x);
    }

    @Override
    public Object invoke(Object object) {
        return primitive_math$float__GT_int.invokeStatic(RT.doubleCast((Number)object));
    }

    @Override
    public final long invokePrim(double d) {
        return primitive_math$float__GT_int.invokeStatic(d);
    }
}

