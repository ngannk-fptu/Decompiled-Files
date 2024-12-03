/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public final class primitive_math$long__GT_ulong
extends AFunction
implements IFn.LO {
    public static Object invokeStatic(long x) {
        return new BigInteger(RT.intCast(1L), ByteBuffer.allocate(RT.intCast(8L)).putLong(x).array());
    }

    @Override
    public Object invoke(Object object) {
        return primitive_math$long__GT_ulong.invokeStatic(RT.longCast((Number)object));
    }

    @Override
    public final Object invokePrim(long l) {
        return primitive_math$long__GT_ulong.invokeStatic(l);
    }
}

