/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;
import java.util.zip.Adler32;

public final class byte_transforms$fn__18376
extends AFunction {
    public static final Keyword const__1 = RT.keyword(null, "seed");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("byte-streams", "to-byte-arrays");
    public static final Var const__8 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__9 = RT.var("clojure.core", "chunk-first");
    public static final Var const__10 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__13 = RT.var("clojure.core", "first");
    public static final Var const__14 = RT.var("clojure.core", "next");

    public static Object invokeStatic(Object x, Object options2) {
        Object temp__5804__auto__18382;
        Adler32 adler = new Adler32();
        Object object = temp__5804__auto__18382 = RT.get(options2, const__1);
        if (object != null && object != Boolean.FALSE) {
            Object seed;
            Object object2 = temp__5804__auto__18382;
            temp__5804__auto__18382 = null;
            Object object3 = seed = object2;
            seed = null;
            adler.update(RT.intCast((Object)RT.byteCast(object3)));
        }
        Object object4 = x;
        x = null;
        Object object5 = options2;
        options2 = null;
        Object seq_18377 = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(object4, object5));
        Object chunk_18378 = null;
        long count_18379 = 0L;
        long i_18380 = 0L;
        while (true) {
            Object ary;
            Object temp__5804__auto__18384;
            if (i_18380 < count_18379) {
                Object ary2;
                Object object6 = ary2 = ((Indexed)chunk_18378).nth(RT.intCast(i_18380));
                ary2 = null;
                adler.update((byte[])object6);
                Object object7 = seq_18377;
                seq_18377 = null;
                Object object8 = chunk_18378;
                chunk_18378 = null;
                ++i_18380;
                chunk_18378 = object8;
                seq_18377 = object7;
                continue;
            }
            Object object9 = seq_18377;
            seq_18377 = null;
            Object object10 = temp__5804__auto__18384 = ((IFn)const__3.getRawRoot()).invoke(object9);
            if (object10 == null || object10 == Boolean.FALSE) break;
            Object object11 = temp__5804__auto__18384;
            temp__5804__auto__18384 = null;
            Object seq_183772 = object11;
            Object object12 = ((IFn)const__8.getRawRoot()).invoke(seq_183772);
            if (object12 != null && object12 != Boolean.FALSE) {
                Object c__6065__auto__18383 = ((IFn)const__9.getRawRoot()).invoke(seq_183772);
                Object object13 = seq_183772;
                seq_183772 = null;
                Object object14 = c__6065__auto__18383;
                Object object15 = c__6065__auto__18383;
                c__6065__auto__18383 = null;
                i_18380 = RT.intCast(0L);
                count_18379 = RT.intCast(RT.count(object15));
                chunk_18378 = object14;
                seq_18377 = ((IFn)const__10.getRawRoot()).invoke(object13);
                continue;
            }
            Object object16 = ary = ((IFn)const__13.getRawRoot()).invoke(seq_183772);
            ary = null;
            adler.update((byte[])object16);
            Object object17 = seq_183772;
            seq_183772 = null;
            i_18380 = 0L;
            count_18379 = 0L;
            chunk_18378 = null;
            seq_18377 = ((IFn)const__14.getRawRoot()).invoke(object17);
        }
        Adler32 adler32 = adler;
        adler = null;
        return Numbers.num(adler32.getValue());
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_transforms$fn__18376.invokeStatic(object3, object4);
    }
}

