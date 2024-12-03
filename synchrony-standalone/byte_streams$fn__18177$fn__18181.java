/*
 * Decompiled with CFR 0.152.
 */
import byte_streams.protocols.Closeable;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class byte_streams$fn__18177$fn__18181
extends AFunction {
    Object source;
    private static Class __cached_class__0;
    public static final Var const__0;
    public static final Var const__1;

    public byte_streams$fn__18177$fn__18181(Object object) {
        this.source = object;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Object invoke() {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(this_.source);
        if (object2 == null) return null;
        if (object2 == Boolean.FALSE) return null;
        Object object3 = this_.source;
        if (Util.classOf(object3) != __cached_class__0) {
            if (object3 instanceof Closeable) {
                object = ((Closeable)object3).close();
                return object;
            }
            object3 = object3;
            __cached_class__0 = Util.classOf(object3);
        }
        byte_streams$fn__18177$fn__18181 this_ = null;
        object = const__1.getRawRoot().invoke(object3);
        return object;
    }

    static {
        const__0 = RT.var("byte-streams.protocols", "closeable?");
        const__1 = RT.var("byte-streams.protocols", "close");
    }
}

