/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$fn__19268
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.format", "Mappable");
    public static final AFn const__6 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 234, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "clj_time/format.clj"));
    public static final AFn const__8 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 234, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "clj_time/format.clj"));

    public static Object invokeStatic() {
        Var var;
        Var v__6812__auto__19270;
        Var var2 = const__0;
        var2.setMeta((IPersistentMap)((Object)const__6));
        Var var3 = v__6812__auto__19270 = var2;
        v__6812__auto__19270 = null;
        if (var3.hasRoot()) {
            var = null;
        } else {
            Var var4 = const__0;
            var4.setMeta((IPersistentMap)((Object)const__8));
            var = var4;
            var4.bindRoot(PersistentArrayMap.EMPTY);
        }
        return var;
    }

    @Override
    public Object invoke() {
        return format$fn__19268.invokeStatic();
    }
}

