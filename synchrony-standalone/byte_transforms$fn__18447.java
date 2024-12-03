/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import java.io.InputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public final class byte_transforms$fn__18447
extends AFunction {
    public static final Var const__0 = RT.var("byte-streams", "to-input-stream");

    public static Object invokeStatic(Object x, Object options2) {
        Object object = x;
        x = null;
        Object object2 = options2;
        options2 = null;
        return new GzipCompressorInputStream((InputStream)((IFn)const__0.getRawRoot()).invoke(object, object2), Boolean.TRUE);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_transforms$fn__18447.invokeStatic(object3, object4);
    }
}

