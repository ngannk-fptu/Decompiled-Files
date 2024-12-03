/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;

public final class byte_transforms$decompress
extends AFunction {
    public static final Var const__0 = RT.var("byte-transforms", "decompress");
    public static final Keyword const__1 = RT.keyword(null, "snappy");
    public static final Var const__2 = RT.var("clojure.core", "deref");
    public static final Var const__3 = RT.var("byte-transforms", "decompressors");
    public static final Var const__4 = RT.var("clojure.core", "keyword");
    public static final Var const__5 = RT.var("clojure.core", "str");
    public static final Var const__6 = RT.var("clojure.core", "name");

    public static Object invokeStatic(Object x, Object algorithm, Object options2) {
        Object f;
        Object temp__5802__auto__18430;
        Object object = temp__5802__auto__18430 = ((IFn)((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot())).invoke(((IFn)const__4.getRawRoot()).invoke(algorithm));
        if (object == null || object == Boolean.FALSE) {
            Object object2 = algorithm;
            algorithm = null;
            throw (Throwable)new IllegalArgumentException((String)((IFn)const__5.getRawRoot()).invoke("Don't recognize decompressor '", ((IFn)const__6.getRawRoot()).invoke(object2), "'"));
        }
        Object object3 = temp__5802__auto__18430;
        temp__5802__auto__18430 = null;
        Object object4 = f = object3;
        f = null;
        Object object5 = x;
        x = null;
        Object object6 = options2;
        options2 = null;
        return ((IFn)object4).invoke(object5, object6);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return byte_transforms$decompress.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object x, Object algorithm) {
        Object object = x;
        x = null;
        Object object2 = algorithm;
        algorithm = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, null);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_transforms$decompress.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object x) {
        Object object = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return byte_transforms$decompress.invokeStatic(object2);
    }
}

