/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class byte_streams$fn__17993
extends AFunction {
    public static final Var const__0 = RT.var("byte-streams", "conversions");
    public static final AFn const__6 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 56, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "byte_streams.clj"));
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 56, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "byte_streams.clj"));
    public static final Var const__8 = RT.var("clojure.core", "atom");
    public static final Var const__9 = RT.var("byte-streams.graph", "conversion-graph");

    public static Object invokeStatic() {
        Var var;
        Var v__6812__auto__17995;
        Var var2 = const__0;
        var2.setMeta((IPersistentMap)((Object)const__6));
        Var var3 = v__6812__auto__17995 = var2;
        v__6812__auto__17995 = null;
        if (var3.hasRoot()) {
            var = null;
        } else {
            Var var4 = const__0;
            var4.setMeta((IPersistentMap)((Object)const__7));
            var = var4;
            var4.bindRoot(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke()));
        }
        return var;
    }

    @Override
    public Object invoke() {
        return byte_streams$fn__17993.invokeStatic();
    }
}

