/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;
import manifold.stream.core.IEventSource;

public final class byte_streams$fn__18090
extends AFunction {
    public static final Var const__0 = RT.var("byte-streams.pushback-stream", "pushback-stream");
    public static final Keyword const__2 = RT.keyword(null, "buffer-size");
    public static final Object const__3 = 65536L;
    public static final Var const__4 = RT.var("manifold.stream", "consume");
    public static final Var const__6 = RT.var("byte-streams.pushback-stream", "->input-stream");

    public static Object invokeStatic(Object s2, Object options2) {
        Object object = options2;
        options2 = null;
        Object ps = ((IFn)const__0.getRawRoot()).invoke(RT.get(object, const__2, const__3));
        ((IFn)const__4.getRawRoot()).invoke(new byte_streams$fn__18090$fn__18091(ps), s2);
        Object object2 = s2;
        s2 = null;
        ((IEventSource)object2).onDrained(new byte_streams$fn__18090$fn__18093(ps));
        Object object3 = ps;
        ps = null;
        return ((IFn)const__6.getRawRoot()).invoke(object3);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$fn__18090.invokeStatic(object3, object4);
    }
}

