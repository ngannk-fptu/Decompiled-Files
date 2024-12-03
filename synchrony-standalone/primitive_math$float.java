/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import primitive_math.Primitives;

public final class primitive_math$float
extends AFunction
implements IFn.DD {
    public static double invokeStatic(double x) {
        return Primitives.toFloat(x);
    }

    @Override
    public Object invoke(Object object) {
        return primitive_math$float.invokeStatic(RT.doubleCast((Number)object));
    }

    @Override
    public final double invokePrim(double d) {
        return primitive_math$float.invokeStatic(d);
    }
}

