/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import java.security.MessageDigest;

public final class byte_transforms$fn__18408
extends AFunction {
    public static final Var const__0 = RT.var("byte-transforms", "hash-digest");
    public static final Var const__1 = RT.var("byte-streams", "to-byte-buffers");

    public static Object invokeStatic(Object x__18393__auto__, Object options__18394__auto__) {
        Object object = x__18393__auto__;
        x__18393__auto__ = null;
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(object, options__18394__auto__);
        Object object3 = options__18394__auto__;
        options__18394__auto__ = null;
        return ((IFn)const__0.getRawRoot()).invoke(MessageDigest.getInstance("sha-256"), object2, object3);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_transforms$fn__18408.invokeStatic(object3, object4);
    }
}

