/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.Numbers;
import clojure.lang.RT;
import java.nio.ByteBuffer;
import primitive_math.Primitives;

public final class byte_streams$cmp_bufs$fn__18306
extends AFunction {
    Object a;
    long sign;
    int a_offset;
    long limit;
    Object b;
    int b_offset;
    public static final Object const__0 = 0L;

    public byte_streams$cmp_bufs$fn__18306(Object object, long l, int n, long l2, Object object2, int n2) {
        this.a = object;
        this.sign = l;
        this.a_offset = n;
        this.limit = l2;
        this.b = object2;
        this.b_offset = n2;
    }

    @Override
    public Object invoke() {
        Object object;
        block2: {
            long cmp;
            long idx = 0L;
            while (true) {
                if (Primitives.gte(idx, this_.limit)) {
                    object = const__0;
                    break block2;
                }
                cmp = Primitives.subtract(Primitives.bitAnd(0xFFFFFFFFL, ((ByteBuffer)this_.a).getInt(RT.intCast(Primitives.add(idx, this_.a_offset)))), Primitives.bitAnd(0xFFFFFFFFL, ((ByteBuffer)this_.b).getInt(RT.intCast(Primitives.add(idx, this_.b_offset)))));
                if (!Primitives.eq(0L, cmp)) break;
                idx = Primitives.add(idx, 4L);
            }
            byte_streams$cmp_bufs$fn__18306 this_ = null;
            object = Numbers.num(Primitives.multiply(this_.sign, cmp));
        }
        return object;
    }
}

