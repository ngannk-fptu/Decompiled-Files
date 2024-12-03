/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import primitive_math.Primitives;

public final class byte_streams$cmp_bufs
extends AFunction
implements IFn.OOL {
    public static long invokeStatic(Object a_SINGLEQUOTE_, Object b_SINGLEQUOTE_) {
        Object object;
        block6: {
            Object object2;
            Object a;
            long diff2 = Primitives.subtract(((Buffer)a_SINGLEQUOTE_).remaining(), ((Buffer)b_SINGLEQUOTE_).remaining());
            long sign2 = diff2 > 0L ? -1L : 1L;
            Object object3 = a = diff2 > 0L ? b_SINGLEQUOTE_ : a_SINGLEQUOTE_;
            if (diff2 > 0L) {
                object2 = a_SINGLEQUOTE_;
                a_SINGLEQUOTE_ = null;
            } else {
                object2 = b_SINGLEQUOTE_;
                b_SINGLEQUOTE_ = null;
            }
            Object b = object2;
            long limit = Primitives.shiftRight(((Buffer)a).remaining(), 2L);
            int a_offset = ((Buffer)a).position();
            int b_offset = ((Buffer)b).position();
            Object cmp = ((IFn)new byte_streams$cmp_bufs$fn__18306(a, sign2, a_offset, limit, b, b_offset)).invoke();
            if (Primitives.eq(0L, RT.longCast(cmp))) {
                long cmp2;
                int limit_SINGLEQUOTE_ = ((Buffer)a).remaining();
                long idx = limit;
                while (true) {
                    if (Primitives.gte(idx, limit_SINGLEQUOTE_)) {
                        object = Numbers.num(diff2);
                        break block6;
                    }
                    cmp2 = Primitives.subtract(RT.longCast((Object)Primitives.toShort(Primitives.bitAnd(255L, ((ByteBuffer)a).get(RT.intCast(Primitives.add(idx, a_offset)))))), RT.longCast((Object)Primitives.toShort(Primitives.bitAnd(255L, ((ByteBuffer)b).get(RT.intCast(Primitives.add(idx, b_offset)))))));
                    if (!Primitives.eq(0L, cmp2)) break;
                    idx = Primitives.inc(idx);
                }
                object = Numbers.num(Primitives.multiply(sign2, cmp2));
            } else {
                object = cmp;
                cmp = null;
            }
        }
        return ((Number)object).longValue();
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$cmp_bufs.invokeStatic(object3, object4);
    }

    @Override
    public final long invokePrim(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$cmp_bufs.invokeStatic(object3, object4);
    }
}

