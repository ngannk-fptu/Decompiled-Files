/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class byte_transforms$fn__18477
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "safe?");
    public static final Keyword const__7 = RT.keyword(null, "fastest?");
    public static final Keyword const__8 = RT.keyword(null, "chunk-size");
    public static final Object const__9 = 100000.0;
    public static final Var const__10 = RT.var("byte-transforms", "bytes->wrapped-out->bytes");

    public static Object invokeStatic(Object x, Object p__18476) {
        Object map__18478;
        Object object;
        Object object2 = p__18476;
        p__18476 = null;
        Object map__184782 = object2;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(map__184782);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = ((IFn)const__1.getRawRoot()).invoke(map__184782);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = map__184782;
                map__184782 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object5));
            } else {
                Object object6 = ((IFn)const__3.getRawRoot()).invoke(map__184782);
                if (object6 != null && object6 != Boolean.FALSE) {
                    Object object7 = map__184782;
                    map__184782 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object7);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__184782;
            map__184782 = null;
        }
        Object options2 = map__18478 = object;
        Object safe_QMARK_ = RT.get(map__18478, const__6, Boolean.FALSE);
        Object fastest_QMARK_ = RT.get(map__18478, const__7, Boolean.FALSE);
        Object object8 = map__18478;
        map__18478 = null;
        Object chunk_size = RT.get(object8, const__8, const__9);
        Object object9 = x;
        x = null;
        Object object10 = safe_QMARK_;
        safe_QMARK_ = null;
        Object object11 = chunk_size;
        chunk_size = null;
        Object object12 = fastest_QMARK_;
        fastest_QMARK_ = null;
        Object object13 = options2;
        options2 = null;
        return ((IFn)const__10.getRawRoot()).invoke(object9, new byte_transforms$fn__18477$fn__18479(object10, object11, object12), object13);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_transforms$fn__18477.invokeStatic(object3, object4);
    }
}

