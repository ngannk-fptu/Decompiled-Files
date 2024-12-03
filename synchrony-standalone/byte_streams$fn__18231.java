/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;

public final class byte_streams$fn__18231
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "chunk-size");
    public static final Keyword const__9 = RT.keyword(null, "close?");
    public static final Var const__10 = RT.var("byte-streams", "convert");
    public static final Object const__11 = RT.classForName("java.nio.channels.ReadableByteChannel");

    public static Object invokeStatic(Object file2, Object channel2, Object p__18230) {
        Object var13_11;
        Object map__18232;
        Object object;
        Object object2 = p__18230;
        p__18230 = null;
        Object map__182322 = object2;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(map__182322);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = ((IFn)const__1.getRawRoot()).invoke(map__182322);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = map__182322;
                map__182322 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object5));
            } else {
                Object object6 = ((IFn)const__3.getRawRoot()).invoke(map__182322);
                if (object6 != null && object6 != Boolean.FALSE) {
                    Object object7 = map__182322;
                    map__182322 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object7);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__182322;
            map__182322 = null;
        }
        Object options2 = map__18232 = object;
        Object chunk_size = RT.get(map__18232, const__6, RT.intCast(1000000.0));
        Object object8 = map__18232;
        map__18232 = null;
        Object close_QMARK_ = RT.get(object8, const__9, Boolean.TRUE);
        Object object9 = file2;
        file2 = null;
        Object object10 = options2;
        options2 = null;
        Object fc = ((IFn)const__10.getRawRoot()).invoke(object9, const__11, object10);
        try {
            long n;
            long idx = 0L;
            while ((n = ((FileChannel)fc).transferTo(idx, RT.longCast((Number)chunk_size), (WritableByteChannel)channel2)) > 0L) {
                idx = Numbers.add(idx, n);
            }
            var13_11 = null;
        }
        finally {
            Object object11 = close_QMARK_;
            close_QMARK_ = null;
            if (object11 != null && object11 != Boolean.FALSE) {
                Object object12 = channel2;
                channel2 = null;
                ((Channel)object12).close();
            }
            Object object13 = fc;
            fc = null;
            ((AbstractInterruptibleChannel)object13).close();
        }
        return var13_11;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return byte_streams$fn__18231.invokeStatic(object4, object5, object6);
    }
}

