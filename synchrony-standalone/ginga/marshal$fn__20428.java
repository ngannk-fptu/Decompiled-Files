/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class marshal$fn__20428
extends AFunction {
    public static final Var const__0 = RT.var("ginga.marshal", "Marshal");
    public static final AFn const__6 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 10, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/marshal.cljc"));
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 10, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/marshal.cljc"));

    public static Object invokeStatic() {
        Var var;
        Var v__6812__auto__20430;
        Var var2 = const__0;
        var2.setMeta((IPersistentMap)((Object)const__6));
        Var var3 = v__6812__auto__20430 = var2;
        v__6812__auto__20430 = null;
        if (var3.hasRoot()) {
            var = null;
        } else {
            Var var4 = const__0;
            var4.setMeta((IPersistentMap)((Object)const__7));
            var = var4;
            var4.bindRoot(PersistentArrayMap.EMPTY);
        }
        return var;
    }

    @Override
    public Object invoke() {
        return marshal$fn__20428.invokeStatic();
    }
}

