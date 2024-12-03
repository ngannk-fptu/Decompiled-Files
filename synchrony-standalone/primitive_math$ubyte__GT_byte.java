/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import primitive_math.Primitives;

public final class primitive_math$ubyte__GT_byte
extends AFunction
implements IFn.LL {
    public static long invokeStatic(long x) {
        return Primitives.toByte(x);
    }

    @Override
    public Object invoke(Object object) {
        return primitive_math$ubyte__GT_byte.invokeStatic(RT.longCast((Number)object));
    }

    @Override
    public final long invokePrim(long l) {
        return primitive_math$ubyte__GT_byte.invokeStatic(l);
    }
}

