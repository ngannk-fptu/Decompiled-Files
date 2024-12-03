/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import primitive_math.Primitives;

public final class primitive_math$byte__GT_ubyte
extends AFunction
implements IFn.LL {
    public static long invokeStatic(long x) {
        return Primitives.toShort(Primitives.bitAnd(x, 255L));
    }

    @Override
    public Object invoke(Object object) {
        return primitive_math$byte__GT_ubyte.invokeStatic(RT.longCast((Number)object));
    }

    @Override
    public final long invokePrim(long l) {
        return primitive_math$byte__GT_ubyte.invokeStatic(l);
    }
}

